package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ScriptVersionConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private ScriptVersion scriptVersion1;
    private ScriptVersion scriptVersion2;
    private ScriptVersion scriptVersion3;

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
        designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        ScriptVersionKey scriptKey1 = new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script1")), 1, "NA");
        ScriptVersionKey scriptKey12 = new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script1")), 2, "NA");
        ScriptVersionKey scriptKey2 = new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("dummy")), 1, "NA");
        SecurityGroup securityGroup = SecurityGroup.builder()
                .metadataKey(new SecurityGroupKey(UUID.randomUUID()))
                .name("DEFAULT")
                .teamKeys(new HashSet<>())
                .securedObjects(Stream.of(scriptKey1, scriptKey12, scriptKey2).collect(Collectors.toSet()))
                .build();

        scriptVersion1 = new ScriptVersionBuilder(IdentifierTools.getScriptIdentifier("1"), 1)
                .securityGroupKey(securityGroup.getMetadataKey())
                .securityGroupName(securityGroup.getName())
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        scriptVersion2 = new ScriptVersionBuilder(IdentifierTools.getScriptIdentifier("1"), 2)
                .securityGroupKey(securityGroup.getMetadataKey())
                .securityGroupName(securityGroup.getName())
                .name("script1")
                .numberOfActions(2)
                .numberOfParameters(2)
                .build();
        scriptVersion3 = new ScriptVersionBuilder(IdentifierTools.getScriptIdentifier("2"), 1)
                .securityGroupKey(securityGroup.getMetadataKey())
                .securityGroupName(securityGroup.getName())
                .numberOfActions(3)
                .numberOfParameters(3)
                .build();
    }

    @Test
    void scriptVersionNotExistTest() {
        assertFalse(ScriptVersionConfiguration.getInstance().exists(scriptVersion1));
    }

    @Test
    void scriptVersionExistsTest() {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        assertTrue(ScriptVersionConfiguration.getInstance().exists(scriptVersion1.getMetadataKey()));
    }

    @Test
    void scriptVersionInsertTest() {
        assertEquals(0, ScriptVersionConfiguration.getInstance().getAll().size());
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);

        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
        assertTrue(ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey()).isPresent());
        assertEquals(scriptVersion1, ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey()).get());
    }

    @Test
    void scriptVersionInsertMultipleTest() {
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
    void scriptVersionInsertAlreadyExistsTest() {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        assertThrows(MetadataAlreadyExistsException.class, () -> ScriptVersionConfiguration.getInstance().insert(scriptVersion1));
    }

    @Test
    void scriptVersionDeleteTest() {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);

        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
        ScriptVersionConfiguration.getInstance().delete(
                new ScriptVersionKey(new ScriptKey(scriptVersion1.getScriptId()),
                        scriptVersion1.getNumber(),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
    }

    @Test
    void scriptVersionDeleteMultipleTest() {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        ScriptVersionConfiguration.getInstance().insert(scriptVersion2);

        assertEquals(2, ScriptVersionConfiguration.getInstance().getAll().size());
        ScriptVersionConfiguration.getInstance().softDelete(new ScriptVersionKey(
                new ScriptKey(scriptVersion1.getScriptId()),
                        scriptVersion1.getNumber(), "NA"), LocalDateTime.now().toString());
        assertEquals(2, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAllActive().size());

        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        assertEquals(3, ScriptVersionConfiguration.getInstance().getAll().size());
        assertEquals(2, ScriptVersionConfiguration.getInstance().getAllActive().size());

    }

    @Test
    void scriptVersionDeleteDoesNotExistTest() {
        ScriptVersionConfiguration.getInstance().delete(scriptVersion1.getMetadataKey());
    }

    @Test
    void scriptVersionGetTest() {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);

        Optional<ScriptVersion> fetchedScriptVersion = ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey());
        assertTrue(fetchedScriptVersion.isPresent());
        assertEquals(scriptVersion1, fetchedScriptVersion.get());
    }

    @Test
    void scriptVersionGetNotExistsTest() {
        assertFalse(ScriptVersionConfiguration.getInstance().exists(scriptVersion1.getMetadataKey()));
    }

    @Test
    void scriptUpdateTest() {
        String localDateTime = LocalDateTime.now().toString();
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        Optional<ScriptVersion> fetchedScriptVersion = ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey());
        assertTrue(fetchedScriptVersion.isPresent());
        assertEquals("version of script", fetchedScriptVersion.get().getDescription());

        scriptVersion1.setDescription("new description");
        scriptVersion1.setLastModifiedBy("username");
        scriptVersion1.setLastModifiedAt(localDateTime);
        ScriptVersionConfiguration.getInstance().update(scriptVersion1);

        fetchedScriptVersion = ScriptVersionConfiguration.getInstance().get(scriptVersion1.getMetadataKey());
        assertTrue(fetchedScriptVersion.isPresent());
        assertEquals("new description", fetchedScriptVersion.get().getDescription());
        assertEquals("username", fetchedScriptVersion.get().getLastModifiedBy());
        assertEquals(localDateTime, fetchedScriptVersion.get().getLastModifiedAt());
    }

    @Test
    void scriptGetAllVersionsOfScriptOnlyTest() {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        ScriptVersionConfiguration.getInstance().insert(scriptVersion2);

        Set<ScriptVersion> scriptVersions = ScriptVersionConfiguration.getInstance().getByScriptKey(new ScriptKey(scriptVersion1.getScriptId()));

        assertEquals(Stream.of(scriptVersion1, scriptVersion2).collect(Collectors.toSet()), scriptVersions);
    }

    @Test
    void scriptGetAllVersionsOfScriptMultipleTest() {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        ScriptVersionConfiguration.getInstance().insert(scriptVersion2);
        ScriptVersionConfiguration.getInstance().insert(scriptVersion3);

        Set<ScriptVersion> scriptVersions = ScriptVersionConfiguration.getInstance().getByScriptKey(new ScriptKey(scriptVersion1.getScriptId()));

        assertEquals(Stream.of(scriptVersion1, scriptVersion2).collect(Collectors.toSet()), scriptVersions);
    }

}
