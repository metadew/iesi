package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.connection.ConnectionService;
import io.metadew.iesi.server.rest.connection.ConnectionsController;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.connection.dto.ConnectionParameterDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;

    @Test
    void testGetAllNoUser() throws Exception {
        assertThatThrownBy(() -> connectionsController.getAll())
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
    void testGetAllNoConnectionReadPrivilege() throws Exception {
        assertThatThrownBy(() -> connectionsController.getAll())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_READ@PUBLIC"})
    void testGetConnectionReadPrivilege() throws Exception {
        connectionsController.getAll();
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
    void testGetByNameNoConnectionRead() throws Exception {
        assertThatThrownBy(() -> connectionsController.getByName("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_READ@PUBLIC"})
    void testGetByNameConnectionRead() throws Exception {
        connectionsController.getByName("test");
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
    void testGetByNameAndVersionNoConnectionsReadPrivilege() throws Exception {
        assertThatThrownBy(() -> connectionsController.get("test", "env"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_READ@PUBLIC"})
    void testGetByNameAndVersionAdminConnectionsReadPrivilege() throws Exception {
        Connection connection = Connection.builder()
                .connectionKey(new ConnectionKey("test", "env"))
                .type("type")
                .description("description")
                .parameters(Stream.of(
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "env"), "param1"), "value1")
                ).collect(Collectors.toList()))
                .build();
        when(connectionService.getByNameAndEnvironment("test", "env"))
                .thenReturn(Optional.of(connection));
        connectionsController.get("test", "env");
    }

    // create connections
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
    void testCreateNoConnectionsWrite() throws Exception {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .environment("env")
                .type("type")
                .description("description")
                .parameters(Stream.of(
                        new ConnectionParameterDto("param1", "value1")
                ).collect(Collectors.toList()))
                .build();
        assertThatThrownBy(() -> connectionsController.post(connectionDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testCreateConnectionsWrite() throws Exception {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .environment("env")
                .type("type")
                .description("description")
                .parameters(Stream.of(
                        new ConnectionParameterDto("param1", "value1")
                ).collect(Collectors.toList()))
                .build();
        connectionsController.post(connectionDto);
    }

    // update bulk connections
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
    void testUpdateBulkNoConnectionWritePrivilege() throws Exception {
        List<ConnectionDto> connectionDtos = Collections.singletonList(ConnectionDto.builder()
                        .name("test")
                        .environment("env")
                        .type("type")
                        .description("description")
                        .parameters(Stream.of(
                                new ConnectionParameterDto("param1", "value1")
                        ).collect(Collectors.toList()))
                        .build());
        assertThatThrownBy(() -> connectionsController.putAll(connectionDtos))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testUpdateBulkConnectionWritePrivilege() throws Exception {
        List<ConnectionDto> connectionDtos = Collections.singletonList(ConnectionDto.builder()
                .name("test")
                .environment("env")
                .type("type")
                .description("description")
                .parameters(Stream.of(
                        new ConnectionParameterDto("param1", "value1")
                ).collect(Collectors.toList()))
                .build());
        connectionsController.putAll(connectionDtos);
    }

    // update single connection
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
    void testUpdateSingleNoConnectionWritePrivilege() throws Exception {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .environment("env")
                .type("type")
                .description("description")
                .parameters(Stream.of(
                        new ConnectionParameterDto("param1", "value1")
                ).collect(Collectors.toList()))
                .build();
        assertThatThrownBy(() -> connectionsController.put("test", "env", connectionDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testUpdateSingleConnectionWritePrivilege() throws Exception {
        ConnectionDto connectionDto = ConnectionDto.builder()
                .name("test")
                .environment("env")
                .type("type")
                .description("description")
                .parameters(Stream.of(
                        new ConnectionParameterDto("param1", "value1")
                ).collect(Collectors.toList()))
                .build();
        connectionsController.put("test", "env", connectionDto);
    }

    //delete all
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
    void testDeleteAllNoConnectionWritePrivilege() throws Exception {
        assertThatThrownBy(() -> connectionsController.deleteAll())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testDeleteAllConnectionWritePrivilege() throws Exception {
        connectionsController.deleteAll();
    }

    //delete by name
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
    void testDeleteByNameNoConnectionWritePrivilege() throws Exception {
        assertThatThrownBy(() -> connectionsController.deleteByName("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testDeleteByNameConnectionWritePrivilege() throws Exception {
        connectionsController.deleteByName("test");
    }

    //delete by name and version
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
    void testDeleteByNameAndVersionNoConnectionWritePrivilege() throws Exception {
        assertThatThrownBy(() -> connectionsController.delete("test", "env"))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    @WithIesiUser(username = "spring",
            authorities = {"CONNECTIONS_WRITE@PUBLIC"})
    void testDeleteByNameAndVersionConnectionWritePrivilege() throws Exception {
        connectionsController.delete("test", "env");
    }

}
