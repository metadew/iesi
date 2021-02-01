package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScriptParameterConfigurationTest {
    private DesignMetadataRepository designMetadataRepository;
    private ScriptParameter scriptParameter1;
    private ScriptParameter scriptParameter2;

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
        assertFalse(ScriptParameterConfiguration.getInstance().exists(scriptParameter1.getMetadataKey()));
    }

    @Test
    void scriptParameterExistsTest() {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);
        assertTrue(ScriptParameterConfiguration.getInstance().exists(scriptParameter1.getMetadataKey()));
    }

    @Test
    void scriptParameterInsertTest() {
        assertEquals(0, ScriptParameterConfiguration.getInstance().getAll().size());

        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);

        assertEquals(1, ScriptParameterConfiguration.getInstance().getAll().size());
        assertTrue(ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).isPresent());
        assertEquals(scriptParameter1, ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).get());
    }

    @Test
    void scriptParameterInsertAlreadyExistsTest() {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);
        assertThrows(MetadataAlreadyExistsException.class, () -> ScriptParameterConfiguration.getInstance().insert(scriptParameter1));
    }

    @Test
    void scriptParameterDeleteTest() {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);
        assertEquals(1, ScriptParameterConfiguration.getInstance().getAll().size());

        ScriptParameterConfiguration.getInstance().delete(scriptParameter1.getMetadataKey());
        assertEquals(0, ScriptParameterConfiguration.getInstance().getAll().size());
        assertFalse(ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).isPresent());
    }

    @Test
    void scriptParameterDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> ScriptParameterConfiguration.getInstance().delete(scriptParameter1.getMetadataKey()));
    }

    @Test
    void scriptParameterGetTest() {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);

        assertTrue(ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).isPresent());
        assertEquals(scriptParameter1, ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).get());
    }

    @Test
    void scriptParameterGetNotExistsTest() {
        assertFalse(ScriptParameterConfiguration.getInstance().exists(scriptParameter1));
        assertFalse(ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).isPresent());
    }

    @Test
    void scriptParameterUpdateTest() {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);

        Optional<ScriptParameter> fetchedScriptParameter = ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey());
        assertTrue(fetchedScriptParameter.isPresent());
        assertEquals("parameter value", fetchedScriptParameter.get().getValue());

        scriptParameter1.setValue("new value");
        ScriptParameterConfiguration.getInstance().update(scriptParameter1);

        fetchedScriptParameter = ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey());
        assertTrue(fetchedScriptParameter.isPresent());
        assertEquals(fetchedScriptParameter.get().getValue(), "new value");
    }

}
