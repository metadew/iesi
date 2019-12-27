package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
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
        scriptVersion = new ScriptVersion(new ScriptVersionKey("1", 1),
                "version of script");
        script = new Script(new ScriptKey("1", 1), "testScriptExist",
                "script for testing", scriptVersion,
                new ArrayList<>(), actions);
        try{
            ScriptConfiguration.getInstance().insert(script);
        }catch(ScriptAlreadyExistsException ignored){
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
    public void scriptNotExistsTest() {
        assertFalse(ScriptConfiguration.getInstance().exists("testScript", 0));
    }

    @Test
    public void scriptVersionExistsTest(){
        assertTrue(ScriptVersionConfiguration.getInstance().exists(scriptVersion.getMetadataKey()));
    }

    @Test
    public void scriptExistsTest() throws ScriptAlreadyExistsException {
        assertTrue(ScriptConfiguration.getInstance().exists("testScriptExist", 1));
    }

    @Test
    public void scriptInsertTest() throws ScriptAlreadyExistsException {
        int nbBefore = ScriptConfiguration.getInstance().getAll().size();
        Script script = createScript();
        ScriptConfiguration.getInstance().insert(script);
        int nbAfter = ScriptConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void scriptInsertAlreadyExistsTest() {
        assertThrows(ScriptAlreadyExistsException.class,() -> ScriptConfiguration.getInstance().insert(script));
    }

    @Test
    public void scriptDeleteTest() throws MetadataDoesNotExistException {
        ScriptConfiguration.getInstance().delete(script.getMetadataKey());
    }

    @Test
    public void scriptDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        Script deleteScript = createScript();
        assertThrows(MetadataDoesNotExistException.class,() -> ScriptConfiguration.getInstance().delete(deleteScript.getMetadataKey()));
    }

    @Test
    public void scriptGetTest() {
        Optional<Script> newScript = ScriptConfiguration.getInstance().get(script.getMetadataKey());
        assertTrue(newScript.isPresent());
        assertEquals(script.getMetadataKey().getScriptId(), newScript.get().getMetadataKey().getScriptId());
    }

    @Test
    public void scriptGetNotExistsTest(){
        ScriptKey scriptKey = new ScriptKey("3", 1);
        assertFalse(ScriptConfiguration.getInstance().exists(scriptKey));
        assertFalse(ScriptConfiguration.getInstance().get(scriptKey).isPresent());
    }

    @Test
    public void scriptUpdateTest() throws ScriptDoesNotExistException {
        Script scriptUpdate = script;
        String newDescription = "new description";
        scriptUpdate.setDescription(newDescription);
        ScriptConfiguration.getInstance().update(scriptUpdate);
        Optional<Script> checkScript = ScriptConfiguration.getInstance().get(scriptUpdate.getMetadataKey());
        assertEquals(checkScript.get().getDescription(), newDescription);
    }


    @Test
    public void insertScriptMultipleActionsTest() throws ScriptAlreadyExistsException {
        // setup
        Script scriptMultipleActions = createScript();
        Action action1 = new Action(new ActionKey("1", 1, "1"), 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>());
        Action action2 = new Action(new ActionKey("1", 1, "2"), 2, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>());
        List<Action> multipleActions = new ArrayList<>();
        multipleActions.add(action1);
        multipleActions.add(action2);
        scriptMultipleActions.setActions(multipleActions);

        // insert
        int nbBefore = ScriptConfiguration.getInstance().getAll().size();
        ScriptConfiguration.getInstance().insert(scriptMultipleActions);
        int nbAfter = ScriptConfiguration.getInstance().getAll().size();

        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void retrieveMultipleActionsTest() throws ScriptAlreadyExistsException {
        // setup
        Script scriptMultipleActions = createScript();
        Action action1 = new Action(new ActionKey("1", 1, "1"), 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>());
        Action action2 = new Action(new ActionKey("1", 1, "2"), 2, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>());
        List<Action> multipleActions = new ArrayList<>();
        multipleActions.add(action1);
        multipleActions.add(action2);
        scriptMultipleActions.setActions(multipleActions);

        // insert
        ScriptConfiguration.getInstance().insert(scriptMultipleActions);

        // retrieve
        Optional<Script> newScript = ScriptConfiguration.getInstance().get(scriptMultipleActions.getMetadataKey());
        assertEquals(newScript.get().getActions().size(), 2);
    }

    @Test
    public void updateMultipleActionParametersTest() throws ScriptAlreadyExistsException {
        // setup
        //action 1
        ActionParameterKey actionParameterKey = new ActionParameterKey("1", 1,
                "1", "firstParameter");
        ActionParameter actionParameter = new ActionParameter(actionParameterKey, "parameter value");
        ActionParameterKey actionParameterKey2 = new ActionParameterKey("1", 1,
                "1", "secondParameter");
        ActionParameter actionParameter2 = new ActionParameter(actionParameterKey2, "parameter value2");
        List<ActionParameter> actionParameters = new ArrayList<>();
        actionParameters.add(actionParameter);
        actionParameters.add(actionParameter2);
        Action action1 = new Action(new ActionKey("1", 1, "1"), 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", actionParameters);

        // action 2
        ActionParameterKey actionParameterKey3 = new ActionParameterKey("1", 1,
                "2", "thirdParameter");
        ActionParameter actionParameter3 = new ActionParameter(actionParameterKey, "parameter value");
        ActionParameterKey actionParameterKey4 = new ActionParameterKey("1", 1,
                "1", "fourthParameter");
        ActionParameter actionParameter4 = new ActionParameter(actionParameterKey2, "parameter value2");
        List<ActionParameter> actionParameters2 = new ArrayList<>();
        actionParameters2.add(actionParameter3);
        actionParameters2.add(actionParameter4);
        Action action2 = new Action(new ActionKey("1", 1, "2"), 2, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", actionParameters2);
        List<Action> multipleActions = new ArrayList<>();
        multipleActions.add(action1);
        multipleActions.add(action2);

        Script newScript = createScript();
        newScript.setActions(multipleActions);

        // insert
        ScriptConfiguration.getInstance().insert(newScript);

        // retrieve
        Optional<Script> retrievedScript = ScriptConfiguration.getInstance().get(newScript.getMetadataKey());
        List<ActionParameter> retrievedActionParameters = retrievedScript.get().getActions().get(0).getParameters();
        assertEquals(retrievedActionParameters.get(0).getValue(), actionParameter.getValue());
        assertEquals(retrievedScript.get().getActions().get(0).getParameters().size(), 2);
    }

    private Script createScript(){
        List<Action> actions = new ArrayList<>();
        actions.add(new Action(new ActionKey("2", 1, "1"), 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>()));
        ScriptVersion scriptVersion = new ScriptVersion(new ScriptVersionKey("2", 1),
                "version of script");
        Script script = new Script(new ScriptKey("2", 1), "testScriptInsert",
                "script for testing", scriptVersion,
                new ArrayList<>(), actions);
        return script;
    }

}
