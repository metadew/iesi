package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
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

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class ScriptConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;

    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
//        configurationTestSetup.executeSetup("DesignObjects.json", "DesignTables.json");
//
//        configurationTestSetup.getDesignMetadataRepository().setMetadataObjects(
//                configurationTestSetup.getMetadataObjects()
//        );
//        configurationTestSetup.getDesignMetadataRepository().setMetadataTables(
//                configurationTestSetup.getMetadataTables()
//        );
//        configurationTestSetup.getDesignMetadataRepository().createAllTables();
//        scriptConfiguration.setMetadataRepository(configurationTestSetup.getDesignMetadataRepository());
    }

    @After
    public void clearDatabase(){
        designMetadataRepository.cleanAllTables();
        // configurationTestSetup.getDesignMetadataRepository().dropAllTables();
    }

    @Test
    public void scriptNotExistsTest() {
        assertFalse(ScriptConfiguration.getInstance().exists("testScript", 0));
    }

    @Test
    public void scriptExistsTest() throws ScriptAlreadyExistsException{
        List<Action> actions = new ArrayList<>();
        actions.add(new Action(new ActionKey("1", 1, "1"), 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", new ArrayList<>()));
        ScriptVersion scriptVersion = new ScriptVersion(new ScriptVersionKey("1", 1),
                "version of script");
        Script script = new Script(new ScriptKey("1"), "script", "testScriptExist",
                "script for testing", scriptVersion,
                new ArrayList<>(), actions);
        ScriptConfiguration.getInstance().insert(script);
        assertTrue(ScriptConfiguration.getInstance().exists("testScriptExist", 1));
        }

    }
