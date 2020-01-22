package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScriptParameterConfigurationTest {
    private DesignMetadataRepository designMetadataRepository;
    private ScriptParameter scriptParameter1;
    private ScriptParameter scriptParameter2;

    @Before
    public void setup() {
        designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        scriptParameter1 = new ScriptParameterBuilder("1", 1, "parameter1")
                .value("parameter value")
                .build();
        scriptParameter2 = new ScriptParameterBuilder("1", 1, "parameter2")
                .value("parameter value")
                .build();
    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    public void scriptParameterNotExistTest() throws MetadataAlreadyExistsException {
        assertFalse(ScriptParameterConfiguration.getInstance().exists(scriptParameter1.getMetadataKey()));
    }

    @Test
    public void scriptParameterExistsTest() throws MetadataAlreadyExistsException {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);
        assertTrue(ScriptParameterConfiguration.getInstance().exists(scriptParameter1.getMetadataKey()));
    }

    @Test
    public void scriptParameterInsertTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ScriptParameterConfiguration.getInstance().getAll().size());

        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);

        assertEquals(1, ScriptParameterConfiguration.getInstance().getAll().size());
        assertTrue(ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).isPresent());
        assertEquals(scriptParameter1, ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).get());
    }

    @Test
    public void scriptParameterInsertAlreadyExistsTest() throws MetadataAlreadyExistsException {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);
        assertThrows(MetadataAlreadyExistsException.class, () -> ScriptParameterConfiguration.getInstance().insert(scriptParameter1));
    }

    @Test
    public void scriptParameterDeleteTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);
        assertEquals(1, ScriptParameterConfiguration.getInstance().getAll().size());

        ScriptParameterConfiguration.getInstance().delete(scriptParameter1.getMetadataKey());
        assertEquals(0, ScriptParameterConfiguration.getInstance().getAll().size());
        assertFalse(ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).isPresent());
    }

    @Test
    public void scriptParameterDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        assertThrows(MetadataDoesNotExistException.class, () -> ScriptParameterConfiguration.getInstance().delete(scriptParameter1.getMetadataKey()));
    }

    @Test
    public void scriptParameterGetTest() throws MetadataAlreadyExistsException {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);

        assertTrue(ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).isPresent());
        assertEquals(scriptParameter1, ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).get());
    }

    @Test
    public void scriptParameterGetNotExistsTest() {
        assertFalse(ScriptParameterConfiguration.getInstance().exists(scriptParameter1));
        assertFalse(ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey()).isPresent());
    }

    @Test
    public void scriptParameterUpdateTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ScriptParameterConfiguration.getInstance().insert(scriptParameter1);

        Optional<ScriptParameter> fetchedScriptParameter = ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey());
        assertEquals("parameter value", fetchedScriptParameter.get().getValue());

        scriptParameter1.setValue("new value");
        ScriptParameterConfiguration.getInstance().update(scriptParameter1);

        fetchedScriptParameter = ScriptParameterConfiguration.getInstance().get(scriptParameter1.getMetadataKey());
        assertEquals(fetchedScriptParameter.get().getValue(), "new value");
    }

}
