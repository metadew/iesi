package io.metadew.iesi.server.rest.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.IDatabaseDatasetImplementationService;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDto;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDtoModelAssembler;
import io.metadew.iesi.server.rest.dataset.dto.DatasetPostDto;
import io.metadew.iesi.server.rest.dataset.dto.IDatasetDtoService;
import io.metadew.iesi.server.rest.dataset.implementation.database.DatabaseDatasetImplementationDto;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"test"})
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
    private IDatabaseDatasetImplementationService datasetImplementationService;

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
    void testGetAllNoDatasetReadPrivilege() {
        Pageable pageable = Pageable.unpaged();
        assertThatThrownBy(() -> datasetController.getAll(pageable, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetDatasetReadPrivilege() {
        when(datasetDtoService
                .fetchAll(SecurityContextHolder.getContext().getAuthentication(), Pageable.unpaged(), new HashSet<>()))
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
    void testGetImplementationsByDatasetUUIDNoReadPrivilege() {
        assertThatThrownBy(() -> datasetController.getImplementationsByDatasetUuid(UUID.randomUUID()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetImplementationsByDatasetUUIDReadPrivilege() {
        UUID datasetUuid = UUID.randomUUID();

        when(datasetService.get(new DatasetKey(datasetUuid))).thenReturn(Optional.of(Dataset.builder().securityGroupName("PUBLIC").build()));
        when(datasetDtoService
                .fetchImplementationsByDatasetUuid(datasetUuid))
                .thenReturn(new ArrayList<>());
        datasetController.getImplementationsByDatasetUuid(datasetUuid);
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
    void testGetImplementationByUUIDNoReadPrivilege() {
        assertThatThrownBy(() -> datasetController.getImplementationByUuid(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetImplementationByUUIDReadPrivilege() {
        UUID datasetUuid = UUID.randomUUID();
        UUID datasetImplementationUuid = UUID.randomUUID();
        DatabaseDatasetImplementationDto inMemoryDatasetImplementationDto = DatabaseDatasetImplementationDto.builder().uuid(datasetImplementationUuid).build();
        when(datasetService.get(new DatasetKey(datasetUuid))).thenReturn(Optional.of(Dataset.builder().securityGroupName("PUBLIC").build()));
        when(datasetDtoService
                .fetchImplementationByUuid(datasetImplementationUuid))
                .thenReturn(Optional.of(inMemoryDatasetImplementationDto));
        assertThat(datasetController.getImplementationByUuid(datasetUuid, datasetImplementationUuid)).isEqualTo(inMemoryDatasetImplementationDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetImplementationByUUIDWrongGroup() {
        UUID datasetUuid = UUID.randomUUID();
        UUID datasetImplementationUuid = UUID.randomUUID();
        when(datasetService.get(new DatasetKey(datasetUuid))).thenReturn(Optional.of(Dataset.builder().securityGroupName("PRIVATE").build()));
        when(datasetDtoService
                .fetchImplementationByUuid(datasetImplementationUuid))
                .thenReturn(Optional.of(new DatabaseDatasetImplementationDto()));
        assertThatThrownBy(() -> datasetController.getImplementationByUuid(datasetUuid, datasetImplementationUuid)).isInstanceOf(AccessDeniedException.class);
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
    void testGetByNameNoDatasetRead() {
        assertThatThrownBy(() -> datasetController.getByName("dataset"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetByIdDatasetRead() {
        UUID uuid = UUID.randomUUID();

        when(datasetService.getByName("dataset"))
                .thenReturn(Optional.of(
                        new Dataset(
                                new DatasetKey(uuid),
                                new SecurityGroupKey(uuid),
                                "PUBLIC",
                                "dataset",
                                new HashSet<>()
                        )));
        when(datasetDtoModelAssembler.toModel(
                new Dataset(
                        new DatasetKey(uuid),
                        new SecurityGroupKey(uuid),
                        "PUBLIC",
                        "dataset",
                        new HashSet<>()
                )))
                .thenReturn(DatasetDto.builder()
                        .name("dataset")
                        .securityGroupName("PUBLIC")
                        .uuid(uuid)
                        .implementations(new HashSet<>())
                        .build());
        datasetController.getByName("dataset");
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetByIdWrongSecurityGroup() {
        UUID uuid = UUID.randomUUID();
        UUID securityGroupUUID = UUID.randomUUID();
        when(datasetService.getByName("dataset"))
                .thenReturn(Optional.of(
                        new Dataset(
                                new DatasetKey(uuid),
                                new SecurityGroupKey(securityGroupUUID),
                                "PRIVATE",
                                "dataset",
                                new HashSet<>()
                        )));
        when(datasetDtoModelAssembler.toModel(
                new Dataset(
                        new DatasetKey(uuid),
                        new SecurityGroupKey(securityGroupUUID),
                        "PRIVATE",
                        "dataset",
                        new HashSet<>()
                )))
                .thenReturn(DatasetDto.builder()
                        .name("dataset")
                        .securityGroupName("PRIVATE")
                        .uuid(uuid)
                        .implementations(new HashSet<>())
                        .build());
        assertThatThrownBy(() -> datasetController.getByName("dataset")).isInstanceOf(AccessDeniedException.class);
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
    void testGetImplementationsByUuidNoDatasetRead() {
        UUID uuid = UUID.randomUUID();
        assertThatThrownBy(() -> datasetController.getImplementationsByDatasetUuid(uuid))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetImplementationsByUuidDatasetRead() {
        UUID uuid = UUID.randomUUID();
        when(datasetService.get(new DatasetKey(uuid))).thenReturn(Optional.of(Dataset.builder().securityGroupName("PUBLIC").build()));
        when(datasetDtoService.fetchImplementationsByDatasetUuid(uuid))
                .thenReturn(new ArrayList<>()
                );
        datasetController.getImplementationsByDatasetUuid(uuid);
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
                .securityGroupName("PUBLIC")
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
                .securityGroupName("PUBLIC")
                .implementations(new HashSet<>())
                .build();
        when(datasetService.getByName(datasetPostDto.getName())).thenReturn(Optional.empty());
        datasetController.create(datasetPostDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testCreateWrongSecurityGroup() {
        DatasetPostDto datasetPostDto = DatasetPostDto.builder()
                .name("dataset")
                .securityGroupName("PRIVATE")
                .implementations(new HashSet<>())
                .build();
        assertThatThrownBy(() -> datasetController.create(datasetPostDto)).isInstanceOf(AccessDeniedException.class);

    }

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
    void testUpdateNoDatasetWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        DatasetPutDto datasetPutDto = DatasetPutDto.builder()
                .name("dataset")
                .securityGroupName("PUBLIC")
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
                .name("dataset")
                .securityGroupName("PUBLIC")
                .implementations(new HashSet<>())
                .build();
        Dataset dataset = Dataset.builder()
                .metadataKey(new DatasetKey(uuid))
                .name("dataset")
                .securityGroupKey(new SecurityGroupKey(uuid))
                .securityGroupName("PUBLIC")
                .datasetImplementations(new HashSet<>())
                .build();

        when(datasetService.exists(new DatasetKey(uuid)))
                .thenReturn(true);
        when(datasetService.get(new DatasetKey(uuid)))
                .thenReturn(Optional.of(dataset));

        datasetController.update(uuid, datasetPutDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testUpdateDatasetWrongSecurityGroup() {
        UUID uuid = UUID.randomUUID();
        DatasetPutDto datasetPutDto = DatasetPutDto.builder()
                .name("dataset")
                .securityGroupName("PRIVATE")
                .implementations(new HashSet<>())
                .build();

        assertThatThrownBy(() -> datasetController.update(uuid, datasetPutDto)).isInstanceOf(AccessDeniedException.class);
    }

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
    void testDeleteByUuidNoDatasetWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        assertThatThrownBy(() -> datasetController.delete(uuid))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testDeleteByUuidDatasetWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        when(datasetService.get(new DatasetKey(uuid)))
                .thenReturn(Optional.of(Dataset.builder().securityGroupName("PUBLIC").build()));
        when(datasetService.exists(new DatasetKey(uuid)))
                .thenReturn(true);
        datasetController.delete(uuid);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testDeleteByUuidDatasetWrongSecurityGroup() {
        UUID uuid = UUID.randomUUID();
        when(datasetService.get(new DatasetKey(uuid)))
                .thenReturn(Optional.of(Dataset.builder().securityGroupName("PRIVATE").build()));
        assertThatThrownBy(() -> datasetController.delete(uuid)).isInstanceOf(AccessDeniedException.class);
    }

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
    void testDeleteImplementationsByDatasetUUIDNoWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        assertThatThrownBy(() -> datasetController.deleteImplementationsByDatasetUuid(uuid))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testDeleteImplementationsByDatasetUUIDWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        when(datasetService.get(new DatasetKey(uuid)))
                .thenReturn(Optional.of(Dataset.builder().securityGroupName("PUBLIC").build()));
        when(datasetService.exists(new DatasetKey(uuid)))
                .thenReturn(true);
        datasetController.deleteImplementationsByDatasetUuid(uuid);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testDeleteImplementationsByDatasetUUIDWrongSecurityGroup() {
        UUID uuid = UUID.randomUUID();
        when(datasetService.get(new DatasetKey(uuid)))
                .thenReturn(Optional.of(Dataset.builder().securityGroupName("PRIVATE").build()));
        assertThatThrownBy(() -> datasetController.deleteImplementationsByDatasetUuid(uuid)).isInstanceOf(AccessDeniedException.class);

    }

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
    void testDeleteImplementationByUUIDNoWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        assertThatThrownBy(() -> datasetController.deleteImplementationByUuid(UUID.randomUUID(), uuid))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testDeleteImplementationByUUIDWritePrivilege() {
        UUID uuid = UUID.randomUUID();
        UUID datasetUuid = UUID.randomUUID();
        when(datasetService.get(new DatasetKey(datasetUuid)))
                .thenReturn(Optional.of(Dataset.builder().securityGroupName("PUBLIC").build()));
        when(datasetImplementationService.exists(new DatasetImplementationKey(uuid)))
                .thenReturn(true);
        datasetController.deleteImplementationByUuid(datasetUuid, uuid);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testDeleteImplementationByUUIDWrongSecurityGroup() {
        UUID datasetUUID = UUID.randomUUID();
        UUID implementationUUID = UUID.randomUUID();

        when(datasetService.get(new DatasetKey(datasetUUID))).thenReturn(Optional.of(Dataset.builder().securityGroupName("PRIVATE").build()));

        assertThatThrownBy(() -> datasetController.deleteImplementationByUuid(datasetUUID, implementationUUID)).isInstanceOf(AccessDeniedException.class);
    }

}
