package io.metadew.iesi.metadata.configuration.script.design;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.script.design.ScriptDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptDesignTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptDesignTraceConfigurationTest {

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

    @Test
    void scriptGetAllEmptyTest() {
        assertThat(ScriptDesignTraceConfiguration.getInstance().getAll())
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
        ScriptDesignTraceConfiguration.getInstance().insert(scriptDesignTrace1);
        ScriptDesignTraceConfiguration.getInstance().insert(scriptDesignTrace2);

        assertThat(ScriptDesignTraceConfiguration.getInstance().getAll())
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
        ScriptDesignTraceConfiguration.getInstance().insert(scriptDesignTrace1);

        assertThat(ScriptDesignTraceConfiguration.getInstance().get(scriptDesignTraceKey))
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
        ScriptDesignTraceConfiguration.getInstance().insert(scriptDesignTrace1);
        assertThat(ScriptDesignTraceConfiguration.getInstance().get(scriptDesignTraceKey))
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
        ScriptDesignTraceConfiguration.getInstance().insert(scriptDesignTrace1);

        ScriptDesignTraceConfiguration.getInstance().insert(scriptDesignTrace2);

        assertThat(ScriptDesignTraceConfiguration.getInstance().getAll())
                .containsOnly(scriptDesignTrace1, scriptDesignTrace2);

        ScriptDesignTraceConfiguration.getInstance().delete(scriptDesignTraceKey);

        assertThat(ScriptDesignTraceConfiguration.getInstance().getAll())
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
        ScriptDesignTraceConfiguration.getInstance().insert(scriptDesignTrace1);

        assertThat(ScriptDesignTraceConfiguration.getInstance().get(scriptDesignTraceKey))
                .hasValue(scriptDesignTrace1);

        scriptDesignTrace1.setScriptDescription("description 2");
        scriptDesignTrace1.setParentProcessId(-2L);
        scriptDesignTrace1.setScriptId(UUID.randomUUID().toString());
        scriptDesignTrace1.setScriptName("script 2");
        scriptDesignTrace1.setSecurityGroupName("PUBLIC 2");

        ScriptDesignTraceConfiguration.getInstance().update(scriptDesignTrace1);

        assertThat(ScriptDesignTraceConfiguration.getInstance().get(scriptDesignTraceKey))
                .hasValue(scriptDesignTrace1);
    }

}
