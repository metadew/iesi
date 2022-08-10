package io.metadew.iesi.metadata.configuration.script.result;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptResultConfigurationTest {

    @BeforeAll
    static void prepare() {
        // Configuration.getInstance();
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
        // Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    @BeforeEach
    void setup() {
    }

    @Test
    void scriptGetAllEmptyTest() {
        assertThat(ScriptResultConfiguration.getInstance().getAll())
                .isEmpty();
    }

    @Test
    void scriptGetAllTest() {
        ScriptResult scriptResult1 = ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey(UUID.randomUUID().toString(), -1L))
                .scriptId(UUID.randomUUID().toString())
                .status(ScriptRunStatus.RUNNING)
                .parentProcessId(-1L)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1, ChronoUnit.SECONDS))
                .environment("test")
                .scriptName("script")
                .scriptVersion(1L)
                .securityGroupName("PUBLIC")
                .build();
        ScriptResult scriptResult2 = ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey(UUID.randomUUID().toString(), -1L))
                .scriptId(UUID.randomUUID().toString())
                .status(ScriptRunStatus.RUNNING)
                .parentProcessId(-1L)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1, ChronoUnit.SECONDS))
                .environment("test")
                .scriptName("script")
                .scriptVersion(1L)
                .securityGroupName("PUBLIC")
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult1);
        ScriptResultConfiguration.getInstance().insert(scriptResult2);

        assertThat(ScriptResultConfiguration.getInstance().getAll())
                .containsOnly(scriptResult1, scriptResult2);

    }

    @Test
    void scriptGetByIdTest() {
        ScriptResultKey scriptResultKey = new ScriptResultKey(UUID.randomUUID().toString(), -1L);
        ScriptResult scriptResult1 = ScriptResult.builder()
                .scriptResultKey(scriptResultKey)
                .scriptId(UUID.randomUUID().toString())
                .status(ScriptRunStatus.RUNNING)
                .parentProcessId(-1L)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1, ChronoUnit.SECONDS))
                .environment("test")
                .scriptName("script")
                .scriptVersion(1L)
                .securityGroupName("PUBLIC")
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult1);

        assertThat(ScriptResultConfiguration.getInstance().get(scriptResultKey))
                .hasValue(scriptResult1);
    }

    @Test
    void scriptInsertTest() {
        ScriptResultKey scriptResultKey = new ScriptResultKey(UUID.randomUUID().toString(), -1L);
        ScriptResult scriptResult1 = ScriptResult.builder()
                .scriptResultKey(scriptResultKey)
                .scriptId(UUID.randomUUID().toString())
                .status(ScriptRunStatus.RUNNING)
                .parentProcessId(-1L)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1, ChronoUnit.SECONDS))
                .environment("test")
                .scriptName("script")
                .scriptVersion(1L)
                .securityGroupName("PUBLIC")
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult1);
        assertThat(ScriptResultConfiguration.getInstance().get(scriptResultKey))
                .hasValue(scriptResult1);
    }

    @Test
    void scriptDeleteTest() {
        ScriptResultKey scriptResultKey = new ScriptResultKey(UUID.randomUUID().toString(), -1L);
        ScriptResult scriptResult1 = ScriptResult.builder()
                .scriptResultKey(scriptResultKey)
                .scriptId(UUID.randomUUID().toString())
                .status(ScriptRunStatus.RUNNING)
                .parentProcessId(-1L)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1, ChronoUnit.SECONDS))
                .environment("test")
                .scriptName("script")
                .scriptVersion(1L)
                .securityGroupName("PUBLIC")
                .build();
        ScriptResult scriptResult2 = ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey(UUID.randomUUID().toString(), -1L))
                .scriptId(UUID.randomUUID().toString())
                .status(ScriptRunStatus.RUNNING)
                .parentProcessId(-1L)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1, ChronoUnit.SECONDS))
                .environment("test")
                .scriptName("script")
                .scriptVersion(1L)
                .securityGroupName("PUBLIC")
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult1);

        ScriptResultConfiguration.getInstance().insert(scriptResult2);

        assertThat(ScriptResultConfiguration.getInstance().getAll())
                .containsOnly(scriptResult1, scriptResult2);

        ScriptResultConfiguration.getInstance().delete(scriptResultKey);

        assertThat(ScriptResultConfiguration.getInstance().getAll())
                .containsOnly(scriptResult2);
    }

    @Test
    void scriptUpdateTest() {
        ScriptResultKey scriptResultKey = new ScriptResultKey(UUID.randomUUID().toString(), -1L);
        ScriptResult scriptResult1 = ScriptResult.builder()
                .scriptResultKey(scriptResultKey)
                .scriptId(UUID.randomUUID().toString())
                .status(ScriptRunStatus.RUNNING)
                .parentProcessId(-1L)
                .startTimestamp(LocalDateTime.now())
                .environment("test")
                .scriptName("script")
                .scriptVersion(1L)
                .securityGroupName("PUBLIC")
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult1);

        assertThat(ScriptResultConfiguration.getInstance().get(scriptResultKey))
                .hasValue(scriptResult1);

        LocalDateTime endTimestamp = LocalDateTime.now().plus(1, ChronoUnit.SECONDS);
        scriptResult1.setStatus(ScriptRunStatus.SUCCESS);
        scriptResult1.setEndTimestamp(endTimestamp);

        ScriptResultConfiguration.getInstance().update(scriptResult1);

        assertThat(ScriptResultConfiguration.getInstance().get(scriptResultKey))
                .hasValue(scriptResult1);
    }

}
