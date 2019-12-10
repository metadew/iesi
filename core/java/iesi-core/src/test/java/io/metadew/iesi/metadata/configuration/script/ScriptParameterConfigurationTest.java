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
    ScriptParameter scriptParameter;

    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();

        scriptParameter = new ScriptParameter(new ScriptParameterKey("1", 1,
                "parameter"),
                "Parameter of script");
        try{
            ScriptParameterConfiguration.getInstance().insert(scriptParameter);
        }catch(MetadataAlreadyExistsException ignored){
            // if script already is in database do nothing
        }
    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    public void scriptParameterNotExistTest(){
        ScriptParameterKey scriptParameterKey = new ScriptParameterKey("non", 1,
                "parameter");
        assertFalse(ScriptParameterConfiguration.getInstance().exists(scriptParameterKey));
    }

    @Test
    public void scriptParameterExistsTest(){
        assertTrue(ScriptParameterConfiguration.getInstance().exists(scriptParameter.getMetadataKey()));
    }

    @Test
    public void scriptParameterInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ScriptParameterConfiguration.getInstance().getAll().size();
        ScriptParameter scriptParameter = createScriptParameter();
        ScriptParameterConfiguration.getInstance().insert(scriptParameter);
        int nbAfter = ScriptParameterConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void scriptParameterInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ScriptParameterConfiguration.getInstance().insert(scriptParameter));
    }

    @Test
    public void scriptParameterDeleteTest() throws MetadataDoesNotExistException {
        ScriptParameterConfiguration.getInstance().delete(scriptParameter.getMetadataKey());
    }

    @Test
    public void scriptParameterDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ScriptParameter deleteScriptParameter = createScriptParameter();
        assertThrows(MetadataDoesNotExistException.class,() -> ScriptParameterConfiguration.getInstance().delete(deleteScriptParameter.getMetadataKey()));
    }

    @Test
    public void scriptParameterGetTest() {
        Optional<ScriptParameter> newScriptParameter = ScriptParameterConfiguration.getInstance().get(scriptParameter.getMetadataKey());
        assertTrue(newScriptParameter.isPresent());
        assertEquals(scriptParameter.getMetadataKey().getScriptId(), newScriptParameter.get().getMetadataKey().getScriptId());
        assertEquals(scriptParameter.getMetadataKey().getScriptVersionNumber(), newScriptParameter.get().getMetadataKey().getScriptVersionNumber());
        assertEquals(scriptParameter.getMetadataKey().getParameterName(), newScriptParameter.get().getMetadataKey().getParameterName());
    }

    @Test
    public void scriptParameterGetNotExistsTest(){
        ScriptParameterKey scriptParameterKey = new ScriptParameterKey("3", 1,
                "par");
        assertFalse(ScriptParameterConfiguration.getInstance().exists(scriptParameterKey));
        assertFalse(ScriptParameterConfiguration.getInstance().get(scriptParameterKey).isPresent());
    }

    @Test
    public void scriptParameterUpdateTest() throws MetadataDoesNotExistException {
        ScriptParameter scriptParameterUpdate = scriptParameter;
        String newValue = "new value";
        scriptParameterUpdate.setValue(newValue);
        ScriptParameterConfiguration.getInstance().update(scriptParameterUpdate);
        Optional<ScriptParameter> checkScriptParameter = ScriptParameterConfiguration.getInstance().get(scriptParameterUpdate.getMetadataKey());
        assertEquals(checkScriptParameter.get().getValue(), newValue);
    }

    private ScriptParameter createScriptParameter(){
        return new ScriptParameter(new ScriptParameterKey("2", 1, "new"),
                "created with function");
    }
}
