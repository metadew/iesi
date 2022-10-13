package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, ActionConfiguration.class, ActionParameterConfiguration.class })
@ActiveProfiles("test")
class DesignDatasetConfigurationTest {

    ActionParameter actionParameter11;
    ActionParameter actionParameter12;
    Action action1;
    Action action2;
    ActionParameter actionParameter21;
    ActionParameter actionParameter22;
    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    ActionConfiguration actionConfiguration;

    @Autowired
    ActionParameterConfiguration actionParameterConfiguration;

    @BeforeEach
    void setup() {
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

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void actionNotExistsTest() {
        ActionKey nonExistActionKey = new ActionKey(new ScriptKey("non_exist", 2L), "non");
        assertFalse(actionConfiguration.exists(nonExistActionKey));
    }

    @Test
    void actionExistsOnlyTest() {
        actionConfiguration.insert(action1);
        assertTrue(actionConfiguration.exists(action1.getMetadataKey()));
        assertTrue(actionParameterConfiguration.exists(actionParameter11.getMetadataKey()));
        assertTrue(actionParameterConfiguration.exists(actionParameter12.getMetadataKey()));
    }

    @Test
    void actionExistsTest() {
        actionConfiguration.insert(action1);
        actionConfiguration.insert(action2);
        assertTrue(actionConfiguration.exists(action1.getMetadataKey()));
        assertTrue(actionParameterConfiguration.exists(actionParameter11.getMetadataKey()));
        assertTrue(actionParameterConfiguration.exists(actionParameter12.getMetadataKey()));
    }

    @Test
    void actionInsertOnlyTest() {
        assertEquals(0, actionConfiguration.getAll().size());
        assertEquals(0, actionParameterConfiguration.getAll().size());
        actionConfiguration.insert(action1);

        assertTrue(actionConfiguration.get(action1.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList()), actionConfiguration.get(action1.getMetadataKey()).get().getParameters());
        assertEquals(action1, actionConfiguration.get(action1.getMetadataKey()).get());
    }

    @Test
    void actionInsertTest() {
        assertEquals(0, actionConfiguration.getAll().size());
        assertEquals(0, actionParameterConfiguration.getAll().size());
        actionConfiguration.insert(action1);
        actionConfiguration.insert(action2);

        assertTrue(actionConfiguration.get(action1.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList()), actionConfiguration.get(action1.getMetadataKey()).get().getParameters());
        assertEquals(action1, actionConfiguration.get(action1.getMetadataKey()).get());
        assertTrue(actionConfiguration.get(action2.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter21, actionParameter22).collect(Collectors.toList()), actionConfiguration.get(action2.getMetadataKey()).get().getParameters());
        assertEquals(action2, actionConfiguration.get(action2.getMetadataKey()).get());
    }

    @Test
    void actionInsertAlreadyExistsOnlyTest() {
        actionConfiguration.insert(action1);
        assertThrows(MetadataAlreadyExistsException.class, () -> actionConfiguration.insert(action1));
    }

    @Test
    void actionInsertAlreadyExistsTest() {
        actionConfiguration.insert(action1);
        actionConfiguration.insert(action2);
        assertThrows(MetadataAlreadyExistsException.class, () -> actionConfiguration.insert(action1));
    }

    @Test
    void actionDeleteOnlyTest() {
        assertEquals(0, actionConfiguration.getAll().size());
        assertEquals(0, actionParameterConfiguration.getAll().size());
        actionConfiguration.insert(action1);

        assertEquals(1, actionConfiguration.getAll().size());
        assertEquals(2, actionParameterConfiguration.getAll().size());

        actionConfiguration.delete(action1.getMetadataKey());
        assertEquals(0, actionConfiguration.getAll().size());
        assertEquals(0, actionParameterConfiguration.getAll().size());
    }

    @Test
    void actionDeleteTest() {
        assertEquals(0, actionConfiguration.getAll().size());
        assertEquals(0, actionParameterConfiguration.getAll().size());
        actionConfiguration.insert(action1);
        actionConfiguration.insert(action2);

        assertEquals(2, actionConfiguration.getAll().size());
        assertEquals(4, actionParameterConfiguration.getAll().size());

        actionConfiguration.delete(action1.getMetadataKey());
        assertEquals(1, actionConfiguration.getAll().size());
        assertEquals(2, actionParameterConfiguration.getAll().size());
    }

    @Test
    void actionDeleteDoesNotExistOnlyTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> actionConfiguration.delete(action1.getMetadataKey()));
    }

    @Test
    void actionDeleteDoesNotExistTest() {
        actionConfiguration.insert(action2);
        assertThrows(MetadataDoesNotExistException.class, () -> actionConfiguration.delete(action1.getMetadataKey()));
    }

    @Test
    void actionGetOnlyTest() {
        assertEquals(0, actionConfiguration.getAll().size());
        assertEquals(0, actionParameterConfiguration.getAll().size());
        actionConfiguration.insert(action1);

        assertTrue(actionConfiguration.get(action1.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList()), actionConfiguration.get(action1.getMetadataKey()).get().getParameters());
        assertEquals(action1, actionConfiguration.get(action1.getMetadataKey()).get());
    }

    @Test
    void actionGetTest() {
        assertEquals(0, actionConfiguration.getAll().size());
        assertEquals(0, actionParameterConfiguration.getAll().size());
        actionConfiguration.insert(action1);
        actionConfiguration.insert(action2);

        assertTrue(actionConfiguration.get(action1.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter11, actionParameter12).collect(Collectors.toList()), actionConfiguration.get(action1.getMetadataKey()).get().getParameters());
        assertEquals(action1, actionConfiguration.get(action1.getMetadataKey()).get());

        assertTrue(actionConfiguration.get(action2.getMetadataKey()).isPresent());
        assertEquals(Stream.of(actionParameter21, actionParameter22).collect(Collectors.toList()), actionConfiguration.get(action2.getMetadataKey()).get().getParameters());
        assertEquals(action2, actionConfiguration.get(action2.getMetadataKey()).get());
    }

    @Test
    void actionGetNotExistsOnlyTest() {
        assertFalse(actionConfiguration.exists(action1.getMetadataKey()));
    }

    @Test
    void actionGetNotExistsTest() {
        actionConfiguration.insert(action2);
        assertFalse(actionConfiguration.exists(action1.getMetadataKey()));
    }

    @Test
    void actionUpdateOnlyTest() {
        actionConfiguration.insert(action1);
        Optional<Action> actionFetched = actionConfiguration.get(action1.getMetadataKey());

        assertTrue(actionFetched.isPresent());
        assertEquals("dummy", actionFetched.get().getDescription());

        action1.setDescription("new description");
        actionConfiguration.update(action1);

        actionFetched = actionConfiguration.get(action1.getMetadataKey());
        assertTrue(actionFetched.isPresent());
        assertEquals("new description", actionFetched.get().getDescription());
    }

    @Test
    void actionUpdateTest() {
        actionConfiguration.insert(action1);
        actionConfiguration.insert(action2);
        Optional<Action> actionFetched = actionConfiguration.get(action1.getMetadataKey());

        assertTrue(actionFetched.isPresent());
        assertEquals("dummy", actionFetched.get().getDescription());

        action1.setDescription("new description");
        actionConfiguration.update(action1);

        actionFetched = actionConfiguration.get(action1.getMetadataKey());
        assertTrue(actionFetched.isPresent());
        assertEquals("new description", actionFetched.get().getDescription());
    }

    @Test
    void actionGetAllTest() {
        assertEquals(0, actionConfiguration.getAll().size());

        actionConfiguration.insert(action1);
        actionConfiguration.insert(action2);

        assertEquals(2, actionConfiguration.getAll().size());
        assertEquals(4, actionParameterConfiguration.getAll().size());
        assertEquals(Stream.of(action1, action2).collect(Collectors.toList()), actionConfiguration.getAll());
    }

    @Test
    void actionDeleteFromScriptOnlyTest() {
        assertEquals(0, actionConfiguration.getAll().size());

        actionConfiguration.insert(action1);
        actionConfiguration.insert(action2);

        assertEquals(2, actionConfiguration.getAll().size());
        assertEquals(4, actionParameterConfiguration.getAll().size());
        assertEquals(Stream.of(action1, action2).collect(Collectors.toList()), actionConfiguration.getAll());

        actionConfiguration.deleteByScript(new ScriptKey("1", 1));

        assertEquals(0, actionConfiguration.getAll().size());
        assertEquals(0, actionParameterConfiguration.getAll().size());
    }

    @Test
    void actionDeleteFromScriptTest() {
        assertEquals(0, actionConfiguration.getAll().size());

        Action action = new ActionBuilder("1", 2, "2")
                .numberOfParameters(1)
                .build();

        actionConfiguration.insert(action1);
        actionConfiguration.insert(action2);
        actionConfiguration.insert(action);

        assertEquals(3, actionConfiguration.getAll().size());
        assertEquals(5, actionParameterConfiguration.getAll().size());
        assertEquals(Stream.of(action1, action2, action).collect(Collectors.toList()), actionConfiguration.getAll());

        actionConfiguration.deleteByScript(new ScriptKey("1", 1));

        assertEquals(1, actionConfiguration.getAll().size());
        assertEquals(1, actionParameterConfiguration.getAll().size());
    }

}
