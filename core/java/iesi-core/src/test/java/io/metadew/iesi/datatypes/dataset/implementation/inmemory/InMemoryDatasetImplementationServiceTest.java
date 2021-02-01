package io.metadew.iesi.datatypes.dataset.implementation.inmemory;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.metadew.iesi.datatypes.dataset.DatasetBuilder.generateDataset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class InMemoryDatasetImplementationServiceTest {

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

    // public Optional<InMemoryDatasetImplementation> getDatasetImplementation(String name, List<String> labels) {

    @Test
    void testGetDatasetImplementationByDatasetIdAndLabels() {
        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationService.getInstance().getDatasetImplementation(new DatasetKey((UUID) datasetMap.get("datasetUUID")), Stream.of("label000", "label001").collect(Collectors.toList())))
                .hasValue((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation0"));
        assertThat(InMemoryDatasetImplementationService.getInstance().getDatasetImplementation(new DatasetKey((UUID) datasetMap.get("datasetUUID")), Stream.of("label010", "label011").collect(Collectors.toList())))
                .hasValue((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation1"));
    }

    @Test
    void testGetDatasetImplementationByDatasetIdAndLabelsNoMatch() {
        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationService.getInstance().getDatasetImplementation(new DatasetKey((UUID) datasetMap.get("datasetUUID")), Stream.of("label000").collect(Collectors.toList())))
                .isEmpty();
        assertThat(InMemoryDatasetImplementationService.getInstance().getDatasetImplementation(new DatasetKey(UUID.randomUUID()), Stream.of("label010", "label011").collect(Collectors.toList())))
                .isEmpty();
    }

    @Test
    void testGetDatasetImplementationByNameAndLabels() {
        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationService.getInstance().getDatasetImplementation("dataset0", Stream.of("label000", "label001").collect(Collectors.toList())))
                .hasValue((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation0"));
        assertThat(InMemoryDatasetImplementationService.getInstance().getDatasetImplementation("dataset0", Stream.of("label010", "label011").collect(Collectors.toList())))
                .hasValue((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation1"));
    }

    @Test
    void testGetDatasetImplementationByNameAndLabelsNoMatch() {
        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 1);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationService.getInstance().getDatasetImplementation("dataset0", Stream.of("label000").collect(Collectors.toList())))
                .isEmpty();
        assertThat(InMemoryDatasetImplementationService.getInstance().getDatasetImplementation("dataset1", Stream.of("label010", "label011").collect(Collectors.toList())))
                .isEmpty();
    }

    @Test
    void testGetDatasetItem() {
        DataTypeHandler dataTypeHandler = DataTypeHandler.getInstance();
        DataTypeHandler dataTypeHandlerSpy = Mockito.spy(dataTypeHandler);
        Whitebox.setInternalState(DataTypeHandler.class, "instance", dataTypeHandlerSpy);

        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        doReturn(new Text("value000"))
                .when(dataTypeHandlerSpy)
                .resolve("value000", executionRuntime);
        doReturn(new Text("value001"))
                .when(dataTypeHandlerSpy)
                .resolve("value001", executionRuntime);
        doReturn(new Text("value010"))
                .when(dataTypeHandlerSpy)
                .resolve("value010", executionRuntime);
        doReturn(new Text("value011"))
                .when(dataTypeHandlerSpy)
                .resolve("value011", executionRuntime);


        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItem((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation0"), "key000", executionRuntime))
                .hasValue(new Text("value000"));
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItem((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation0"), "key001", executionRuntime))
                .hasValue(new Text("value001"));
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItem((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation1"), "key010", executionRuntime))
                .hasValue(new Text("value010"));
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItem((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation1"), "key011", executionRuntime))
                .hasValue(new Text("value011"));

        Whitebox.setInternalState(DataTypeHandler.class, "instance", (DataTypeHandler) null);
    }

    @Test
    void testGetDatasetItems() {
        DataTypeHandler dataTypeHandler = DataTypeHandler.getInstance();
        DataTypeHandler dataTypeHandlerSpy = Mockito.spy(dataTypeHandler);
        Whitebox.setInternalState(DataTypeHandler.class, "instance", dataTypeHandlerSpy);

        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        doReturn(new Text("value000"))
                .when(dataTypeHandlerSpy)
                .resolve("value000", executionRuntime);
        doReturn(new Text("value001"))
                .when(dataTypeHandlerSpy)
                .resolve("value001", executionRuntime);
        doReturn(new Text("value010"))
                .when(dataTypeHandlerSpy)
                .resolve("value010", executionRuntime);
        doReturn(new Text("value011"))
                .when(dataTypeHandlerSpy)
                .resolve("value011", executionRuntime);


        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItems((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation0"),  executionRuntime))
                .isEqualTo(
                        Stream.of(
                                new AbstractMap.SimpleEntry<String, DataType>("key000", new Text("value000")),
                                new AbstractMap.SimpleEntry<String, DataType>("key001", new Text("value001"))
                        ).
                                collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                        AbstractMap.SimpleEntry::getValue))
                );
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItems((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation1"), executionRuntime))
                .isEqualTo(
                        Stream.of(
                                new AbstractMap.SimpleEntry<String, DataType>("key010", new Text("value010")),
                                new AbstractMap.SimpleEntry<String, DataType>("key011", new Text("value011"))
                        ).
                                collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                        AbstractMap.SimpleEntry::getValue))
                );

        Whitebox.setInternalState(DataTypeHandler.class, "instance", (DataTypeHandler) null);
    }

    @Test
    void testGetDatasetItemsEmpty() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 0);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItems((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation0"),  executionRuntime))
                .isEmpty();
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItems((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation1"), executionRuntime))
                .isEmpty();

    }

    @Test
    void testGetDatasetItemNoMatch() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 2);
        DatasetConfiguration.getInstance().insert((Dataset) datasetMap.get("dataset"));
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItem((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation0"), "key100", executionRuntime))
                .isEmpty();
        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItem((InMemoryDatasetImplementation) datasetMap.get("datasetImplementation1"), "key110", executionRuntime))
                .isEmpty();
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
