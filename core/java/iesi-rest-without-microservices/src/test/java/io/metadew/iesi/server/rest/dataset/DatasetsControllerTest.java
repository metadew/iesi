package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelPostDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValuePostDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationPostDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"http", "test"})
@DirtiesContext
class DatasetsControllerTest {

    @Autowired
    private DatasetController datasetController;

    @MockBean
    private DatasetDtoModelAssembler datasetDtoModelAssembler;

    @MockBean
    private PagedResourcesAssembler<DatasetDto> datasetDtoPagedResourcesAssembler;

    @MockBean
    private IDatasetService datasetService;

    @MockBean
    private IDatasetImplementationService datasetImplementationService;

    @MockBean
    private IDatasetDtoService datasetDtoService;

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetAll() {
        DatasetDto datasetDto1 = DatasetDto.builder()
                .uuid(UUID.randomUUID())
                .implementations(new HashSet<>())
                .name("dataset1")
                .build();
        DatasetDto datasetDto2 = DatasetDto.builder()
                .uuid(UUID.randomUUID())
                .implementations(new HashSet<>())
                .name("dataset2")
                .build();
        Page<DatasetDto> page = new PageImpl<>(
                Stream.of(
                        datasetDto1,
                        datasetDto2
                ).collect(Collectors.toList()),
                Pageable.unpaged(),
                2);
        when(datasetDtoService
                .fetchAll(Pageable.unpaged(), new HashSet<>()))
                .thenReturn(page);

        when(datasetDtoModelAssembler.toModel(datasetDto1))
                .thenReturn(datasetDto1);
        when(datasetDtoModelAssembler.toModel(datasetDto2))
                .thenReturn(datasetDto2);
        PagedModel<DatasetDto> pagedModel = new PagedModel<>(
                Stream.of(
                        datasetDto1,
                        datasetDto2
                ).collect(Collectors.toList()),
                new PagedModel.PageMetadata(2, 0, 2, 1),
                new Link("http://localhost", "self")
        );
        when(datasetDtoPagedResourcesAssembler.toModel(eq(page), (RepresentationModelAssembler<DatasetDto, DatasetDto>) any()))
                .thenReturn(pagedModel);

        assertThat(datasetController.getAll(Pageable.unpaged(), null))
                .isEqualTo(pagedModel);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetAllEmpty() {
        Page<DatasetDto> page = new PageImpl<>(
                new ArrayList<>(),
                Pageable.unpaged(),
                2);
        when(datasetDtoService
                .fetchAll(Pageable.unpaged(), new HashSet<>()))
                .thenReturn(page);

        PagedModel<DatasetDto> pagedModel = new PagedModel<>(
                new ArrayList<>(),
                new PagedModel.PageMetadata(0, 0, 0, 1),
                new Link("http://localhost", "self")
        );
        when(datasetDtoPagedResourcesAssembler.toEmptyModel(page, DatasetDto.class))
                .thenReturn(new PagedModel<>(
                        new ArrayList<>(),
                        new PagedModel.PageMetadata(0, 0, 0, 1),
                        new Link("http://localhost", "self")));

        assertThat(datasetController.getAll(Pageable.unpaged(), null))
                .isEqualTo(pagedModel);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetById() {
        UUID uuid = UUID.randomUUID();
        Dataset dataset = new Dataset(
                new DatasetKey(uuid),
                "dataset",
                new HashSet<>()
        );
        DatasetDto datasetDto = DatasetDto.builder()
                .name("dataset")
                .uuid(uuid)
                .implementations(new HashSet<>())
                .build();
        when(datasetService.get(new DatasetKey(uuid)))
                .thenReturn(Optional.of(dataset));
        when(datasetDtoModelAssembler.toModel(dataset))
                .thenReturn(datasetDto);
        assertThat(datasetController.get(uuid))
                .isEqualTo(datasetDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetByIdNotFound() {
        UUID uuid = UUID.randomUUID();
        when(datasetService.get(new DatasetKey(uuid)))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> datasetController.get(uuid))
                .isInstanceOf(MetadataDoesNotExistException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetByUuidImplementations() {
        UUID uuid = UUID.randomUUID();

        List<DatasetImplementationDto> datasetImplementationDtoList = new ArrayList<>();
        datasetImplementationDtoList.add(new InMemoryDatasetImplementationDto());

        when(datasetDtoService.fetchImplementationsByUuid(uuid))
                .thenReturn(datasetImplementationDtoList);

        assertThat(datasetController.getImplementationsByUuid(uuid))
                .containsAll(datasetImplementationDtoList);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_READ@PUBLIC"})
    void testGetByUuidImplementationsNotFound() {
        UUID uuid = UUID.randomUUID();

        when(datasetDtoService.fetchImplementationsByUuid(uuid))
                .thenReturn(null);

        assertThatThrownBy(() -> datasetController.get(uuid))
                .isInstanceOf(MetadataDoesNotExistException.class);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testCreateDatasetsWrite() {
        DatasetPostDto datasetPostDto = DatasetPostDto.builder()
                .name("dataset")
                .implementations(Stream.of(
                        InMemoryDatasetImplementationPostDto.builder()
                                .keyValues(Stream.of(
                                        InMemoryDatasetImplementationKeyValuePostDto.builder()
                                                .key("key1")
                                                .value("value1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .labels(Stream.of(
                                        DatasetImplementationLabelPostDto.builder()
                                                .label("label1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build()
                ).collect(Collectors.toSet()))
                .build();
        Dataset expectedDataset = Dataset.builder()
                .metadataKey(new DatasetKey(UUID.randomUUID()))
                .name("dataset")
                .datasetImplementations(Stream.of(
                        InMemoryDatasetImplementation.builder()
                                .metadataKey(new DatasetImplementationKey(UUID.randomUUID()))
                                .datasetKey(new DatasetKey(UUID.randomUUID()))
                                .name("dataset")
                                .keyValues(Stream.of(
                                        InMemoryDatasetImplementationKeyValue.builder()
                                                .metadataKey(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()))
                                                .datasetImplementationKey(new DatasetImplementationKey(UUID.randomUUID()))
                                                .key("key1")
                                                .value("value1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .datasetImplementationLabels(Stream.of(
                                        DatasetImplementationLabel.builder()
                                                .metadataKey(new DatasetImplementationLabelKey(UUID.randomUUID()))
                                                .datasetImplementationKey(new DatasetImplementationKey(UUID.randomUUID()))
                                                .value("label1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .build();

        DatasetDto datasetDto = DatasetDto.builder()
                .name("dataset")
                .uuid(UUID.randomUUID())
                .implementations(Stream.of(
                        InMemoryDatasetImplementationDto.builder()
                                .keyValues(Stream.of(
                                        InMemoryDatasetImplementationKeyValueDto.builder()
                                                .uuid(UUID.randomUUID())
                                                .key("key1")
                                                .value("value1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .labels(Stream.of(
                                        DatasetImplementationLabelDto.builder()
                                                .uuid(UUID.randomUUID())
                                                .label("label1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build()
                )
                        .map(e-> e.getUuid())
                        .collect(Collectors.toSet()))
                .build();
        when(datasetDtoModelAssembler.toModel((Dataset) any()))
                .thenReturn(datasetDto);
        when(datasetService.exists("dataset"))
                .thenReturn(false);

        ResponseEntity<DatasetDto> responseEntity = datasetController.create(datasetPostDto);

        verify(datasetService, times(1))
                .create(argThat(dataset -> equalsWithoutUuid(expectedDataset, dataset)));
        verify(datasetDtoModelAssembler, times(1))
                .toModel((Dataset) argThat(dataset -> equalsWithoutUuid(expectedDataset, (Dataset) dataset)));
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isEqualTo(datasetDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testCreateDatasetsAlreadyExists() {
        DatasetPostDto datasetPostDto = DatasetPostDto.builder()
                .name("dataset")
                .implementations(Stream.of(
                        InMemoryDatasetImplementationPostDto.builder()
                                .keyValues(Stream.of(
                                        InMemoryDatasetImplementationKeyValuePostDto.builder()
                                                .key("key1")
                                                .value("value1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .labels(Stream.of(
                                        DatasetImplementationLabelPostDto.builder()
                                                .label("label1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build()
                ).collect(Collectors.toSet()))
                .build();

        when(datasetService.exists("dataset"))
                .thenReturn(true);

        ResponseEntity<DatasetDto> responseEntity = datasetController.create(datasetPostDto);

        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    public boolean equalsWithoutUuid(Dataset dataset1, Dataset dataset2) {
        if (!dataset1.getName().equals(dataset2.getName())) {
            return false;
        } else if (dataset1.getDatasetImplementations().size() != dataset2.getDatasetImplementations().size()) {
            return false;
        } else if (dataset1.getDatasetImplementations().stream()
                .noneMatch(datasetImplementation1 -> dataset2.getDatasetImplementations().stream()
                        .anyMatch(datasetImplementation2 -> equalsWithoutUuid(datasetImplementation1, datasetImplementation2)))) {
            return false;
        } else {
            return true;
        }
    }

    public boolean equalsWithoutUuid(DatasetImplementation datasetImplementation1, DatasetImplementation datasetImplementation2) {
        if (!(datasetImplementation1 instanceof InMemoryDatasetImplementation
                && datasetImplementation2 instanceof InMemoryDatasetImplementation)) {
            return false;
        } else if (!datasetImplementation1.getName().equals(datasetImplementation2.getName())) {
            return false;
        } else if (((InMemoryDatasetImplementation) datasetImplementation1).getKeyValues().size() != ((InMemoryDatasetImplementation) datasetImplementation2).getKeyValues().size()) {
            return false;
        } else if (((InMemoryDatasetImplementation) datasetImplementation1).getKeyValues().stream()
                .noneMatch(keyValue1 -> ((InMemoryDatasetImplementation) datasetImplementation2).getKeyValues().stream()
                        .anyMatch(keyValue2 -> keyValue2.getKey().equals(keyValue1.getKey())
                                && keyValue2.getValue().equals(keyValue1.getValue())))) {
            return false;
        } else if (datasetImplementation1.getDatasetImplementationLabels().stream()
                .noneMatch(label1 -> datasetImplementation2.getDatasetImplementationLabels().stream()
                        .anyMatch(label2 -> label2.getValue().equals(label1.getValue())))) {
            return false;
        } else {
            return true;
        }
    }


    public boolean equalsWithoutUuid(DatasetImplementationDto datasetImplementation1, DatasetImplementationDto datasetImplementation2) {
        if (!(datasetImplementation1 instanceof InMemoryDatasetImplementationDto
                && datasetImplementation2 instanceof InMemoryDatasetImplementationDto)) {
            return false;
        } else if (((InMemoryDatasetImplementationDto) datasetImplementation1).getKeyValues().size() != ((InMemoryDatasetImplementationDto) datasetImplementation2).getKeyValues().size()) {
            return false;
        } else if (((InMemoryDatasetImplementationDto) datasetImplementation1).getKeyValues().stream()
                .noneMatch(keyValue1 -> ((InMemoryDatasetImplementationDto) datasetImplementation2).getKeyValues().stream()
                        .anyMatch(keyValue2 -> keyValue2.getKey().equals(keyValue1.getKey())
                                && keyValue2.getValue().equals(keyValue1.getValue())))) {
            return false;
        } else if (datasetImplementation1.getLabels().stream()
                .noneMatch(label1 -> datasetImplementation2.getLabels().stream()
                        .anyMatch(label2 -> label2.getLabel().equals(label1.getLabel())))) {
            return false;
        } else {
            return true;
        }
    }
/*
    public boolean equalsWithoutUuid(DatasetDto dataset1, DatasetDto dataset2) {
        if (!dataset1.getName().equals(dataset2.getName())) {
            return false;
        } else if (dataset1.getImplementations().size() != dataset2.getImplementations().size()) {
            return false;
        } else if (dataset1.getImplementations().stream()
                .noneMatch(datasetImplementation1 -> dataset2.getImplementations().stream()
                        .anyMatch(datasetImplementation2 -> equalsWithoutUuid(datasetImplementation1, datasetImplementation2)))) {
            return false;
        } else {
            return true;
        }
    }
*/
    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testUpdateDataset() {
        UUID datasetUuid = UUID.randomUUID();
        DatasetPutDto datasetPutDto = DatasetPutDto.builder()
                .uuid(datasetUuid)
                .name("dataset")
                .implementations(Stream.of(
                        InMemoryDatasetImplementationPostDto.builder()
                                .keyValues(Stream.of(
                                        InMemoryDatasetImplementationKeyValuePostDto.builder()
                                                .key("key1")
                                                .value("value1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .labels(Stream.of(
                                        DatasetImplementationLabelPostDto.builder()
                                                .label("label1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build()
                ).collect(Collectors.toSet()))
                .build();
        Dataset expectedDataset = Dataset.builder()
                .metadataKey(new DatasetKey(datasetUuid))
                .name("dataset")
                .datasetImplementations(Stream.of(
                        InMemoryDatasetImplementation.builder()
                                .metadataKey(new DatasetImplementationKey(UUID.randomUUID()))
                                .datasetKey(new DatasetKey(UUID.randomUUID()))
                                .name("dataset")
                                .keyValues(Stream.of(
                                        InMemoryDatasetImplementationKeyValue.builder()
                                                .metadataKey(new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()))
                                                .datasetImplementationKey(new DatasetImplementationKey(UUID.randomUUID()))
                                                .key("key1")
                                                .value("value1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .datasetImplementationLabels(Stream.of(
                                        DatasetImplementationLabel.builder()
                                                .metadataKey(new DatasetImplementationLabelKey(UUID.randomUUID()))
                                                .datasetImplementationKey(new DatasetImplementationKey(UUID.randomUUID()))
                                                .value("label1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .build();

        DatasetDto datasetDto = DatasetDto.builder()
                .name("dataset")
                .uuid(UUID.randomUUID())
                .implementations(Stream.of(
                        InMemoryDatasetImplementationDto.builder()
                                .keyValues(Stream.of(
                                        InMemoryDatasetImplementationKeyValueDto.builder()
                                                .uuid(UUID.randomUUID())
                                                .key("key1")
                                                .value("value1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .labels(Stream.of(
                                        DatasetImplementationLabelDto.builder()
                                                .uuid(UUID.randomUUID())
                                                .label("label1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build()
                )
                        .map(e -> e.getUuid())
                        .collect(Collectors.toSet()))
                .build();
        when(datasetDtoModelAssembler.toModel((Dataset) any()))
                .thenReturn(datasetDto);
        when(datasetService.exists(new DatasetKey(datasetUuid)))
                .thenReturn(true);
        when(datasetService.get(new DatasetKey(datasetUuid)))
                .thenReturn(Optional.of(expectedDataset));

        ResponseEntity<DatasetDto> responseEntity = datasetController.update(datasetUuid, datasetPutDto);

        verify(datasetService, times(1))
                .update(argThat(dataset -> equalsWithoutUuid(expectedDataset, dataset)));
        verify(datasetDtoModelAssembler, times(1))
                .toModel((Dataset) argThat(dataset -> equalsWithoutUuid(expectedDataset, (Dataset) dataset)));
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isEqualTo(datasetDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testUpdateDatasetBadRequest() {
        DatasetPutDto datasetPutDto = DatasetPutDto.builder()
                .uuid(UUID.randomUUID())
                .name("dataset")
                .implementations(new HashSet<>())
                .build();
        ResponseEntity<DatasetDto> responseEntity = datasetController.update(UUID.randomUUID(), datasetPutDto);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testUpdateDatasetDoesNotExist() {
        UUID datasetUuid = UUID.randomUUID();
        DatasetPutDto datasetPutDto = DatasetPutDto.builder()
                .uuid(datasetUuid)
                .name("dataset")
                .implementations(new HashSet<>())
                .build();
        when(datasetService.exists(new DatasetKey(datasetUuid)))
                .thenReturn(false);

        ResponseEntity<DatasetDto> responseEntity = datasetController.update(datasetUuid, datasetPutDto);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testDeleteById() {
        UUID datasetUuid = UUID.randomUUID();
        when(datasetService.exists(new DatasetKey(datasetUuid)))
                .thenReturn(true);
        ResponseEntity<Object> responseEntity = datasetController.delete(datasetUuid);
        verify(datasetService, times(1))
                .delete((new DatasetKey(datasetUuid)));
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"DATASETS_WRITE@PUBLIC"})
    void testDeleteByIdNotFound() {
        UUID datasetUuid = UUID.randomUUID();
        when(datasetService.exists(new DatasetKey(datasetUuid)))
                .thenReturn(false);
        ResponseEntity<Object> responseEntity = datasetController.delete(datasetUuid);
        verify(datasetService, times(0))
                .delete((new DatasetKey(datasetUuid)));
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
