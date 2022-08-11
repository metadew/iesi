package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {Configuration.class, SpringContext.class, MetadataRepositoryConfiguration.class, ScriptVersionConfiguration.class })
class ScriptVersionConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private ScriptVersion scriptVersion1;
    private ScriptVersion scriptVersion2;
    private ScriptVersion scriptVersion3;

    @Autowired
    private static MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ScriptVersionConfiguration scriptVersionConfiguration;

    @BeforeAll
    static void prepare() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        metadataRepositoryConfiguration
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
        assertFalse(scriptVersionConfiguration.exists(scriptVersion1));
    }

    @Test
    void scriptVersionExistsTest() {
        scriptVersionConfiguration.insert(scriptVersion1);
        assertTrue(scriptVersionConfiguration.exists(scriptVersion1.getMetadataKey()));
    }

    @Test
    void scriptVersionInsertTest() {
        assertEquals(0, scriptVersionConfiguration.getAll().size());
        scriptVersionConfiguration.insert(scriptVersion1);

        assertEquals(1, scriptVersionConfiguration.getAll().size());
        assertTrue(scriptVersionConfiguration.get(scriptVersion1.getMetadataKey()).isPresent());
        assertEquals(scriptVersion1, scriptVersionConfiguration.get(scriptVersion1.getMetadataKey()).get());
    }

    @Test
    void scriptVersionInsertMultipleTest() {
        assertEquals(0, scriptVersionConfiguration.getAll().size());
        scriptVersionConfiguration.insert(scriptVersion1);
        scriptVersionConfiguration.insert(scriptVersion2);

        assertEquals(2, scriptVersionConfiguration.getAll().size());
        assertTrue(scriptVersionConfiguration.get(scriptVersion1.getMetadataKey()).isPresent());
        assertEquals(scriptVersion1, scriptVersionConfiguration.get(scriptVersion1.getMetadataKey()).get());
        assertTrue(scriptVersionConfiguration.get(scriptVersion2.getMetadataKey()).isPresent());
        assertEquals(scriptVersion2, scriptVersionConfiguration.get(scriptVersion2.getMetadataKey()).get());
    }

    @Test
    void scriptVersionInsertAlreadyExistsTest() {
        scriptVersionConfiguration.insert(scriptVersion1);
        assertThrows(MetadataAlreadyExistsException.class, () -> scriptVersionConfiguration.insert(scriptVersion1));
    }

    @Test
    void scriptVersionDeleteTest() {
        scriptVersionConfiguration.insert(scriptVersion1);

        assertEquals(1, scriptVersionConfiguration.getAll().size());
        scriptVersionConfiguration.delete(scriptVersion1.getMetadataKey());
        assertEquals(0, scriptVersionConfiguration.getAll().size());
    }

    @Test
    void scriptVersionDeleteMultipleTest() {
        scriptVersionConfiguration.insert(scriptVersion1);
        scriptVersionConfiguration.insert(scriptVersion2);

        assertEquals(2, scriptVersionConfiguration.getAll().size());
        scriptVersionConfiguration.delete(scriptVersion1.getMetadataKey());
        assertEquals(1, scriptVersionConfiguration.getAll().size());
    }

    @Test
    void scriptVersionDeleteDoesNotExistTest() {
        scriptVersionConfiguration.delete(scriptVersion1.getMetadataKey());
    }

    @Test
    void scriptVersionGetTest() {
        scriptVersionConfiguration.insert(scriptVersion1);

        Optional<ScriptVersion> fetchedScriptVersion = scriptVersionConfiguration.get(scriptVersion1.getMetadataKey());
        assertTrue(fetchedScriptVersion.isPresent());
        assertEquals(scriptVersion1, fetchedScriptVersion.get());
    }

    @Test
    void scriptVersionGetNotExistsTest() {
        assertFalse(scriptVersionConfiguration.exists(scriptVersion1.getMetadataKey()));
    }

    @Test
    void scriptUpdateTest() {
        String localDateTime = LocalDateTime.now().toString();
        scriptVersionConfiguration.insert(scriptVersion1);
        Optional<ScriptVersion> fetchedScriptVersion = scriptVersionConfiguration.get(scriptVersion1.getMetadataKey());
        assertTrue(fetchedScriptVersion.isPresent());
        assertEquals("version of script", fetchedScriptVersion.get().getDescription());

        scriptVersion1.setDescription("new description");
        scriptVersion1.setLastModifiedBy("username");
        scriptVersion1.setLastModifiedAt(localDateTime);
        scriptVersionConfiguration.update(scriptVersion1);

        fetchedScriptVersion = scriptVersionConfiguration.get(scriptVersion1.getMetadataKey());
        assertTrue(fetchedScriptVersion.isPresent());
        assertEquals("new description", fetchedScriptVersion.get().getDescription());
        assertEquals("username", fetchedScriptVersion.get().getLastModifiedBy());
        assertEquals(localDateTime, fetchedScriptVersion.get().getLastModifiedAt());
    }

    @Test
    void scriptGetAllVersionsOfScriptOnlyTest() {
        scriptVersionConfiguration.insert(scriptVersion1);
        scriptVersionConfiguration.insert(scriptVersion2);

        List<ScriptVersion> scriptVersions = scriptVersionConfiguration.getByScriptId(scriptVersion1.getScriptId());

        assertEquals(Stream.of(scriptVersion1, scriptVersion2).collect(Collectors.toList()), scriptVersions);
    }

    @Test
    void scriptGetAllVersionsOfScriptMultipleTest() {
        scriptVersionConfiguration.insert(scriptVersion1);
        scriptVersionConfiguration.insert(scriptVersion2);
        scriptVersionConfiguration.insert(scriptVersion3);

        List<ScriptVersion> scriptVersions = scriptVersionConfiguration.getByScriptId(scriptVersion1.getScriptId());

        assertEquals(Stream.of(scriptVersion1, scriptVersion2).collect(Collectors.toList()), scriptVersions);
    }

}
