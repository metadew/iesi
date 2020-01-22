package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScriptVersionConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private ScriptVersion scriptVersion1;
    private ScriptVersion scriptVersion2;

    @Before
    public void setup() {
        designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();

        scriptVersion1 = new ScriptVersionBuilder("1", 1)
                .description("version of script")
                .build();
        scriptVersion2 = new ScriptVersionBuilder("1", 1)
                .description("version of script")
                .build();


    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    public void scriptVersionNotExistTest(){
        assertFalse(ScriptVersionConfiguration.getInstance().exists(scriptVersion1));
    }

    @Test
    public void scriptVersionExistsTest() throws MetadataAlreadyExistsException {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        assertTrue(ScriptVersionConfiguration.getInstance().exists(scriptVersion1.getMetadataKey()));
    }

    @Test
    public void scriptVersionInsertTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ScriptVersionConfiguration.getInstance().getAll().size());
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);

        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
        assertTrue(ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey()).isPresent());
        assertEquals(scriptVersion1, ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey()).get());
    }
    @Test
    public void scriptVersionInsertMultipleTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ScriptVersionConfiguration.getInstance().getAll().size());
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        ScriptVersionConfiguration.getInstance().insert(scriptVersion2);

        assertEquals(2, ScriptVersionConfiguration.getInstance().getAll().size());
        assertTrue(ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey()).isPresent());
        assertEquals(scriptVersion1, ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey()).get());
        assertTrue(ScriptVersionConfiguration.getInstance().get(scriptVersion2.getMetadataKey()).isPresent());
        assertEquals(scriptVersion2, ScriptVersionConfiguration.getInstance().get(scriptVersion2.getMetadataKey()).get());
    }

    @Test
    public void scriptVersionInsertAlreadyExistsTest() throws MetadataAlreadyExistsException {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        assertThrows(MetadataAlreadyExistsException.class,() -> ScriptVersionConfiguration.getInstance().insert(scriptVersion1));
    }

    @Test
    public void scriptVersionDeleteTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);

        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
        ScriptVersionConfiguration.getInstance().delete(scriptVersion1.getMetadataKey());
        assertEquals(0, ScriptVersionConfiguration.getInstance().getAll().size());

    }

    @Test
    public void scriptVersionDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        assertThrows(MetadataDoesNotExistException.class,() -> ScriptVersionConfiguration.getInstance().delete(scriptVersion1.getMetadataKey()));
    }

    @Test
    public void scriptVersionGetTest() throws MetadataAlreadyExistsException {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);

        Optional<ScriptVersion> fetchedScriptVersion = ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey());
        assertTrue(fetchedScriptVersion.isPresent());
        assertEquals(scriptVersion1, fetchedScriptVersion.get());
    }

    @Test
    public void scriptVersionGetNotExistsTest(){
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey("3", 1);
        assertFalse(ScriptVersionConfiguration.getInstance().exists(scriptVersionKey));
        assertFalse(ScriptVersionConfiguration.getInstance().get(scriptVersionKey).isPresent());
    }

    @Test
    public void scriptUpdateTest() throws MetadataDoesNotExistException {
        ScriptVersion scriptVersionUpdate = scriptVersion1;
        String newDescription = "new description";
        scriptVersionUpdate.setDescription(newDescription);
        ScriptVersionConfiguration.getInstance().update(scriptVersionUpdate);
        Optional<ScriptVersion> checkScriptVersion = ScriptVersionConfiguration.getInstance().get(scriptVersionUpdate.getMetadataKey());
        assertEquals(checkScriptVersion.get().getDescription(), newDescription);
    }

    private ScriptVersion createScriptVersion(){
        return new ScriptVersion(new ScriptVersionKey("2", 1),
                "version of script");
    }
}
