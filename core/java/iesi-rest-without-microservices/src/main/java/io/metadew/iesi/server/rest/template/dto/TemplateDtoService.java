package io.metadew.iesi.server.rest.template.dto;

import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherAnyValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherFixedValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherTemplate;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValueKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.template.TemplateFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
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

    @Override
    public Optional<TemplateDto> fetchByName(Authentication authentication, String name, long version) {
        return templateDtoRepository.getByName(authentication, name, version);
    }

    @Override
    public Template convertToEntity(TemplateDto templateDto) {
        TemplateKey templateKey = new TemplateKey(IdentifierTools.getTemplateIdentifier(templateDto.getName(), templateDto.getVersion()));
        return new Template(
                templateKey,
                templateDto.getName(),
                templateDto.getVersion(),
                templateDto.getDescription(),
                templateDto.getMatchers().stream().map(matcherDto -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    if (matcherDto.getMatcherValue() instanceof MatcherAnyDto) {
                        return new Matcher(matcherKey, templateKey, matcherDto.getKey(), new MatcherAnyValue(
                                new MatcherValueKey(UUID.randomUUID()), matcherKey
                        ));
                    } else if (matcherDto.getMatcherValue() instanceof MatcherFixedDto) {
                        return new Matcher(matcherKey, templateKey, matcherDto.getKey(), new MatcherFixedValue(
                                new MatcherValueKey(UUID.randomUUID()), matcherKey, ((MatcherFixedDto) matcherDto.getMatcherValue()).getValue())
                        );
                    } else if (matcherDto.getMatcherValue() instanceof MatcherTemplateDto) {
                        return new Matcher(matcherKey, templateKey, matcherDto.getKey(), new MatcherTemplate(
                                new MatcherValueKey(UUID.randomUUID()), matcherKey, ((MatcherTemplateDto) matcherDto.getMatcherValue()).getTemplateName(), ((MatcherTemplateDto) matcherDto.getMatcherValue()).getTemplateVersion()
                        ));
                    }
                    return null;
                })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }
}
