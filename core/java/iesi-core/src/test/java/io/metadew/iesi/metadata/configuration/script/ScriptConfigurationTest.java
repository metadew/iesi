package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
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
        script11 = new ScriptBuilder("script1", 1)
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        script12 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("script2"), 2)
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        script2 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("script1"), 1)
                .name("scriptTest")
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
        assertEquals(scriptFetched.get().toString(), script11.toString());
    }

    @Test
    void scriptInsertMultipleVersionsTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);
        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        Optional<Script> scriptFetched2 = ScriptConfiguration.getInstance().get(script12.getMetadataKey());
        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
        assertEquals(scriptFetched.get().toString(), script11.toString());
        assertTrue(ScriptConfiguration.getInstance().get(script12.getMetadataKey()).isPresent());
        assertEquals(scriptFetched2.get().toString(), script12.toString());
    }

    @Test
    void scriptInsertMultipleScriptsTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);
        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        Optional<Script> scriptFetched2 = ScriptConfiguration.getInstance().get(script2.getMetadataKey());
        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
        assertTrue(ScriptConfiguration.getInstance().get(script2.getMetadataKey()).isPresent());
        assertEquals(scriptFetched.get().toString(), script11.toString());
        assertEquals(scriptFetched2.get().toString(), script2.toString());
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
        assertEquals(scriptFetched.get().toString(), script11.toString());
    }

    @Test
    void scriptGetMultipleTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
    }

    @Test
    void scriptGetMultipleVersionsTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        Optional<Script> scriptFetched2 = ScriptConfiguration.getInstance().get(script12.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(scriptFetched.get().toString(), script11.toString());
        assertEquals(scriptFetched2.get().toString(), script12.toString());
    }

    @Test
    void scriptGetByNameTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);
        ScriptConfiguration.getInstance().insert(script2);
        List<Script> scriptFetched = ScriptConfiguration.getInstance().getByName("scriptTest");
        assertEquals(1, scriptFetched.size());
    }

    @Test
    void scriptGetLatestVersionTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().getLatestVersion("script1");
        assertTrue(scriptFetched.isPresent());
        assertEquals(scriptFetched.get().toString(), script2.toString());
    }

    @Test
    void scriptGetNotExistsTest() {
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getMetadataKey()));
        assertThrows(MetadataDoesNotExistException.class,
                () -> {
                    ScriptConfiguration.getInstance().get(script11.getMetadataKey());
                });
    }

    @Test
    void scriptUpdateTest() {
        ScriptConfiguration.getInstance().insert(script11);
        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals("dummy", scriptFetched.get().getDescription());

        scriptFetched.get().setDescription("new description");
        ScriptConfiguration.getInstance().update(script11);

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

        script11Fetched.get().setDescription("new description");
        script12Fetched.get().setDescription("new description");
        ScriptConfiguration.getInstance().update(script11Fetched.get());
        ScriptConfiguration.getInstance().update(script12Fetched.get());

        assertTrue(script11Fetched.isPresent());
        assertEquals("new description", script11Fetched.get().getDescription());
        assertTrue(script12Fetched.isPresent());
        assertEquals("new description", script12Fetched.get().getDescription());
    }

}
