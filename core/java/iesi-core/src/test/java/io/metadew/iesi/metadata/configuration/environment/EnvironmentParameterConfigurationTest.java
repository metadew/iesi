package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, EnvironmentParameterConfiguration.class })
@ActiveProfiles("test")
class EnvironmentParameterConfigurationTest {

    EnvironmentParameter environmentParameter11;
    EnvironmentParameter environmentParameter12;
    EnvironmentParameter environmentParameter2;

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    EnvironmentParameterConfiguration environmentParameterConfiguration;


    @BeforeEach
    void setup() {
        environmentParameter11 = new EnvironmentParameterBuilder("env1", "parameter name 1")
                .value("parameter value")
                .build();
        environmentParameter12 = new EnvironmentParameterBuilder("env1", "parameter name 2")
                .value("parameter value")
                .build();
        environmentParameter2 = new EnvironmentParameterBuilder("env2", "parameter name")
                .value("parameter value")
                .build();

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void environmentParameterNotExistsOnlyTest() {
        assertFalse(environmentParameterConfiguration.exists(environmentParameter11));
    }

    @Test
    void environmentParameterNotExistsSimilarEnvNameTest() {
        environmentParameterConfiguration.insert(environmentParameter12);
        assertFalse(environmentParameterConfiguration.exists(environmentParameter11));
    }

    @Test
    void environmentParameterExistsTest() {
        environmentParameterConfiguration.insert(environmentParameter11);
        assertTrue(environmentParameterConfiguration.exists(environmentParameter11.getMetadataKey()));
    }

    @Test
    void environmentParameterInsertOnlyTest() {
        assertEquals(0, environmentParameterConfiguration.getAll().size());

        environmentParameterConfiguration.insert(environmentParameter11);
        Optional<EnvironmentParameter> fetchedEnvironmentParameter = environmentParameterConfiguration.get(environmentParameter11.getMetadataKey());

        assertEquals(1, environmentParameterConfiguration.getAll().size());
        assertTrue(fetchedEnvironmentParameter.isPresent());
        assertEquals(environmentParameter11, fetchedEnvironmentParameter.get());
    }

    @Test
    void environmentParameterInsertMultipleTest() {
        assertEquals(0, environmentParameterConfiguration.getAll().size());

        environmentParameterConfiguration.insert(environmentParameter11);
        environmentParameterConfiguration.insert(environmentParameter12);
        environmentParameterConfiguration.insert(environmentParameter2);
        Optional<EnvironmentParameter> fetchedEnvironmentParameter11 = environmentParameterConfiguration.get(environmentParameter11.getMetadataKey());
        Optional<EnvironmentParameter> fetchedEnvironmentParameter12 = environmentParameterConfiguration.get(environmentParameter12.getMetadataKey());
        Optional<EnvironmentParameter> fetchedEnvironmentParameter2 = environmentParameterConfiguration.get(environmentParameter2.getMetadataKey());

        assertEquals(3, environmentParameterConfiguration.getAll().size());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals(environmentParameter11, fetchedEnvironmentParameter11.get());
        assertTrue(fetchedEnvironmentParameter12.isPresent());
        assertEquals(environmentParameter12, fetchedEnvironmentParameter12.get());
        assertTrue(fetchedEnvironmentParameter2.isPresent());
        assertEquals(environmentParameter2, fetchedEnvironmentParameter2.get());
    }

    @Test
    void environmentParameterInsertAlreadyExistsTest() {
        environmentParameterConfiguration.insert(environmentParameter11);
        assertThrows(MetadataAlreadyExistsException.class, () -> environmentParameterConfiguration.insert(environmentParameter11));
    }

    @Test
    void environmentParameterDeleteTest() {
        environmentParameterConfiguration.insert(environmentParameter11);
        environmentParameterConfiguration.delete(environmentParameter11.getMetadataKey());
    }

    @Test
    void environmentParameterDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> environmentParameterConfiguration.delete(environmentParameter2.getMetadataKey()));
    }

    @Test
    void environmentParameterGetTest() {
        assertEquals(0, environmentParameterConfiguration.getAll().size());

        environmentParameterConfiguration.insert(environmentParameter11);
        environmentParameterConfiguration.insert(environmentParameter12);
        environmentParameterConfiguration.insert(environmentParameter2);
        Optional<EnvironmentParameter> fetchedEnvironmentParameter11 = environmentParameterConfiguration.get(environmentParameter11.getMetadataKey());
        Optional<EnvironmentParameter> fetchedEnvironmentParameter12 = environmentParameterConfiguration.get(environmentParameter12.getMetadataKey());
        Optional<EnvironmentParameter> fetchedEnvironmentParameter2 = environmentParameterConfiguration.get(environmentParameter2.getMetadataKey());

        assertEquals(3, environmentParameterConfiguration.getAll().size());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals(environmentParameter11, fetchedEnvironmentParameter11.get());
        assertTrue(fetchedEnvironmentParameter12.isPresent());
        assertEquals(environmentParameter12, fetchedEnvironmentParameter12.get());
        assertTrue(fetchedEnvironmentParameter2.isPresent());
        assertEquals(environmentParameter2, fetchedEnvironmentParameter2.get());
    }

    @Test
    void environmentParameterGetAllTest() {
        assertEquals(0, environmentParameterConfiguration.getAll().size());

        environmentParameterConfiguration.insert(environmentParameter11);
        environmentParameterConfiguration.insert(environmentParameter12);
        environmentParameterConfiguration.insert(environmentParameter2);

        assertEquals(Stream.of(environmentParameter11, environmentParameter12, environmentParameter2).collect(Collectors.toList()), environmentParameterConfiguration.getAll());
    }

    @Test
    void environmentParameterGetNotExistsTest() {
        assertFalse(environmentParameterConfiguration.exists(environmentParameter11));
    }

    @Test
    void environmentParameterUpdateOnlyTest() {
        environmentParameterConfiguration.insert(environmentParameter11);
        Optional<EnvironmentParameter> fetchedEnvironmentParameter11 = environmentParameterConfiguration.get(environmentParameter11.getMetadataKey());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals("parameter value", fetchedEnvironmentParameter11.get().getValue());

        environmentParameter11.setValue("new value");
        environmentParameterConfiguration.update(environmentParameter11);

        fetchedEnvironmentParameter11 = environmentParameterConfiguration.get(environmentParameter11.getMetadataKey());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals("new value", fetchedEnvironmentParameter11.get().getValue());
    }

    @Test
    void environmentParameterUpdateMultipleTest() {
        environmentParameterConfiguration.insert(environmentParameter11);
        environmentParameterConfiguration.insert(environmentParameter12);

        Optional<EnvironmentParameter> fetchedEnvironmentParameter11 = environmentParameterConfiguration.get(environmentParameter11.getMetadataKey());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals("parameter value", fetchedEnvironmentParameter11.get().getValue());

        environmentParameter11.setValue("new value");
        environmentParameterConfiguration.update(environmentParameter11);

        fetchedEnvironmentParameter11 = environmentParameterConfiguration.get(environmentParameter11.getMetadataKey());
        assertTrue(fetchedEnvironmentParameter11.isPresent());
        assertEquals("new value", fetchedEnvironmentParameter11.get().getValue());
    }

}
