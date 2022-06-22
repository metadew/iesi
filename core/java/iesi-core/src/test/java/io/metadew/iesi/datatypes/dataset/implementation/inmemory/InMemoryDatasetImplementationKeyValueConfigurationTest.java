package io.metadew.iesi.datatypes.dataset.implementation.inmemory;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.metadew.iesi.datatypes.dataset.DatasetBuilder.generateDataset;
import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDatasetImplementationKeyValueConfigurationTest {

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
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .exists(new InMemoryDatasetImplementationKeyValueKey((UUID) datasetMap.get("datasetImplementation0KeyValue0UUID"))))
                .isTrue();
    }

    @Test
    void testExistsNotExisting() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .exists(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID())))
                .isFalse();
    }


    @Test
    void testGetById() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .get(new InMemoryDatasetImplementationKeyValueKey((UUID) datasetMap.get("datasetImplementation0KeyValue0UUID"))))
                .hasValue((InMemoryDatasetImplementationKeyValue) datasetMap.get("datasetImplementation0KeyValue0"));
    }


    @Test
    void testGetByIdDoesNotExist() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 0);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance().get(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID())))
                .isEmpty();
    }

    @Test
    void testGetAll() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testGetByDatasetImplementationId() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID"))))
                .containsOnly(
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"));
    }

    @Test
    void testGetByDatasetImplementationIdAndKey() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")), "key200"))
                .hasValue((InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"));
    }

    @Test
    void testUpdate() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .update(new InMemoryDatasetImplementationKeyValue(
                        new InMemoryDatasetImplementationKeyValueKey((UUID) datasetMap2.get("datasetImplementation0KeyValue0UUID")),
                        new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")),
                        "key",
                        "value")
                );
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(new InMemoryDatasetImplementationKeyValue(
                                new InMemoryDatasetImplementationKeyValueKey((UUID) datasetMap2.get("datasetImplementation0KeyValue0UUID")),
                                new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")),
                                "key",
                                "value"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testGetByDatasetImplementationIdAndKeyNoMatch() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")), "key202"))
                .isEmpty();
    }

    @Test
    void testGetAllNoDatasets() {
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .isEmpty();
    }

    @Test
    void testDeleteDatasets() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .delete(new InMemoryDatasetImplementationKeyValueKey((UUID) datasetMap2.get("datasetImplementation0KeyValue0UUID")));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .delete(new InMemoryDatasetImplementationKeyValueKey((UUID) datasetMap1.get("datasetImplementation0KeyValue0UUID")));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testDeleteByDatasetImplementationId() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));

        InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .deleteByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testInsertDatasets() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap2.get("dataset"));
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        InMemoryDatasetImplementationKeyValueKey inMemoryDatasetImplementationKeyValueKey = new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID());
        InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .insert(new InMemoryDatasetImplementationKeyValue(
                        inMemoryDatasetImplementationKeyValueKey,
                        new DatasetImplementationKey((UUID) datasetMap1.get("datasetImplementation0UUID")),
                        "key",
                        "value")
                );
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getAll())
                .containsOnly(
                        new InMemoryDatasetImplementationKeyValue(
                                inMemoryDatasetImplementationKeyValueKey,
                                new DatasetImplementationKey((UUID) datasetMap1.get("datasetImplementation0UUID")),
                                "key",
                                "value"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (InMemoryDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

}
