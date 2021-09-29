package io.metadew.iesi.datatypes.dataset.implementation.database;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.metadew.iesi.datatypes.dataset.DatasetBuilder.generateDataset;
import static org.assertj.core.api.Assertions.assertThat;

class DatasetImplementationKeyValueConfigurationTest {

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
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .exists(new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap.get("datasetImplementation0KeyValue0UUID"))))
                .isTrue();
    }

    @Test
    void testExistsNotExisting() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .exists(new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID())))
                .isFalse();
    }


    @Test
    void testGetById() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .get(new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap.get("datasetImplementation0KeyValue0UUID"))))
                .hasValue((DatabaseDatasetImplementationKeyValue) datasetMap.get("datasetImplementation0KeyValue0"));
    }


    @Test
    void testGetByIdDoesNotExist() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 0);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance().get(new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID())))
                .isEmpty();
    }

    @Test
    void testGetAll() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testGetByDatasetImplementationId() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID"))))
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"));
    }

    @Test
    void testGetByDatasetImplementationIdAndKey() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")), "key200"))
                .hasValue((DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"));
    }

    @Test
    void testUpdate() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .update(new DatabaseDatasetImplementationKeyValue(
                        new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap2.get("datasetImplementation0KeyValue0UUID")),
                        new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")),
                        "key",
                        "value")
                );
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(new DatabaseDatasetImplementationKeyValue(
                                new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap2.get("datasetImplementation0KeyValue0UUID")),
                                new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")),
                                "key",
                                "value"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testGetByDatasetImplementationIdAndKeyNoMatch() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")), "key202"))
                .isEmpty();
    }

    @Test
    void testGetAllNoDatasets() {
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .isEmpty();
    }

    @Test
    void testDeleteDatasets() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .delete(new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap2.get("datasetImplementation0KeyValue0UUID")));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .delete(new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap1.get("datasetImplementation0KeyValue0UUID")));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testDeleteByDatasetImplementationId() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));

        DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .deleteByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testInsertDatasets() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        DatabaseDatasetImplementationKeyValueKey datasetImplementationKeyValueKey = new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID());
        DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .insert(new DatabaseDatasetImplementationKeyValue(
                        datasetImplementationKeyValueKey,
                        new DatasetImplementationKey((UUID) datasetMap1.get("datasetImplementation0UUID")),
                        "key",
                        "value")
                );
        assertThat(DatabaseDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        new DatabaseDatasetImplementationKeyValue(
                                datasetImplementationKeyValueKey,
                                new DatasetImplementationKey((UUID) datasetMap1.get("datasetImplementation0UUID")),
                                "key",
                                "value"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

}
