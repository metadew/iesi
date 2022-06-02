package io.metadew.iesi.server.rest.template.dto;

import io.metadew.iesi.server.rest.template.TemplateFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TemplateDtoService implements ITemplateDtoService {

    private final ITemplateDtoRepository templateDtoRepository;

    public TemplateDtoService(ITemplateDtoRepository templateDtoRepository) {
        this.templateDtoRepository = templateDtoRepository;
    }

    @Override
    public Page<TemplateDto> fetchAll(Authentication authentication, Pageable pageable, boolean onlyLatestVersion, Set<TemplateFilter> templateFilters) {
        return templateDtoRepository.getAll(authentication, pageable, onlyLatestVersion, templateFilters);
    }
}
