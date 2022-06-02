package io.metadew.iesi.server.rest.template.dto;

import io.metadew.iesi.server.rest.template.TemplateFilter;
import io.metadew.iesi.server.rest.template.dto.TemplateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.Authentication;

import java.util.Set;

public interface ITemplateDtoService {
    Page<TemplateDto> fetchAll(Authentication authentication, Pageable pageable, boolean lastVersion, Set<TemplateFilter> templateFilters);
}