package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScriptConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private Script script11;
    private Script script12;
    private Script script2;

    @BeforeEach
    void setup() {
        designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        designMetadataRepository.createAllTables();
//        script11 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("script1"), 1)
//                .name("script1")
//                .numberOfActions(2)
//                .numberOfParameters(2)
//                .build();
        List<ActionParameter> actionParameterList = new ArrayList<>();
        ActionParameter actionParameter = ActionParameter.builder()
                .actionParameterKey(ActionParameterKey.builder().actionKey(ActionKey.builder().scriptKey(ScriptKey.builder().scriptId("id").scriptVersion(1L)
                        .build()).actionId("id").build()).parameterName("param").build()).value("value").build();
        actionParameterList.add(actionParameter);
        Action action = Action.builder().actionKey(ActionKey.builder().actionId("id").scriptKey(ScriptKey.builder().scriptVersion(1L).scriptId("id").build()).build())
                .component("component").condition("condition").errorExpected("error").description("desc").errorStop("stop").iteration("ite").name("name").number(1L).retries("2").type("type")
                .parameters(actionParameterList)
                .build();
        List<Action> actions = new ArrayList<>();
        actions.add(action);
        List<ScriptLabel> labels =  new ArrayList<>();
        ScriptLabel scriptLabel = ScriptLabel.builder().scriptLabelKey(ScriptLabelKey.builder().id("id").build()).scriptKey(ScriptKey.builder().scriptVersion(1L).scriptId("id").build())
                .name("name").value("value").build();
        labels.add(scriptLabel);
        List<ScriptParameter> parameters = new ArrayList<>();
        ScriptParameter scriptParameter = ScriptParameter.builder().scriptParameterKey(ScriptParameterKey.builder().parameterName("param").scriptKey(ScriptKey.builder()
                .scriptId("id").scriptVersion(1L).build()).build()).build();
        parameters.add(scriptParameter);
        ScriptVersion version = ScriptVersion.builder().description("desc").scriptVersionKey(ScriptVersionKey.builder().scriptKey(ScriptKey.builder()
                .scriptVersion(1L).scriptId("id").build()).build()).build();
        script11 = Script.builder().scriptKey(ScriptKey.builder().scriptId("id").scriptVersion(1L).build()).actions(actions).description("desc").labels(labels).name("name")
                .parameters(parameters).version(version).build();
        script12 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("scriptf1"), 2)
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        script2 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("dummy"), 1)
                .numberOfActions(3)
                .numberOfParameters(3)
                .build();
    }

    @AfterEach
    void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    void scriptNotExistsKeyTest() {
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getMetadataKey()));
    }

    @Test
    void scriptNotExistsNameTest() {
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getName()));
    }

    @Test
    void scriptNotExistsKeyMultipleTest() {
        ScriptConfiguration.getInstance().insert(script12);
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getMetadataKey()));
    }

    @Test
    void scriptNotExistsNameMultipleTest() {
        ScriptConfiguration.getInstance().insert(script2);
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getName()));
    }

    @Test
    void scriptExistsKeyTest() {
        ScriptConfiguration.getInstance().insert(script11);
        assertTrue(ScriptConfiguration.getInstance().exists(script11.getMetadataKey()));
    }

    @Test
    void scriptExistsNameTest() {
        ScriptConfiguration.getInstance().insert(script11);
        assertTrue(ScriptConfiguration.getInstance().exists(script11.getName()));
    }

    @Test
    void scriptInsertTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
        Assertions.assertEquals(script11, scriptFetched.get());
    }

    @Test
    void scriptInsertMultipleVersionsTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
        assertEquals(script11, ScriptConfiguration.getInstance().get(script11.getMetadataKey()).get());
        assertTrue(ScriptConfiguration.getInstance().get(script12.getMetadataKey()).isPresent());
        assertEquals(script12, ScriptConfiguration.getInstance().get(script12.getMetadataKey()).get());
    }

    @Test
    void scriptInsertMultipleScriptsTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
        assertEquals(script11, ScriptConfiguration.getInstance().get(script11.getMetadataKey()).get());
        assertTrue(ScriptConfiguration.getInstance().get(script2.getMetadataKey()).isPresent());
        assertEquals(script2, ScriptConfiguration.getInstance().get(script2.getMetadataKey()).get());
    }

    @Test
    void scriptInsertAlreadyExistsTest() {
        ScriptConfiguration.getInstance().insert(script11);
        assertThrows(MetadataAlreadyExistsException.class, () -> ScriptConfiguration.getInstance().insert(script11));
    }

    @Test
    void scriptDeleteOnlyTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
    }

    @Test
    void scriptDeleteMultipleVersionTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
    }

    @Test
    void scriptDeleteMultipleTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
    }

    @Test
    void scriptDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> ScriptConfiguration.getInstance().delete(script11.getMetadataKey()));
    }

    @Test
    void scriptDeleteDoesNotExistMultipleVersionsTest() {
        ScriptConfiguration.getInstance().insert(script12);
        assertThrows(MetadataDoesNotExistException.class, () -> ScriptConfiguration.getInstance().delete(script11.getMetadataKey()));
    }

    @Test
    void scriptGetTest() {
        ScriptConfiguration.getInstance().insert(script11);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    void scriptGetMultipleTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    void scriptGetMultipleVersionsTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    void scriptGetByNameTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        List<Script> scriptFetched = ScriptConfiguration.getInstance().getByName("script1");
        assertEquals(Stream.of(script11, script12).collect(Collectors.toList()), scriptFetched);
    }

    @Test
    void scriptGetLatestVersionTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().getLatestVersion("script1");
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    void scriptGetNotExistsTest() {
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getMetadataKey()));
        assertFalse(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
    }

    @Test
    void scriptUpdateTest() {
        ScriptConfiguration.getInstance().insert(script11);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals("dummy", scriptFetched.get().getDescription());

        script11.setDescription("new description");
        ScriptConfiguration.getInstance().update(script11);

        scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals("new description", scriptFetched.get().getDescription());
    }

    @Test
    void scriptUpdateMultipleVersionsTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        Optional<Script> script11Fetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(script11Fetched.isPresent());
        assertEquals("dummy", script11Fetched.get().getDescription());
        Optional<Script> script12Fetched = ScriptConfiguration.getInstance().get(script12.getMetadataKey());
        assertTrue(script12Fetched.isPresent());
        assertEquals("dummy", script12Fetched.get().getDescription());

        script11.setDescription("new description");
        script12.setDescription("new description");
        ScriptConfiguration.getInstance().update(script11);
        ScriptConfiguration.getInstance().update(script12);

        script11Fetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(script11Fetched.isPresent());
        assertEquals("new description", script11Fetched.get().getDescription());
        script12Fetched = ScriptConfiguration.getInstance().get(script12.getMetadataKey());
        assertTrue(script12Fetched.isPresent());
        assertEquals("new description", script12Fetched.get().getDescription());
    }

}
