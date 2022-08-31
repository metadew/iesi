package io.metadew.iesi.server.rest.builder.template;

import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherAnyValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherFixedValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherTemplate;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValueKey;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateBuilder {
    public static Template simpleTemplate(UUID templateUuid, String name, long version) {
        MatcherKey anyMatcherKey = new MatcherKey(UUID.randomUUID());
        MatcherKey fixedMatcherKey = new MatcherKey(UUID.randomUUID());
        MatcherKey templateMatcherKey = new MatcherKey(UUID.randomUUID());

        return Template.builder()
                .metadataKey(new TemplateKey(templateUuid))
                .name(name)
                .description(name + " desc")
                .version(version)
                .matchers(Stream.of(
                        new Matcher(anyMatcherKey, new TemplateKey(templateUuid), "key1", new MatcherAnyValue(new MatcherValueKey(UUID.randomUUID()), anyMatcherKey)),
                        new Matcher(fixedMatcherKey, new TemplateKey(templateUuid), "key2", new MatcherFixedValue(new MatcherValueKey(UUID.randomUUID()), fixedMatcherKey, "key2")),
                        new Matcher(templateMatcherKey, new TemplateKey(templateUuid), "key3", new MatcherTemplate(new MatcherValueKey(UUID.randomUUID()), templateMatcherKey, "templateName", 1L))
                ).collect(Collectors.toList()))
                .build();
    }
}
