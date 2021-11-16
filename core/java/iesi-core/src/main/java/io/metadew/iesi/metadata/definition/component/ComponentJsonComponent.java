package io.metadew.iesi.metadata.definition.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.MetadataJsonComponent;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupJsonComponent;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComponentJsonComponent {

    public enum Field {
        TYPE("component"),
        ID_KEY("id"),
        COMPONENT_TYPE_KEY("type"),
        NAME_KEY("name"),
        DESCRIPTION_KEY("description"),
        VERSION_KEY("version"),
        PARAMETERS_KEY("parameters"),
        ATTRIBUTES_KEY("attributes");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<Component> {
        @Override
        public Component deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String componentId = IdentifierTools.getComponentIdentifier(node.get(Field.NAME_KEY.value()).asText());
            String securityGroupName;

            if (node.get(SecurityGroupJsonComponent.Field.SECURITY_GROUP_NAME.value()) != null) {
                securityGroupName = node.get(SecurityGroupJsonComponent.Field.SECURITY_GROUP_NAME.value()).asText();
            } else {
                securityGroupName = "PUBLIC";
            }
            SecurityGroupKey securityGroupKey = SecurityGroupService.getInstance().get(securityGroupName)
                    .map(Metadata::getMetadataKey)
                    .orElseThrow(() -> new RuntimeException("could not find Security Group " + securityGroupName));

            JsonNode versionNode = node.get(Field.VERSION_KEY.value());
            long versionNumber = versionNode.get(ComponentVersionJsonComponent.Field.NUMBER_KEY.value()).asLong();

            //version
            ComponentVersion componentVersion = new ComponentVersion(
                    new ComponentVersionKey(
                            componentId, versionNumber),
                    versionNode.get(ComponentVersionJsonComponent.Field.DESCRIPTION_KEY.value()).asText());

            // component parameters
            List<ComponentParameter> componentParameters = new ArrayList<>();
            for (JsonNode componentParameterNode : node.get(Field.PARAMETERS_KEY.value())) {
                componentParameters.add(new ComponentParameter(new ComponentParameterKey(
                        componentId,
                        versionNumber,
                        componentParameterNode.get(ComponentParameterJsonComponent.Field.NAME_KEY.value()).asText()),
                        componentParameterNode.get(ComponentParameterJsonComponent.Field.VALUE_KEY.value()).asText()));
            }

            // component attributes
            List<ComponentAttribute> componentAttributes = new ArrayList<>();
            for (JsonNode componentAttributeNode : node.get(Field.ATTRIBUTES_KEY.value())) {
                componentAttributes.add(new ComponentAttribute(
                        new ComponentAttributeKey(
                                new ComponentKey(componentId, versionNumber),
                                new EnvironmentKey(componentAttributeNode.get(ComponentAttributeJsonComponent.Field.ENVIRONMENT_KEY.value()).asText()),
                                componentAttributeNode.get(ComponentAttributeJsonComponent.Field.NAME_KEY.value()).asText()),
                        componentAttributeNode.get(ComponentAttributeJsonComponent.Field.VALUE_KEY.value()).asText()));
            }

            return new Component(
                    new ComponentKey(componentId, versionNumber),
                    securityGroupKey,
                    securityGroupName,
                    node.get(Field.COMPONENT_TYPE_KEY.value()).asText(),
                    node.get(Field.NAME_KEY.value()).asText(),
                    node.get(Field.DESCRIPTION_KEY.value()).asText(),
                    componentVersion,
                    componentParameters,
                    componentAttributes);
        }
    }

    public static class Serializer extends JsonSerializer<Component> {
        @Override
        public void serialize(Component component, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(MetadataJsonComponent.Field.TYPE_KEY.value(), ComponentJsonComponent.Field.TYPE.value());

            jsonGenerator.writeObjectFieldStart(MetadataJsonComponent.Field.DATA_KEY.value());

            jsonGenerator.writeStringField(Field.ID_KEY.value(), component.getMetadataKey().getId());
            jsonGenerator.writeStringField(Field.COMPONENT_TYPE_KEY.value(), component.getType());
            jsonGenerator.writeStringField(Field.NAME_KEY.value(), component.getName());
            jsonGenerator.writeStringField(Field.DESCRIPTION_KEY.value(), component.getDescription());

            // write version
            ComponentVersion componentVersion = component.getVersion();
            jsonGenerator.writeObjectFieldStart(Field.VERSION_KEY.value());
            jsonGenerator.writeNumberField(ComponentVersionJsonComponent.Field.NUMBER_KEY.value(), componentVersion.getMetadataKey().getComponentKey().getVersionNumber());
            jsonGenerator.writeStringField(ComponentVersionJsonComponent.Field.DESCRIPTION_KEY.value(), componentVersion.getDescription());
            jsonGenerator.writeEndObject();

            // write parameters
            jsonGenerator.writeArrayFieldStart(Field.PARAMETERS_KEY.value());
            for (ComponentParameter componentParameter : component.getParameters()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField(ComponentParameterJsonComponent.Field.NAME_KEY.value(), componentParameter.getMetadataKey().getParameterName());
                jsonGenerator.writeStringField(ComponentParameterJsonComponent.Field.VALUE_KEY.value(), componentParameter.getValue());
                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();


            // write parameters
            jsonGenerator.writeArrayFieldStart(Field.ATTRIBUTES_KEY.value());
            for (ComponentAttribute componentAttribute : component.getAttributes()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField(ComponentAttributeJsonComponent.Field.NAME_KEY.value(), componentAttribute.getMetadataKey().getComponentAttributeName());
                jsonGenerator.writeStringField(ComponentAttributeJsonComponent.Field.VALUE_KEY.value(), componentAttribute.getValue());
                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();

        }
    }
}
