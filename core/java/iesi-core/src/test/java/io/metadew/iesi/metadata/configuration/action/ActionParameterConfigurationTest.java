package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes =  { TestConfiguration.class, ActionParameterConfiguration.class })
@ActiveProfiles("test")
class ActionParameterConfigurationTest {

    ActionParameter actionParameter1;
    DesignMetadataRepository designMetadataRepository;
    ActionParameter actionParameter2;

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    ActionParameterConfiguration actionParameterConfiguration;

    @BeforeEach
    void setup() {
        actionParameter1 = new ActionParameterBuilder("1", 1, "1", "firstParameter")
                .value("parameter value")
                .build();
        actionParameter2 = new ActionParameterBuilder("1", 1, "1", "secondParameter")
                .value("parameter value")
                .build();

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void actionParameterNotExistsOnlyTest() {
        assertFalse(actionParameterConfiguration.exists(actionParameter1));
    }

    @Test
    void actionParameterNotExistsTest() {
        actionParameterConfiguration.insert(actionParameter2);
        assertFalse(actionParameterConfiguration.exists(actionParameter1));
    }

    @Test
    void actionParameterExistsOnlyTest() {
        actionParameterConfiguration.insert(actionParameter1);
        assertTrue(actionParameterConfiguration.exists(actionParameter1.getMetadataKey()));
    }

    @Test
    void actionParameterExistsTest() {
        actionParameterConfiguration.insert(actionParameter1);
        actionParameterConfiguration.insert(actionParameter2);
        assertTrue(actionParameterConfiguration.exists(actionParameter1.getMetadataKey()));
        assertTrue(actionParameterConfiguration.exists(actionParameter2.getMetadataKey()));
    }

    @Test
    void actionParameterInsertOnlyTest() {
        assertEquals(0, actionParameterConfiguration.getAll().size());

        actionParameterConfiguration.insert(actionParameter1);

        assertEquals(1, actionParameterConfiguration.getAll().size());
        assertTrue(actionParameterConfiguration.get(actionParameter1.getMetadataKey()).isPresent());
        assertEquals(actionParameter1, actionParameterConfiguration.get(actionParameter1.getMetadataKey()).get());
    }

    @Test
    void actionParameterInsertTest() {
        actionParameterConfiguration.insert(actionParameter2);
        assertEquals(1, actionParameterConfiguration.getAll().size());
        actionParameterConfiguration.insert(actionParameter1);

        assertTrue(actionParameterConfiguration.get(actionParameter1.getMetadataKey()).isPresent());
        assertEquals(actionParameter1, actionParameterConfiguration.get(actionParameter1.getMetadataKey()).get());
        assertEquals(2, actionParameterConfiguration.getAll().size());
    }

    @Test
    void actionParameterInsertAlreadyExistsTest() {
        actionParameterConfiguration.insert(actionParameter1);
        assertThrows(MetadataAlreadyExistsException.class, () -> actionParameterConfiguration.insert(actionParameter1));
    }

    @Test
    void actionDeleteTest() {
        actionParameterConfiguration.insert(actionParameter1);
        assertEquals(1, actionParameterConfiguration.getAll().size());
        actionParameterConfiguration.delete(actionParameter1.getMetadataKey());
        assertEquals(0, actionParameterConfiguration.getAll().size());
    }

    @Test
    void actionDeleteDoesNotExistOnlyTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> actionParameterConfiguration.delete(actionParameter1.getMetadataKey()));
    }

    @Test
    void setActionParameterGetOnlyTest() {
        actionParameterConfiguration.insert(actionParameter1);

        Optional<ActionParameter> actionParameter1Fetched = actionParameterConfiguration.get(actionParameter1.getMetadataKey());
        assertTrue(actionParameter1Fetched.isPresent());
        assertEquals(actionParameter1, actionParameter1Fetched.get());
    }

    @Test
    void actionDeleteDoesNotExistTest() {
        actionParameterConfiguration.insert(actionParameter2);
        assertThrows(MetadataDoesNotExistException.class, () -> actionParameterConfiguration.delete(actionParameter1.getMetadataKey()));
    }

    @Test
    void setActionParameterGetTest() {
        actionParameterConfiguration.insert(actionParameter1);
        actionParameterConfiguration.insert(actionParameter2);

        Optional<ActionParameter> actionParameter1Fetched = actionParameterConfiguration.get(actionParameter1.getMetadataKey());
        assertTrue(actionParameter1Fetched.isPresent());
        assertEquals(actionParameter1, actionParameter1Fetched.get());
    }

    @Test
    void actionParameterGetNotExistsTest() {
        actionParameterConfiguration.insert(actionParameter1);
        actionParameterConfiguration.insert(actionParameter2);

        ActionParameterKey newActionParameterKey = new ActionParameterKey("3", 4, "not exist", "test parameter");
        assertFalse(actionParameterConfiguration.get(newActionParameterKey).isPresent());
    }

    @Test
    void actionParameterUpdateOnlyTest() {
        actionParameterConfiguration.insert(actionParameter1);
        Optional<ActionParameter> actionParameterFetched = actionParameterConfiguration.get(actionParameter1.getMetadataKey());
        assertTrue(actionParameterFetched.isPresent());
        assertEquals("parameter value", actionParameter1.getValue());

        actionParameter1.setValue("new value");
        actionParameterConfiguration.update(actionParameter1);

        actionParameterFetched = actionParameterConfiguration.get(actionParameter1.getMetadataKey());
        assertTrue(actionParameterFetched.isPresent());
        assertEquals("new value", actionParameter1.getValue());
    }

    @Test
    void actionParameterUpdateTest() {
        actionParameterConfiguration.insert(actionParameter1);
        actionParameterConfiguration.insert(actionParameter2);
        Optional<ActionParameter> actionParameterFetched = actionParameterConfiguration.get(actionParameter1.getMetadataKey());
        assertTrue(actionParameterFetched.isPresent());
        assertEquals("parameter value", actionParameter1.getValue());

        actionParameter1.setValue("new value");
        actionParameterConfiguration.update(actionParameter1);

        actionParameterFetched = actionParameterConfiguration.get(actionParameter1.getMetadataKey());
        assertTrue(actionParameterFetched.isPresent());
        assertEquals("new value", actionParameter1.getValue());
    }
}
