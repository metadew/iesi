package io.metadew.iesi.server.rest.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.connection.dto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"http", "test"})
@DirtiesContext
class ConnectionsControllerSecurityTest {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private ConnectionsController connectionsController;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private ConnectionDtoService connectionDtoService;

    @MockBean
    private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;

    @Test
    void testGetAllNoUser() {
        assertThatThrownBy(() -> connectionsController.getAll(Pageable.unpaged(), ""))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
                    // "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
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
    void testGetAllNoConnectionReadPrivilege() {
        assertThatThrownBy(() -> connectionsController.getAll(Pageable.unpaged(), ""))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_READ@PUBLIC"})
    void testGetConnectionReadPrivilege() {
        when(connectionDtoService.getAll(SecurityContextHolder.getContext().getAuthentication(), Pageable.unpaged(), new ArrayList<>()))
                .thenReturn(new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0));
        connectionsController.getAll(Pageable.unpaged(), null);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
                    // "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
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
    void testGetByNameNoConnectionRead() {
        assertThatThrownBy(() -> connectionsController.getByName("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_READ@PUBLIC"})
    void testGetByNameNozConnectionReadNoResult() {
        assertThatThrownBy(() -> connectionsController.getByName("test"))
                .isInstanceOf(MetadataDoesNotExistException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    // "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
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
    void testCreateNoConnectionsWrite() {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .securityGroupName("PUBLIC")
                .environments(
                        Stream.of(new ConnectionEnvironmentDto(
                                "env",
                                Stream.of(
                                        new ConnectionParameterDto("param1", "value1")
                                ).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .type("type")
                .description("description")
                .build();
        assertThatThrownBy(() -> connectionsController.post(connectionDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    // "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "CONNECTION_WRITE@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
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
    void testCreateConnectionsWriteWrongGroup() {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .securityGroupName("GROUPA")
                .environments(
                        Stream.of(new ConnectionEnvironmentDto(
                                "env",
                                Stream.of(
                                        new ConnectionParameterDto("param1", "value1")
                                ).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .type("type")
                .description("description")
                .build();
        assertThatThrownBy(() -> connectionsController.post(connectionDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testCreateConnectionsWrite() {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .securityGroupName("PUBLIC")
                .environments(
                        Stream.of(new ConnectionEnvironmentDto(
                                "env",
                                Stream.of(
                                        new ConnectionParameterDto("param1", "value1")
                                ).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .type("type")
                .description("description")
                .build();
        connectionsController.post(connectionDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    // "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
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
    void testUpdateBulkNoConnectionWritePrivilege() {
        List<ConnectionDto> connectionDtos = Collections.singletonList(ConnectionDto.builder()
                .name("test")
                .environments(
                        Stream.of(new ConnectionEnvironmentDto(
                                "env",
                                Stream.of(
                                        new ConnectionParameterDto("param1", "value1")
                                ).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .type("type")
                .description("description")
                .build());
        assertThatThrownBy(() -> connectionsController.putAll(connectionDtos))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testUpdateBulkConnectionWritePrivilege() {
        List<ConnectionDto> connectionDtos = Collections.singletonList(ConnectionDto.builder()
                .name("test")
                .securityGroupName("PUBLIC")
                .environments(
                        Stream.of(new ConnectionEnvironmentDto(
                                "env",
                                Stream.of(
                                        new ConnectionParameterDto("param1", "value1")
                                ).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .type("type")
                .description("description")
                .build());

        connectionsController.putAll(connectionDtos);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testUpdateBulkConnectionWritePrivilegeWrongGroup() {
        List<ConnectionDto> connectionDtos = Collections.singletonList(ConnectionDto.builder()
                .name("test")
                .securityGroupName("GROUPA")
                .environments(
                        Stream.of(new ConnectionEnvironmentDto(
                                "env",
                                Stream.of(
                                        new ConnectionParameterDto("param1", "value1")
                                ).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .type("type")
                .description("description")
                .build());
        assertThatThrownBy(() -> connectionsController.putAll(connectionDtos)).isInstanceOf(AccessDeniedException.class);
        ;
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    // "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
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
    void testUpdateSingleNoConnectionWritePrivilege() {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .securityGroupName("PUBLIC")
                .environments(
                        Stream.of(new ConnectionEnvironmentDto(
                                "env",
                                Stream.of(
                                        new ConnectionParameterDto("param1", "value1")
                                ).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .type("type")
                .description("description")
                .build();
        assertThatThrownBy(() -> connectionsController.put("test", connectionDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testUpdateSingleConnectionWritePrivilege() {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .securityGroupName("PUBLIC")
                .environments(
                        Stream.of(new ConnectionEnvironmentDto(
                                "env",
                                Stream.of(
                                        new ConnectionParameterDto("param1", "value1")
                                ).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .type("type")
                .description("description")
                .build();
        connectionsController.put("test", connectionDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@GROUPA"})
    void testUpdateSingleConnectionWritePrivilegeWrongGroup() {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .securityGroupName("PUBLIC")
                .environments(
                        Stream.of(new ConnectionEnvironmentDto(
                                "env",
                                Stream.of(
                                        new ConnectionParameterDto("param1", "value1")
                                ).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .type("type")
                .description("description")
                .build();
        assertThatThrownBy(() -> connectionsController.put("test", connectionDto))
                .isInstanceOf(AccessDeniedException.class);
        ;
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    // "CONNECTIONS_WRITE@PUBLIC",
                    "CONNECTIONS_READ@PUBLIC",
                    "ENVIRONMENTS_WRITE@PUBLIC",
                    "ENVIRONMENTS_READ@PUBLIC",
                    "EXECUTION_REQUESTS_WRITE@PUBLIC",
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
    void testDeleteByNameNoConnectionWritePrivilege() {
        assertThatThrownBy(() -> connectionsController.deleteByName("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testDeleteByNameConnectionWritePrivilege() {
        ConnectionDto connectionDto = new ConnectionDto(
                "connectionA",
                "PUBLIC",
                "http",
                "",
                new HashSet<>()
        );
        when(connectionDtoService.getByName(null, "connectionA")).thenReturn(Optional.of(connectionDto));
        connectionsController.deleteByName("connectionA");
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testDeleteByNameConnectionWritePrivilegeWrongGroup() {
        ConnectionDto connectionDto = new ConnectionDto(
                "connectionA",
                "GROUPA",
                "http",
                "",
                new HashSet<>()
        );
        when(connectionDtoService.getByName(null, "connectionA")).thenReturn(Optional.of(connectionDto));
        assertThatThrownBy(() -> connectionsController.getByName("connectionA")).isInstanceOf(AccessDeniedException.class);
    }
}
