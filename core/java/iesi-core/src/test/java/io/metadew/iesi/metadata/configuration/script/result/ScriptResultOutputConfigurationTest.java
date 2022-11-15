package io.metadew.iesi.metadata.configuration.script.result;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, ScriptResultOutputConfiguration.class })
@ActiveProfiles("test")
class ScriptResultOutputConfigurationTest {

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    ScriptResultOutputConfiguration scriptResultOutputConfiguration;

    @BeforeEach
    void setup() {
        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
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
