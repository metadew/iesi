package io.metadew.iesi.server.rest.template.dto;

import io.metadew.iesi.server.rest.template.TemplateFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.Set;

public interface ITemplateDtoRepository {
    Page<TemplateDto> getAll(Authentication authentication, Pageable pageable, boolean onlyLatestVersion, Set<TemplateFilter> templateFilters);
    Optional<TemplateDto> getByName(Authentication authentication, String name, long version);
}
