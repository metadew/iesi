package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDatasetService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.reflect.Whitebox;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DatasetServiceTest {

    private static DatasetHandler datasetHandlerSpy;

    @BeforeAll
    static void setup() {
        DatasetHandler datasetHandler = DatasetHandler.getInstance();
        datasetHandlerSpy = Mockito.spy(datasetHandler);
        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", datasetHandlerSpy);
    }

    @AfterAll
    static void destroy() {
        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", (DatasetHandler) null);
    }

    @Test
    void equalsEqualsTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        KeyValueDataset dataset1 = mock(KeyValueDataset.class);
        KeyValueDataset dataset2 = mock(KeyValueDataset.class);
        Mockito
                .doReturn(Stream.of(
                        new AbstractMap.SimpleEntry<String, DataType>("key1", new Text("value1")),
                        new AbstractMap.SimpleEntry<String, DataType>("key2", new Text("value2")),
                        new AbstractMap.SimpleEntry<String, DataType>("key3", new Array(Stream.of(new Text("value3")).collect(Collectors.toList())))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .when(datasetHandlerSpy).getDataItems(dataset1, executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value1")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key1", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value2")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key2", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Array(Stream.of(new Text("value3")).collect(Collectors.toList()))))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key3", executionRuntime);
        Mockito
                .doReturn(Stream.of(
                        new AbstractMap.SimpleEntry<String, DataType>("key1", new Text("value1")),
                        new AbstractMap.SimpleEntry<String, DataType>("key2", new Text("value2")),
                        new AbstractMap.SimpleEntry<String, DataType>("key3", new Array(Stream.of(new Text("value3")).collect(Collectors.toList())))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .when(datasetHandlerSpy).getDataItems(dataset2, executionRuntime);

        Mockito
                .doReturn(Optional.of(new Text("value1")))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key1", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value2")))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key2", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Array(Stream.of(new Text("value3")).collect(Collectors.toList()))))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key3", executionRuntime);

        assertThat(KeyValueDatasetService.getInstance().equals(dataset1, dataset2, executionRuntime))
                .isTrue();
    }

    @Test
    void equalsNotEqualsValueTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        KeyValueDataset dataset1 = mock(KeyValueDataset.class);
        KeyValueDataset dataset2 = mock(KeyValueDataset.class);
        Mockito
                .doReturn(Stream.of(
                        new AbstractMap.SimpleEntry<String, DataType>("key1", new Text("value1")),
                        new AbstractMap.SimpleEntry<String, DataType>("key2", new Text("value2")),
                        new AbstractMap.SimpleEntry<String, DataType>("key3", new Array(Stream.of(new Text("value3")).collect(Collectors.toList())))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .when(datasetHandlerSpy).getDataItems(dataset1, executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value1")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key1", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value2")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key2", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Array(Stream.of(new Text("value3")).collect(Collectors.toList()))))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key3", executionRuntime);
        Mockito
                .doReturn(Stream.of(
                        new AbstractMap.SimpleEntry<String, DataType>("key1", new Text("value1")),
                        new AbstractMap.SimpleEntry<String, DataType>("key2", new Text("value3")),
                        new AbstractMap.SimpleEntry<String, DataType>("key3", new Array(Stream.of(new Text("value3")).collect(Collectors.toList())))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .when(datasetHandlerSpy).getDataItems(dataset2, executionRuntime);

        Mockito
                .doReturn(Optional.of(new Text("value1")))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key1", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value3")))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key2", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Array(Stream.of(new Text("value3")).collect(Collectors.toList()))))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key3", executionRuntime);

        assertThat(KeyValueDatasetService.getInstance().equals(dataset1, dataset2, executionRuntime))
                .isFalse();
    }


    @Test
    void equalsNotEqualsTooFewKeysTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        KeyValueDataset dataset1 = mock(KeyValueDataset.class);
        KeyValueDataset dataset2 = mock(KeyValueDataset.class);
        Mockito
                .doReturn(Stream.of(
                        new AbstractMap.SimpleEntry<String, DataType>("key1", new Text("value1")),
                        new AbstractMap.SimpleEntry<String, DataType>("key2", new Text("value2")),
                        new AbstractMap.SimpleEntry<String, DataType>("key3", new Array(Stream.of(new Text("value3")).collect(Collectors.toList())))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .when(datasetHandlerSpy).getDataItems(dataset1, executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value1")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key1", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value2")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key2", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Array(Stream.of(new Text("value3")).collect(Collectors.toList()))))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key3", executionRuntime);
        Mockito
                .doReturn(Stream.of(
                        new AbstractMap.SimpleEntry<String, DataType>("key1", new Text("value1")),
                        new AbstractMap.SimpleEntry<String, DataType>("key3", new Array(Stream.of(new Text("value3")).collect(Collectors.toList())))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .when(datasetHandlerSpy).getDataItems(dataset2, executionRuntime);

        Mockito
                .doReturn(Optional.of(new Text("value1")))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key1", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Array(Stream.of(new Text("value3")).collect(Collectors.toList()))))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key3", executionRuntime);
        Mockito
                .doReturn(Optional.empty())
                .when(datasetHandlerSpy).getDataItem(dataset2, "key2", executionRuntime);

        assertThat(KeyValueDatasetService.getInstance().equals(dataset1, dataset2, executionRuntime))
                .isFalse();
    }


    @Test
    void equalsNotEqualsTooManyKeysTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        KeyValueDataset dataset1 = mock(KeyValueDataset.class);
        KeyValueDataset dataset2 = mock(KeyValueDataset.class);
        Mockito
                .doReturn(Stream.of(
                        new AbstractMap.SimpleEntry<String, DataType>("key1", new Text("value1")),
                        new AbstractMap.SimpleEntry<String, DataType>("key2", new Text("value2")),
                        new AbstractMap.SimpleEntry<String, DataType>("key3", new Array(Stream.of(new Text("value3")).collect(Collectors.toList())))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .when(datasetHandlerSpy).getDataItems(dataset1, executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value1")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key1", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value2")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key2", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Array(Stream.of(new Text("value3")).collect(Collectors.toList()))))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key3", executionRuntime);
        Mockito
                .doReturn(Stream.of(
                        new AbstractMap.SimpleEntry<String, DataType>("key1", new Text("value1")),
                        new AbstractMap.SimpleEntry<String, DataType>("key2", new Text("value2")),
                        new AbstractMap.SimpleEntry<String, DataType>("key4", new Text("value3")),
                        new AbstractMap.SimpleEntry<String, DataType>("key3", new Array(Stream.of(new Text("value3")).collect(Collectors.toList())))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .when(datasetHandlerSpy).getDataItems(dataset2, executionRuntime);

        Mockito
                .doReturn(Optional.of(new Text("value1")))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key1", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value2")))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key2", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value3")))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key4", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Array(Stream.of(new Text("value3")).collect(Collectors.toList()))))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key3", executionRuntime);

        assertThat(KeyValueDatasetService.getInstance().equals(dataset1, dataset2, executionRuntime))
                .isFalse();
    }


    @Test
    void equalsEqualsNullsTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        KeyValueDataset dataset1 = mock(KeyValueDataset.class);

        assertThat(KeyValueDatasetService.getInstance().equals(null, null, executionRuntime))
                .isTrue();
        assertThat(KeyValueDatasetService.getInstance().equals(null, dataset1, executionRuntime))
                .isFalse();
        assertThat(KeyValueDatasetService.getInstance().equals(dataset1, null, executionRuntime))
                .isFalse();
    }


}
