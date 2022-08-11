package io.metadew.iesi.metadata.configuration.script.result;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.connection.trace.ConnectionTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptParameterConfiguration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Configuration.class, SpringContext.class, MetadataRepositoryConfiguration.class, ScriptResultOutputConfiguration.class})
class ScriptResultOutputConfigurationTest {

    @Autowired
    private static MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ScriptResultOutputConfiguration scriptResultOutputConfiguration;

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
        assertThat(scriptResultOutputConfiguration.getAll())
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
        scriptResultOutputConfiguration.insert(scriptResultOutput1);
        scriptResultOutputConfiguration.insert(scriptResultOutput2);

        assertThat(scriptResultOutputConfiguration.getAll())
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
        scriptResultOutputConfiguration.insert(scriptResultOutput1);

        assertThat(scriptResultOutputConfiguration.get(scriptResultOutputKey1))
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
        scriptResultOutputConfiguration.insert(scriptResultOutput1);
        scriptResultOutputConfiguration.insert(scriptResultOutput2);
        scriptResultOutputConfiguration.insert(scriptResultOutput3);

        assertThat(scriptResultOutputConfiguration.getByRunId(runUuid.toString()))
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
        scriptResultOutputConfiguration.insert(scriptResultOutput1);
        assertThat(scriptResultOutputConfiguration.get(scriptResultOutputKey))
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
        scriptResultOutputConfiguration.insert(scriptResultOutput1);

        scriptResultOutputConfiguration.insert(scriptResultOutput2);

        assertThat(scriptResultOutputConfiguration.getAll())
                .containsOnly(scriptResultOutput1, scriptResultOutput2);

        scriptResultOutputConfiguration.delete(scriptResultOutputKey);

        assertThat(scriptResultOutputConfiguration.getAll())
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
        scriptResultOutputConfiguration.insert(scriptResultOutput);

        assertThat(scriptResultOutputConfiguration.get(scriptResultOutputKey))
                .hasValue(scriptResultOutput);

        ScriptResultOutput updatedScriptResultOutput = ScriptResultOutput.builder()
                .scriptResultOutputKey(scriptResultOutputKey)
                .scriptId(scriptId)
                .value("value2")
                .build();

        scriptResultOutputConfiguration.update(updatedScriptResultOutput);

        assertThat(scriptResultOutputConfiguration.get(scriptResultOutputKey))
                .hasValue(updatedScriptResultOutput);
    }

}
