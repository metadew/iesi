package io.metadew.iesi.datatypes.dataset;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationJsonComponent;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.*;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelJsonComponent;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.definition.Metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatasetJsonComponent {

    public enum Field {
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
            List<DatasetImplementation> datasetImplementations = new ArrayList<>();
            for (JsonNode implementationNode : node.get(Field.IMPLEMENTATIONS_KEY.value())) {
                List<DatasetImplementationLabel> datasetImplementationLabels = new ArrayList<>();
                DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
                for (JsonNode labelNode : implementationNode.get(DatasetImplementationJsonComponent.Field.LABELS_KEY.value())) {
                    datasetImplementationLabels.add(DatasetImplementationLabel.builder()
                            .metadataKey(new DatasetImplementationLabelKey())
                            .datasetImplementationKey(datasetImplementationKey)
                            .value(labelNode.get(DatasetImplementationLabelJsonComponent.Field.LABEL_KEY.value()).asText())
                            .build());
                }

                String type = implementationNode.get(DatasetImplementationJsonComponent.Field.TYPE_KEY.value()).asText();
                if (type.equalsIgnoreCase(InMemoryDatasetImplementationJsonComponent.Field.TYPE.value())) {
                    List<InMemoryDatasetImplementationKeyValue> keyValues = new ArrayList<>();
                    for (JsonNode keyValueNode : implementationNode.get(InMemoryDatasetImplementationJsonComponent.Field.KEY_VALUES_KEY.value())) {
                        keyValues.add(InMemoryDatasetImplementationKeyValue.builder()
                                .metadataKey(new InMemoryDatasetImplementationKeyValueKey())
                                .datasetImplementationKey(datasetImplementationKey)
                                .key(keyValueNode.get(InMemoryDatasetImplementationKeyValueJsonComponent.Field.KEY_KEY.value()).asText())
                                .value(keyValueNode.get(InMemoryDatasetImplementationKeyValueJsonComponent.Field.VALUE_KEY.value()).asText())
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
}
