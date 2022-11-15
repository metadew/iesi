package io.metadew.iesi.datatypes.dataset.implementation.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
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
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.metadew.iesi.datatypes.dataset.DatasetBuilder.generateDataset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, DatasetConfiguration.class, DatasetImplementationConfiguration.class, DatabaseDatasetImplementationKeyValueConfiguration.class,
        DataTypeHandler.class })
@ActiveProfiles("test")
class DatabaseDatasetImplementationServiceTest {

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    DatasetConfiguration datasetConfiguration;

    @SpyBean
    DataTypeHandler dataTypeHandlerSpy;

    @BeforeEach
    void setUp() {
        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void testGetDatasetItem() {
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
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItem((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"), "key000", executionRuntime))
                .hasValue(new Text("value000"));
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItem((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"), "key001", executionRuntime))
                .hasValue(new Text("value001"));
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItem((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation1"), "key010", executionRuntime))
                .hasValue(new Text("value010"));
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItem((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation1"), "key011", executionRuntime))
                .hasValue(new Text("value011"));
    }

    @Test
    void testGetDatasetItems() {
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
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItems((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"), executionRuntime))
                .isEqualTo(
                        Stream.of(
                                new AbstractMap.SimpleEntry<String, DataType>("key000", new Text("value000")),
                                new AbstractMap.SimpleEntry<String, DataType>("key001", new Text("value001"))
                        ).
                                collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                        AbstractMap.SimpleEntry::getValue))
                );
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItems((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation1"), executionRuntime))
                .isEqualTo(
                        Stream.of(
                                new AbstractMap.SimpleEntry<String, DataType>("key010", new Text("value010")),
                                new AbstractMap.SimpleEntry<String, DataType>("key011", new Text("value011"))
                        ).
                                collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                        AbstractMap.SimpleEntry::getValue))
                );
    }

    @Test
    void testGetDatasetItemsEmpty() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 0);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItems((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"), executionRuntime))
                .isEmpty();
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItems((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation1"), executionRuntime))
                .isEmpty();

    }

    @Test
    void testGetDatasetItemNoMatch() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 2);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItem((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"), "key100", executionRuntime))
                .isEmpty();
        assertThat(DatabaseDatasetImplementationService.getInstance()
                .getDataItem((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation1"), "key110", executionRuntime))
                .isEmpty();
    }

    @Test
    void testResolve() throws JsonProcessingException {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());
        DatabaseDatasetImplementation databaseDatasetImplementation = DatabaseDatasetImplementation.builder()
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

        DataType dataType = DatabaseDatasetImplementationService.getInstance()
                .resolve(
                        databaseDatasetImplementation,
                        "key",
                        jsonNode,
                        executionRuntime
                );
        assertThat(dataType)
                .isInstanceOf(DatabaseDatasetImplementation.class);
        assertThat(((DatabaseDatasetImplementation) dataType).getKeyValues())
                .hasSize(2)
                .usingElementComparatorOnFields("key", "value")
                .containsOnly(
                        new DatabaseDatasetImplementationKeyValue(
                                new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                datasetImplementationKey,
                                "key1",
                                "value1"
                        ),
                        new DatabaseDatasetImplementationKeyValue(
                                new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                datasetImplementationKey,
                                "key2",
                                "value2"
                        ));
    }

    @Test
    void testResolveNested() throws JsonProcessingException {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());

        when(executionRuntime.resolveVariables(anyString()))
                .thenAnswer((Answer<String>) invocation -> {
                    Object[] args = invocation.getArguments();
                    return (String) args[0];
                });
        when(executionRuntime.resolveConceptLookup(anyString()))
                .thenAnswer((Answer<LookupResult>) invocation -> {
                    Object[] args = invocation.getArguments();
                    return new LookupResult((String) args[0], null, null);
                });



        DatabaseDatasetImplementation databaseDatasetImplementation = DatabaseDatasetImplementation.builder()
                .metadataKey(datasetImplementationKey)
                .datasetKey(datasetKey)
                .name("dataset")
                .datasetImplementationLabels(Stream.of(
                        new DatasetImplementationLabel(
                                new DatasetImplementationLabelKey(UUID.randomUUID()),
                                datasetImplementationKey,
                                "label1"
                        )).collect(Collectors.toSet()))
                .keyValues(new HashSet<>())
                .build();

        Dataset dataset = new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                Stream.of(databaseDatasetImplementation).collect(Collectors.toSet())
        );

        datasetConfiguration.insert(dataset);

        ObjectNode jsonNode = (ObjectNode) new ObjectMapper().readTree("{\"key1\":{\"key2\":\"value2\"}}");

        DataType dataType = dataTypeHandlerSpy
                .resolve(
                        databaseDatasetImplementation,
                        "key",
                        jsonNode,
                        executionRuntime);

        assertThat(dataType instanceof DatabaseDatasetImplementation).isTrue();
        assertThat(((DatabaseDatasetImplementation) dataType).getKeyValues())
                .hasSize(1)
                .usingElementComparatorOnFields("key")
                .containsOnly(
                        new DatabaseDatasetImplementationKeyValue(
                                new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                datasetImplementationKey,
                                "key1",
                                "dataset"
                        ));


        DataType dataType1 = dataTypeHandlerSpy
                .resolve(((DatabaseDatasetImplementation) dataType).getKeyValues().iterator().next().getValue(), executionRuntime);

        assertThat(dataType1 instanceof DatabaseDatasetImplementation).isTrue();
        assertThat(((DatabaseDatasetImplementation) dataType1).getKeyValues())
                .hasSize(1)
                .usingElementComparatorOnFields("key")
                .containsOnly(
                        new DatabaseDatasetImplementationKeyValue(
                                new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                datasetImplementationKey,
                                "key2",
                                "value2"
                        ));
    }

}
