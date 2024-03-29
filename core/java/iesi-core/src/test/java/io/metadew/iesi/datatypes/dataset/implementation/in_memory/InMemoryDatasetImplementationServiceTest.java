package io.metadew.iesi.datatypes.dataset.implementation.in_memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = DataTypeHandler.class )
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext
@ActiveProfiles("test")
class InMemoryDatasetImplementationServiceTest {

    @SpyBean
    DataTypeHandler dataTypeHandlerSpy;

    @Test
    void testGetDatasetItem() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        doReturn(new Text("value1"))
                .when(dataTypeHandlerSpy)
                .resolve("value1", executionRuntime);

        UUID datasetImplemenationUuid = UUID.randomUUID();
        InMemoryDatasetImplementation inMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                new DatasetImplementationKey(datasetImplemenationUuid),
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                Stream.of(new DatasetImplementationLabel(
                                new DatasetImplementationLabelKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "label1"
                        )
                ).collect(Collectors.toSet()),
                Stream.of(
                        new InMemoryDatasetImplementationKeyValue(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "key1",
                                new Text("value1")
                        )
                ).collect(Collectors.toSet())
        );
        assertThat(InMemoryDatasetImplementationService.getInstance().getDataItem(inMemoryDatasetImplementation, "key1", executionRuntime))
                .hasValue(new Text("value1"));
    }

     @Test
    void testGetDatasetItems() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        doReturn(new Text("value1"))
                .when(dataTypeHandlerSpy)
                .resolve("value1", executionRuntime);
        doReturn(new Text("value2"))
                .when(dataTypeHandlerSpy)
                .resolve("value2", executionRuntime);

        UUID datasetImplemenationUuid = UUID.randomUUID();
        InMemoryDatasetImplementation inMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                new DatasetImplementationKey(datasetImplemenationUuid),
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                Stream.of(new DatasetImplementationLabel(
                                new DatasetImplementationLabelKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "label1"
                        )
                ).collect(Collectors.toSet()),
                Stream.of(
                        new InMemoryDatasetImplementationKeyValue(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "key1",
                                new Text("value1")
                        ),
                        new InMemoryDatasetImplementationKeyValue(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "key2",
                                new Text("value2"))
                ).collect(Collectors.toSet())
        );
        assertThat(InMemoryDatasetImplementationService.getInstance().getDataItems(inMemoryDatasetImplementation, executionRuntime))
                .isEqualTo(
                        Stream.of(
                                new AbstractMap.SimpleEntry<String, DataType>("key1", new Text("value1")),
                                new AbstractMap.SimpleEntry<String, DataType>("key2", new Text("value2"))
                        ).
                                collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                        AbstractMap.SimpleEntry::getValue))
                );
    }


    @Test
    void testGetDatasetItemsEmpty() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        UUID datasetImplemenationUuid = UUID.randomUUID();
        InMemoryDatasetImplementation inMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                new DatasetImplementationKey(datasetImplemenationUuid),
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                new HashSet<>(),
                new HashSet<>()
        );

        assertThat(InMemoryDatasetImplementationService.getInstance()
                .getDataItems(inMemoryDatasetImplementation, executionRuntime))
                .isEmpty();
    }

    @Test
    void testGetDatasetItemNoMatch() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        UUID datasetImplemenationUuid = UUID.randomUUID();
        InMemoryDatasetImplementation inMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                new DatasetImplementationKey(datasetImplemenationUuid),
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                Stream.of(new DatasetImplementationLabel(
                                new DatasetImplementationLabelKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "label1"
                        )
                ).collect(Collectors.toSet()),
                Stream.of(
                        new InMemoryDatasetImplementationKeyValue(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "key1",
                                new Text("value1")
                        )
                ).collect(Collectors.toSet())
        );
        assertThat(InMemoryDatasetImplementationService.getInstance().getDataItem(inMemoryDatasetImplementation, "key100", executionRuntime))
                .isEmpty();
    }

    @Test
    void testSetDataItemNewKey() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        doReturn(new Text("value1"))
                .when(dataTypeHandlerSpy)
                .resolve("value1", executionRuntime);

        doReturn(new Text("value"))
                .when(dataTypeHandlerSpy)
                .resolve("value", executionRuntime);

        UUID datasetImplemenationUuid = UUID.randomUUID();
        InMemoryDatasetImplementation inMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                new DatasetImplementationKey(datasetImplemenationUuid),
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                Stream.of(new DatasetImplementationLabel(
                                new DatasetImplementationLabelKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "label1"
                        )
                ).collect(Collectors.toSet()),
                Stream.of(
                        new InMemoryDatasetImplementationKeyValue(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "key1",
                                new Text("value1")
                        )
                ).collect(Collectors.toSet())
        );

        InMemoryDatasetImplementationService.getInstance().setDataItem(inMemoryDatasetImplementation, "key2", new Text("value"));

        assertThat(InMemoryDatasetImplementationService.getInstance().getDataItem(inMemoryDatasetImplementation, "key2", executionRuntime))
                .hasValue(new Text("value"));
    }

    @Test
    void testSetDataItemExistingKey() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        doReturn(new Text("value1"))
                .when(dataTypeHandlerSpy)
                .resolve("value1", executionRuntime);

        doReturn(new Text("value"))
                .when(dataTypeHandlerSpy)
                .resolve("value", executionRuntime);

        UUID datasetImplemenationUuid = UUID.randomUUID();
        InMemoryDatasetImplementation inMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                new DatasetImplementationKey(datasetImplemenationUuid),
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                Stream.of(new DatasetImplementationLabel(
                                new DatasetImplementationLabelKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "label1"
                        )
                ).collect(Collectors.toSet()),
                Stream.of(
                        new InMemoryDatasetImplementationKeyValue(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "key1",
                                new Text("value1")
                        )
                ).collect(Collectors.toSet())
        );

        InMemoryDatasetImplementationService.getInstance().setDataItem(inMemoryDatasetImplementation, "key1", new Text("value"));

        assertThat(InMemoryDatasetImplementationService.getInstance().getDataItem(inMemoryDatasetImplementation, "key1", executionRuntime))
                .hasValue(new Text("value"));
    }

    @Test
    void testResolve() throws JsonProcessingException {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());
        InMemoryDatasetImplementation datasetImplementation = InMemoryDatasetImplementation.builder()
                .metadataKey(datasetImplementationKey)
                .datasetKey(new DatasetKey(UUID.randomUUID()))
                .name("dataset")
                .datasetImplementationLabels(Stream.of(
                        new DatasetImplementationLabel(
                                new DatasetImplementationLabelKey(UUID.randomUUID()),
                                datasetImplementationKey,
                                "label1"
                        )).collect(Collectors.toSet()))
                .keyValues(new HashSet<>())
                .build();

        ObjectNode jsonNode = (ObjectNode) new ObjectMapper().readTree("{\"key1\":\"value1\",\"key2\":\"value2\"}");

        DataType dataType = InMemoryDatasetImplementationService.getInstance()
                .resolve(
                        datasetImplementation,
                        "key",
                        jsonNode,
                        executionRuntime
                );
        assertThat(dataType).isInstanceOf(InMemoryDatasetImplementation.class);
        assertThat(((InMemoryDatasetImplementation)dataType).getKeyValues())
                .hasSize(2)
                .usingElementComparatorOnFields("key", "value")
                .containsOnly(
                        new InMemoryDatasetImplementationKeyValue(
                                new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                datasetImplementationKey,
                                "key1",
                                new Text("value1")
                        ),
                        new InMemoryDatasetImplementationKeyValue(
                                new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                datasetImplementationKey,
                                "key2",
                                new Text("value2")
                        ));
    }

}
