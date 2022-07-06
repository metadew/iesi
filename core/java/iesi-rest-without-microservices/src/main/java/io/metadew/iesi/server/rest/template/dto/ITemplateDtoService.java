package io.metadew.iesi.server.rest.template.dto;

import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.server.rest.template.TemplateFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.Set;

public interface ITemplateDtoService {
    Page<TemplateDto> fetchAll(Authentication authentication, Pageable pageable, boolean lastVersion, Set<TemplateFilter> templateFilters);
    Optional<TemplateDto> fetchByName(Authentication authentication, String name, long version);
    Template convertToEntity(TemplateDto templateDto);
}