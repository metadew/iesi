package io.metadew.iesi.server.rest.executionrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestController;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestService;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoModelAssembler;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestPostDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestPostDto;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"http", "test"})
@DirtiesContext
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

    @MockBean
    private ScriptConfiguration scriptConfiguration;

    @Test
    void testGetAllNoUser() {
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
    void testGetAllNoExecutionRequestReadPrivilege() {
        Pageable pageable = Pageable.unpaged();
        assertThatThrownBy(() -> executionRequestController.getAll(pageable, null, null, null, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_READ@PUBLIC"})
    void testGetExecutionRequestReadPrivilege() {
        when(executionRequestService
                .getAll(any(), eq(Pageable.unpaged()), eq(new ArrayList<>())))
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
    void testGetByNameNoExecutionRequestRead() {
        assertThatThrownBy(() -> executionRequestController.getById("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_READ@PUBLIC"})
    void testGetByNameExecutionRequestRead() {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .environment("test")
                                .exit(false)
                                .executionRequestId("id")
                                .securityGroupName("PUBLIC")
                                .impersonations(new HashSet<>())
                                .parameters(new HashSet<>())
                                .scriptExecutionRequestId("id")
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED)
                                .build()
                ).collect(Collectors.toSet()))
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        when(executionRequestService
                .getById(null, "id"))
                .thenReturn(Optional.of(executionRequestDto));
        executionRequestController.getById("id");
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_READ@PUBLIC"})
    void testGetByNameExecutionRequestWrongSecurityGroup() {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .environment("test")
                                .exit(false)
                                .executionRequestId("id")
                                .securityGroupName("PRIVATE")
                                .impersonations(new HashSet<>())
                                .parameters(new HashSet<>())
                                .scriptExecutionRequestId("id")
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED)
                                .build()
                ).collect(Collectors.toSet()))
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        when(executionRequestService
                .getById(null, "id"))
                .thenReturn(Optional.of(executionRequestDto));
        assertThatThrownBy(() -> executionRequestController.getById("id"))
                .isInstanceOf(AccessDeniedException.class);
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
    void testCreateNoExecutionRequestsWrite() {
        ExecutionRequestPostDto executionRequestDto = ExecutionRequestPostDto.builder()
                .executionRequestLabels(new HashSet<>())
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestPostDto.builder()
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .environment("test")
                                .exit(false)
                                .impersonations(new HashSet<>())
                                .parameters(new HashSet<>())
                                .build()
                ).collect(Collectors.toSet()))
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
    void testCreateExecutionRequestsWrite() {
        ExecutionRequestPostDto executionRequestDto = ExecutionRequestPostDto.builder()
                .executionRequestLabels(new HashSet<>())
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestPostDto.builder()
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .environment("test")
                                .exit(false)
                                .impersonations(new HashSet<>())
                                .parameters(new HashSet<>())
                                .build()
                ).collect(Collectors.toSet()))
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        when(scriptConfiguration
                .getSecurityGroup("script1"))
                .thenReturn(Optional.of(new SecurityGroup(
                        new SecurityGroupKey(UUID.randomUUID()),
                        "PUBLIC",
                        new HashSet<>(),
                        new HashSet<>())));
        executionRequestController.post(executionRequestDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_WRITE@PUBLIC"})
    void testCreateExecutionRequestsWriteWrongSecurityGroup() {
        ExecutionRequestPostDto executionRequestDto = ExecutionRequestPostDto.builder()
                .executionRequestLabels(new HashSet<>())
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestPostDto.builder()
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .environment("test")
                                .exit(false)
                                .impersonations(new HashSet<>())
                                .parameters(new HashSet<>())
                                .build()
                ).collect(Collectors.toSet()))
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        when(scriptConfiguration
                .getSecurityGroup("script1"))
                .thenReturn(Optional.of(new SecurityGroup(
                        new SecurityGroupKey(UUID.randomUUID()),
                        "PRIVATE",
                        new HashSet<>(),
                        new HashSet<>())));
        assertThatThrownBy(() -> executionRequestController.post(executionRequestDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    // update bulk components
//    @Test
//    @WithIesiUser(username = "spring",
//            authorities = {
//                    "SCRIPTS_WRITE@PUBLIC",
//                    "SCRIPTS_READ@PUBLIC",
//                    "COMPONENTS_WRITE@PUBLIC",
//                    "COMPONENTS_READ@PUBLIC",
//                    "CONNECTIONS_WRITE@PUBLIC",
//                    "CONNECTIONS_READ@PUBLIC",
//                    "ENVIRONMENTS_WRITE@PUBLIC",
//                    "ENVIRONMENTS_READ@PUBLIC",
//                    // "EXECUTION_REQUESTS_WRITE@PUBLIC",
//                    "EXECUTION_REQUESTS_READ@PUBLIC",
//                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
//                    "SCRIPT_EXECUTIONS_READ@PUBLIC",
//                    "IMPERSONATIONS_READ@PUBLIC",
//                    "IMPERSONATIONS_WRITE@PUBLIC",
//                    "SCRIPT_RESULTS_READ@PUBLIC",
//                    "USERS_WRITE@PUBLIC",
//                    "USERS_READ@PUBLIC",
//                    "USERS_DELETE@PUBLIC",
//                    "TEAMS_WRITE@PUBLIC",
//                    "TEAMS_READ@PUBLIC",
//                    "ROLES_WRITE@PUBLIC",
//                    "GROUPS_WRITE@PUBLIC",
//                    "GROUPS_READ@PUBLIC",
//                    "DATASETS_READ@PUBLIC",
//                    "DATASETS_WRITE@PUBLIC"})
//    void testUpdateBulkNoExecutionRequestWritePrivilege() {
//        List<ExecutionRequestDto> executionRequestDtos = Collections.singletonList(ExecutionRequestDto.builder()
//                .executionRequestId("id")
//                .executionRequestLabels(new HashSet<>())
//                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
//                .requestTimestamp(LocalDateTime.now())
//                .scriptExecutionRequests(new HashSet<>())
//                .context("context")
//                .description("description")
//                .email("email")
//                .name("name")
//                .scope("scope")
//                .build());
//        assertThatThrownBy(() -> executionRequestController.putAll(executionRequestDtos))
//                .isInstanceOf(AccessDeniedException.class);
//    }
//
//    @Test
//    @WithIesiUser(username = "spring",
//            authorities = {"EXECUTION_REQUESTS_WRITE@PUBLIC"})
//    void testUpdateBulkExecutionRequestWritePrivilege() {
//        List<ExecutionRequestDto> executionRequestDtos = Collections.singletonList(
//                ExecutionRequestDto.builder()
//                        .executionRequestId("id")
//                        .executionRequestLabels(new HashSet<>())
//                        .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
//                        .requestTimestamp(LocalDateTime.now())
//                        .scriptExecutionRequests(new HashSet<>())
//                        .context("context")
//                        .description("description")
//                        .email("email")
//                        .name("name")
//                        .scope("scope")
//                        .build());
//        executionRequestController.putAll(executionRequestDtos);
//    }

    // update single component
//    @Test
//    @WithIesiUser(username = "spring",
//            authorities = {
//                    "SCRIPTS_WRITE@PUBLIC",
//                    "SCRIPTS_READ@PUBLIC",
//                    "COMPONENTS_WRITE@PUBLIC",
//                    "COMPONENTS_READ@PUBLIC",
//                    "CONNECTIONS_WRITE@PUBLIC",
//                    "CONNECTIONS_READ@PUBLIC",
//                    "ENVIRONMENTS_WRITE@PUBLIC",
//                    "ENVIRONMENTS_READ@PUBLIC",
//                    // "EXECUTION_REQUESTS_WRITE@PUBLIC",
//                    "EXECUTION_REQUESTS_READ@PUBLIC",
//                    "SCRIPT_EXECUTIONS_WRITE@PUBLIC",
//                    "SCRIPT_EXECUTIONS_READ@PUBLIC",
//                    "IMPERSONATIONS_READ@PUBLIC",
//                    "IMPERSONATIONS_WRITE@PUBLIC",
//                    "SCRIPT_RESULTS_READ@PUBLIC",
//                    "USERS_WRITE@PUBLIC",
//                    "USERS_READ@PUBLIC",
//                    "USERS_DELETE@PUBLIC",
//                    "TEAMS_WRITE@PUBLIC",
//                    "TEAMS_READ@PUBLIC",
//                    "ROLES_WRITE@PUBLIC",
//                    "GROUPS_WRITE@PUBLIC",
//                    "GROUPS_READ@PUBLIC",
//                    "DATASETS_READ@PUBLIC",
//                    "DATASETS_WRITE@PUBLIC"})
//    void testUpdateSingleNoExecutionRequestWritePrivilege() {
//        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
//                .executionRequestId("id")
//                .executionRequestLabels(new HashSet<>())
//                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
//                .requestTimestamp(LocalDateTime.now())
//                .scriptExecutionRequests(new HashSet<>())
//                .context("context")
//                .description("description")
//                .email("email")
//                .name("name")
//                .scope("scope")
//                .build();
//        assertThatThrownBy(() -> executionRequestController.put("id", executionRequestDto))
//                .isInstanceOf(AccessDeniedException.class);
//    }
//
//    @Test
//    @WithIesiUser(username = "spring",
//            authorities = {"EXECUTION_REQUESTS_WRITE@PUBLIC"})
//    void testUpdateSingleExecutionRequestWritePrivilege() {
//        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
//                .executionRequestId("id")
//                .executionRequestLabels(new HashSet<>())
//                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
//                .requestTimestamp(LocalDateTime.now())
//                .scriptExecutionRequests(new HashSet<>())
//                .context("context")
//                .description("description")
//                .email("email")
//                .name("name")
//                .scope("scope")
//                .build();
//        executionRequestController.put("id", executionRequestDto);
//    }

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
    void testDeleteByNameNoExecutionRequestWritePrivilege() {
        assertThatThrownBy(() -> executionRequestController.deleteById("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_WRITE@PUBLIC"})
    void testDeleteByNameExecutionRequestWritePrivilege() {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .environment("test")
                                .exit(false)
                                .executionRequestId("id")
                                .securityGroupName("PUBLIC")
                                .impersonations(new HashSet<>())
                                .parameters(new HashSet<>())
                                .scriptExecutionRequestId("id")
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED)
                                .build()
                ).collect(Collectors.toSet()))
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        when(executionRequestService
                .getById(null, "id"))
                .thenReturn(Optional.of(executionRequestDto));
        executionRequestController.deleteById("id");
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_WRITE@PRIVATE"})
    void testDeleteByNameExecutionRequestWrongSecurityGroup() {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("id")
                .executionRequestLabels(new HashSet<>())
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .environment("test")
                                .exit(false)
                                .executionRequestId("id")
                                .securityGroupName("PUBLIC")
                                .impersonations(new HashSet<>())
                                .parameters(new HashSet<>())
                                .scriptExecutionRequestId("id")
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED)
                                .build()
                ).collect(Collectors.toSet()))
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();
        when(executionRequestService
                .getById(null, "id"))
                .thenReturn(Optional.of(executionRequestDto));
        assertThatThrownBy(() -> executionRequestController.deleteById("id"))
                .isInstanceOf(AccessDeniedException.class);
    }

}
