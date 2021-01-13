package io.metadew.iesi.datatypes.dataset.implementation.inmemory;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
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

class InMemoryDatasetImplementationServiceTest {

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
    void testSetDataItemNewKey() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));

        InMemoryDatasetImplementationService.getInstance()
                .setDataItem((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation0"),
                        "key",
                        new Text("value")
                );

        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID"))))
                .hasSize(2);
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID")), "key000"))
                .isPresent()
                .map(InMemoryDatasetImplementationKeyValue::getValue)
                .get()
                .isEqualTo("value000");
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID")), "key"))
                .isPresent()
                .map(InMemoryDatasetImplementationKeyValue::getValue)
                .get()
                .isEqualTo("value");
    }

    @Test
    void testSetDataItemExistingKey() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));

        InMemoryDatasetImplementationService.getInstance()
                .setDataItem((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation0"),
                        "key000",
                        new Text("value001")
                );

        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationId(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID"))))
                .hasSize(1);
        assertThat(InMemoryDatasetImplementationKeyValueConfiguration.getInstance()
                .getByDatasetImplementationIdAndKey(new DatasetImplementationKey((UUID) datasetMap.get("datasetImplementation0UUID")), "key000"))
                .isPresent()
                .map(InMemoryDatasetImplementationKeyValue::getValue)
                .get()
                .isEqualTo("value001");
    }


}
