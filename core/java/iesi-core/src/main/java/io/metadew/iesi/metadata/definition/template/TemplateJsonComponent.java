package io.metadew.iesi.metadata.definition.template;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherJsonComponent;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.*;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplateJsonComponent {

    public enum Field {
        NAME_KEY("name"),
        DESCRIPTION_KEY("description"),
        VERSION_KEY("version"),
        MATCHERS_KEY("matchers");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<Template> {
        @Override
        public Template deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String name = node.get(Field.NAME_KEY.value()).asText();
            long version = node.get(Field.VERSION_KEY.value()).asLong();
            String description = node.get(Field.DESCRIPTION_KEY.value()).asText();
            TemplateKey templateKey = TemplateKey.builder()
                    .id(IdentifierTools.getTemplateIdentifier(name, version))
                    .build();

            List<Matcher> matchers = new ArrayList<>();
            for (JsonNode matcherNode : node.get(Field.MATCHERS_KEY.value())) {
                String key = matcherNode.get(MatcherJsonComponent.Field.KEY_KEY.value()).asText();
                MatcherKey matcherKey = MatcherKey.builder()
                        .id(IdentifierTools.getMatcherValueIdentifier(templateKey, key))
                        .build();
                JsonNode matcherValueNode = matcherNode.get(MatcherJsonComponent.Field.MATCHER_VALUE_KEY.value());
                String matcherValueType = matcherValueNode.get(MatcherValueJsonComponent.Field.TYPE_KEY.value()).asText();
                MatcherValue matcherValue;
                if (matcherValueType.equals(MatcherAnyValueJsonComponent.Field.TYPE.value())) {
                    matcherValue = MatcherAnyValue.builder()
                            .matcherValueKey(MatcherValueKey.builder().id(IdentifierTools.getMatcherValueIdentifier(templateKey, matcherKey)).build())
                            .matcherKey(matcherKey)
                            .build();
                } else if (matcherValueType.equals(MatcherFixedValueJsonComponent.Field.TYPE.value())) {
                    matcherValue = MatcherFixedValue.builder()
                            .metadataKey(MatcherValueKey.builder().id(IdentifierTools.getMatcherValueIdentifier(templateKey, matcherKey)).build())
                            .value(matcherValueNode.get(MatcherFixedValueJsonComponent.Field.VALUE_KEY.value()).asText())
                            .matcherKey(matcherKey)
                            .build();
                } else if (matcherValueType.equals(MatcherTemplateJsonComponent.Field.TYPE.value())) {
                    matcherValue = MatcherTemplate.builder()
                            .metadataKey(MatcherValueKey.builder().id(IdentifierTools.getMatcherValueIdentifier(templateKey, matcherKey)).build())
                            .templateName(matcherValueNode.get(MatcherTemplateJsonComponent.Field.TEMPLATE_NAME_KEY.value()).asText())
                            .templateVersion(matcherValueNode.get(MatcherTemplateJsonComponent.Field.TEMPLATE_VERSION_KEY.value()).asLong())
                            .matcherKey(matcherKey)
                            .build();
                } else {
                    throw new RuntimeException("Cannot create MatcherValue of type " + matcherValueType);
                }

                matchers.add(
                        Matcher.builder()
                                .matcherKey(matcherKey)
                                .key(matcherNode.get(MatcherJsonComponent.Field.KEY_KEY.value()).asText())
                                .templateKey(templateKey)
                                .matcherValue(matcherValue)
                                .build()
                );
            }

            return Template.builder()
                    .metadataKey(templateKey)
                    .name(name)
                    .version(version)
                    .description(description)
                    .matchers(matchers)
                    .build();
        }

    }
}
