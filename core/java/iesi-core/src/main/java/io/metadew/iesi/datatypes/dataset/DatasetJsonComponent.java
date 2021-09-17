package io.metadew.iesi.datatypes.dataset;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import io.metadew.iesi.datatypes.dataset.implementation.*;
import io.metadew.iesi.datatypes.dataset.implementation.database.*;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelJsonComponent;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.MetadataJsonComponent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DatasetJsonComponent {

    public enum Field {
        TYPE("dataset"),
        NAME_KEY("name"),
        IMPLEMENTATIONS_KEY("implementations");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<Dataset> {
        @Override
        public Dataset deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String name = node.get(Field.NAME_KEY.value()).asText();
            DatasetKey datasetKey = DatasetConfiguration.getInstance().getByName(name)
                    .map(Metadata::getMetadataKey)
                    .orElse(new DatasetKey());
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
                if (type.equalsIgnoreCase(DatasetImplementationType.IN_MEMORY.value())) {
                    Set<DatasetImplementationKeyValue> keyValues = new HashSet<>();
                    for (JsonNode keyValueNode : implementationNode.get(DatabaseDatasetImplementationJsonComponent.Field.KEY_VALUES_KEY.value())) {
                        keyValues.add(DatasetImplementationKeyValue.builder()
                                .metadataKey(new DatasetImplementationKeyValueKey())
                                .datasetImplementationKey(datasetImplementationKey)
                                .key(keyValueNode.get(DatabaseDatasetImplementationKeyValueJsonComponent.Field.KEY_KEY.value()).asText())
                                .value(keyValueNode.get(DatabaseDatasetImplementationKeyValueJsonComponent.Field.VALUE_KEY.value()).asText())
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
                } else {
                    throw new RuntimeException("Cannot create DatasetImplementation of type " + type);
                }
            }
            return Dataset.builder()
                    .metadataKey(datasetKey)
                    .name(name)
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
                    jsonGenerator.writeStringField(DatasetImplementationJsonComponent.Field.TYPE_KEY.value(), DatasetImplementationType.IN_MEMORY.value());
                    jsonGenerator.writeArrayFieldStart(DatabaseDatasetImplementationJsonComponent.Field.KEY_VALUES_KEY.value());
                    for (DatasetImplementationKeyValue datasetImplementationKeyValue : ((DatabaseDatasetImplementation) datasetImplementation).getKeyValues()) {
                        jsonGenerator.writeStartObject();
                        jsonGenerator.writeStringField(DatabaseDatasetImplementationKeyValueJsonComponent.Field.KEY_KEY.value(), datasetImplementationKeyValue.getKey());
                        jsonGenerator.writeStringField(DatabaseDatasetImplementationKeyValueJsonComponent.Field.VALUE_KEY.value(), datasetImplementationKeyValue.getValue());
                        jsonGenerator.writeEndObject();
                    }
                    jsonGenerator.writeEndArray();
                } else {
                    // TODO
                }
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        }
    }
}
