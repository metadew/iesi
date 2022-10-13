package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.action.ActionParameterConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.tools.IdentifierTools;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, ScriptConfiguration.class, ScriptVersionConfiguration.class, ScriptParameterConfiguration.class, ScriptLabelConfiguration.class,
        ActionConfiguration.class, ActionParameterConfiguration.class })
@ActiveProfiles("test")
class ScriptConfigurationTest {

    Script script11;
    Script script12;
    Script script2;

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    ScriptConfiguration scriptConfiguration;

    @Autowired
    ActionConfiguration actionConfiguration;

    @Autowired
    ScriptVersionConfiguration scriptVersionConfiguration;

    @Autowired
    ScriptParameterConfiguration scriptParameterConfiguration;

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

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void scriptNotExistsKeyTest() {
        assertFalse(scriptConfiguration.exists(script11.getMetadataKey()));
    }

    @Test
    void scriptNotExistsNameTest() {
        assertFalse(scriptConfiguration.exists(script11.getName()));
    }

    @Test
    void scriptNotExistsKeyMultipleTest() {
        scriptConfiguration.insert(script12);
        assertFalse(scriptConfiguration.exists(script11.getMetadataKey()));
    }

    @Test
    void scriptNotExistsNameMultipleTest() {
        scriptConfiguration.insert(script2);
        assertFalse(scriptConfiguration.exists(script11.getName()));
    }

    @Test
    void scriptExistsKeyTest() {
        scriptConfiguration.insert(script11);
        assertTrue(scriptConfiguration.exists(script11.getMetadataKey()));
    }

    @Test
    void scriptExistsNameTest() {
        scriptConfiguration.insert(script11);
        assertTrue(scriptConfiguration.exists(script11.getName()));
    }

    @Test
    void scriptInsertTest() {
        assertEquals(0, scriptConfiguration.getAll().size());

        scriptConfiguration.insert(script11);

        assertEquals(1, scriptConfiguration.getAll().size());
        assertTrue(scriptConfiguration.get(script11.getMetadataKey()).isPresent());
        assertEquals(script11, scriptConfiguration.get(script11.getMetadataKey()).get());
        assertEquals(2, actionConfiguration.getAll().size());
        assertEquals(2, scriptParameterConfiguration.getAll().size());
        assertEquals(1, scriptVersionConfiguration.getAll().size());
    }

    @Test
    void scriptInsertMultipleVersionsTest() {
        assertEquals(0, scriptConfiguration.getAll().size());

        scriptConfiguration.insert(script11);
        scriptConfiguration.insert(script12);

        assertEquals(2, scriptConfiguration.getAll().size());
        assertTrue(scriptConfiguration.get(script11.getMetadataKey()).isPresent());
        assertEquals(script11, scriptConfiguration.get(script11.getMetadataKey()).get());
        assertTrue(scriptConfiguration.get(script12.getMetadataKey()).isPresent());
        assertEquals(script12, scriptConfiguration.get(script12.getMetadataKey()).get());
        assertEquals(4, actionConfiguration.getAll().size());
        assertEquals(4, scriptParameterConfiguration.getAll().size());
        assertEquals(2, scriptVersionConfiguration.getAll().size());
    }

    @Test
    void scriptInsertMultipleScriptsTest() {
        assertEquals(0, scriptConfiguration.getAll().size());

        scriptConfiguration.insert(script11);
        scriptConfiguration.insert(script2);

        assertEquals(2, scriptConfiguration.getAll().size());
        assertTrue(scriptConfiguration.get(script11.getMetadataKey()).isPresent());
        assertEquals(script11, scriptConfiguration.get(script11.getMetadataKey()).get());
        assertTrue(scriptConfiguration.get(script2.getMetadataKey()).isPresent());
        assertEquals(script2, scriptConfiguration.get(script2.getMetadataKey()).get());
        assertEquals(5, actionConfiguration.getAll().size());
        assertEquals(5, scriptParameterConfiguration.getAll().size());
        assertEquals(2, scriptVersionConfiguration.getAll().size());
    }

    @Test
    void scriptInsertAlreadyExistsTest() {
        scriptConfiguration.insert(script11);
        assertThrows(MetadataAlreadyExistsException.class, () -> scriptConfiguration.insert(script11));
    }

    @Test
    void scriptDeleteOnlyTest() {
        assertEquals(0, scriptConfiguration.getAll().size());

        scriptConfiguration.insert(script11);

        assertEquals(1, scriptConfiguration.getAll().size());

        scriptConfiguration.delete(script11.getMetadataKey());
        assertEquals(0, scriptConfiguration.getAll().size());
        assertEquals(0, scriptVersionConfiguration.getAll().size());
        assertEquals(0, scriptParameterConfiguration.getAll().size());
        assertEquals(0, actionConfiguration.getAll().size());
    }

    @Test
    void scriptDeleteMultipleVersionTest() {
        assertEquals(0, scriptConfiguration.getAll().size());

        scriptConfiguration.insert(script11);
        scriptConfiguration.insert(script12);

        assertEquals(2, scriptConfiguration.getAll().size());

        scriptConfiguration.delete(script11.getMetadataKey());
        assertEquals(1, scriptConfiguration.getAll().size());
        assertEquals(1, scriptVersionConfiguration.getAll().size());
        assertEquals(2, scriptParameterConfiguration.getAll().size());
        assertEquals(2, actionConfiguration.getAll().size());
    }

    @Test
    void scriptDeleteMultipleTest() {
        assertEquals(0, scriptConfiguration.getAll().size());

        scriptConfiguration.insert(script11);
        scriptConfiguration.insert(script2);

        assertEquals(2, scriptConfiguration.getAll().size());

        scriptConfiguration.delete(script11.getMetadataKey());
        assertEquals(1, scriptConfiguration.getAll().size());
        assertEquals(1, scriptVersionConfiguration.getAll().size());
        assertEquals(3, scriptParameterConfiguration.getAll().size());
        assertEquals(3, actionConfiguration.getAll().size());
    }

    @Test
    void scriptDeleteDoesNotExistTest() {
        scriptConfiguration.delete(script11.getMetadataKey());
    }

    @Test
    void scriptDeleteDoesNotExistMultipleVersionsTest() {
        scriptConfiguration.insert(script12);
        scriptConfiguration.delete(script11.getMetadataKey());
    }

    @Test
    void scriptGetTest() {
        scriptConfiguration.insert(script11);

        Optional<Script> scriptFetched = scriptConfiguration.get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    void scriptGetMultipleTest() {
        scriptConfiguration.insert(script11);
        scriptConfiguration.insert(script2);

        Optional<Script> scriptFetched = scriptConfiguration.get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    void scriptGetMultipleVersionsTest() {
        scriptConfiguration.insert(script11);
        scriptConfiguration.insert(script12);

        Optional<Script> scriptFetched = scriptConfiguration.get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals(script11, scriptFetched.get());
    }

    @Test
    void scriptGetByNameTest() {
        scriptConfiguration.insert(script11);
        scriptConfiguration.insert(script12);

        List<Script> scriptFetched = scriptConfiguration.getByName("script1");
        assertEquals(Stream.of(script11, script12).collect(Collectors.toList()), scriptFetched);
    }

    @Test
    void scriptGetLatestVersionTest() {
        scriptConfiguration.insert(script11);
        scriptConfiguration.insert(script12);

        Optional<Script> scriptFetched = scriptConfiguration.getLatestVersion("script1");
        assertTrue(scriptFetched.isPresent());
        assertEquals(script12, scriptFetched.get());
    }

    @Test
    void scriptGetNotExistsTest() {
        assertFalse(scriptConfiguration.exists(script11.getMetadataKey()));
        assertFalse(scriptConfiguration.get(script11.getMetadataKey()).isPresent());
    }

    @Test
    void scriptUpdateTest() {
        scriptConfiguration.insert(script11);

        Optional<Script> scriptFetched = scriptConfiguration.get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals("dummy", scriptFetched.get().getDescription());

        script11.setDescription("new description");
        scriptConfiguration.update(script11);

        scriptFetched = scriptConfiguration.get(script11.getMetadataKey());
        assertTrue(scriptFetched.isPresent());
        assertEquals("new description", scriptFetched.get().getDescription());
        assertEquals(script11, scriptConfiguration.get(script11.getMetadataKey()).get());
    }

    @Test
    void scriptUpdateMultipleVersionsTest() {
        scriptConfiguration.insert(script11);
        scriptConfiguration.insert(script12);

        Optional<Script> script11Fetched = scriptConfiguration.get(script11.getMetadataKey());
        assertTrue(script11Fetched.isPresent());
        assertEquals("dummy", script11Fetched.get().getDescription());
        Optional<Script> script12Fetched = scriptConfiguration.get(script12.getMetadataKey());
        assertTrue(script12Fetched.isPresent());
        assertEquals("dummy", script12Fetched.get().getDescription());

        script11.setDescription("new description");
        scriptConfiguration.update(script11);

        script11Fetched = scriptConfiguration.get(script11.getMetadataKey());
        assertTrue(script11Fetched.isPresent());
        assertEquals(script11, script11Fetched.get());
        script12Fetched = scriptConfiguration.get(script12.getMetadataKey());
        assertTrue(script12Fetched.isPresent());
        assertEquals("new description", script12Fetched.get().getDescription());
    }

}
