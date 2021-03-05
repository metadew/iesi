package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvironmentConfigurationTest {

    private ConnectivityMetadataRepository connectivityMetadataRepository;
    private Environment environment1;
    private Environment environment2;

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

    @BeforeEach
    void setup() {
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

    @Test
    void environmentNotExistsTest() {
        assertFalse(EnvironmentConfiguration.getInstance().exists(environment1));
    }

    @Test
    void environmentExistsTest() {
        EnvironmentConfiguration.getInstance().insert(environment1);
        assertTrue(EnvironmentConfiguration.getInstance().exists(environment1));
    }

    @Test
    void environmentInsertTest() {
        assertEquals(0, EnvironmentConfiguration.getInstance().getAll().size());

        EnvironmentConfiguration.getInstance().insert(environment1);

        assertEquals(1, EnvironmentConfiguration.getInstance().getAll().size());
        Optional<Environment> fetchedEnvironment = EnvironmentConfiguration.getInstance().get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals(environment1, fetchedEnvironment.get());
    }

    @Test
    void environmentInsertAlreadyExistsTest() {
        EnvironmentConfiguration.getInstance().insert(environment1);
        assertThrows(MetadataAlreadyExistsException.class,() -> EnvironmentConfiguration.getInstance().insert(environment1));
    }

    @Test
    void environmentDeleteTest() {
        EnvironmentConfiguration.getInstance().insert(environment1);
        assertEquals(1, EnvironmentConfiguration.getInstance().getAll().size());

        EnvironmentConfiguration.getInstance().delete(environment1.getMetadataKey());

        assertEquals(0, EnvironmentConfiguration.getInstance().getAll().size());
    }

    @Test
    void environmentDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() -> EnvironmentConfiguration.getInstance().delete(environment1.getMetadataKey()));
    }

    @Test
    void environmentGetTest() {
        assertEquals(0, EnvironmentConfiguration.getInstance().getAll().size());

        EnvironmentConfiguration.getInstance().insert(environment1);

        assertEquals(1, EnvironmentConfiguration.getInstance().getAll().size());
        Optional<Environment> fetchedEnvironment = EnvironmentConfiguration.getInstance().get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals(environment1, fetchedEnvironment.get());
    }

    @Test
    void environmentGetNotExistsTest(){
        assertFalse(EnvironmentConfiguration.getInstance().get(environment1.getMetadataKey()).isPresent());
    }

    @Test
    void environmentUpdateTest() {
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
    void environmentGetAllTest() {
        EnvironmentConfiguration.getInstance().insert(environment1);
        EnvironmentConfiguration.getInstance().insert(environment2);

        assertEquals(Stream.of(environment1, environment2).collect(Collectors.toList()), EnvironmentConfiguration.getInstance().getAll());
    }

    @Test
    void environmentDeleteAllTest() {
        EnvironmentConfiguration.getInstance().insert(environment1);
        EnvironmentConfiguration.getInstance().insert(environment2);
        assertEquals(2, EnvironmentConfiguration.getInstance().getAll().size());

        EnvironmentConfiguration.getInstance().deleteAll();

        assertEquals(0, EnvironmentConfiguration.getInstance().getAll().size());
        assertEquals(0, EnvironmentParameterConfiguration.getInstance().getAll().size());
    }

}
