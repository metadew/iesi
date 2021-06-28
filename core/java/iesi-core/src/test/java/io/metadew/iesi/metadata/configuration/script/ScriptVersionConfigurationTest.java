package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        scriptVersion1 = new ScriptVersionBuilder("1", 1)
                .description("version of script")
                .build();
        scriptVersion2 = new ScriptVersionBuilder("1", 2)
                .description("version of script")
                .build();
        scriptVersion3 = new ScriptVersionBuilder("2", 2)
                .description("version of script")
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
        ScriptVersionConfiguration.getInstance().delete(new ScriptVersionKey(
                new ScriptKey(scriptVersion1.getScriptId(),
                        scriptVersion1.getNumber(),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))));
        assertEquals(1, ScriptVersionConfiguration.getInstance().getAll().size());
    }

    @Test
    void scriptVersionDeleteMultipleTest() {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        ScriptVersionConfiguration.getInstance().insert(scriptVersion2);

        assertEquals(2, ScriptVersionConfiguration.getInstance().getAll().size());
        ScriptVersionConfiguration.getInstance().delete(new ScriptVersionKey(
                new ScriptKey(scriptVersion1.getScriptId(),
                        scriptVersion1.getNumber(),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))));
        assertEquals(2, ScriptVersionConfiguration.getInstance().getAll().size());

        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        assertEquals(3, ScriptVersionConfiguration.getInstance().getAll().size());

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

        List<ScriptVersion> scriptVersions = ScriptVersionConfiguration.getInstance().getActiveByScriptId(scriptVersion1.getScriptId());

        assertEquals(Stream.of(scriptVersion1, scriptVersion2).collect(Collectors.toList()), scriptVersions);
    }

    @Test
    void scriptGetAllVersionsOfScriptMultipleTest() {
        ScriptVersionConfiguration.getInstance().insert(scriptVersion1);
        ScriptVersionConfiguration.getInstance().insert(scriptVersion2);
        ScriptVersionConfiguration.getInstance().insert(scriptVersion3);

        List<ScriptVersion> scriptVersions = ScriptVersionConfiguration.getInstance().getActiveByScriptId(scriptVersion1.getScriptId());

        assertEquals(Stream.of(scriptVersion1, scriptVersion2).collect(Collectors.toList()), scriptVersions);
    }

}
