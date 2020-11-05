package io.metadew.iesi.metadata.configuration.execution;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.repository.ExecutionServerMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class ScriptExecutionConfigurationTest {

    private ExecutionServerMetadataRepository executionServerMetadataRepository;
    private ScriptExecution scriptExecution;

    @BeforeEach
    void setup() {
        executionServerMetadataRepository = RepositoryTestSetup.getExecutionServerMetadataRepository();
        executionServerMetadataRepository.createAllTables();
        scriptExecution = ScriptExecution.builder()
                .scriptExecutionKey(ScriptExecutionKey.builder().id("id").build())
                .endTimestamp(LocalDateTime.MAX)
                .runId("id")
                .scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("id").build())
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.MAX)
                .build();
    }


    @AfterEach
    void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        executionServerMetadataRepository.dropAllTables();
    }

    @Test
    void setScriptExecutionExistsTest() {
        assertThat(ScriptExecutionConfiguration.getInstance().getAll())
                .isEmpty();
    }

    @Test
    void setScriptExecutionInsert() {
        ScriptExecutionConfiguration.getInstance().insert(scriptExecution);
        assertEquals(1, ScriptExecutionConfiguration.getInstance().getAll().size());
    }
}
