package io.metadew.iesi.datatypes.dataset.implementation.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import lombok.extern.log4j.Log4j2;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

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

@SpringBootTest(classes = { DatasetConfiguration.class, DatasetImplementationConfiguration.class, DatabaseDatasetImplementationKeyValueConfiguration.class, DataTypeHandler.class} )
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Log4j2
class DatabaseDatasetImplementationServiceTest {


    @Autowired
    private DatasetConfiguration datasetConfiguration;

    @Autowired
    private DatabaseDatasetImplementationKeyValueConfiguration databaseDatasetImplementationKeyValueConfiguration;

    @SpyBean
    private DataTypeHandler dataTypeHandlerSpy;

    @Test
    void testGetDatasetImplementationByDatasetIdAndLabels() {
        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationService.getInstance().getDatasetImplementation(new DatasetKey((UUID) datasetMap.get("datasetUUID")), Stream.of("label000", "label001").collect(Collectors.toList())))
                .hasValue((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"));
        assertThat(DatabaseDatasetImplementationService.getInstance().getDatasetImplementation(new DatasetKey((UUID) datasetMap.get("datasetUUID")), Stream.of("label010", "label011").collect(Collectors.toList())))
                .hasValue((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation1"));
    }

    @Test
    void testGetDatasetImplementationByDatasetIdAndLabelsNoMatch() {
        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationService.getInstance().getDatasetImplementation(new DatasetKey((UUID) datasetMap.get("datasetUUID")), Stream.of("label000").collect(Collectors.toList())))
                .isEmpty();
        assertThat(DatabaseDatasetImplementationService.getInstance().getDatasetImplementation(new DatasetKey(UUID.randomUUID()), Stream.of("label010", "label011").collect(Collectors.toList())))
                .isEmpty();
    }

    @Test
    void testGetDatasetImplementationByNameAndLabels() {
        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationService.getInstance().getDatasetImplementation("dataset0", Stream.of("label000", "label001").collect(Collectors.toList())))
                .hasValue((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"));
        assertThat(DatabaseDatasetImplementationService.getInstance().getDatasetImplementation("dataset0", Stream.of("label010", "label011").collect(Collectors.toList())))
                .hasValue((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation1"));
    }

    @Test
    void testGetDatasetImplementationByNameAndLabelsNoMatch() {
        Map<String, Object> datasetMap = generateDataset(0, 2, 2, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));
        assertThat(DatabaseDatasetImplementationService.getInstance().getDatasetImplementation("dataset0", Stream.of("label000").collect(Collectors.toList())))
                .isEmpty();
        assertThat(DatabaseDatasetImplementationService.getInstance().getDatasetImplementation("dataset1", Stream.of("label010", "label011").collect(Collectors.toList())))
                .isEmpty();
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
    void testSetDataItemNewKey() {
        Map<String, Object> datasetMap = generateDataset(0, 1, 1, 1);
        datasetConfiguration.insert((Dataset) datasetMap.get("dataset"));

        DatabaseDatasetImplementationService.getInstance()
                .setDataItem((DatabaseDatasetImplementation) datasetMap.get("datasetImplementation0"),
                        "key",
                        new Text("value")
                );

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

        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());

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

        DatabaseDatasetImplementationService databaseDatasetImplementationService = DatabaseDatasetImplementationService.getInstance();
        DatabaseDatasetImplementationService databaseDatasetImplementationServiceSpy = Mockito.spy(databaseDatasetImplementationService);
        Whitebox.setInternalState(DatabaseDatasetImplementationService.class, "instance", databaseDatasetImplementationServiceSpy);
        Whitebox.setInternalState(DatasetImplementationHandler.class, "instance", (DatasetImplementationHandler) null);

        DataType dataType = DatabaseDatasetImplementationService.getInstance()
                .resolve(
                        databaseDatasetImplementation,
                        "key",
                        jsonNode,
                        executionRuntime
                );
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
        Whitebox.setInternalState(DatabaseDatasetImplementationService.class, "instance", (DatabaseDatasetImplementationService) null);
        Whitebox.setInternalState(DatasetImplementationHandler.class, "instance", (DatasetImplementationHandler) null);
    }

}
