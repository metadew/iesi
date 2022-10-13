package io.metadew.iesi.metadata.configuration.script.trace;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.script.trace.ScriptTrace;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, ScriptTraceConfiguration.class })
@ActiveProfiles("test")
class ScriptTraceConfigurationTest {

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    private ScriptTraceConfiguration scriptTraceConfiguration;

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
        assertThat(scriptTraceConfiguration.getAll())
                .isEmpty();
    }

    @Test
    void scriptGetAllTest() {
        ScriptTrace scriptTrace1 = ScriptTrace.builder()
                .scriptTraceKey(new ScriptTraceKey(UUID.randomUUID().toString(), -1L))
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        ScriptTrace scriptTrace2 = ScriptTrace.builder()
                .scriptTraceKey(new ScriptTraceKey(UUID.randomUUID().toString(), -1L))
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptTraceConfiguration.insert(scriptTrace1);
        scriptTraceConfiguration.insert(scriptTrace2);

        assertThat(scriptTraceConfiguration.getAll())
                .containsOnly(scriptTrace1, scriptTrace2);

    }

    @Test
    void scriptGetByIdTest() {
        ScriptTraceKey scriptTraceKey = new ScriptTraceKey(UUID.randomUUID().toString(), -1L);
        ScriptTrace scriptTrace1 = ScriptTrace.builder()
                .scriptTraceKey(scriptTraceKey)
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptTraceConfiguration.insert(scriptTrace1);

        assertThat(scriptTraceConfiguration.get(scriptTraceKey))
                .hasValue(scriptTrace1);
    }

    @Test
    void scriptInsertTest() {
        ScriptTraceKey scriptTraceKey = new ScriptTraceKey(UUID.randomUUID().toString(), -1L);
        ScriptTrace scriptTrace1 = ScriptTrace.builder()
                .scriptTraceKey(scriptTraceKey)
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptTraceConfiguration.insert(scriptTrace1);
        assertThat(scriptTraceConfiguration.get(scriptTraceKey))
                .hasValue(scriptTrace1);
    }

    @Test
    void scriptDeleteTest() {
        ScriptTraceKey scriptTraceKey = new ScriptTraceKey(UUID.randomUUID().toString(), -1L);
        ScriptTrace scriptTrace1 = ScriptTrace.builder()
                .scriptTraceKey(scriptTraceKey)
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        ScriptTrace scriptTrace2 = ScriptTrace.builder()
                .scriptTraceKey(new ScriptTraceKey(UUID.randomUUID().toString(), -1L))
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptTraceConfiguration.insert(scriptTrace1);

        scriptTraceConfiguration.insert(scriptTrace2);

        assertThat(scriptTraceConfiguration.getAll())
                .containsOnly(scriptTrace1, scriptTrace2);

        scriptTraceConfiguration.delete(scriptTraceKey);

        assertThat(scriptTraceConfiguration.getAll())
                .containsOnly(scriptTrace2);
    }

    @Test
    void scriptUpdateTest() {
        ScriptTraceKey scriptTraceKey = new ScriptTraceKey(UUID.randomUUID().toString(), -1L);
        ScriptTrace scriptTrace1 = ScriptTrace.builder()
                .scriptTraceKey(scriptTraceKey)
                .scriptId(UUID.randomUUID().toString())
                .parentProcessId(-1L)
                .scriptName("script")
                .scriptDescription("description")
                .securityGroupName("PUBLIC")
                .build();
        scriptTraceConfiguration.insert(scriptTrace1);

        assertThat(scriptTraceConfiguration.get(scriptTraceKey))
                .hasValue(scriptTrace1);

        scriptTrace1.setScriptDescription("description 2");
        scriptTrace1.setParentProcessId(-2L);
        scriptTrace1.setScriptId(UUID.randomUUID().toString());
        scriptTrace1.setScriptName("script 2");
        scriptTrace1.setSecurityGroupName("PUBLIC 2");

        scriptTraceConfiguration.update(scriptTrace1);

        assertThat(scriptTraceConfiguration.get(scriptTraceKey))
                .hasValue(scriptTrace1);
    }

}
