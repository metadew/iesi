package io.metadew.iesi.datatypes.dataset.implementation.in_memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

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
        // TODO
    }

    @Test
    void testGetDatasetImplementationByDatasetIdAndLabelsNoMatch() {
        // TODO
    }

    @Test
    void testGetDatasetImplementationByNameAndLabels() {
        // TODO
    }

    @Test
    void testGetDatasetImplementationByNameAndLabelsNoMatch() {
        // TODO
    }

    @Test
    void testGetDatasetItem() {
        DataTypeHandler dataTypeHandler = DataTypeHandler.getInstance();
        DataTypeHandler dataTypeHandlerSpy = Mockito.spy(dataTypeHandler);
        Whitebox.setInternalState(DataTypeHandler.class, "instance", dataTypeHandlerSpy);

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
                        new DatasetImplementationKeyValue(new DatasetImplementationKeyValueKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplemenationUuid),
                                "key1",
                                "value1"
                        )
                ).collect(Collectors.toSet())
        );
        assertThat(InMemoryDatasetImplementationService.getInstance().getDataItem(inMemoryDatasetImplementation, "key1", executionRuntime))
                .hasValue(new Text("value1"));
    }

    @Test
    void testGetDatasetItems() {
        // TODO
    }

    @Test
    void testGetDatasetItemsEmpty() {
        // TODO
    }

    @Test
    void testGetDatasetItemNoMatch() {
        // TODO
    }

    @Test
    void testSetDataItemNewKey() {
        // TODO
    }

    @Test
    void testSetDataItemExistingKey() {
        // TODO
    }

    @Test
    void testResolve() throws JsonProcessingException {
        // TODO
    }

    @Test
    void testResolveNested() throws JsonProcessingException {
        // TODO
    }

}
