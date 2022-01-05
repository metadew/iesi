package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatasetBuilder {

    public static Map<String, Object> generateDataset(int datasetIndex, int implementationCount, int labelCount, int keyValueCount) {
        Map<String, Object> info = new HashMap<>();

        UUID datasetUUID = UUID.randomUUID();
        info.put("datasetUUID", datasetUUID);
        Dataset dataset = Dataset.builder()
                .metadataKey(new DatasetKey(datasetUUID))
                .name(String.format("dataset%d", datasetIndex))
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName("PUBLIC")
                .datasetImplementations(
                        IntStream.range(0, implementationCount).boxed()
                                .map(implementationIndex -> {
                                    UUID datasetImplementationUUID = UUID.randomUUID();
                                    info.put(String.format("datasetImplementation%dUUID", implementationIndex), datasetImplementationUUID);
                                    DatabaseDatasetImplementation databaseDatasetImplementation = DatabaseDatasetImplementation.builder()
                                            .metadataKey(new DatasetImplementationKey(datasetImplementationUUID))
                                            .datasetKey(new DatasetKey(datasetUUID))
                                            .name(String.format("dataset%d", datasetIndex))
                                            .datasetImplementationLabels(
                                                    IntStream.range(0, labelCount).boxed()
                                                            .map(labelIndex -> {
                                                                        UUID datasetImplementationLabelUUID = UUID.randomUUID();
                                                                        info.put(String.format("datasetImplementation%dLabel%dUUID", implementationIndex, labelIndex), datasetImplementationLabelUUID);
                                                                        DatasetImplementationLabel datasetImplementationLabel = DatasetImplementationLabel.builder()
                                                                                .metadataKey(new DatasetImplementationLabelKey(datasetImplementationLabelUUID))
                                                                                .datasetImplementationKey(new DatasetImplementationKey(datasetImplementationUUID))
                                                                                .value(String.format("label%d%d%d", datasetIndex, implementationIndex, labelIndex))
                                                                                .build();
                                                                        info.put(String.format("datasetImplementation%dLabel%d", implementationIndex, labelIndex), datasetImplementationLabel);
                                                                        return datasetImplementationLabel;
                                                                    }
                                                            ).collect(Collectors.toSet()))
                                            .keyValues(
                                                    IntStream.range(0, keyValueCount).boxed()
                                                            .map(keyValueIndex -> {
                                                                UUID datasetImplementationKeyValueUUID = UUID.randomUUID();
                                                                info.put(String.format("datasetImplementation%dKeyValue%dUUID", implementationIndex, keyValueIndex), datasetImplementationKeyValueUUID);
                                                                DatabaseDatasetImplementationKeyValue datasetImplementationKeyValue = DatabaseDatasetImplementationKeyValue.builder()
                                                                        .metadataKey(new DatabaseDatasetImplementationKeyValueKey(datasetImplementationKeyValueUUID))
                                                                        .datasetImplementationKey(new DatasetImplementationKey(datasetImplementationUUID))
                                                                        .key(String.format("key%d%d%d", datasetIndex, implementationIndex, keyValueIndex))
                                                                        .value(String.format("value%d%d%d", datasetIndex, implementationIndex, keyValueIndex))
                                                                        .build();
                                                                info.put(String.format("datasetImplementation%dKeyValue%d", implementationIndex, keyValueIndex), datasetImplementationKeyValue);
                                                                return datasetImplementationKeyValue;
                                                            }).collect(Collectors.toSet())
                                            )
                                            .build();
                                    info.put(String.format("datasetImplementation%d", implementationIndex), databaseDatasetImplementation);
                                    return databaseDatasetImplementation;
                                })
                                .collect(Collectors.toSet()))
                .build();
        info.put("dataset", dataset);
        return info;
    }
}
