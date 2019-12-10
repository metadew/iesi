package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptVersionConfiguration;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class ScriptConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private Script script;
    ScriptVersion scriptVersion;

    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();

        List<Action> actions = new ArrayList<>();
        actions.add(new Action(new ActionKey("1", 1, "1"), 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>()));
        scriptVersion = new ScriptVersion(new ScriptVersionKey("1", 1),
                "version of script");
        script = new Script(new ScriptKey("1"), "script", "testScriptExist",
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
        List<Action> actions = new ArrayList<>();
        actions.add(new Action(new ActionKey("2", 1, "1"), 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>()));
        ScriptVersion scriptVersion = new ScriptVersion(new ScriptVersionKey("2", 1),
                "version of script");
        Script script = new Script(new ScriptKey("2"), "script", "testScriptInsert",
                "script for testing", scriptVersion,
                new ArrayList<>(), actions);
        ScriptConfiguration.getInstance().insert(script);
        int nbAfter = ScriptConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

}
