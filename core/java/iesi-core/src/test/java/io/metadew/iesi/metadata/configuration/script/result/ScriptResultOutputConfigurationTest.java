package io.metadew.iesi.metadata.configuration.script.result;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptResultOutputConfigurationTest {

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
        assertThat(ScriptResultOutputConfiguration.getInstance().getAll())
                .isEmpty();
    }

    @Test
    void scriptGetAllTest() {
        ScriptResultOutput scriptResultOutput1 = ScriptResultOutput.builder()
                .scriptResultOutputKey(new ScriptResultOutputKey(UUID.randomUUID().toString(), -1L, "output1"))
                .scriptId(UUID.randomUUID().toString())
                .value("value1")
                .build();
        ScriptResultOutput scriptResultOutput2 = ScriptResultOutput.builder()
                .scriptResultOutputKey(new ScriptResultOutputKey(UUID.randomUUID().toString(), -1L, "output1"))
                .scriptId(UUID.randomUUID().toString())
                .value("value1")
                .build();
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput1);
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput2);

        assertThat(ScriptResultOutputConfiguration.getInstance().getAll())
                .containsOnly(scriptResultOutput1, scriptResultOutput2);

    }

    @Test
    void scriptGetByIdTest() {
        ScriptResultOutputKey scriptResultOutputKey1 = new ScriptResultOutputKey(UUID.randomUUID().toString(), -1L, "output1");
        ScriptResultOutput scriptResultOutput1 = ScriptResultOutput.builder()
                .scriptResultOutputKey(scriptResultOutputKey1)
                .scriptId(UUID.randomUUID().toString())
                .value("value1")
                .build();
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput1);

        assertThat(ScriptResultOutputConfiguration.getInstance().get(scriptResultOutputKey1))
                .hasValue(scriptResultOutput1);
    }

    @Test
    void scriptGetByRunIdTest() {
        UUID runUuid = UUID.randomUUID();
        ScriptResultOutputKey scriptResultOutputKey1 = new ScriptResultOutputKey(runUuid.toString(), -1L, "output1");
        ScriptResultOutput scriptResultOutput1 = ScriptResultOutput.builder()
                .scriptResultOutputKey(scriptResultOutputKey1)
                .scriptId(UUID.randomUUID().toString())
                .value("value1")
                .build();
        ScriptResultOutputKey scriptResultOutputKey2 = new ScriptResultOutputKey(runUuid.toString(), -1L, "output2");
        ScriptResultOutput scriptResultOutput2 = ScriptResultOutput.builder()
                .scriptResultOutputKey(scriptResultOutputKey2)
                .scriptId(UUID.randomUUID().toString())
                .value("value1")
                .build();
        ScriptResultOutputKey scriptResultOutputKey3 = new ScriptResultOutputKey(UUID.randomUUID().toString(), -1L, "output2");
        ScriptResultOutput scriptResultOutput3 = ScriptResultOutput.builder()
                .scriptResultOutputKey(scriptResultOutputKey3)
                .scriptId(UUID.randomUUID().toString())
                .value("value1")
                .build();
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput1);
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput2);
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput3);

        assertThat(ScriptResultOutputConfiguration.getInstance().getByRunId(runUuid.toString()))
                .containsOnly(scriptResultOutput1, scriptResultOutput2);
    }

    @Test
    void scriptInsertTest() {
        ScriptResultOutputKey scriptResultOutputKey = new ScriptResultOutputKey(UUID.randomUUID().toString(), -1L, "output1");
        ScriptResultOutput scriptResultOutput1 = ScriptResultOutput.builder()
                .scriptResultOutputKey(scriptResultOutputKey)
                .scriptId(UUID.randomUUID().toString())
                .value("value1")
                .build();
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput1);
        assertThat(ScriptResultOutputConfiguration.getInstance().get(scriptResultOutputKey))
                .hasValue(scriptResultOutput1);
    }

    @Test
    void scriptDeleteTest() {
        ScriptResultOutputKey scriptResultOutputKey = new ScriptResultOutputKey(UUID.randomUUID().toString(), -1L, "output1");
        ScriptResultOutput scriptResultOutput1 = ScriptResultOutput.builder()
                .scriptResultOutputKey(scriptResultOutputKey)
                .scriptId(UUID.randomUUID().toString())
                .value("value1")
                .build();
        ScriptResultOutput scriptResultOutput2 = ScriptResultOutput.builder()
                .scriptResultOutputKey(new ScriptResultOutputKey(UUID.randomUUID().toString(), -1L, "output1"))
                .scriptId(UUID.randomUUID().toString())
                .value("value1")
                .build();
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput1);

        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput2);

        assertThat(ScriptResultOutputConfiguration.getInstance().getAll())
                .containsOnly(scriptResultOutput1, scriptResultOutput2);

        ScriptResultOutputConfiguration.getInstance().delete(scriptResultOutputKey);

        assertThat(ScriptResultOutputConfiguration.getInstance().getAll())
                .containsOnly(scriptResultOutput2);
    }

    @Test
    void scriptUpdateTest() {
        ScriptResultOutputKey scriptResultOutputKey = new ScriptResultOutputKey(UUID.randomUUID().toString(), -1L, "output1");
        String scriptId = UUID.randomUUID().toString();
        ScriptResultOutput scriptResultOutput = ScriptResultOutput.builder()
                .scriptResultOutputKey(scriptResultOutputKey)
                .scriptId(scriptId)
                .value("value1")
                .build();
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput);

        assertThat(ScriptResultOutputConfiguration.getInstance().get(scriptResultOutputKey))
                .hasValue(scriptResultOutput);

        ScriptResultOutput updatedScriptResultOutput = ScriptResultOutput.builder()
                .scriptResultOutputKey(scriptResultOutputKey)
                .scriptId(scriptId)
                .value("value2")
                .build();

        ScriptResultOutputConfiguration.getInstance().update(updatedScriptResultOutput);

        assertThat(ScriptResultOutputConfiguration.getInstance().get(scriptResultOutputKey))
                .hasValue(updatedScriptResultOutput);
    }

}
