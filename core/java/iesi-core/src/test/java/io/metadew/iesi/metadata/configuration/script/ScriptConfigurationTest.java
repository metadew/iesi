package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScriptConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private ScriptVersion script11;
    private ScriptVersion script12;
    private ScriptVersion script2;

    @BeforeAll
    static void prepare() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    @BeforeEach
    void setup() {
        ScriptVersionKey scriptKey1 = new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script1")), 1, "NA");
        ScriptVersionKey scriptKey12 = new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script1")), 2, "NA");
        ScriptVersionKey scriptKey2 = new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("dummy")), 1, "NA");
        SecurityGroup securityGroup = SecurityGroup.builder()
                .metadataKey(new SecurityGroupKey(UUID.randomUUID()))
                .name("DEFAULT")
                .teamKeys(new HashSet<>())
                .securedObjects(Stream.of(scriptKey1, scriptKey12, scriptKey2).collect(Collectors.toSet()))
                .build();

        script11 = new ScriptVersionBuilder(IdentifierTools.getScriptIdentifier("script1"), 1)
                .securityGroupKey(securityGroup.getMetadataKey())
                .securityGroupName(securityGroup.getName())
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        script12 = new ScriptVersionBuilder(IdentifierTools.getScriptIdentifier("script1"), 2)
                .securityGroupKey(securityGroup.getMetadataKey())
                .securityGroupName(securityGroup.getName())
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        script2 = new ScriptVersionBuilder(IdentifierTools.getScriptIdentifier("dummy"), 1)
                .securityGroupKey(securityGroup.getMetadataKey())
                .securityGroupName(securityGroup.getName())
                .numberOfActions(3)
                .numberOfParameters(3)
                .build();
    }

    @Test
    void scriptNotExistsKeyTest() {
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getScript().getMetadataKey()));
    }

    @Test
    void scriptNotExistsNameTest() {
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getScript().getName()));
    }

    @Test
    void scriptNotExistsKeyMultipleTest() {
        ScriptConfiguration.getInstance().insert(script12.getScript());
        assertTrue(ScriptConfiguration.getInstance().exists(script11.getScript().getMetadataKey()));
    }

    @Test
    void scriptNotExistsNameMultipleTest() {
        ScriptConfiguration.getInstance().insert(script2.getScript());
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getScript().getName()));
    }

    @Test
    void scriptExistsKeyTest() {
        ScriptConfiguration.getInstance().insert(script11.getScript());
        assertTrue(ScriptConfiguration.getInstance().exists(script11.getScript().getMetadataKey()));
    }

    @Test
    void scriptExistsNameTest() {
        ScriptConfiguration.getInstance().insert(script11.getScript());
        assertTrue(ScriptConfiguration.getInstance().exists(script11.getScript().getName()));
    }

    @Test
    void scriptInsertTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11.getScript());

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey()).isPresent());
        assertEquals(script11.getScript(), ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey()).get());
    }

    @Test
    void scriptInsertMultipleVersionsTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11.getScript());

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey()).isPresent());
        assertEquals(script11.getScript(), ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey()).get());
    }

    @Test
    void scriptInsertMultipleScriptsTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11.getScript());
        ScriptConfiguration.getInstance().insert(script2.getScript());

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey()).isPresent());
        assertEquals(script11.getScript(), ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey()).get());
        assertTrue(ScriptConfiguration.getInstance().get(script2.getScript().getMetadataKey()).isPresent());
        assertEquals(script2.getScript(), ScriptConfiguration.getInstance().get(script2.getScript().getMetadataKey()).get());
    }

    @Test
    void scriptInsertAlreadyExistsTest() {
        ScriptConfiguration.getInstance().insert(script11.getScript());
        assertThrows(MetadataAlreadyExistsException.class, () -> ScriptConfiguration.getInstance().insert(script11.getScript()));
    }

//    @Test
//    void scriptDeletedNotExistsTest() {
//        ScriptConfiguration.getInstance().insert(script11.getScript());
//        ScriptConfiguration.getInstance().delete(script11.getScript().getMetadataKey());
//        assertThrows(MetadataDoesNotExistException.class, () -> ScriptConfiguration.getInstance().restoreDeletedScript(
//                new ScriptKey(script11.getScript().getMetadataKey().getScriptId(),
//                        script11.getScript().getVersion().getNumber(), "NA")));
//    }

    @Test
    void scriptDeletedTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().insert(script11.getScript());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().delete(script11.getScript().getMetadataKey());
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
    }

    @Test
    void scriptMultipleVersionDeletedTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().insert(script11.getScript());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().delete(script11.getScript().getMetadataKey());
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
    }

    @Test
    void scriptSoftDeletedTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().insert(script11.getScript());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().softDelete(script11.getScript().getMetadataKey());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptConfiguration.getInstance().getAllActive().size());
    }

    @Test
    void scriptDeleteDoesNotExistTest() {
        ScriptConfiguration.getInstance().delete(script11.getScript().getMetadataKey());
    }

    @Test
    void scriptDeleteDoesNotExistMultipleVersionsTest() {
        ScriptConfiguration.getInstance().insert(script12.getScript());
        ScriptConfiguration.getInstance().delete(script11.getScript().getMetadataKey());
    }

    @Test
    void scriptGetTest() {
        ScriptConfiguration.getInstance().insert(script11.getScript());

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11.getScript(), scriptFetched.get());
    }

    @Test
    void scriptGetMultipleTest() {
        ScriptConfiguration.getInstance().insert(script11.getScript());
        ScriptConfiguration.getInstance().insert(script2.getScript());

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11.getScript(), scriptFetched.get());
    }

    @Test
    void scriptGetMultipleVersionsTest() {
        ScriptConfiguration.getInstance().insert(script11.getScript());

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11.getScript(), scriptFetched.get());
    }

    @Test
    void scriptGetByNameTest() {
        ScriptConfiguration.getInstance().insert(script11.getScript());

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().getActiveByName("script1");
        assertEquals(script11.getScript(), scriptFetched.get());
    }

//    @Test
//    void scriptGetLatestVersionTest() {
//        ScriptConfiguration.getInstance().insert(script11.getScript());
//        ScriptConfiguration.getInstance().insert(script12.getScript());
//
//        Optional<Script> scriptFetched = ScriptVConfiguration.getInstance().getLatestVersion("script1");
//        assertTrue(scriptFetched.isPresent());
//        assertEquals(script12.getScript(), scriptFetched.get());
//    }

    @Test
    void scriptGetNotExistsTest() {
        assertFalse(ScriptConfiguration.getInstance().exists(script11.getScript().getMetadataKey()));
        assertFalse(ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey()).isPresent());
    }

    @Test
    void scriptUpdateTest() {
        ScriptConfiguration.getInstance().insert(script11.getScript());

        Optional<Script> scriptFetched = ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals("dummy", scriptFetched.get().getDescription());

        script11.getScript().setDescription("new description");
        ScriptConfiguration.getInstance().update(script11.getScript());

        scriptFetched = ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals("new description", scriptFetched.get().getDescription());
        assertEquals(script11.getScript(), ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey()).get());
    }

//    @Test
//    void scriptUpdateMultipleVersionsTest() {
//        ScriptConfiguration.getInstance().insert(script11.getScript());
//        ScriptConfiguration.getInstance().insert(script12.getScript());
//
//        Optional<Script> script11.getScript()Fetched = ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey());
//        assertTrue(script11.getScript()Fetched.isPresent());
//        assertEquals("dummy", script11.getScript()Fetched.get().getDescription());
//        Optional<Script> script12.getScript()Fetched = ScriptConfiguration.getInstance().get(script12.getScript().getMetadataKey());
//        assertTrue(script12.getScript()Fetched.isPresent());
//        assertEquals("dummy", script12.getScript()Fetched.get().getDescription());
//
//        script11.getScript().setDescription("new description");
//        ScriptConfiguration.getInstance().update(script11.getScript());
//
//        script11.getScript()Fetched = ScriptConfiguration.getInstance().get(script11.getScript().getMetadataKey());
//        assertTrue(script11.getScript()Fetched.isPresent());
//        assertEquals(script11.getScript(), script11.getScript()Fetched.get());
//        script12.getScript()Fetched = ScriptConfiguration.getInstance().get(script12.getScript().getMetadataKey());
//        assertTrue(script12.getScript()Fetched.isPresent());
//        assertEquals("new description", script12.getScript()Fetched.get().getDescription());
//    }

}
