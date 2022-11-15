package io.metadew.iesi.datatypes.dataset.implementation.database;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.metadew.iesi.datatypes.dataset.DatasetBuilder.generateDataset;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, DatasetConfiguration.class, DatabaseDatasetImplementationKeyValueConfiguration.class, DatasetImplementationConfiguration.class })
@ActiveProfiles("test")
class DatasetImplementationKeyValueConfigurationTest {

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    DatasetConfiguration datasetConfiguration;

    @Autowired
    DatabaseDatasetImplementationKeyValueConfiguration databaseDatasetImplementationKeyValueConfiguration;

    @BeforeEach
    void beforeEach() {
        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void afterEach() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void testExists() {

        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());
        DatabaseDatasetImplementationKeyValueKey databaseDatasetImplementationKeyValueKey = new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID());

        DatasetImplementation datasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                new HashSet<>(),
                Stream.of(new DatabaseDatasetImplementationKeyValue(
                        databaseDatasetImplementationKeyValueKey,
                        datasetImplementationKey,
                        "key",
                        "value"
                )).collect(Collectors.toSet()));

        Set<DatasetImplementation> datasetImplementations = Stream.of(datasetImplementation).collect(Collectors.toSet());

        datasetConfiguration.insert(new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                datasetImplementations
        ));

        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .exists(databaseDatasetImplementationKeyValueKey))
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

    @Test
    void testSetDataItemExistingKey() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));

        DatabaseDatasetImplementationService.getInstance()
                .setDataItem((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"),
                        "key000",
                        new Text("value001")
                );

        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID"))))
                .hasSize(1);
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID")), "key000"))
                .isPresent()
                .map(DatabaseDatasetImplementationKeyValue::getValue)
                .get()
                .isEqualTo("value001");
    }

    @Test
    void testSetDataItemNewKey() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));

        DatasetImplementationHandler.getInstance()
                .setDataItem((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"),
                        "key",
                        new Text("value"));

        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID"))))
                .hasSize(2);
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID")), "key000"))
                .isPresent()
                .map(DatabaseDatasetImplementationKeyValue::getValue)
                .get()
                .isEqualTo("value000");
        assertThat(databaseDatasetImplementationKeyValueConfiguration
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID")), "key"))
                .isPresent()
                .map(DatabaseDatasetImplementationKeyValue::getValue)
                .get()
                .isEqualTo("value");
    }

}
