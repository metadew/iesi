package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.component.ComponentService;
import io.metadew.iesi.server.rest.component.ComponentsController;
import io.metadew.iesi.server.rest.component.IComponentService;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.dto.ComponentDtoResourceAssembler;
import io.metadew.iesi.server.rest.component.dto.ComponentParameterDto;
import io.metadew.iesi.server.rest.component.dto.ComponentVersionDto;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
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
class ComponentsControllerSecurityTest {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private ComponentsController componentsController;

    @MockBean
    private ComponentService componentService;

    @MockBean
    private ComponentDtoResourceAssembler componentDtoResourceAssembler;

    @Test
    void testGetAllNoUser() throws Exception {
        assertThatThrownBy(() -> componentsController.getAll())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    //"COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
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
    void testGetAllNoComponentReadPrivilege() throws Exception {
        assertThatThrownBy(() -> componentsController.getAll())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_READ@PUBLIC"})
    void testGetComponentReadPrivilege() throws Exception {
        componentsController.getAll();
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    //"COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
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
    void testGetByNameNoComponentRead() throws Exception {
        assertThatThrownBy(() -> componentsController.getByName("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_READ@PUBLIC"})
    void testGetByNameComponentRead() throws Exception {
        componentsController.getByName("test");
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
                    //"COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
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
    void testGetByNameAndVersionNoComponentsReadPrivilege() throws Exception {
        assertThatThrownBy(() -> componentsController.get("test", 1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_READ@PUBLIC"})
    void testGetByNameAndVersionAdminComponentsReadPrivilege() throws Exception {
        Component component = Component.builder()
                .name("component")
                .type("type")
                .description("description")
                .attributes(new ArrayList<>())
                .version(new ComponentVersion(new ComponentVersionKey("component", 1L), "description"))
                .parameters(Stream.of(
                        new ComponentParameter(new ComponentParameterKey("component", 1L, "param1"), "value1")
                ).collect(Collectors.toList()))
                .build();
        when(componentService.getByNameAndVersion("test", 1L))
                .thenReturn(Optional.of(component));
        componentsController.get("test", 1L);
    }

    // create components
    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    //"COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
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
    void testCreateNoComponentsWrite() throws Exception {
        ComponentDto componentDto = ComponentDto.builder()
                .name("component")
                .type("type")
                .description("description")
                .attributes(new ArrayList<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toList()))
                .build();
        assertThatThrownBy(() -> componentsController.post(componentDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_WRITE@PUBLIC"})
    void testCreateComponentsWrite() throws Exception {
        ComponentDto componentDto = ComponentDto.builder()
                .name("component")
                .type("type")
                .description("description")
                .attributes(new ArrayList<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toList()))
                .build();
        componentsController.post(componentDto);
    }

    // update bulk components
    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    //"COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
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
    void testUpdateBulkNoComponentWritePrivilege() throws Exception {
        List<ComponentDto> componentDto = Collections.singletonList(ComponentDto.builder()
                .name("component")
                .type("type")
                .description("description")
                .attributes(new ArrayList<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toList()))
                .build());
        assertThatThrownBy(() -> componentsController.putAll(componentDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_WRITE@PUBLIC"})
    void testUpdateBulkComponentWritePrivilege() throws Exception {
        List<ComponentDto> componentDto = Collections.singletonList(ComponentDto.builder()
                .name("component")
                .type("type")
                .description("description")
                .attributes(new ArrayList<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toList()))
                .build());
        componentsController.putAll(componentDto);
    }

    // update single component
    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    //"COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
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
    void testUpdateSingleNoComponentWritePrivilege() throws Exception {
        ComponentDto componentDto = ComponentDto.builder()
                .name("component")
                .type("type")
                .description("description")
                .attributes(new ArrayList<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toList()))
                .build();
        assertThatThrownBy(() -> componentsController.put("component", 1L, componentDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_WRITE@PUBLIC"})
    void testUpdateSingleComponentWritePrivilege() throws Exception {
        ComponentDto componentDto = ComponentDto.builder()
                .name("component")
                .type("type")
                .description("description")
                .attributes(new ArrayList<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toList()))
                .build();
        componentsController.put("component", 1L, componentDto);
    }

    //delete all
    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    //"COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
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
    void testDeleteAllNoComponentWritePrivilege() throws Exception {
        assertThatThrownBy(() -> componentsController.deleteAll())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_WRITE@PUBLIC"})
    void testDeleteAllComponentWritePrivilege() throws Exception {
        componentsController.deleteAll();
    }

    //delete by name
    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    //"COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
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
    void testDeleteByNameNoComponentWritePrivilege() throws Exception {
        assertThatThrownBy(() -> componentsController.deleteByName("test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_WRITE@PUBLIC"})
    void testDeleteByNameComponentWritePrivilege() throws Exception {
        componentsController.deleteByName("test");
    }

    //delete by name and version
    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    "SCRIPTS_READ@PUBLIC",
                    //"COMPONENTS_WRITE@PUBLIC",
                    "COMPONENTS_READ@PUBLIC",
                    "CONNECTIONS_WRITE@PUBLIC",
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
    void testDeleteByNameAndVersionNoComponentWritePrivilege() throws Exception {
        assertThatThrownBy(() -> componentsController.delete("test", 1L))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_WRITE@PUBLIC"})
    void testDeleteByNameAndVersionComponentWritePrivilege() throws Exception {
        componentsController.delete("test", 1L);
    }

}
