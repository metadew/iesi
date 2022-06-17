package io.metadew.iesi.server.rest.template;

import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.service.template.ITemplateService;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.template.dto.ITemplateDtoService;
import io.metadew.iesi.server.rest.template.dto.TemplateDto;
import io.metadew.iesi.server.rest.template.dto.TemplateDtoResourceAssembler;
import lombok.extern.log4j.Log4j2;
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

import java.util.*;

@RestController
@RequestMapping("/templates")
@ConditionalOnWebApplication
@Log4j2
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
        if (templateService.get(templateDto.getName(), templateDto.getVersion()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The template %s with version %s already exists", templateDto.getName(), templateDto.getVersion()));
        }
        templateService.insert(templateDtoService.convertToEntity(templateDto));
        return ResponseEntity.ok(templateDto);
    }

    @PutMapping("/{name}/{version}")
    public TemplateDto put(@PathVariable String name, @PathVariable long version, @RequestBody TemplateDto templateDto) {
        if (!templateDto.getName().equals(name)) throw new DataBadRequestException(name);
        if (templateDto.getVersion() != version) throw new DataBadRequestException(version);

        if (!templateService.get(name, version).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The template %s with version %s does not exist", name, version));
        }

        Template template = templateDtoService.convertToEntity(templateDto);

        templateService.update(template);
        return templateDto;
    }

    @DeleteMapping("/{name}/{version}")
    public ResponseEntity<?> delete(@PathVariable String name, @PathVariable long version) {
        if (!templateService.get(name, version).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The template %s with version %s does not exist", name, version));
        }

        templateService.delete(new TemplateKey(IdentifierTools.getTemplateIdentifier(name, version)));

        return ResponseEntity.status(HttpStatus.OK).build();

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
