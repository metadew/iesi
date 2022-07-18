package io.metadew.iesi.server.rest.builder.template;

import io.metadew.iesi.server.rest.template.dto.*;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateDtoBuilder {
    public static TemplateDto simpleTemplateDto(UUID templateUuid, String name, long version) {
        return TemplateDto.builder()
                .uuid(templateUuid)
                .name(name)
                .description(name + " desc")
                .version(version)
                .matchers(
                        Stream.of(
                                new MatcherDto("key1", new MatcherAnyDto()),
                                new MatcherDto("key2", new MatcherFixedDto("key2")),
                                new MatcherDto("key3", new MatcherTemplateDto("templateName", 1L))
                        ).collect(Collectors.toSet())
                ).build();
    }
}
