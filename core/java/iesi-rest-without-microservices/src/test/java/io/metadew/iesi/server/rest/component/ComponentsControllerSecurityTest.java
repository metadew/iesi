package io.metadew.iesi.server.rest.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.component.ComponentService;
import io.metadew.iesi.server.rest.component.ComponentsController;
import io.metadew.iesi.server.rest.component.dto.*;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
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
class ComponentsControllerSecurityTest {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private ComponentsController componentsController;

    @MockBean
    private ComponentService componentService;

    @MockBean
    private ComponentDtoService componentDtoService;

    @MockBean
    private ComponentDtoResourceAssembler componentDtoResourceAssembler;

    @MockBean
    private ComponentDtoRepository componentDtoRepository;

    @MockBean
    private PagedResourcesAssembler<ComponentDto> componentDtoPagedResourcesAssembler;

    @Test
    void testGetAllNoUser() throws Exception {
        Pageable pageable = Pageable.unpaged();
        assertThatThrownBy(() -> componentsController.getAll(pageable, null))
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
        Pageable pageable = Pageable.unpaged();
        assertThatThrownBy(() -> componentsController.getAll(pageable, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_READ@PUBLIC"})
    void testGetComponentReadPrivilege() throws Exception {
        when(componentDtoService
                .getAll(Pageable.unpaged(), new ArrayList<>()))
                .thenReturn(new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0));
        componentsController.getAll(Pageable.unpaged(), null);
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
        Pageable pageable = Pageable.unpaged();
        assertThatThrownBy(() -> componentsController.getByName(pageable, "test"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"COMPONENTS_READ@PUBLIC"})
    void testGetByNameComponentRead() throws Exception {
        Pageable pageable = Pageable.unpaged();
        when(componentDtoService
                .getByName(Pageable.unpaged(), "test"))
                .thenReturn(new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0));
        componentsController.getByName(pageable, "test");
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
        ComponentDto componentDto = ComponentDto.builder()
                .name("test")
                .type("type")
                .description("description")
                .attributes(new HashSet<>())
                .version(new ComponentVersionDto(1L, "version description"))
                .parameters(new HashSet<>())
                .attributes(new HashSet<>())
                .build();
        when(componentDtoService.getByNameAndVersion("test", 1L))
                .thenReturn(Optional.of(componentDto));
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
                .attributes(new HashSet<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toSet()))
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
                .attributes(new HashSet<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toSet()))
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
                .attributes(new HashSet<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toSet()))
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
                .attributes(new HashSet<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toSet()))
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
                .attributes(new HashSet<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toSet()))
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
                .attributes(new HashSet<>())
                .version(new ComponentVersionDto(1, "description"))
                .parameters(Stream.of(new ComponentParameterDto("param1", "value1")).collect(Collectors.toSet()))
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
