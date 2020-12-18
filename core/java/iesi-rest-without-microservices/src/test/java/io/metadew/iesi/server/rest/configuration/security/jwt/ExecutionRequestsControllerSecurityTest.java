package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestController;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestService;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoModelAssembler;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"http", "test", "security"})
class ExecutionRequestsControllerSecurityTest {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private ExecutionRequestController executionRequestController;

    @MockBean
    private ExecutionRequestDtoModelAssembler executionRequestDtoModelAssembler;

    @MockBean
    private ExecutionRequestService executionRequestService;

    @MockBean
    private PagedResourcesAssembler<ExecutionRequestDto> executionRequestDtoResourceAssemblerPage;

    @Test
    void testGetAllNoUser() throws Exception {
        Pageable pageable = Pageable.unpaged();
        assertThatThrownBy(() -> executionRequestController.getAll(pageable, null, null, null, null))
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
                    // "EXECUTION_REQUESTS_READ@PUBLIC",
                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
                    "SCRIPT_EXECUTIONS_READ@PUBLIC",
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
    void testGetAllNoExecutionRequestReadPrivilege() throws Exception {
        Pageable pageable = Pageable.unpaged();
        assertThatThrownBy(() -> executionRequestController.getAll(pageable, null, null, null, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_READ@PUBLIC"})
    void testGetExecutionRequestReadPrivilege() throws Exception {
        when(executionRequestService
                .getAll(Pageable.unpaged(), new ArrayList<>()))
                .thenReturn(new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0));
        executionRequestController.getAll(Pageable.unpaged(), null, null, null, null);
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
                    // "EXECUTION_REQUESTS_READ@PUBLIC",
                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
                    "SCRIPT_EXECUTIONS_READ@PUBLIC",
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
    void testGetByNameNoExecutionRequestRead() throws Exception {
        assertThatThrownBy(() -> executionRequestController.getById("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_READ@PUBLIC"})
    void testGetByNameExecutionRequestRead() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(new HashSet<>())
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        when(executionRequestService
                .getById("id"))
                .thenReturn(Optional.of(executionRequestDto));
        executionRequestController.getById("id");
    }

    // create components
    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    // "EXECUTION_REQUESTS_WRITE@PUBLIC",
                    "EXECUTION_REQUESTS_READ@PUBLIC",
                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
                    "SCRIPT_EXECUTIONS_READ@PUBLIC",
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
    void testCreateNoExecutionRequestsWrite() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(new HashSet<>())
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        assertThatThrownBy(() -> executionRequestController.post(executionRequestDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_WRITE@PUBLIC"})
    void testCreateExecutionRequestsWrite() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(new HashSet<>())
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        executionRequestController.post(executionRequestDto);
    }

    // update bulk components
    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    // "EXECUTION_REQUESTS_WRITE@PUBLIC",
                    "EXECUTION_REQUESTS_READ@PUBLIC",
                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
                    "SCRIPT_EXECUTIONS_READ@PUBLIC",
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
    void testUpdateBulkNoExecutionRequestWritePrivilege() throws Exception {
        List<ExecutionRequestDto> executionRequestDtos = Collections.singletonList(ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(new HashSet<>())
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build());
        assertThatThrownBy(() -> executionRequestController.putAll(executionRequestDtos))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_WRITE@PUBLIC"})
    void testUpdateBulkExecutionRequestWritePrivilege() throws Exception {
        List<ExecutionRequestDto> executionRequestDtos = Collections.singletonList(
                ExecutionRequestDto.builder()
                        .executionRequestId("id")
                        .executionRequestLabels(new HashSet<>())
                        .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                        .requestTimestamp(LocalDateTime.now())
                        .scriptExecutionRequests(new HashSet<>())
                        .context("context")
                        .description("description")
                        .email("email")
                        .name("name")
                        .scope("scope")
                        .build());
        executionRequestController.putAll(executionRequestDtos);
    }

    // update single component
    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    // "EXECUTION_REQUESTS_WRITE@PUBLIC",
                    "EXECUTION_REQUESTS_READ@PUBLIC",
                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
                    "SCRIPT_EXECUTIONS_READ@PUBLIC",
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
    void testUpdateSingleNoExecutionRequestWritePrivilege() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(new HashSet<>())
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        assertThatThrownBy(() -> executionRequestController.put("id", executionRequestDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_WRITE@PUBLIC"})
    void testUpdateSingleExecutionRequestWritePrivilege() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(new HashSet<>())
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        executionRequestController.put("id", executionRequestDto);
    }

    //delete by name
    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    // "EXECUTION_REQUESTS_WRITE@PUBLIC",
                    "EXECUTION_REQUESTS_READ@PUBLIC",
                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
                    "SCRIPT_EXECUTIONS_READ@PUBLIC",
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
    void testDeleteByNameNoExecutionRequestWritePrivilege() throws Exception {
        assertThatThrownBy(() -> executionRequestController.deleteById("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_WRITE@PUBLIC"})
    void testDeleteByNameExecutionRequestWritePrivilege() throws Exception {
        executionRequestController.deleteById("test");
    }

}
