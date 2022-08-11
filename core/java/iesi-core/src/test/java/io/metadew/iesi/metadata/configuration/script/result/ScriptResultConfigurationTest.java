package io.metadew.iesi.metadata.configuration.script.result;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Configuration.class, SpringContext.class, MetadataRepositoryConfiguration.class, ScriptResultConfiguration.class })
class ScriptResultConfigurationTest {

    @Autowired
    private static MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ScriptResultConfiguration scriptResultConfiguration;

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
    }

    @Test
    void scriptGetAllEmptyTest() {
        assertThat(scriptResultConfiguration.getAll())
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
        scriptResultConfiguration.insert(scriptResult1);
        scriptResultConfiguration.insert(scriptResult2);

        assertThat(scriptResultConfiguration.getAll())
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
        scriptResultConfiguration.insert(scriptResult1);

        assertThat(scriptResultConfiguration.get(scriptResultKey))
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
        scriptResultConfiguration.insert(scriptResult1);
        assertThat(scriptResultConfiguration.get(scriptResultKey))
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
        scriptResultConfiguration.insert(scriptResult1);

        scriptResultConfiguration.insert(scriptResult2);

        assertThat(scriptResultConfiguration.getAll())
                .containsOnly(scriptResult1, scriptResult2);

        scriptResultConfiguration.delete(scriptResultKey);

        assertThat(scriptResultConfiguration.getAll())
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
        scriptResultConfiguration.insert(scriptResult1);

        assertThat(scriptResultConfiguration.get(scriptResultKey))
                .hasValue(scriptResult1);

        LocalDateTime endTimestamp = LocalDateTime.now().plus(1, ChronoUnit.SECONDS);
        scriptResult1.setStatus(ScriptRunStatus.SUCCESS);
        scriptResult1.setEndTimestamp(endTimestamp);

        scriptResultConfiguration.update(scriptResult1);

        assertThat(scriptResultConfiguration.get(scriptResultKey))
                .hasValue(scriptResult1);
    }

}
