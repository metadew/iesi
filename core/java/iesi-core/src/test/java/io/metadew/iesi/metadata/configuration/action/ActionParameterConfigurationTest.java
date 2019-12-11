package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
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

public class ActionParameterConfigurationTest {

    ActionParameter actionParameter;
    DesignMetadataRepository designMetadataRepository;

    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        ActionParameterKey actionParameterKey = new ActionParameterKey("1", 1,
                "1", "firstParameter");
        actionParameter = new ActionParameter(actionParameterKey, "parameter value");
        try{
            ActionParameterConfiguration.getInstance().insert(actionParameter);
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
    public void actionParameterNotExistsTest() {
        ActionParameterKey nonExistActionKey = new ActionParameterKey("non_exist", 2, "non", "parameter");
        assertFalse(ActionParameterConfiguration.getInstance().exists(nonExistActionKey));
    }

    @Test
    public void actionParameterExistsTest(){
        assertTrue(ActionParameterConfiguration.getInstance().exists(actionParameter.getMetadataKey()));
    }

    @Test
    public void actionParameterInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ActionParameterConfiguration.getInstance().getAll().size();
        ActionParameter newActionParameter = createActionParameter();
        ActionParameterConfiguration.getInstance().insert(newActionParameter);
        int nbAfter = ActionParameterConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void actionParameterInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ActionParameterConfiguration.getInstance().insert(actionParameter));
    }

    @Test
    public void actionDeleteTest() throws MetadataDoesNotExistException {
        ActionParameterConfiguration.getInstance().delete(actionParameter.getMetadataKey());
    }

    @Test
    public void actionDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ActionParameter deleteScript = createActionParameter();
        assertThrows(MetadataDoesNotExistException.class,() -> ActionParameterConfiguration.getInstance().delete(deleteScript.getMetadataKey()));
    }

    @Test
    public void setActionParameterGetTest() {
        Optional<ActionParameter> newActionParameter = ActionParameterConfiguration.getInstance().get(actionParameter.getMetadataKey());
        assertTrue(newActionParameter.isPresent());
        assertEquals(actionParameter.getMetadataKey().getScriptId(), newActionParameter.get().getMetadataKey().getScriptId());
        assertEquals(actionParameter.getMetadataKey().getScriptVersionNumber(), newActionParameter.get().getMetadataKey().getScriptVersionNumber());
        assertEquals(actionParameter.getMetadataKey().getActionId(), newActionParameter.get().getMetadataKey().getActionId());
    }

    @Test
    public void actionParameterGetNotExistsTest(){
        ActionParameterKey newActionParameterKey = new ActionParameterKey("3", 4, "not exist",
                "test parameter");
        assertFalse(ActionParameterConfiguration.getInstance().exists(newActionParameterKey));
        assertFalse(ActionParameterConfiguration.getInstance().get(newActionParameterKey).isPresent());
    }

    @Test
    public void actionParameterUpdateTest() throws MetadataDoesNotExistException {
        ActionParameter actionParameterUpdate = actionParameter;
        String newValue = "new value";
        actionParameterUpdate.setValue(newValue);
        ActionParameterConfiguration.getInstance().update(actionParameterUpdate);
        Optional<ActionParameter> checkScript =
                ActionParameterConfiguration.getInstance().get(actionParameterUpdate.getMetadataKey());
        assertTrue(checkScript.isPresent() && checkScript.get().getValue().equals(newValue));
    }

    public ActionParameter createActionParameter(){
        ActionParameterKey newActionParameterKey = new ActionParameterKey("scriptIdNb", 3,
                "actionId", "newParameter");
        return new ActionParameter(newActionParameterKey, "new parameter description");
    }
}
