package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScriptVersionConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    ScriptVersion scriptVersion;

    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();

        scriptVersion = new ScriptVersion(new ScriptVersionKey("1", 1),
                "version of script");
        try{
            ScriptVersionConfiguration.getInstance().insert(scriptVersion);
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
    public void scriptVersionNotExistTest(){
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey("non", 1);
        assertFalse(ScriptVersionConfiguration.getInstance().exists(scriptVersionKey));
    }

    @Test
    public void scriptVersionExistsTest(){
        assertTrue(ScriptVersionConfiguration.getInstance().exists(scriptVersion.getMetadataKey()));
    }

    @Test
    public void scriptVersionInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ScriptVersionConfiguration.getInstance().getAll().size();
        ScriptVersion scriptVersion = createScriptVersion();
        ScriptVersionConfiguration.getInstance().insert(scriptVersion);
        int nbAfter = ScriptVersionConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void scriptVersionInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ScriptVersionConfiguration.getInstance().insert(scriptVersion));
    }

    @Test
    public void scriptVersionDeleteTest() throws MetadataDoesNotExistException {
        ScriptVersionConfiguration.getInstance().delete(scriptVersion.getMetadataKey());
    }

    @Test
    public void scriptVersionDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ScriptVersion deleteScriptVersion = createScriptVersion();
        assertThrows(MetadataDoesNotExistException.class,() -> ScriptVersionConfiguration.getInstance().delete(deleteScriptVersion.getMetadataKey()));
    }

    @Test
    public void scriptVersionGetTest() {
        Optional<ScriptVersion> newScriptVersion = ScriptVersionConfiguration.getInstance().get(scriptVersion.getMetadataKey());
        assertTrue(newScriptVersion.isPresent());
        assertEquals(scriptVersion.getMetadataKey().getScriptId(), newScriptVersion.get().getMetadataKey().getScriptId());
        assertEquals(scriptVersion.getMetadataKey().getVersionNumber(), newScriptVersion.get().getMetadataKey().getVersionNumber());
    }

    @Test
    public void scriptVersionGetNotExistsTest(){
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey("3", 1);
        assertFalse(ScriptVersionConfiguration.getInstance().exists(scriptVersionKey));
        assertFalse(ScriptVersionConfiguration.getInstance().get(scriptVersionKey).isPresent());
    }

    @Test
    public void scriptUpdateTest() throws MetadataDoesNotExistException {
        ScriptVersion scriptVersionUpdate = scriptVersion;
        String newDescription = "new description";
        scriptVersionUpdate.setDescription(newDescription);
        ScriptVersionConfiguration.getInstance().update(scriptVersionUpdate);
        Optional<ScriptVersion> checkScriptVersion = ScriptVersionConfiguration.getInstance().get(scriptVersionUpdate.getMetadataKey());
        assertEquals(checkScriptVersion.get().getDescription(), newDescription);
    }

    private ScriptVersion createScriptVersion(){
        return new ScriptVersion(new ScriptVersionKey("2", 1),
                "version of script");
    }
}
