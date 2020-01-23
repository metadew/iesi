package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EnvironmentConfigurationTest {

    private ConnectivityMetadataRepository connectivityMetadataRepository;
    private Environment environment1;
    private Environment environment2;

    @Before
    public void setup() {
        connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        environment1 = new EnvironmentBuilder("env1")
                .description("description")
                .numberOfParameters(2)
                .build();
        environment2 = new EnvironmentBuilder("env2")
                .description("description")
                .numberOfParameters(2)
                .build();
    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        connectivityMetadataRepository.dropAllTables();
    }

    @Test
    public void environmentNotExistsTest() {
        assertFalse(EnvironmentConfiguration.getInstance().exists(environment1));
    }

    @Test
    public void environmentExistsTest() throws MetadataAlreadyExistsException {
        EnvironmentConfiguration.getInstance().insert(environment1);
        assertTrue(EnvironmentConfiguration.getInstance().exists(environment1));
    }

    @Test
    public void environmentInsertTest() throws MetadataAlreadyExistsException {
        assertEquals(0, EnvironmentConfiguration.getInstance().getAll().size());

        EnvironmentConfiguration.getInstance().insert(environment1);

        assertEquals(1, EnvironmentConfiguration.getInstance().getAll().size());
        Optional<Environment> fetchedEnvironment = EnvironmentConfiguration.getInstance().get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals(environment1, fetchedEnvironment.get());
    }

    @Test
    public void environmentInsertAlreadyExistsTest() throws MetadataAlreadyExistsException {
        EnvironmentConfiguration.getInstance().insert(environment1);
        assertThrows(MetadataAlreadyExistsException.class,() -> EnvironmentConfiguration.getInstance().insert(environment1));
    }

    @Test
    public void environmentDeleteTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        EnvironmentConfiguration.getInstance().insert(environment1);
        assertEquals(1, EnvironmentConfiguration.getInstance().getAll().size());

        EnvironmentConfiguration.getInstance().delete(environment1.getMetadataKey());

        assertEquals(0, EnvironmentConfiguration.getInstance().getAll().size());
    }

    @Test
    public void environmentDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() -> EnvironmentConfiguration.getInstance().delete(environment1.getMetadataKey()));
    }

    @Test
    public void environmentGetTest() throws MetadataAlreadyExistsException {
        assertEquals(0, EnvironmentConfiguration.getInstance().getAll().size());

        EnvironmentConfiguration.getInstance().insert(environment1);

        assertEquals(1, EnvironmentConfiguration.getInstance().getAll().size());
        Optional<Environment> fetchedEnvironment = EnvironmentConfiguration.getInstance().get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals(environment1, fetchedEnvironment.get());
    }

    @Test
    public void environmentGetNotExistsTest(){
        assertFalse(EnvironmentConfiguration.getInstance().get(environment1.getMetadataKey()).isPresent());
    }

    @Test
    public void environmentUpdateTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        EnvironmentConfiguration.getInstance().insert(environment1);
        Optional<Environment> fetchedEnvironment = EnvironmentConfiguration.getInstance().get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals("description", fetchedEnvironment.get().getDescription());

        environment1.setDescription("new description");
        EnvironmentConfiguration.getInstance().update(environment1);

        fetchedEnvironment = EnvironmentConfiguration.getInstance().get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals("new description", fetchedEnvironment.get().getDescription());
    }

    @Test
    public void environmentGetAllTest() throws MetadataAlreadyExistsException {
        EnvironmentConfiguration.getInstance().insert(environment1);
        EnvironmentConfiguration.getInstance().insert(environment2);

        assertEquals(Stream.of(environment1, environment2).collect(Collectors.toList()), EnvironmentConfiguration.getInstance().getAll());
    }

    @Test
    public void environmentDeleteAllTest() throws MetadataAlreadyExistsException {
        EnvironmentConfiguration.getInstance().insert(environment1);
        EnvironmentConfiguration.getInstance().insert(environment2);
        assertEquals(2, EnvironmentConfiguration.getInstance().getAll().size());

        EnvironmentConfiguration.getInstance().deleteAll();

        assertEquals(0, EnvironmentConfiguration.getInstance().getAll().size());
        assertEquals(0, EnvironmentParameterConfiguration.getInstance().getAll().size());
    }

}
