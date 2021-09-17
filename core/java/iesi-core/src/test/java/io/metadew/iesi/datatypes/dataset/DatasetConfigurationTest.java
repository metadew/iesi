package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.metadew.iesi.datatypes.dataset.DatasetBuilder.generateDataset;
import static org.assertj.core.api.Assertions.assertThat;

class DatasetConfigurationTest {

    @BeforeAll
    static void prepare() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
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

    @Test
    void updateDataset() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        Dataset dataset = (Dataset) datasetMap1.get("dataset");
        DatasetConfiguration.getInstance().insert(dataset);
        dataset.getDatasetImplementations().clear();
        UUID datasetImplementationUuid = UUID.randomUUID();
        dataset.getDatasetImplementations().add(
                new DatabaseDatasetImplementation(
                        new DatasetImplementationKey(datasetImplementationUuid),
                        dataset.getMetadataKey(),
                        dataset.getName(),
                        Stream.of(
                                DatasetImplementationLabel.builder()
                                        .metadataKey(new DatasetImplementationLabelKey(UUID.randomUUID()))
                                        .datasetImplementationKey(new DatasetImplementationKey(datasetImplementationUuid))
                                        .value("label1")
                                        .build()
                        ).collect(Collectors.toSet()),
                        Stream.of(
                                DatasetImplementationKeyValue.builder()
                                        .metadataKey(new DatasetImplementationKeyValueKey(UUID.randomUUID()))
                                        .datasetImplementationKey(new DatasetImplementationKey(datasetImplementationUuid))
                                        .key("key1")
                                        .value("Value1")
                                        .build()
                        ).collect(Collectors.toSet())

                )
        );
        DatasetConfiguration.getInstance().update(dataset);

        assertThat(DatasetConfiguration.getInstance().get(dataset.getMetadataKey()))
                .hasValue(dataset);

    }

}
