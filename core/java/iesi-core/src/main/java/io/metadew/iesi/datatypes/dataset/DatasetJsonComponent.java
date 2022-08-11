package io.metadew.iesi.datatypes.dataset;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.dataset.implementation.*;
import io.metadew.iesi.datatypes.dataset.implementation.database.*;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelJsonComponent;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.MetadataJsonComponent;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class DatasetJsonComponent {

    public enum Field {
        TYPE("dataset"),
        NAME_KEY("name"),
        SECURITY_GROUP_NAME_KEY("securityGroupName"),
        IMPLEMENTATIONS_KEY("implementations");

        private final String label;
        private final DatasetConfiguration datasetConfiguration = SpringContext.getBean(DatasetConfiguration.class);

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<Dataset> {
        @Override
        public Dataset deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String name = Optional.ofNullable(node.get(Field.NAME_KEY.value())).map(JsonNode::asText)
                    .orElseThrow(() -> new RuntimeException("Name field is a mandatory parameter"));
            DatasetKey datasetKey = SpringContext.getBean(DatasetConfiguration.class).getByName(name)
                    .map(Metadata::getMetadataKey)
                    .orElse(new DatasetKey());
            String securityGroupName = Optional.ofNullable(node.get(Field.SECURITY_GROUP_NAME_KEY.value())).map(JsonNode::asText).orElse("PUBLIC");
            SecurityGroupKey securityGroupKey = SpringContext.getBean(SecurityGroupConfiguration.class).getByName(securityGroupName)
                    .map(Metadata::getMetadataKey)
                    .orElseThrow(() -> new RuntimeException("Could not find security group with name " + securityGroupName));
            Set<DatasetImplementation> datasetImplementations = new HashSet<>();
            for (JsonNode implementationNode : node.get(Field.IMPLEMENTATIONS_KEY.value())) {
                Set<DatasetImplementationLabel> datasetImplementationLabels = new HashSet<>();
                DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
                for (JsonNode labelNode : implementationNode.get(DatasetImplementationJsonComponent.Field.LABELS_KEY.value())) {
                    datasetImplementationLabels.add(DatasetImplementationLabel.builder()
                            .metadataKey(new DatasetImplementationLabelKey())
                            .datasetImplementationKey(datasetImplementationKey)
                            .value(labelNode.get(DatasetImplementationLabelJsonComponent.Field.LABEL_KEY.value()).asText())
                            .build());
                }

                String type = implementationNode.get(DatasetImplementationJsonComponent.Field.TYPE_KEY.value()).asText();
                if (type.equalsIgnoreCase(DatasetImplementationType.DATABASE.value())) {
                    Set<DatabaseDatasetImplementationKeyValue> keyValues = new HashSet<>();
                    for (JsonNode keyValueNode : implementationNode.get(DatasetImplementationJsonComponent.Field.KEY_VALUES_KEY.value())) {
                        keyValues.add(DatabaseDatasetImplementationKeyValue.builder()
                                .metadataKey(new DatabaseDatasetImplementationKeyValueKey())
                                .datasetImplementationKey(datasetImplementationKey)
                                .key(keyValueNode.get(DatasetImplementationKeyValueJsonComponent.Field.KEY_KEY.value()).asText())
                                .value(keyValueNode.get(DatasetImplementationKeyValueJsonComponent.Field.VALUE_KEY.value()).asText())
                                .build());
                    }
                    datasetImplementations.add(
                            DatabaseDatasetImplementation.builder()
                                    .metadataKey(datasetImplementationKey)
                                    .datasetKey(datasetKey)
                                    .name(name)
                                    .datasetImplementationLabels(datasetImplementationLabels)
                                    .keyValues(keyValues)
                                    .build()
                    );
                } else if (type.equalsIgnoreCase(DatasetImplementationType.IN_MEMORY.value())) {
                    Set<InMemoryDatasetImplementationKeyValue> keyValues = new HashSet<>();
                    for (JsonNode keyValueNode : implementationNode.get(DatasetImplementationJsonComponent.Field.KEY_VALUES_KEY.value())) {
                        keyValues.add(InMemoryDatasetImplementationKeyValue.builder()
                                .metadataKey(new InMemoryDatasetImplementationKeyValueKey())
                                .datasetImplementationKey(datasetImplementationKey)
                                .key(keyValueNode.get(DatasetImplementationKeyValueJsonComponent.Field.KEY_KEY.value()).asText())
                                .value(new Text(keyValueNode.get(DatasetImplementationKeyValueJsonComponent.Field.VALUE_KEY.value()).asText()))
                                .build());
                    }
                    datasetImplementations.add(
                            InMemoryDatasetImplementation.builder()
                                    .metadataKey(datasetImplementationKey)
                                    .datasetKey(datasetKey)
                                    .name(name)
                                    .datasetImplementationLabels(datasetImplementationLabels)
                                    .keyValues(keyValues)
                                    .build()
                    );
                }
                else {
                    throw new RuntimeException("Cannot create DatasetImplementation of type " + type);
                }
            }
            return Dataset.builder()
                    .metadataKey(datasetKey)
                    .name(name)
                    .securityGroupKey(securityGroupKey)
                    .securityGroupName(securityGroupName)
                    .datasetImplementations(datasetImplementations)
                    .build();
        }

    }

    public static class Serializer extends JsonSerializer<Dataset> {
        @Override
        public void serialize(Dataset dataset, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(MetadataJsonComponent.Field.TYPE_KEY.value(), DatasetJsonComponent.Field.TYPE.value());

            jsonGenerator.writeObjectFieldStart(MetadataJsonComponent.Field.DATA_KEY.value());

            jsonGenerator.writeStringField(DatasetJsonComponent.Field.NAME_KEY.value(), dataset.getName());
            jsonGenerator.writeArrayFieldStart(Field.IMPLEMENTATIONS_KEY.value());
            for (DatasetImplementation datasetImplementation : dataset.getDatasetImplementations()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeArrayFieldStart(DatasetImplementationJsonComponent.Field.LABELS_KEY.value());
                for (DatasetImplementationLabel datasetImplementationLabel : datasetImplementation.getDatasetImplementationLabels()) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField(DatasetImplementationLabelJsonComponent.Field.LABEL_KEY.value(), datasetImplementationLabel.getValue());
                    jsonGenerator.writeEndObject();
                }
                jsonGenerator.writeEndArray();

                if (datasetImplementation instanceof DatabaseDatasetImplementation) {
                    jsonGenerator.writeStringField(DatasetImplementationJsonComponent.Field.TYPE_KEY.value(), DatasetImplementationType.DATABASE.value());
                    jsonGenerator.writeArrayFieldStart(DatasetImplementationJsonComponent.Field.KEY_VALUES_KEY.value());
                    for (DatabaseDatasetImplementationKeyValue datasetImplementationKeyValue : ((DatabaseDatasetImplementation) datasetImplementation).getKeyValues()) {
                        jsonGenerator.writeStartObject();
                        jsonGenerator.writeStringField(DatasetImplementationKeyValueJsonComponent.Field.KEY_KEY.value(), datasetImplementationKeyValue.getKey());
                        jsonGenerator.writeStringField(DatasetImplementationKeyValueJsonComponent.Field.VALUE_KEY.value(), datasetImplementationKeyValue.getValue());
                        jsonGenerator.writeEndObject();
                    }
                    jsonGenerator.writeEndArray();
                } else if (datasetImplementation instanceof InMemoryDatasetImplementation) {
                    jsonGenerator.writeStringField(DatasetImplementationJsonComponent.Field.TYPE_KEY.value(), DatasetImplementationType.IN_MEMORY.value());
                    jsonGenerator.writeArrayFieldStart(DatasetImplementationJsonComponent.Field.KEY_VALUES_KEY.value());
                    for (InMemoryDatasetImplementationKeyValue datasetImplementationKeyValue : ((InMemoryDatasetImplementation) datasetImplementation).getKeyValues()) {
                        jsonGenerator.writeStringField(DatasetImplementationKeyValueJsonComponent.Field.KEY_KEY.value(), datasetImplementationKeyValue.getKey());
                        jsonGenerator.writeStringField(DatasetImplementationKeyValueJsonComponent.Field.VALUE_KEY.value(), datasetImplementationKeyValue.getValue().toString());
                        jsonGenerator.writeEndObject();
                    }
                }
                else {
                    throw new RuntimeException("dataset implementation type is not correct");
                }
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        }
    }
}
