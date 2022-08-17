package io.metadew.iesi.metadata.configuration.script.design;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.metadata.definition.script.design.ScriptDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptDesignTraceKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = { ScriptDesignTraceConfiguration.class })
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ScriptDesignTraceConfigurationTest {

    @Autowired
    private ScriptDesignTraceConfiguration scriptDesignTraceConfiguration;

    @Test
    void scriptGetAllEmptyTest() {
        assertThat(scriptDesignTraceConfiguration.getAll())
                .isEmpty();
    }

    @Test
    void scriptGetAllTest() {
        ScriptDesignTrace scriptDesignTrace1 = ScriptDesignTrace.builder()
                .scriptDesignTraceKey(new ScriptDesignTraceKey(UUID.randomUUID().toString(), -1L))
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        ScriptDesignTrace scriptDesignTrace2 = ScriptDesignTrace.builder()
                .scriptDesignTraceKey(new ScriptDesignTraceKey(UUID.randomUUID().toString(), -1L))
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptDesignTraceConfiguration.insert(scriptDesignTrace1);
        scriptDesignTraceConfiguration.insert(scriptDesignTrace2);

        assertThat(scriptDesignTraceConfiguration.getAll())
                .containsOnly(scriptDesignTrace1, scriptDesignTrace2);

    }

    @Test
    void scriptGetByIdTest() {
        ScriptDesignTraceKey scriptDesignTraceKey = new ScriptDesignTraceKey(UUID.randomUUID().toString(), -1L);
        ScriptDesignTrace scriptDesignTrace1 = ScriptDesignTrace.builder()
                .scriptDesignTraceKey(scriptDesignTraceKey)
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptDesignTraceConfiguration.insert(scriptDesignTrace1);

        assertThat(scriptDesignTraceConfiguration.get(scriptDesignTraceKey))
                .hasValue(scriptDesignTrace1);
    }

    @Test
    void scriptInsertTest() {
        ScriptDesignTraceKey scriptDesignTraceKey = new ScriptDesignTraceKey(UUID.randomUUID().toString(), -1L);
        ScriptDesignTrace scriptDesignTrace1 = ScriptDesignTrace.builder()
                .scriptDesignTraceKey(scriptDesignTraceKey)
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptDesignTraceConfiguration.insert(scriptDesignTrace1);
        assertThat(scriptDesignTraceConfiguration.get(scriptDesignTraceKey))
                .hasValue(scriptDesignTrace1);
    }

    @Test
    void scriptDeleteTest() {
        ScriptDesignTraceKey scriptDesignTraceKey = new ScriptDesignTraceKey(UUID.randomUUID().toString(), -1L);
        ScriptDesignTrace scriptDesignTrace1 = ScriptDesignTrace.builder()
                .scriptDesignTraceKey(scriptDesignTraceKey)
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        ScriptDesignTrace scriptDesignTrace2 = ScriptDesignTrace.builder()
                .scriptDesignTraceKey(new ScriptDesignTraceKey(UUID.randomUUID().toString(), -1L))
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptDesignTraceConfiguration.insert(scriptDesignTrace1);

        scriptDesignTraceConfiguration.insert(scriptDesignTrace2);

        assertThat(scriptDesignTraceConfiguration.getAll())
                .containsOnly(scriptDesignTrace1, scriptDesignTrace2);

        scriptDesignTraceConfiguration.delete(scriptDesignTraceKey);

        assertThat(scriptDesignTraceConfiguration.getAll())
                .containsOnly(scriptDesignTrace2);
    }

    @Test
    void scriptUpdateTest() {
        ScriptDesignTraceKey scriptDesignTraceKey = new ScriptDesignTraceKey(UUID.randomUUID().toString(), -1L);
        ScriptDesignTrace scriptDesignTrace1 = ScriptDesignTrace.builder()
                .scriptDesignTraceKey(scriptDesignTraceKey)
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptDesignTraceConfiguration.insert(scriptDesignTrace1);

        assertThat(scriptDesignTraceConfiguration.get(scriptDesignTraceKey))
                .hasValue(scriptDesignTrace1);

        scriptDesignTrace1.setScriptDescription("description 2");
        scriptDesignTrace1.setParentProcessId(-2L);
        scriptDesignTrace1.setScriptId(UUID.randomUUID().toString());
        scriptDesignTrace1.setScriptName("script 2");
        scriptDesignTrace1.setSecurityGroupName("PUBLIC 2");

        scriptDesignTraceConfiguration.update(scriptDesignTrace1);

        assertThat(scriptDesignTraceConfiguration.get(scriptDesignTraceKey))
                .hasValue(scriptDesignTrace1);
    }

}
