package io.metadew.iesi.server.rest.template.dto;

import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherAnyValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherFixedValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherTemplate;
import io.metadew.iesi.server.rest.template.TemplateController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TemplateDtoResourceAssembler extends RepresentationModelAssemblerSupport<Template, TemplateDto> {

    public TemplateDtoResourceAssembler() {
        super(TemplateController.class, TemplateDto.class);
    }

    @Override
    public TemplateDto toModel(Template template) {
        TemplateDto templateDto = convertToDto(template);
        return null;
    }

    public TemplateDto toModel(TemplateDto templateDto) {
        return templateDto;
    }

    private TemplateDto convertToDto(Template template) {
        return new TemplateDto(
                template.getName(),
                template.getVersion(),
                template.getDescription(),
                template.getMatchers().stream()
                        .map(this::convertToDto)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );
    }

    private MatcherDto convertToDto(Matcher matcher) {
        if (matcher.getMatcherValue() instanceof MatcherAnyValue) {
            return new MatcherDto(matcher.getKey(), new MatcherAnyDto());
        } else if (matcher.getMatcherValue() instanceof MatcherFixedValue) {
            return new MatcherDto(matcher.getKey(), new MatcherFixedDto(((MatcherFixedValue) matcher.getMatcherValue()).getValue()));
        } else if (matcher.getMatcherValue() instanceof MatcherTemplate) {
            return new MatcherDto(matcher.getKey(), new MatcherTemplateDto(((MatcherTemplate) matcher.getMatcherValue()).getTemplateName(), ((MatcherTemplate) matcher.getMatcherValue()).getTemplateVersion()));
        }
        return null;
    }
}
