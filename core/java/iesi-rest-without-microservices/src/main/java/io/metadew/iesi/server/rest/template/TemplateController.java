package io.metadew.iesi.server.rest.template;

import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.metadata.service.template.ITemplateService;
import io.metadew.iesi.server.rest.template.dto.ITemplateDtoService;
import io.metadew.iesi.server.rest.template.dto.TemplateDto;
import io.metadew.iesi.server.rest.template.dto.TemplateDtoResourceAssembler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/templates")
@ConditionalOnWebApplication
public class TemplateController {

    private final ITemplateDtoService templateDtoService;
    private final ITemplateService templateService;
    private final TemplateDtoResourceAssembler templateDtoResourceAssembler;
    private final PagedResourcesAssembler<TemplateDto> templateDtoPagedResourcesAssembler;

    public TemplateController(
            ITemplateDtoService templateDtoService,
            ITemplateService templateService,
            TemplateDtoResourceAssembler templateDtoResourceAssembler,
            PagedResourcesAssembler<TemplateDto> templateDtoPagedResourcesAssembler
    ) {
        this.templateDtoService = templateDtoService;
        this.templateService = templateService;
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

    @GetMapping("/{name}/{version}")
    public TemplateDto getByNameAndVersion(@PathVariable String name, @PathVariable Long version) {
        Optional<TemplateDto> optionalTemplateDto = templateDtoService.fetchByName(
                SecurityContextHolder.getContext().getAuthentication(),
                name,
                version
        );

        if (!optionalTemplateDto.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cannot find the template %s with the version %s", name, version));
        }

        return optionalTemplateDto.get();
    }

    @PostMapping("")
    public ResponseEntity<TemplateDto> create(@RequestBody TemplateDto templateDto) {
        templateService.insert(templateDtoService.convertToEntity(templateDto));
        return ResponseEntity.ok(templateDto);
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
