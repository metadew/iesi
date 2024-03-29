package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = { EnvironmentConfiguration.class, EnvironmentParameterConfiguration.class })
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class EnvironmentConfigurationTest {

    private ConnectivityMetadataRepository connectivityMetadataRepository;
    private Environment environment1;
    private Environment environment2;

    @Autowired
    private EnvironmentConfiguration environmentConfiguration;

    @Autowired
    private EnvironmentParameterConfiguration environmentParameterConfiguration;

    @BeforeEach
    void setup() {
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
        assertFalse(environmentConfiguration.exists(environment1));
    }

    @Test
    void environmentExistsTest() {
        environmentConfiguration.insert(environment1);
        assertTrue(environmentConfiguration.exists(environment1));
    }

    @Test
    void environmentInsertTest() {
        assertEquals(0, environmentConfiguration.getAll().size());

        environmentConfiguration.insert(environment1);

        assertEquals(1, environmentConfiguration.getAll().size());
        Optional<Environment> fetchedEnvironment = environmentConfiguration.get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals(environment1, fetchedEnvironment.get());
    }

    @Test
    void environmentInsertAlreadyExistsTest() {
        environmentConfiguration.insert(environment1);
        assertThrows(MetadataAlreadyExistsException.class, () -> environmentConfiguration.insert(environment1));
    }

    @Test
    void environmentDeleteTest() {
        environmentConfiguration.insert(environment1);
        assertEquals(1, environmentConfiguration.getAll().size());

        environmentConfiguration.delete(environment1.getMetadataKey());

        assertEquals(0, environmentConfiguration.getAll().size());
    }

    @Test
    void environmentDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> environmentConfiguration.delete(environment1.getMetadataKey()));
    }

    @Test
    void environmentGetTest() {
        assertEquals(0, environmentConfiguration.getAll().size());

        environmentConfiguration.insert(environment1);

        assertEquals(1, environmentConfiguration.getAll().size());
        Optional<Environment> fetchedEnvironment = environmentConfiguration.get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals(environment1, fetchedEnvironment.get());
    }

    @Test
    void environmentGetNotExistsTest() {
        assertFalse(environmentConfiguration.get(environment1.getMetadataKey()).isPresent());
    }

    @Test
    void environmentUpdateTest() {
        environmentConfiguration.insert(environment1);
        Optional<Environment> fetchedEnvironment = environmentConfiguration.get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals("description", fetchedEnvironment.get().getDescription());

        environment1.setDescription("new description");
        environmentConfiguration.update(environment1);

        fetchedEnvironment = environmentConfiguration.get(environment1.getMetadataKey());
        assertTrue(fetchedEnvironment.isPresent());
        assertEquals("new description", fetchedEnvironment.get().getDescription());
    }

    @Test
    void environmentGetAllTest() {
        environmentConfiguration.insert(environment1);
        environmentConfiguration.insert(environment2);

        assertEquals(Stream.of(environment1, environment2).collect(Collectors.toList()), environmentConfiguration.getAll());
    }

    @Test
    void environmentDeleteAllTest() {
        environmentConfiguration.insert(environment1);
        environmentConfiguration.insert(environment2);
        assertEquals(2, environmentConfiguration.getAll().size());

        environmentConfiguration.deleteAll();

        assertEquals(0, environmentConfiguration.getAll().size());
        assertEquals(0, environmentParameterConfiguration.getAll().size());
    }

}
