package io.metadew.iesi.server.rest.scriptExecutionDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.scriptExecutionDto.ScriptExecutionDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.ScriptExecutionDtoController;
import io.metadew.iesi.server.rest.scriptExecutionDto.ScriptExecutionDtoService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"http", "test"})
@DirtiesContext
class ScriptExecutionDtosControllerSecurityTest {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private ScriptExecutionDtoController scriptExecutionDtoController;

    @MockBean
    private ScriptExecutionDtoService scriptExecutionService;

    @Test
    void testGetAllNoUser() throws Exception {
        assertThatThrownBy(() -> scriptExecutionDtoController.getAll())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
                    "EXECUTION_REQUESTS_READ@PUBLIC",
                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
                    // "SCRIPT_EXECUTIONS_READ@PUBLIC",
                    "IMPERSONATIONS_READ@PUBLIC",
                    "IMPERSONATIONS_WRITE@PUBLIC",
                    "SCRIPT_RESULTS_READ@PUBLIC",
                    "USERS_WRITE@PUBLIC",
                    "USERS_READ@PUBLIC",
                    "USERS_DELETE@PUBLIC",
                    "TEAMS_WRITE@PUBLIC",
                    "TEAMS_READ@PUBLIC",
                    "ROLES_WRITE@PUBLIC",
                    "GROUPS_WRITE@PUBLIC",
                    "GROUPS_READ@PUBLIC",
                    "DATASETS_READ@PUBLIC",
                    "DATASETS_WRITE@PUBLIC"})
    void testGetAllNoScriptExecutionReadPrivilege() throws Exception {
        assertThatThrownBy(() -> scriptExecutionDtoController.getAll())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPT_EXECUTIONS_READ@PUBLIC"})
    void testGetScriptExecutionReadPrivilege() throws Exception {
        when(scriptExecutionService
                .getAll(SecurityContextHolder.getContext().getAuthentication()))
                .thenReturn(new ArrayList<>());
        scriptExecutionDtoController.getAll();
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
                    "EXECUTION_REQUESTS_READ@PUBLIC",
                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
                    // "SCRIPT_EXECUTIONS_READ@PUBLIC",
                    "IMPERSONATIONS_READ@PUBLIC",
                    "IMPERSONATIONS_WRITE@PUBLIC",
                    "SCRIPT_RESULTS_READ@PUBLIC",
                    "USERS_WRITE@PUBLIC",
                    "USERS_READ@PUBLIC",
                    "USERS_DELETE@PUBLIC",
                    "TEAMS_WRITE@PUBLIC",
                    "TEAMS_READ@PUBLIC",
                    "ROLES_WRITE@PUBLIC",
                    "GROUPS_WRITE@PUBLIC",
                    "GROUPS_READ@PUBLIC",
                    "DATASETS_READ@PUBLIC",
                    "DATASETS_WRITE@PUBLIC"})
    void testGetByNameNoScriptExecutionRead() throws Exception {
        assertThatThrownBy(() -> scriptExecutionDtoController.getByRunId("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPT_EXECUTIONS_READ@PUBLIC"})
    void testGetByNameScriptExecutionRead() throws Exception {
        List<ScriptExecutionDto> executionRequestDto = Collections.singletonList(ScriptExecutionDto.builder()
                .environment("environment")
                .runId("runId")
                .scriptName("name")
                .scriptVersion(1L)
                .actions(new ArrayList<>())
                .designLabels(new ArrayList<>())
                .endTimestamp(LocalDateTime.now())
                .startTimestamp(LocalDateTime.now())
                .executionLabels(new ArrayList<>())
                .inputParameters(new ArrayList<>())
                .output(new ArrayList<>())
                .parentProcessId(1L)
                .processId(1L)
                .scriptId("scriptId")
                .status(ScriptRunStatus.RUNNING)
                .build());
        when(scriptExecutionService
                .getByRunId(SecurityContextHolder.getContext().getAuthentication(), "runId"))
                .thenReturn(executionRequestDto);
        scriptExecutionDtoController.getByRunId("id");
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
                    "EXECUTION_REQUESTS_READ@PUBLIC",
                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
                    // "SCRIPT_EXECUTIONS_READ@PUBLIC",
                    "IMPERSONATIONS_READ@PUBLIC",
                    "IMPERSONATIONS_WRITE@PUBLIC",
                    "SCRIPT_RESULTS_READ@PUBLIC",
                    "USERS_WRITE@PUBLIC",
                    "USERS_READ@PUBLIC",
                    "USERS_DELETE@PUBLIC",
                    "TEAMS_WRITE@PUBLIC",
                    "TEAMS_READ@PUBLIC",
                    "ROLES_WRITE@PUBLIC",
                    "GROUPS_WRITE@PUBLIC",
                    "GROUPS_READ@PUBLIC",
                    "DATASETS_READ@PUBLIC",
                    "DATASETS_WRITE@PUBLIC"})
    void testGetByRunIdAndProcessIdNoScriptExecutionRead() throws Exception {
        assertThatThrownBy(() -> scriptExecutionDtoController.getByRunIdAndProcessId("test", 1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPT_EXECUTIONS_READ@PUBLIC"})
    void testGetByRunIdAndProcessIdScriptExecutionRead() throws Exception {
        ScriptExecutionDto scriptExecutionDtos = ScriptExecutionDto.builder()
                .environment("environment")
                .runId("runId")
                .scriptName("name")
                .scriptVersion(1L)
                .securityGroupName("PUBLIC")
                .actions(new ArrayList<>())
                .designLabels(new ArrayList<>())
                .endTimestamp(LocalDateTime.now())
                .startTimestamp(LocalDateTime.now())
                .executionLabels(new ArrayList<>())
                .inputParameters(new ArrayList<>())
                .output(new ArrayList<>())
                .parentProcessId(1L)
                .processId(1L)
                .scriptId("scriptId")
                .status(ScriptRunStatus.RUNNING)
                .build();
        when(scriptExecutionService
                .getByRunIdAndProcessId(null, "runId", 1L))
                .thenReturn(Optional.of(scriptExecutionDtos));
        scriptExecutionDtoController.getByRunIdAndProcessId("runId", 1L);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPT_EXECUTIONS_READ@PUBLIC"})
    void testGetByRunIdAndProcessIdScriptExecutionWrongSecurityGroup() throws Exception {
        ScriptExecutionDto scriptExecutionDtos = ScriptExecutionDto.builder()
                .environment("environment")
                .runId("runId")
                .processId(1L)
                .scriptName("name")
                .scriptVersion(1L)
                .securityGroupName("PRIVATE")
                .actions(new ArrayList<>())
                .designLabels(new ArrayList<>())
                .endTimestamp(LocalDateTime.now())
                .startTimestamp(LocalDateTime.now())
                .executionLabels(new ArrayList<>())
                .inputParameters(new ArrayList<>())
                .output(new ArrayList<>())
                .parentProcessId(1L)
                .scriptId("scriptId")
                .status(ScriptRunStatus.RUNNING)
                .build();
        when(scriptExecutionService
                .getByRunIdAndProcessId(null, "runId", 1L))
                .thenReturn(Optional.of(scriptExecutionDtos));
        assertThatThrownBy(() -> scriptExecutionDtoController.getByRunIdAndProcessId("runId", 1L))
                .isInstanceOf(AccessDeniedException.class);
    }

}
