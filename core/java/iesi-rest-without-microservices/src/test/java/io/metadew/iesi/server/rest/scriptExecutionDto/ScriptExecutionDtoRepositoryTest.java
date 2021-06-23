package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.action.design.ActionDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.design.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.action.trace.ActionParameterTraceConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptLabelDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.action.design.ActionDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import io.metadew.iesi.metadata.definition.action.result.ActionResult;
import io.metadew.iesi.metadata.definition.action.result.ActionResultOutput;
import io.metadew.iesi.metadata.definition.action.trace.ActionParameterTrace;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.design.ScriptLabelDesignTrace;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class ScriptExecutionDtoRepositoryTest {

    @Autowired
    private ScriptExecutionDtoRepository scriptExecutionDtoRepository;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ExecutionRequestConfiguration executionRequestConfiguration;

    @Autowired
    private ScriptExecutionConfiguration scriptExecutionConfiguration;

    @Autowired
    private ScriptConfiguration scriptConfiguration;

    @Autowired
    private ScriptResultConfiguration scriptResultConfiguration;

    @Autowired
    private ScriptResultOutputConfiguration scriptResultOutputConfiguration;

    @Autowired
    private ActionResultConfiguration actionResultConfiguration;

    @Autowired
    private ActionResultOutputConfiguration actionResultOutputConfiguration;

    @Autowired
    private ActionDesignTraceConfiguration actionDesignTraceConfiguration;

    @Autowired
    private ActionParameterDesignTraceConfiguration actionParameterDesignTraceConfiguration;

    @Autowired
    private ActionParameterTraceConfiguration actionParameterTraceConfiguration;

    @Autowired
    private ScriptLabelDesignTraceConfiguration scriptLabelDesignTraceConfiguration;

    @AfterEach
    void cleanup() {
        metadataRepositoryConfiguration.clearAllTables();
    }

    @Test
    void getAllNoExecutionRequests() {
        assertThat(scriptExecutionDtoRepository.getAll(null).isEmpty());
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllSingleExecutionRequest() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> scriptExecutionMap = ScriptExecutionBuilder
                .generateExecutionRequest(
                        1,
                        requestTimestamp,
                        1,
                        1,
                        "script1",
                        1L,
                        "PUBLIC",
                        "test",
                        1,
                        1,
                        -1L,
                        -1L,
                        1,
                        2,
                        "Create");

        executionRequestConfiguration.insert((ExecutionRequest) scriptExecutionMap.get("executionRequest"));
        scriptConfiguration.insert((Script) scriptExecutionMap.get("script"));
        scriptExecutionConfiguration.insert((ScriptExecution) scriptExecutionMap.get("scriptExecution"));
        scriptResultConfiguration.insert((ScriptResult) scriptExecutionMap.get("scriptResult"));

        IntStream.range(0, 1).boxed()
                .forEach(scriptResultOutputIndex -> {
                    scriptResultOutputConfiguration.insert(
                            (ScriptResultOutput) scriptExecutionMap.get(String.format("scriptResultOutput%d", scriptResultOutputIndex))
                    );
                });

        IntStream.range(0, 2).boxed()
                .forEach(actionResultIndex -> {
                    actionResultConfiguration.insert(
                            (ActionResult) scriptExecutionMap.get(String.format("actionResult%d", actionResultIndex))
                    );
                });

        IntStream.range(0, 2).boxed()
                .forEach(actionResultOutputIndex -> {
                    actionResultOutputConfiguration.insert(
                            (ActionResultOutput) scriptExecutionMap.get(String.format("actionResultOutput%d", actionResultOutputIndex))
                    );
                });

        IntStream.range(0, 2).boxed()
                .forEach(actionDesignTraceIndex -> {
                    actionDesignTraceConfiguration.insert(
                            (ActionDesignTrace) scriptExecutionMap.get(String.format("actionDesignTrace%d", actionDesignTraceIndex))
                    );
                });

        IntStream.range(0, 2).boxed()
                .forEach(actionParameterDesignTraceIndex -> {
                    actionParameterDesignTraceConfiguration.insert(
                            (ActionParameterDesignTrace) scriptExecutionMap.get(String.format("actionParameterDesignTrace%d", actionParameterDesignTraceIndex))
                    );
                });

        IntStream.range(0, 2).boxed()
                .forEach(actionParameterTraceIndex -> {
                    actionParameterTraceConfiguration.insert(
                            (ActionParameterTrace) scriptExecutionMap.get(String.format("actionParameterTrace%d", actionParameterTraceIndex))
                    );
                });

        scriptLabelDesignTraceConfiguration.insert((ScriptLabelDesignTrace) scriptExecutionMap.get("scriptLabelDesignTrace"));

        System.out.println("scriptExecutions: " + scriptExecutionDtoRepository.getAll(null));
        // assertThat(scriptExecutionDtoRepository.getAll())
        //        .containsOnly((ScriptExecutionDto) scriptExecutionMap.get("scriptExecutionDto"));
    }
}