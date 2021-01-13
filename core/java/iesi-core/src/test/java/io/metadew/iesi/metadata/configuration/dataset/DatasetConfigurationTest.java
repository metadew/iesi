package io.metadew.iesi.metadata.configuration.dataset;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class DatasetConfigurationTest {

    @BeforeAll
    static void prepare() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getDataMetadataRepository()
                .createAllTables();
    }

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getDataMetadataRepository().cleanAllTables();
    }

    @AfterAll
    static void teardown() {
        MetadataRepositoryConfiguration.getInstance()
                .getDataMetadataRepository().dropAllTables();
    }

    @Test
    void testExists() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .exists(new DatasetKey((UUID) datasetMap.get("datasetUUID"))))
                .isTrue();
    }

    @Test
    void testExistsNotExisting() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .exists(new DatasetKey(UUID.randomUUID())))
                .isFalse();
    }

    @Test
    void testExistsByName() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .existsByName("dataset0"))
                .isTrue();
    }

    @Test
    void testExistsByNameNotExisting() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .existsByName("dataset1"))
                .isFalse();
    }

    @Test
    void testGetById() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .get(new DatasetKey((UUID) datasetMap.get("datasetUUID"))))
                .hasValue((Dataset) datasetMap.get("dataset"));
    }

    @Test
    void testGetByIdNoImplementations() {
        Map<String, Object> datasetMap = generateDataset(0, 0, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance().get(new DatasetKey((UUID) datasetMap.get("datasetUUID"))))
                .hasValue((Dataset) datasetMap.get("dataset"));
    }

    @Test
    void testGetByIdNoLabels() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 0, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance().get(new DatasetKey((UUID) datasetMap.get("datasetUUID"))))
                .hasValue((Dataset) datasetMap.get("dataset"));
    }

    @Test
    void testGetByIdNoKeyValues() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 0);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance().get(new DatasetKey((UUID) datasetMap.get("datasetUUID"))))
                .hasValue((Dataset) datasetMap.get("dataset"));
    }

    @Test
    void testGetByIdDoesNotExist() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 0);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance().get(new DatasetKey(UUID.randomUUID())))
                .isEmpty();
    }


    @Test
    void testGetByName() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .get(new DatasetKey((UUID) datasetMap.get("datasetUUID"))))
                .hasValue((Dataset) datasetMap.get("dataset"));
    }

    @Test
    void testGetByNameNoImplementations() {
        Map<String, Object> datasetMap = generateDataset(0, 0, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance().getByName("dataset0"))
                .hasValue((Dataset) datasetMap.get("dataset"));
    }

    @Test
    void testGetByNameNoLabels() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 0, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance().getByName("dataset0"))
                .hasValue((Dataset) datasetMap.get("dataset"));
    }

    @Test
    void testGetByNameNoKeyValues() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 0);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance().getByName("dataset0"))
                .hasValue((Dataset) datasetMap.get("dataset"));
    }

    @Test
    void testGetByNameDoesNotExist() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 0);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance().getByName("dataset1"))
                .isEmpty();
    }

    @Test
    void testGetIdByName() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .getIdByName("dataset0"))
                .hasValue(new DatasetKey((UUID) datasetMap.get("datasetUUID")));
    }

    @Test
    void testGetIdByNameNotExisting() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .getIdByName("dataset1"))
                .isEmpty();
    }

    @Test
    void testGetAll() {
        Map<String, Object> datasetMap1 = generateDataset(1, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 0, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        Map<String, Object> datasetMap3 = generateDataset(3, 1, 0, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap3.get("dataset"));
        Map<String, Object> datasetMap4 = generateDataset(4, 1, 1, 0);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap4.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .getAll())
                .containsOnly((Dataset) datasetMap4.get("dataset"),
                        (Dataset) datasetMap3.get("dataset"),
                        (Dataset) datasetMap2.get("dataset"),
                        (Dataset) datasetMap1.get("dataset"));
    }

    @Test
    void testGetAllNoDatasets() {
        assertThat(DatasetConfiguration.getInstance()
                .getAll())
                .isEmpty();
    }

    @Test
    void testDeleteDatasets() {
        Map<String, Object> datasetMap1 = generateDataset(1, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        assertThat(DatasetConfiguration.getInstance()
                .exists(new DatasetKey((UUID) datasetMap1.get("datasetUUID"))))
                .isTrue();
        DatasetConfiguration.getInstance().delete(new DatasetKey((UUID) datasetMap1.get("datasetUUID")));
        assertThat(DatasetConfiguration.getInstance()
                .exists(new DatasetKey((UUID) datasetMap1.get("datasetUUID"))))
                .isFalse();
    }

    private Map<String, Object> generateDataset(int datasetIndex, int implementationCount, int labelCount, int keyValueCount) {
        Map<String, Object> info = new HashMap<>();

        UUID datasetUUID = UUID.randomUUID();
        info.put("datasetUUID", datasetUUID);
        Dataset dataset = Dataset.builder()
                .metadataKey(new DatasetKey(datasetUUID))
                .name(String.format("dataset%d", datasetIndex))
                .datasetImplementations(
                        IntStream.range(0, implementationCount).boxed()
                                .map(implementationIndex -> {
                                    UUID datasetImplementationUUID = UUID.randomUUID();
                                    info.put(String.format("datasetImplementation%dUUID", implementationIndex), datasetImplementationUUID);
                                    return InMemoryDatasetImplementation.builder()
                                            .metadataKey(new DatasetImplementationKey(datasetImplementationUUID))
                                            .datasetKey(new DatasetKey(datasetUUID))
                                            .name(String.format("dataset%d", datasetIndex))
                                            .datasetImplementationLabels(
                                                    IntStream.range(0, labelCount).boxed()
                                                            .map(labelIndex -> {
                                                                        UUID datasetImplementationLabelUUID = UUID.randomUUID();
                                                                        info.put(String.format("datasetImplementation%dLabel%dUUID", implementationIndex, labelIndex), datasetImplementationLabelUUID);
                                                                        return DatasetImplementationLabel.builder()
                                                                                .metadataKey(new DatasetImplementationLabelKey(datasetImplementationLabelUUID))
                                                                                .datasetImplementationKey(new DatasetImplementationKey(datasetImplementationUUID))
                                                                                .value(String.format("label%d%d%d", datasetIndex, implementationIndex, labelIndex))
                                                                                .build();
                                                                    }
                                                            ).collect(Collectors.toSet()))
                                            .keyValues(
                                                    IntStream.range(0, keyValueCount).boxed()
                                                            .map(keyValueIndex -> {
                                                                UUID datasetImplementationKeyValueUUID = UUID.randomUUID();
                                                                info.put(String.format("datasetImplementation%dKeyValue%dUUID", implementationIndex, keyValueIndex), datasetImplementationKeyValueUUID);

                                                                return InMemoryDatasetImplementationKeyValue.builder()
                                                                        .metadataKey(new InMemoryDatasetImplementationKeyValueKey(datasetImplementationKeyValueUUID))
                                                                        .datasetImplementationKey(new DatasetImplementationKey(datasetImplementationUUID))
                                                                        .key(String.format("key%d%d%d", datasetIndex, implementationIndex, keyValueIndex))
                                                                        .value(String.format("value%d%d%d", datasetIndex, implementationIndex, keyValueIndex))
                                                                        .build();
                                                            }).collect(Collectors.toSet())
                                            )
                                            .build();
                                })
                                .collect(Collectors.toSet()))
                .build();
        info.put("dataset", dataset);
        return info;
    }
}
