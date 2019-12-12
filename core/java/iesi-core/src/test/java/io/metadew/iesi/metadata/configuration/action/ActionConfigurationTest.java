package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
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

public class ActionConfigurationTest {

    ActionParameter actionParameter;
    Action action;
    ActionKey actionKey;
    DesignMetadataRepository designMetadataRepository;

    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        ActionParameterKey actionParameterKey = new ActionParameterKey("1", 1,
                "1", "firstParameter");
        actionParameter = new ActionParameter(actionParameterKey, "parameter value");
        List<ActionParameter> actionParameters = new ArrayList<>();
        actionParameters.add(actionParameter);
        actionKey = new ActionKey("1", 1, "1");
        action = new Action(actionKey, 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", actionParameters);
        try{
            ActionConfiguration.getInstance().insert(action);
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
    public void actionNotExistsTest() {
        ActionKey nonExistActionKey = new ActionKey("non_exist", 2, "non");
        assertFalse(ActionConfiguration.getInstance().exists(nonExistActionKey));
    }

    @Test
    public void actionParameterExistsTest(){
        assertTrue(ActionParameterConfiguration.getInstance().exists(actionParameter.getMetadataKey()));
    }

    @Test
    public void actionExistsTest(){
        assertTrue(ActionConfiguration.getInstance().exists(action.getMetadataKey()));
    }

    @Test
    public void actionInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ActionConfiguration.getInstance().getAll().size();
        Action newAction = createAction();
        ActionConfiguration.getInstance().insert(newAction);
        int nbAfter = ActionConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void actionInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ActionConfiguration.getInstance().insert(action));
    }

    @Test
    public void actionDeleteTest() throws MetadataDoesNotExistException {
        ActionConfiguration.getInstance().delete(action.getMetadataKey());
    }

    @Test
    public void actionDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        Action deleteScript = createAction();
        assertThrows(MetadataDoesNotExistException.class,() -> ActionConfiguration.getInstance().delete(deleteScript.getMetadataKey()));
    }

    @Test
    public void actionGetTest() {
        Optional<Action> newAction = ActionConfiguration.getInstance().get(action.getMetadataKey());
        assertTrue(newAction.isPresent());
        assertEquals(action.getMetadataKey().getScriptId(), newAction.get().getMetadataKey().getScriptId());
        assertEquals(action.getMetadataKey().getScriptVersionNumber(), newAction.get().getMetadataKey().getScriptVersionNumber());
        assertEquals(action.getMetadataKey().getActionId(), newAction.get().getMetadataKey().getActionId());
    }

    @Test
    public void actionGetNotExistsTest(){
        ActionKey actionParameterKey = new ActionKey("3", 4, "not exist");
        assertFalse(ActionConfiguration.getInstance().exists(actionParameterKey));
        assertFalse(ActionConfiguration.getInstance().get(actionParameterKey).isPresent());
    }

    @Test
    public void actionUpdateTest() throws MetadataDoesNotExistException {
        Action actionUpdate = action;
        String newDescription = "new description";
        actionUpdate.setDescription(newDescription);
        ActionConfiguration.getInstance().update(actionUpdate);
        Optional<Action> checkScript = ActionConfiguration.getInstance().get(actionUpdate.getMetadataKey());
        assertTrue(checkScript.isPresent() && checkScript.get().getDescription().equals(newDescription));
    }

    private Action createAction(){
        ActionKey newActionKey = new ActionKey("scriptIdNb", 3, "actionId");
        return new Action(newActionKey, 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>());
    }
}
