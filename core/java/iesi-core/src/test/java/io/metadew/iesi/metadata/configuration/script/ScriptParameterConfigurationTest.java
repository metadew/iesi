package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {Configuration.class, SpringContext.class, MetadataRepositoryConfiguration.class, ScriptParameterConfiguration.class})
class ScriptParameterConfigurationTest {
    private DesignMetadataRepository designMetadataRepository;
    private ScriptParameter scriptParameter1;
    private ScriptParameter scriptParameter2;

    @Autowired
    private static MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ScriptParameterConfiguration scriptParameterConfiguration;

    @BeforeAll
    static void prepare() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    @BeforeEach
    void setup() {
        designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        scriptParameter1 = new ScriptParameterBuilder("1", 1, "parameter1")
                .value("parameter value")
                .build();
        scriptParameter2 = new ScriptParameterBuilder("1", 1, "parameter2")
                .value("parameter value")
                .build();
    }

    @Test
    void scriptParameterNotExistTest() {
        assertFalse(scriptParameterConfiguration.exists(scriptParameter1.getMetadataKey()));
    }

    @Test
    void scriptParameterExistsTest() {
        scriptParameterConfiguration.insert(scriptParameter1);
        assertTrue(scriptParameterConfiguration.exists(scriptParameter1.getMetadataKey()));
    }

    @Test
    void scriptParameterInsertTest() {
        assertEquals(0, scriptParameterConfiguration.getAll().size());

        scriptParameterConfiguration.insert(scriptParameter1);

        assertEquals(1, scriptParameterConfiguration.getAll().size());
        assertTrue(scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).isPresent());
        assertEquals(scriptParameter1, scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).get());
    }

    @Test
    void scriptParameterInsertAlreadyExistsTest() {
        scriptParameterConfiguration.insert(scriptParameter1);
        assertThrows(MetadataAlreadyExistsException.class, () -> scriptParameterConfiguration.insert(scriptParameter1));
    }

    @Test
    void scriptParameterDeleteTest() {
        scriptParameterConfiguration.insert(scriptParameter1);
        assertEquals(1, scriptParameterConfiguration.getAll().size());

        scriptParameterConfiguration.delete(scriptParameter1.getMetadataKey());
        assertEquals(0, scriptParameterConfiguration.getAll().size());
        assertFalse(scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).isPresent());
    }

    @Test
    void scriptParameterDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> scriptParameterConfiguration.delete(scriptParameter1.getMetadataKey()));
    }

    @Test
    void scriptParameterGetTest() {
        scriptParameterConfiguration.insert(scriptParameter1);

        assertTrue(scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).isPresent());
        assertEquals(scriptParameter1, scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).get());
    }

    @Test
    void scriptParameterGetNotExistsTest() {
        assertFalse(scriptParameterConfiguration.exists(scriptParameter1));
        assertFalse(scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).isPresent());
    }

    @Test
    void scriptParameterUpdateTest() {
        scriptParameterConfiguration.insert(scriptParameter1);

        Optional<ScriptParameter> fetchedScriptParameter = scriptParameterConfiguration.get(scriptParameter1.getMetadataKey());
        assertTrue(fetchedScriptParameter.isPresent());
        assertEquals("parameter value", fetchedScriptParameter.get().getValue());

        scriptParameter1.setValue("new value");
        scriptParameterConfiguration.update(scriptParameter1);

        fetchedScriptParameter = scriptParameterConfiguration.get(scriptParameter1.getMetadataKey());
        assertTrue(fetchedScriptParameter.isPresent());
        assertEquals(fetchedScriptParameter.get().getValue(), "new value");
    }

}
