package io.metadew.iesi.server.rest.template;

import io.metadew.iesi.server.rest.template.dto.ITemplateDtoService;
import io.metadew.iesi.server.rest.template.dto.TemplateDto;
import io.metadew.iesi.server.rest.template.dto.TemplateDtoResourceAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final ITemplateDtoService templateDtoService;
    private final TemplateDtoResourceAssembler templateDtoResourceAssembler;
    private final PagedResourcesAssembler<TemplateDto> templateDtoPagedResourcesAssembler;

    public TemplateController(
            ITemplateDtoService templateDtoService,
            TemplateDtoResourceAssembler templateDtoResourceAssembler,
            PagedResourcesAssembler<TemplateDto> templateDtoPagedResourcesAssembler
    ) {
        this.templateDtoService = templateDtoService;
        this.templateDtoResourceAssembler = templateDtoResourceAssembler;
        this.templateDtoPagedResourcesAssembler = templateDtoPagedResourcesAssembler;
    }

    @GetMapping("")
    public PagedModel<TemplateDto> getAll(
            Pageable pageable,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "version") String version
    ) {
        Set<TemplateFilter> templateFilters = extractTemplateFilterOptions(name);
        boolean lastVersion = extractLastVersion(version);
        Page<TemplateDto> templateDtoPage = templateDtoService.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                pageable,
                lastVersion,
                templateFilters
        );
        if (templateDtoPage.hasContent()) {
            return templateDtoPagedResourcesAssembler.toModel(templateDtoPage, templateDtoResourceAssembler::toModel);
        }

        return (PagedModel<TemplateDto>) templateDtoPagedResourcesAssembler.toEmptyModel(templateDtoPage, TemplateDto.class);
    }

    private boolean extractLastVersion(String version) {
        return version != null && version.equalsIgnoreCase("latest");
    }

    private Set<TemplateFilter> extractTemplateFilterOptions(String name) {
        Set<TemplateFilter> templateFilters = new HashSet<>();
        if (name != null) {
            templateFilters.add(new TemplateFilter(TemplateFilterOption.NAME, name, false));
        }

        return templateFilters;
    }
}
