package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
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

public class ScriptConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private Script script11;
    private Script script12;
    private Script script2;

    @Before
    public void setup() {
        designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();

        script11 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("script1"), 1)
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        script12 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("script1"), 2)
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        script2 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("dummy"), 1)
                .numberOfActions(3)
                .numberOfParameters(3)
                .build();
    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    public void scriptNotExistsKeyTest() {
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getMetadataKey()));
    }

    @Test
    public void scriptNotExistsNameTest() {
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getName()));
    }

    @Test
    public void scriptNotExistsKeyMultipleTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script12);
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getMetadataKey()));
    }

    @Test
    public void scriptNotExistsNameMultipleTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script2);
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getName()));
    }

    @Test
    public void scriptExistsKeyTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script11);
        assertTrue(ScriptConfiguration.getInstance().exists(script11.getMetadataKey()));
    }

    @Test
    public void scriptExistsNameTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script11);
        assertTrue(ScriptConfiguration.getInstance().exists(script11.getName()));
    }

    @Test
    public void scriptInsertTest() throws ScriptAlreadyExistsException {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
        assertEquals(script11, ScriptConfiguration.getInstance().get(script11.getMetadataKey()).get());
        assertEquals(2, ActionConfiguration.getInstance().getAll().size());
        assertEquals(2, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
    }

    @Test
    public void scriptInsertMultipleVersionsTest() throws ScriptAlreadyExistsException {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
        assertEquals(script11, ScriptConfiguration.getInstance().get(script11.getMetadataKey()).get());
        assertTrue(ScriptConfiguration.getInstance().get(script12.getMetadataKey()).isPresent());
        assertEquals(script12, ScriptConfiguration.getInstance().get(script12.getMetadataKey()).get());
        assertEquals(4, ActionConfiguration.getInstance().getAll().size());
        assertEquals(4, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(2, ScriptVersionConfiguration.getInstance().getAll().size());
    }
    @Test
    public void scriptInsertMultipleScriptsTest() throws ScriptAlreadyExistsException {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
        assertEquals(script11, ScriptConfiguration.getInstance().get(script11.getMetadataKey()).get());
        assertTrue(ScriptConfiguration.getInstance().get(script2.getMetadataKey()).isPresent());
        assertEquals(script2, ScriptConfiguration.getInstance().get(script2.getMetadataKey()).get());
        assertEquals(5, ActionConfiguration.getInstance().getAll().size());
        assertEquals(5, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(2, ScriptVersionConfiguration.getInstance().getAll().size());
    }

    @Test
    public void scriptInsertAlreadyExistsTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script11);
        assertThrows(ScriptAlreadyExistsException.class,() -> ScriptConfiguration.getInstance().insert(script11));
    }

    @Test
    public void scriptDeleteOnlyTest() throws MetadataDoesNotExistException, ScriptAlreadyExistsException {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
    }

    @Test
    public void scriptDeleteMultipleVersionTest() throws MetadataDoesNotExistException, ScriptAlreadyExistsException {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(2, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(2, ActionConfiguration.getInstance().getAll().size());
    }

    @Test
    public void scriptDeleteMultipleTest() throws MetadataDoesNotExistException, ScriptAlreadyExistsException {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(3, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(3, ActionConfiguration.getInstance().getAll().size());
    }

    @Test
    public void scriptDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() -> ScriptConfiguration.getInstance().delete(script11.getMetadataKey()));
    }

    @Test
    public void scriptDeleteDoesNotExistMultipleVersionsTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script12);
        assertThrows(MetadataDoesNotExistException.class,() -> ScriptConfiguration.getInstance().delete(script11.getMetadataKey()));
    }

    @Test
    public void scriptGetTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script11);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    public void scriptGetMultipleTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    public void scriptGetMultipleVersionsTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    public void scriptGetByNameTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        List<Script> scriptFetched = ScriptConfiguration.getInstance().getByName("script1");
        assertEquals(Stream.of(script11, script12).collect(Collectors.toList()), scriptFetched);
    }

    @Test
    public void scriptGetLatestVersionTest() throws ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().getLatestVersion("script1");
        assertTrue(scriptFetched.isPresent());
        assertEquals(script12, scriptFetched.get());
    }

    @Test
    public void scriptGetNotExistsTest(){
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getMetadataKey()));
        assertFalse(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
    }

    @Test
    public void scriptUpdateTest() throws MetadataDoesNotExistException, ScriptAlreadyExistsException {
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
    public void scriptUpdateMultipleVersionsTest() throws MetadataDoesNotExistException, ScriptAlreadyExistsException {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        Optional<Script> script11Fetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(script11Fetched.isPresent());
        assertEquals("dummy", script11Fetched.get().getDescription());
        Optional<Script> script12Fetched = ScriptConfiguration.getInstance().get(script12.getMetadataKey());
        assertTrue(script12Fetched.isPresent());
        assertEquals("dummy", script12Fetched.get().getDescription());

        script11.setDescription("new description");
        ScriptConfiguration.getInstance().update(script11);

        script11Fetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(script11Fetched.isPresent());
        assertEquals("new description", script11Fetched.get().getDescription());
        script12Fetched = ScriptConfiguration.getInstance().get(script12.getMetadataKey());
        assertTrue(script12Fetched.isPresent());
        assertEquals("new description", script12Fetched.get().getDescription());
    }

}
