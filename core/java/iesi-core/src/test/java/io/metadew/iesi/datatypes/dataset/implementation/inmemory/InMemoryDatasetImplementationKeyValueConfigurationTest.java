package io.metadew.iesi.datatypes.dataset.implementation.database;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.UUID;

import static io.metadew.iesi.datatypes.dataset.DatasetBuilder.generateDataset;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TestConfiguration.class, DatasetConfiguration.class, DatabaseDatasetImplementationKeyValueConfiguration.class, DatasetImplementationConfiguration.class }, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class DatasetImplementationKeyValueConfigurationTest {

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private DatasetConfiguration datasetConfiguration;

    @Autowired
    private DatabaseDatasetImplementationKeyValueConfiguration databaseDatasetImplementationKeyValueConfiguration;

    @Test
    void testExists() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .exists(new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap.get("datasetImplementation0KeyValue0UUID"))))
                .isTrue();
    }

    @Test
    void testExistsNotExisting() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .exists(new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID())))
                .isFalse();
    }


    @Test
    void testGetById() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .get(new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap.get("datasetImplementation0KeyValue0UUID"))))
                .hasValue((DatabaseDatasetImplementationKeyValue) datasetMap.get("datasetImplementation0KeyValue0"));
    }


    @Test
    void testGetByIdDoesNotExist() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 0);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration.get(new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID())))
                .isEmpty();
    }

    @Test
    void testGetAll() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        datasetConfiguration.insert((Dataset) datasetMap2.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
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
        datasetConfiguration.insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        datasetConfiguration.insert((Dataset) datasetMap2.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID"))))
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"));
    }

    @Test
    void testGetByDatasetImplementationIdAndKey() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        datasetConfiguration.insert((Dataset) datasetMap2.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")), "key200"))
                .hasValue((DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"));
    }

    @Test
    void testUpdate() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        datasetConfiguration.insert((Dataset) datasetMap2.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        databaseDatasetImplementationKeyValueConfiguration
                .update(new DatabaseDatasetImplementationKeyValue(
                        new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap2.get("datasetImplementation0KeyValue0UUID")),
                        new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")),
                        "key",
                        "value")
                );
        assertThat(databaseDatasetImplementationKeyValueConfiguration
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
        datasetConfiguration.insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        datasetConfiguration.insert((Dataset) datasetMap2.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")), "key202"))
                .isEmpty();
    }

    @Test
    void testGetAllNoDatasets() {
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getAll())
                .isEmpty();
    }

    @Test
    void testDeleteDatasets() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        datasetConfiguration.insert((Dataset) datasetMap2.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        databaseDatasetImplementationKeyValueConfiguration
                .delete(new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap2.get("datasetImplementation0KeyValue0UUID")));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        databaseDatasetImplementationKeyValueConfiguration
                .delete(new DatabaseDatasetImplementationKeyValueKey((UUID) datasetMap1.get("datasetImplementation0KeyValue0UUID")));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testDeleteByDatasetImplementationId() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        datasetConfiguration.insert((Dataset) datasetMap2.get("dataset"));

        databaseDatasetImplementationKeyValueConfiguration
                .deleteByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap2.get("datasetImplementation0UUID")));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
    }

    @Test
    void testInsertDatasets() {
        Map<String, Object> datasetMap1 = generateDataset(1, 2, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap1.get("dataset"));
        Map<String, Object> datasetMap2 = generateDataset(2, 1, 1, 2);
        datasetConfiguration.insert((Dataset) datasetMap2.get("dataset"));
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getAll())
                .containsOnly(
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap2.get("datasetImplementation0KeyValue1"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation0KeyValue0"),
                        (DatabaseDatasetImplementationKeyValue) datasetMap1.get("datasetImplementation1KeyValue0"));
        DatabaseDatasetImplementationKeyValueKey datasetImplementationKeyValueKey = new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID());
        databaseDatasetImplementationKeyValueConfiguration
                .insert(new DatabaseDatasetImplementationKeyValue(
                        datasetImplementationKeyValueKey,
                        new DatasetImplementationKey((UUID) datasetMap1.get("datasetImplementation0UUID")),
                        "key",
                        "value")
                );
        assertThat(databaseDatasetImplementationKeyValueConfiguration
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
