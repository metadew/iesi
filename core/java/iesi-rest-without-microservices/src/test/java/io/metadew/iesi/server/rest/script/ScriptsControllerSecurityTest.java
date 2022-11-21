package io.metadew.iesi.server.rest.script;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.script.audit.ScriptDesignAuditService;
import io.metadew.iesi.server.rest.script.dto.*;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import io.metadew.iesi.server.rest.user.UserDto;
import io.metadew.iesi.server.rest.user.UserDtoRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles("test")
@DirtiesContext
class ScriptsControllerSecurityTest {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private ScriptsController scriptsController;

    @MockBean
    private ScriptService scriptService;

    @MockBean
    private ScriptDtoService scriptDtoService;

    @MockBean
    private ScriptDtoModelAssembler scriptDtoModelAssembler;

    @MockBean
    private ScriptPostDtoService scriptPostDtoService;

    @MockBean
    private PagedResourcesAssembler<ScriptDto> scriptDtoPagedResourcesAssembler;

    @MockBean
    private UserDtoRepository userDtoRepository;

    @MockBean
    private UserDto userDto;

    @MockBean
    private ScriptDesignAuditService scriptDesignAuditService;

    @Test
    void testGetAllNoUser() {
        Pageable pageable = Pageable.unpaged();
        List<String> expansions = new ArrayList<>();
        assertThatThrownBy(() -> scriptsController.getAll(pageable, expansions, null, null, null))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    // "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
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
    void testGetAllNoScriptReadPrivilege() {
        Pageable pageable = Pageable.unpaged();
        List<String> expansions = new ArrayList<>();
        assertThatThrownBy(() -> scriptsController.getAll(pageable, expansions, null, null, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_READ@PUBLIC"})
    void testGetScriptReadPrivilege() {
        when(scriptDtoService
                .getAll(SecurityContextHolder.getContext().getAuthentication(), Pageable.unpaged(), new ArrayList<>(), false, new ArrayList<>()))
                .thenReturn(new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0));
        scriptsController.getAll(Pageable.unpaged(), new ArrayList<>(), null, null, null);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    // "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
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
    void testGetByNameNoScriptRead() {
        Pageable pageable = Pageable.unpaged();
        List<String> expansions = new ArrayList<>();
        assertThatThrownBy(() -> scriptsController.getByName(pageable, "test", expansions, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_READ@PUBLIC"})
    void testGetByNameScriptRead() {
        when(scriptDtoService
                .getByName(null, Pageable.unpaged(), "test", new ArrayList<>(), false))
                .thenReturn(new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0));
        scriptsController.getByName(Pageable.unpaged(), "test", new ArrayList<>(), null);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC",
                    // "SCRIPTS_READ@PUBLIC",
                    "COMPONENTS_WRITE@PUBLIC",
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
    void testGetByNameAndVersionNoScriptsReadPrivilege() {
        assertThatThrownBy(() -> scriptsController.get("test", 1L, new ArrayList<>()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_READ@PUBLIC"})
    void testGetByNameAndVersionAdminScriptsReadPrivilege() {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("scriptDto")
                .securityGroupName("PUBLIC")
                .description("description")
                .version(new ScriptVersionDto(1L, "description"))
                .parameters(new HashSet<>())
                .actions(new HashSet<>())
                .labels(new HashSet<>())
                .scriptExecutionInformation(null)
                .scriptSchedulingInformation(null)
                .build();
        when(scriptDtoService.getByNameAndVersion(null, "test", 1L, new ArrayList<>()))
                .thenReturn(Optional.of(scriptDto));
        scriptsController.get("test", 1L, new ArrayList<>());
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_READ@PUBLIC"})
    void testGetByNameAndVersionWrongSecurityGroup() {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("scriptDto")
                .securityGroupName("PRIVATE")
                .description("description")
                .version(new ScriptVersionDto(1L, "description"))
                .parameters(new HashSet<>())
                .actions(new HashSet<>())
                .labels(new HashSet<>())
                .scriptExecutionInformation(null)
                .scriptSchedulingInformation(null)
                .build();
        when(scriptDtoService.getByNameAndVersion(null, "test", 1L, new ArrayList<>()))
                .thenReturn(Optional.of(scriptDto));
        assertThatThrownBy(() -> scriptsController.get("test", 1L, new ArrayList<>()))
                .isInstanceOf(AccessDeniedException.class);
    }

    // create components
    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    // "SCRIPTS_WRITE@PUBLIC",
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
    void testCreateNoScriptsWrite() {
        ScriptPostDto scriptDto = ScriptPostDto.builder()
                .name("scriptDto")
                .securityGroupName("PUBLIC")
                .description("description")
                .version(new ScriptVersionDto(1L, "description"))
                .parameters(new HashSet<>())
                .actions(new HashSet<>())
                .labels(new HashSet<>())
                .build();
        assertThatThrownBy(() -> scriptsController.post(scriptDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC"})
    void testCreateScriptsWrite() {
        ScriptPostDto scriptDto = ScriptPostDto.builder()
                .name("scriptDto")
                .securityGroupName("PUBLIC")
                .description("description")
                .version(new ScriptVersionDto(1L, "description"))
                .parameters(new HashSet<>())
                .actions(new HashSet<>())
                .labels(new HashSet<>())
                .build();
        scriptsController.post(scriptDto);
    }

    // update bulk components
    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    // "SCRIPTS_WRITE@PUBLIC",
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
    void testUpdateBulkNoScriptWritePrivilege() {
        List<ScriptPostDto> scriptPostDtos = Collections.singletonList(
                ScriptPostDto.builder()
                        .name("scriptDto")
                        .securityGroupName("PUBLIC")
                        .description("description")
                        .version(new ScriptVersionDto(1L, "description"))
                        .parameters(new HashSet<>())
                        .actions(new HashSet<>())
                        .labels(new HashSet<>())
                        .build());
        assertThatThrownBy(() -> scriptsController.putAll(scriptPostDtos))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC"})
    void testUpdateBulkScriptWritePrivilege() {
        List<ScriptPostDto> scriptPostDtos = Collections.singletonList(
                ScriptPostDto.builder()
                        .name("scriptDto")
                        .securityGroupName("PUBLIC")
                        .description("description")
                        .version(new ScriptVersionDto(1L, "description"))
                        .parameters(new HashSet<>())
                        .actions(new HashSet<>())
                        .labels(new HashSet<>())
                        .build());
        scriptsController.putAll(scriptPostDtos);
    }

    // update single component
    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    // "SCRIPTS_WRITE@PUBLIC",
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
    void testUpdateSingleNoScriptWritePrivilege() {
        ScriptPostDto scriptPostDto = ScriptPostDto.builder()
                .name("scriptDto")
                .securityGroupName("securityGroup")
                .description("description")
                .version(new ScriptVersionDto(1L, "description"))
                .parameters(new HashSet<>())
                .actions(new HashSet<>())
                .labels(new HashSet<>())
                .build();
        assertThatThrownBy(() -> scriptsController.put("component", 1L, scriptPostDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC"})
    void testUpdateSingleScriptWritePrivilege() {
        ScriptPostDto scriptDto = ScriptPostDto.builder()
                .name("scriptDto")
                .securityGroupName("PUBLIC")
                .description("description")
                .version(new ScriptVersionDto(1L, "description"))
                .parameters(new HashSet<>())
                .actions(new HashSet<>())
                .labels(new HashSet<>())
                .build();
        scriptsController.put("scriptDto", 1L, scriptDto);
    }

    //delete by name and version
    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    // "SCRIPTS_WRITE@PUBLIC",
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
    void testDeleteByNameAndVersionNoScriptWritePrivilege() {
        assertThatThrownBy(() -> scriptsController.delete("test", 1L))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_WRITE@PUBLIC"})
    void testDeleteByNameAndVersionScriptWritePrivilege() {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("scriptDto")
                .securityGroupName("PUBLIC")
                .description("description")
                .version(new ScriptVersionDto(1L, "description"))
                .parameters(new HashSet<>())
                .actions(new HashSet<>())
                .labels(new HashSet<>())
                .scriptExecutionInformation(null)
                .scriptSchedulingInformation(null)
                .build();
        when(scriptDtoService.getByNameAndVersion(null, "test", 1L, new ArrayList<>()))
                .thenReturn(Optional.of(scriptDto));
        scriptsController.delete("test", 1L);
    }

    void testDeleteByNameAndVersionWrongSecurityGroup() {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("scriptDto")
                .securityGroupName("PRIVATE")
                .description("description")
                .version(new ScriptVersionDto(1L, "description"))
                .parameters(new HashSet<>())
                .actions(new HashSet<>())
                .labels(new HashSet<>())
                .scriptExecutionInformation(null)
                .scriptSchedulingInformation(null)
                .build();
        when(scriptDtoService.getByNameAndVersion(null, "test", 1L, new ArrayList<>()))
                .thenReturn(Optional.of(scriptDto));
        assertThatThrownBy(() -> scriptsController.delete("test", 1L))
                .isInstanceOf(AccessDeniedException.class);
    }

}
