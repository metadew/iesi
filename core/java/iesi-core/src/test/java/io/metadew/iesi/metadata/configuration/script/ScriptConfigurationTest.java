package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
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
    private Script script11;
    private Script script12;
    private Script script2;

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
        ScriptKey scriptKey1 = new ScriptKey(IdentifierTools.getScriptIdentifier("script1"), 1);
        ScriptKey scriptKey12 = new ScriptKey(IdentifierTools.getScriptIdentifier("script1"), 2);
        ScriptKey scriptKey2 = new ScriptKey(IdentifierTools.getScriptIdentifier("dummy"), 1);

        SecurityGroup securityGroup = SecurityGroup.builder()
                .metadataKey(new SecurityGroupKey(UUID.randomUUID()))
                .name("DEFAULT")
                .teamKeys(new HashSet<>())
                .securedObjects(Stream.of(scriptKey1, scriptKey12, scriptKey2).collect(Collectors.toSet()))
                .build();

        script11 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("script1"), 1)
                .securityGroupKey(securityGroup.getMetadataKey())
                .securityGroupName(securityGroup.getName())
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        script12 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("script1"), 2)
                .securityGroupKey(securityGroup.getMetadataKey())
                .securityGroupName(securityGroup.getName())
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        script2 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("dummy"), 1)
                .securityGroupKey(securityGroup.getMetadataKey())
                .securityGroupName(securityGroup.getName())
                .numberOfActions(3)
                .numberOfParameters(3)
                .build();
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

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertTrue(ScriptConfiguration.getInstance().get(script11.getMetadataKey()).isPresent());
        assertEquals(script11, ScriptConfiguration.getInstance().get(script11.getMetadataKey()).get());
        assertEquals(2, ActionConfiguration.getInstance().getAll().size());
        assertEquals(2, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
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
        assertEquals(4, ActionConfiguration.getInstance().getAll().size());
        assertEquals(4, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(2, ScriptVersionConfiguration.getInstance().getAll().size());
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
        assertEquals(5, ActionConfiguration.getInstance().getAll().size());
        assertEquals(5, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(2, ScriptVersionConfiguration.getInstance().getAll().size());
    }

    @Test
    void scriptInsertAlreadyExistsTest() {
        ScriptConfiguration.getInstance().insert(script11);
        assertThrows(MetadataAlreadyExistsException.class, () -> ScriptConfiguration.getInstance().insert(script11));
    }

   /* @Test
    void scriptDeleteOnlyTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(0, ActionConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptConfiguration.getInstance().getAllDeleted().size());
    }*/

    /*@Test
    void scriptDeleteMultipleVersionTest() {
        List<ScriptVersion> versions = new ArrayList<>();
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(2, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(2, ActionConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptConfiguration.getInstance().getAllDeleted().size());
        ScriptVersionConfiguration.getInstance().getDeleted(new ScriptVersionKey(script11.getMetadataKey())).ifPresent(versions::add);
        assertEquals(1, versions.size());
    }*/

    /*@Test
    void scriptDeleteMultipleTest() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script2);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(3, ScriptParameterConfiguration.getInstance().getAll().size());
        assertEquals(3, ActionConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptConfiguration.getInstance().getAllDeleted().size());
    }*/

    /*@Test
    void scriptRestoreAfterDeletion() {
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().insert(script11);

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptConfiguration.getInstance().getAllDeleted().size());

        Script script = ScriptConfiguration.getInstance().
                getDeleted(script11.getMetadataKey()).orElseThrow(() -> new MetadataDoesNotExistException(script11.getMetadataKey()));

        ScriptConfiguration.getInstance().restoreDeletedScript(new ScriptKey(
                script.getMetadataKey().getScriptId(),
                script.getVersion().getNumber(),
                script.getDeletedAt()));

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(0, ScriptConfiguration.getInstance().getAllDeleted().size());
    }
*/
    @Test
    void multipleScriptRestoreAfterDeletion() {
        List<ScriptVersion> versions = new ArrayList<>();
        assertEquals(0, ScriptConfiguration.getInstance().getAll().size());
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().insert(script12);

        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());

        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());

        assertEquals(1, ScriptConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());

        ScriptVersionConfiguration.getInstance().restoreDeletedScriptVersion(new ScriptVersionKey(script11.getMetadataKey()));
        assertEquals(2, ScriptConfiguration.getInstance().getAll().size());

    }

    @Test
    void scriptRestoreAlreadyExistsTest() {
        ScriptConfiguration.getInstance().insert(script11);
        assertThrows(MetadataAlreadyExistsException.class, () -> ScriptConfiguration.getInstance().restoreDeletedScript(
                new ScriptKey(script11.getMetadataKey().getScriptId(),
                script11.getVersion().getNumber(),
                script11.getDeletedAt())));
    }

    @Test
    void scriptDeletedNotExistsTest() {
        ScriptConfiguration.getInstance().insert(script11);
        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
        assertThrows(MetadataDoesNotExistException.class, () -> ScriptConfiguration.getInstance().restoreDeletedScript(
                new ScriptKey(script11.getMetadataKey().getScriptId(),
                        script11.getVersion().getNumber(),
                        script11.getDeletedAt())));
    }


    @Test
    void scriptDeleteDoesNotExistTest() {
        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
    }

    @Test
    void scriptDeleteDoesNotExistMultipleVersionsTest() {
        ScriptConfiguration.getInstance().insert(script12);
        ScriptConfiguration.getInstance().delete(script11.getMetadataKey());
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
        assertEquals(script12, scriptFetched.get());
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
        ScriptConfiguration.getInstance().update(script11);

        script11Fetched = ScriptConfiguration.getInstance().get(script11.getMetadataKey());
        assertTrue(script11Fetched.isPresent());
        assertEquals("new description", script11Fetched.get().getDescription());
        script12Fetched = ScriptConfiguration.getInstance().get(script12.getMetadataKey());
        assertTrue(script12Fetched.isPresent());
        assertEquals("new description", script12Fetched.get().getDescription());
    }

}
