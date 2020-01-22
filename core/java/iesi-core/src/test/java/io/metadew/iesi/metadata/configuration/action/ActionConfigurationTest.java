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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActionConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private ActionParameter actionParameter11;
    private ActionParameter actionParameter12;
    private Action action1;
    private Action action2;
    private List<Action> actions;
    private ActionParameter actionParameter21;
    private ActionParameter actionParameter22;

    @Before
    public void setup() {
        designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
//        actionParameter11 = new ActionParameter(new ActionParameterKey("1", 1, "1", "firstParameter"), "first parameter value");
//        actionParameter12 = new ActionParameter(new ActionParameterKey("1", 1, "1", "secondParameter"), "second parameter value");
//        actionParameter21 = new ActionParameter(new ActionParameterKey("1", 1, "1", "firstParameter"), "first parameter value");
//        actionParameter22 = new ActionParameter(new ActionParameterKey("1", 1, "1", "secondParameter"), "second parameter value");
//        actionParameters1 = Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList());
//        actionParameters2 = Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList());
//        action1 = new Action(new ActionKey("1", 1, "1"), 1, "fwk.dummy",
//                "dummy", "dummy", "", "", "", "", "",
//                "0", actionParameters1);
//        action2 = new Action(new ActionKey("1", 1, "2"), 2, "fwk.dummy",
//                "dummy", "dummy", "", "", "", "", "",
//                "0", actionParameters2);

        action1 = new ActionBuilder("1", 1, "1")
                .numberOfParameters(2)
                .build();
        action2 = new ActionBuilder("1", 1, "2")
                .numberOfParameters(2)
                .build();
        actionParameter11 = action1.getParameters().get(0);
        actionParameter12 = action1.getParameters().get(1);
        actionParameter21 = action2.getParameters().get(0);
        actionParameter22 = action2.getParameters().get(1);
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
    public void actionExistsOnlyTest() throws MetadataAlreadyExistsException {
        ActionConfiguration.getInstance().insert(action1);
        assertTrue(ActionConfiguration.getInstance().exists(action1.getMetadataKey()));
        assertTrue(ActionParameterConfiguration.getInstance().exists(actionParameter11.getMetadataKey()));
        assertTrue(ActionParameterConfiguration.getInstance().exists(actionParameter12.getMetadataKey()));
    }

    @Test
    public void actionExistsTest() throws MetadataAlreadyExistsException {
        ActionConfiguration.getInstance().insert(action1);
        ActionConfiguration.getInstance().insert(action2);
        assertTrue(ActionConfiguration.getInstance().exists(action1.getMetadataKey()));
        assertTrue(ActionParameterConfiguration.getInstance().exists(actionParameter11.getMetadataKey()));
        assertTrue(ActionParameterConfiguration.getInstance().exists(actionParameter12.getMetadataKey()));
    }

    @Test
    public void actionInsertOnlyTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionParameterConfiguration.getInstance().getAll().size());
        ActionConfiguration.getInstance().insert(action1);

        assertTrue(ActionConfiguration.getInstance().get(action1.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList()), ActionConfiguration.getInstance().get(action1.getMetadataKey()).get().getParameters());
        assertEquals(action1, ActionConfiguration.getInstance().get(action1.getMetadataKey()).get());
    }

    @Test
    public void actionInsertTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionParameterConfiguration.getInstance().getAll().size());
        ActionConfiguration.getInstance().insert(action1);
        ActionConfiguration.getInstance().insert(action2);

        assertTrue(ActionConfiguration.getInstance().get(action1.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList()), ActionConfiguration.getInstance().get(action1.getMetadataKey()).get().getParameters());
        assertEquals(action1, ActionConfiguration.getInstance().get(action1.getMetadataKey()).get());
        assertTrue(ActionConfiguration.getInstance().get(action2.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter21, actionParameter22).collect(Collectors.toList()), ActionConfiguration.getInstance().get(action2.getMetadataKey()).get().getParameters());
        assertEquals(action2, ActionConfiguration.getInstance().get(action2.getMetadataKey()).get());
    }

    @Test
    public void actionInsertAlreadyExistsOnlyTest() throws MetadataAlreadyExistsException {
        ActionConfiguration.getInstance().insert(action1);
        assertThrows(MetadataAlreadyExistsException.class,() -> ActionConfiguration.getInstance().insert(action1));
    }
    @Test
    public void actionInsertAlreadyExistsTest() throws MetadataAlreadyExistsException {
        ActionConfiguration.getInstance().insert(action1);
        ActionConfiguration.getInstance().insert(action2);
        assertThrows(MetadataAlreadyExistsException.class,() -> ActionConfiguration.getInstance().insert(action1));
    }

    @Test
    public void actionDeleteOnlyTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionParameterConfiguration.getInstance().getAll().size());
        ActionConfiguration.getInstance().insert(action1);

        assertEquals(1, ActionConfiguration.getInstance().getAll().size());
        assertEquals(2, ActionParameterConfiguration.getInstance().getAll().size());

        ActionConfiguration.getInstance().delete(action1.getMetadataKey());
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    public void actionDeleteTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionParameterConfiguration.getInstance().getAll().size());
        ActionConfiguration.getInstance().insert(action1);
        ActionConfiguration.getInstance().insert(action2);

        assertEquals(2, ActionConfiguration.getInstance().getAll().size());
        assertEquals(4, ActionParameterConfiguration.getInstance().getAll().size());

        ActionConfiguration.getInstance().delete(action1.getMetadataKey());
        assertEquals(1, ActionConfiguration.getInstance().getAll().size());
        assertEquals(2, ActionParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    public void actionDeleteDoesNotExistOnlyTest() {
        assertThrows(MetadataDoesNotExistException.class,() -> ActionConfiguration.getInstance().delete(action1.getMetadataKey()));
    }

    @Test
    public void actionDeleteDoesNotExistTest() throws MetadataAlreadyExistsException {
        ActionConfiguration.getInstance().insert(action2);
        assertThrows(MetadataDoesNotExistException.class,() -> ActionConfiguration.getInstance().delete(action1.getMetadataKey()));
    }

    @Test
    public void actionGetOnlyTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionParameterConfiguration.getInstance().getAll().size());
        ActionConfiguration.getInstance().insert(action1);

        assertTrue(ActionConfiguration.getInstance().get(action1.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList()), ActionConfiguration.getInstance().get(action1.getMetadataKey()).get().getParameters());
        assertEquals(action1, ActionConfiguration.getInstance().get(action1.getMetadataKey()).get());
    }

    @Test
    public void actionGetTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionParameterConfiguration.getInstance().getAll().size());
        ActionConfiguration.getInstance().insert(action1);
        ActionConfiguration.getInstance().insert(action2);

        assertTrue(ActionConfiguration.getInstance().get(action1.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList()), ActionConfiguration.getInstance().get(action1.getMetadataKey()).get().getParameters());
        assertEquals(action1, ActionConfiguration.getInstance().get(action1.getMetadataKey()).get());

        assertTrue(ActionConfiguration.getInstance().get(action2.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter21, actionParameter22).collect(Collectors.toList()), ActionConfiguration.getInstance().get(action2.getMetadataKey()).get().getParameters());
        assertEquals(action2, ActionConfiguration.getInstance().get(action2.getMetadataKey()).get());
    }

    @Test
    public void actionGetNotExistsOnlyTest(){
        assertFalse(ActionConfiguration.getInstance().exists(action1.getMetadataKey()));
    }

    @Test
    public void actionGetNotExistsTest() throws MetadataAlreadyExistsException {
        ActionConfiguration.getInstance().insert(action2);
        assertFalse(ActionConfiguration.getInstance().exists(action1.getMetadataKey()));
    }

    @Test
    public void actionUpdateOnlyTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ActionConfiguration.getInstance().insert(action1);
        Optional<Action> actionFetched = ActionConfiguration.getInstance().get(action1.getMetadataKey());

        assertTrue(actionFetched.isPresent());
        assertEquals("dummy", actionFetched.get().getDescription());

        action1.setDescription("new description");
        ActionConfiguration.getInstance().update(action1);

        actionFetched = ActionConfiguration.getInstance().get(action1.getMetadataKey());
        assertTrue(actionFetched.isPresent());
        assertEquals("new description", actionFetched.get().getDescription());
    }

    @Test
    public void actionUpdateTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ActionConfiguration.getInstance().insert(action1);
        ActionConfiguration.getInstance().insert(action2);
        Optional<Action> actionFetched = ActionConfiguration.getInstance().get(action1.getMetadataKey());

        assertTrue(actionFetched.isPresent());
        assertEquals("dummy", actionFetched.get().getDescription());

        action1.setDescription("new description");
        ActionConfiguration.getInstance().update(action1);

        actionFetched = ActionConfiguration.getInstance().get(action1.getMetadataKey());
        assertTrue(actionFetched.isPresent());
        assertEquals("new description", actionFetched.get().getDescription());
    }

    @Test
    public void actionGetAllTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());

        ActionConfiguration.getInstance().insert(action1);
        ActionConfiguration.getInstance().insert(action2);

        assertEquals(2, ActionConfiguration.getInstance().getAll().size());
        assertEquals(4, ActionParameterConfiguration.getInstance().getAll().size());
        assertEquals(Stream.of(action1, action2).collect(Collectors.toList()), ActionConfiguration.getInstance().getAll());
    }

    @Test
    public void actionDeleteFromScriptOnlyTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());

        ActionConfiguration.getInstance().insert(action1);
        ActionConfiguration.getInstance().insert(action2);

        assertEquals(2, ActionConfiguration.getInstance().getAll().size());
        assertEquals(4, ActionParameterConfiguration.getInstance().getAll().size());
        assertEquals(Stream.of(action1, action2).collect(Collectors.toList()), ActionConfiguration.getInstance().getAll());

        ActionConfiguration.getInstance().deleteActionsFromScript("1", 1);

        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    public void actionDeleteFromScriptTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        Action action = new Action(new ActionKey("1", 2, "2"), 2, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", Stream.of(new ActionParameter(new ActionParameterKey("1", 1, "2", "name"), "value")).collect(Collectors.toList()));

        ActionConfiguration.getInstance().insert(action1);
        ActionConfiguration.getInstance().insert(action2);
        ActionConfiguration.getInstance().insert(action);

        assertEquals(3, ActionConfiguration.getInstance().getAll().size());
        assertEquals(5, ActionParameterConfiguration.getInstance().getAll().size());
        assertEquals(Stream.of(action1, action2, action).collect(Collectors.toList()), ActionConfiguration.getInstance().getAll());

        ActionConfiguration.getInstance().deleteActionsFromScript("1", 1);

        assertEquals(1, ActionConfiguration.getInstance().getAll().size());
        assertEquals(1, ActionParameterConfiguration.getInstance().getAll().size());
    }

}
