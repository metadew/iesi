package io.metadew.iesi.server.rest.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationService;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.dataset.*;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"http", "test", "security"})
@DirtiesContext
class DatasetsControllerSecurityTest {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private DatasetController datasetController;

    @MockBean
    private DatasetDtoModelAssembler datasetDtoModelAssembler;

    @MockBean
    private IDatasetService datasetService;

    @MockBean
    private IDatasetImplementationService datasetImplementationService;

    @MockBean
    private PagedResourcesAssembler<DatasetDto> datasetDtoPagedResourcesAssembler;

    @MockBean
    private IDatasetDtoService datasetDtoService;

    @Test
    void testGetAllNoUser() {
        Pageable pageable = Pageable.unpaged();
        assertThatThrownBy(() -> datasetController.getAll(pageable, null))
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
                    // "DATASETS_READ@PUBLIC",
                    "DATASETS_WRITE@PUBLIC"})
    void testGetAllNoScriptReadPrivilege() {
        Pageable pageable = Pageable.unpaged();
        assertThatThrownBy(() -> datasetController.getAll(pageable, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetScriptReadPrivilege() {
        when(datasetDtoService
                .fetchAll(Pageable.unpaged(), new HashSet<>()))
                .thenReturn(new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0));
        datasetController.getAll(Pageable.unpaged(), null);
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
                    // "DATASETS_READ@PUBLIC",
                    "DATASETS_WRITE@PUBLIC"})
    void testGetByNameNoScriptRead() {
        UUID uuid = UUID.randomUUID();
        assertThatThrownBy(() -> datasetController.get(uuid))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetByIdDatasetRead() {
        UUID uuid = UUID.randomUUID();
        when(datasetService.get(new DatasetKey(uuid)))
                .thenReturn(Optional.of(
                        new Dataset(
                                new DatasetKey(uuid),
                                "dataset",
                                new HashSet<>()
                        )));
        when(datasetDtoModelAssembler.toModel(
                new Dataset(
                        new DatasetKey(uuid),
                        "dataset",
                        new HashSet<>()
                )))
                .thenReturn(DatasetDto.builder()
                        .name("dataset")
                        .uuid(uuid)
                        .implementations(new HashSet<>())
                        .build());
        datasetController.get(uuid);
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
                    "DATASETS_READ@PUBLIC"
                    //"DATASETS_WRITE@PUBLIC"
            })
    void testCreateNoDatasetsWrite() {
        DatasetPostDto datasetPostDto = DatasetPostDto.builder()
                .name("dataset")
                .implementations(new HashSet<>())
                .build();
        assertThatThrownBy(() -> datasetController.create(datasetPostDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testCreateDatasetsWrite() {
        DatasetPostDto datasetPostDto = DatasetPostDto.builder()
                .name("dataset")
                .implementations(new HashSet<>())
                .build();
        datasetController.create(datasetPostDto);
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
                    "DATASETS_READ@PUBLIC"
                    //"DATASETS_WRITE@PUBLIC
            })
    void testUpdateBulkNoScriptWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        DatasetPutDto datasetPutDto = DatasetPutDto.builder()
                .uuid(uuid)
                .name("dataset")
                .implementations(new HashSet<>())
                .build();

        assertThatThrownBy(() -> datasetController.update(uuid, datasetPutDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testUpdateDatasetWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        DatasetPutDto datasetPutDto = DatasetPutDto.builder()
                .uuid(uuid)
                .name("dataset")
                .implementations(new HashSet<>())
                .build();
        Dataset dataset = Dataset.builder()
                .metadataKey(new DatasetKey(uuid))
                .name("dataset")
                .datasetImplementations(new HashSet<>())
                .build();

        when(datasetService.exists(new DatasetKey(uuid)))
                .thenReturn(true);
        when(datasetService.get(new DatasetKey(uuid)))
                .thenReturn(Optional.of(dataset));

        datasetController.update(uuid, datasetPutDto);
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
                    "DATASETS_READ@PUBLIC"
                    //"DATASETS_WRITE@PUBLIC"
            })
    void testDeleteByNameNoScriptWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        assertThatThrownBy(() -> datasetController.delete(uuid))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testDeleteByNameScriptWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        when(datasetService.exists(new DatasetKey(uuid)))
                .thenReturn(true);
        datasetController.delete(uuid);
    }

}
