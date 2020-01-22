package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.action.ActionParameterConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScriptConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private Script script;
    private ScriptVersion scriptVersion;
    private List<Action> actions;

    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        actions = new ArrayList<>();
        actions.add(new Action(new ActionKey("1", 1, "1"), 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>()));
        actions.add(new Action(new ActionKey("1", 1, "2"), 2, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>()));
        scriptVersion = new ScriptVersion(new ScriptVersionKey("1", 1),
                "version of script");
        script = new Script(new ScriptKey("1", 1), "testScriptExist",
                "script for testing", scriptVersion,
                new ArrayList<>(), actions);
    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    public void scriptNotExistsTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        assertFalse(ScriptConfiguration.getInstance().exists("testScript", 0));
    }

//    @Test
//    public void scriptVersionExistsTest() throws ScriptAlreadyExistsException {
//        ScriptConfiguration.getInstance().insert(script);
//        assertTrue(ScriptVersionConfiguration.getInstance().exists(scriptVersion.getMetadataKey()));
//    }

    @Test
    public void scriptExistsTest() throws ScriptAlreadyExistsException {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().insert(script);
        assertFalse(ScriptConfiguration.getInstance().exists("testScriptExist", 0));
        assertTrue(ScriptConfiguration.getInstance().exists("testScriptExist", 1));
    }

    @Test
    public void scriptInsertTest() throws ScriptAlreadyExistsException {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script);

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script.getMetadataKey()).isPresent());
        assertEquals(script, ScriptConfiguration.getInstance().get(script.getMetadataKey()).get());
    }

    @Test
    public void scriptInsertAlreadyExistsTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script);
        assertThrows(ScriptAlreadyExistsException.class,() -> ScriptConfiguration.getInstance().insert(script));
    }

    @Test
    public void scriptDeleteTest() throws MetadataDoesNotExistException, ScriptAlreadyExistsException {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().insert(script);
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().delete(script.getMetadataKey());
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    public void scriptDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        assertThrows(MetadataDoesNotExistException.class,() -> ScriptConfiguration.getInstance().delete(script.getMetadataKey()));
    }

    @Test
    public void scriptGetTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script);
        Optional<Script> newScript = ScriptConfiguration.getInstance().get(script.getMetadataKey());
        assertTrue(newScript.isPresent());
        assertEquals(script.getMetadataKey().getScriptId(), newScript.get().getMetadataKey().getScriptId());
    }

    @Test
    public void scriptGetNotExistsTest(){
        assertFalse(ScriptConfiguration.getInstance().exists(script.getMetadataKey()));
        assertFalse(ScriptConfiguration.getInstance().get(script.getMetadataKey()).isPresent());
    }

    @Test
    public void scriptUpdateTest() throws ScriptDoesNotExistException, ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script);
        String newDescription = "new description";
        script.setDescription(newDescription);
        ScriptConfiguration.getInstance().update(script);
        Optional<Script> checkScript = ScriptConfiguration.getInstance().get(script.getMetadataKey());
        assertEquals(checkScript.get().getDescription(), newDescription);
    }

}
