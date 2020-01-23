package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EnvironmentParameterConfigurationTest {
    
    private EnvironmentParameter environmentParameter11;
    private ConnectivityMetadataRepository connectivityMetadataRepository;
    private EnvironmentParameter environmentParameter12;
    private EnvironmentParameter environmentParameter2;

    @Before
    public void setup() {
        connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        environmentParameter11 = new EnvironmentParameterBuilder("env1", "parameter name 1")
                .value("parameter value")
                .build();
        environmentParameter12 = new EnvironmentParameterBuilder("env1", "parameter name 2")
                .value("parameter value")
                .build();
        environmentParameter2 = new EnvironmentParameterBuilder("env2", "parameter name")
                .value("parameter value")
                .build();
    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        connectivityMetadataRepository.dropAllTables();
    }

    @Test
    public void environmentParameterNotExistsOnlyTest() {
        assertFalse(EnvironmentParameterConfiguration.getInstance().exists(environmentParameter11));
    }

    @Test
    public void environmentParameterNotExistsSimilarEnvNameTest() throws MetadataAlreadyExistsException {
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter12);
        assertFalse(EnvironmentParameterConfiguration.getInstance().exists(environmentParameter11));
    }

    @Test
    public void environmentParameterExistsTest() throws MetadataAlreadyExistsException {
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11);
        assertTrue(EnvironmentParameterConfiguration.getInstance().exists(environmentParameter11.getMetadataKey()));
    }

    @Test
    public void environmentParameterInsertOnlyTest() throws MetadataAlreadyExistsException {
        assertEquals(0, EnvironmentParameterConfiguration.getInstance().getAll().size());

        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11);
        Optional<EnvironmentParameter> fetchedEnvironmentParameter = EnvironmentParameterConfiguration.getInstance().get(environmentParameter11.getMetadataKey());

        assertEquals(1, EnvironmentParameterConfiguration.getInstance().getAll().size());
        assertTrue(fetchedEnvironmentParameter.isPresent());
        assertEquals(environmentParameter11, fetchedEnvironmentParameter.get());
    }

    @Test
    public void environmentParameterInsertMultipleTest() throws MetadataAlreadyExistsException {
        assertEquals(0, EnvironmentParameterConfiguration.getInstance().getAll().size());

        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11);
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter12);
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter2);
        Optional<EnvironmentParameter> fetchedEnvironmentParameter11 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter11.getMetadataKey());
        Optional<EnvironmentParameter> fetchedEnvironmentParameter12 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter12.getMetadataKey());
        Optional<EnvironmentParameter> fetchedEnvironmentParameter2 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter2.getMetadataKey());

        assertEquals(3, EnvironmentParameterConfiguration.getInstance().getAll().size());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals(environmentParameter11, fetchedEnvironmentParameter11.get());
        assertTrue(fetchedEnvironmentParameter12.isPresent());
        assertEquals(environmentParameter12, fetchedEnvironmentParameter12.get());
        assertTrue(fetchedEnvironmentParameter2.isPresent());
        assertEquals(environmentParameter2, fetchedEnvironmentParameter2.get());
    }

    @Test
    public void environmentParameterInsertAlreadyExistsTest() throws MetadataAlreadyExistsException {
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11);
        assertThrows(MetadataAlreadyExistsException.class,() -> EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11));
    }

    @Test
    public void environmentParameterDeleteTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11);
        EnvironmentParameterConfiguration.getInstance().delete(environmentParameter11.getMetadataKey());
    }

    @Test
    public void environmentParameterDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() -> EnvironmentParameterConfiguration.getInstance().delete(environmentParameter2.getMetadataKey()));
    }

    @Test
    public void environmentParameterGetTest() throws MetadataAlreadyExistsException {
        assertEquals(0, EnvironmentParameterConfiguration.getInstance().getAll().size());

        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11);
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter12);
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter2);
        Optional<EnvironmentParameter> fetchedEnvironmentParameter11 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter11.getMetadataKey());
        Optional<EnvironmentParameter> fetchedEnvironmentParameter12 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter12.getMetadataKey());
        Optional<EnvironmentParameter> fetchedEnvironmentParameter2 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter2.getMetadataKey());

        assertEquals(3, EnvironmentParameterConfiguration.getInstance().getAll().size());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals(environmentParameter11, fetchedEnvironmentParameter11.get());
        assertTrue(fetchedEnvironmentParameter12.isPresent());
        assertEquals(environmentParameter12, fetchedEnvironmentParameter12.get());
        assertTrue(fetchedEnvironmentParameter2.isPresent());
        assertEquals(environmentParameter2, fetchedEnvironmentParameter2.get());
    }

    @Test
    public void environmentParameterGetAllTest() throws MetadataAlreadyExistsException {
        assertEquals(0, EnvironmentParameterConfiguration.getInstance().getAll().size());

        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11);
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter12);
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter2);

        assertEquals(Stream.of(environmentParameter11, environmentParameter12, environmentParameter2).collect(Collectors.toList()), EnvironmentParameterConfiguration.getInstance().getAll());
    }

    @Test
    public void environmentParameterGetNotExistsTest(){
        assertFalse(EnvironmentParameterConfiguration.getInstance().exists(environmentParameter11));
    }

    @Test
    public void environmentParameterUpdateOnlyTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11);
        Optional<EnvironmentParameter> fetchedEnvironmentParameter11 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter11.getMetadataKey());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals("parameter value", fetchedEnvironmentParameter11.get().getValue());

        environmentParameter11.setValue("new value");
        EnvironmentParameterConfiguration.getInstance().update(environmentParameter11);

        fetchedEnvironmentParameter11 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter11.getMetadataKey());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals("new value", fetchedEnvironmentParameter11.get().getValue());
    }

    @Test
    public void environmentParameterUpdateMultipleTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter11);
        EnvironmentParameterConfiguration.getInstance().insert(environmentParameter12);

        Optional<EnvironmentParameter> fetchedEnvironmentParameter11 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter11.getMetadataKey());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals("parameter value", fetchedEnvironmentParameter11.get().getValue());

        environmentParameter11.setValue("new value");
        EnvironmentParameterConfiguration.getInstance().update(environmentParameter11);

        fetchedEnvironmentParameter11 = EnvironmentParameterConfiguration.getInstance().get(environmentParameter11.getMetadataKey());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals("new value", fetchedEnvironmentParameter11.get().getValue());
    }

}
