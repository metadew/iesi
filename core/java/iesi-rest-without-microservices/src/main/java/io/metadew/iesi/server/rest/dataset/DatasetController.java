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
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.service.user.IESIPrivilege;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDto;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDtoModelAssembler;
import io.metadew.iesi.server.rest.dataset.dto.DatasetPostDto;
import io.metadew.iesi.server.rest.dataset.dto.IDatasetDtoService;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationPostDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationPostDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RestController
@CrossOrigin
@RequestMapping("/datasets")
@ConditionalOnWebApplication
public class DatasetController {

    private final DatasetDtoModelAssembler datasetDtoModelAssembler;
    private final IDatasetService datasetService;
    private final PagedResourcesAssembler<DatasetDto> datasetDtoPagedResourcesAssembler;
    private final IDatasetDtoService datasetDtoService;
    private final IDatasetImplementationService datasetImplementationService;
    private final IesiSecurityChecker iesiSecurityChecker;


    @Autowired
    public DatasetController(DatasetDtoModelAssembler datasetDtoModelAssembler,
                             IDatasetService datasetService,
                             IDatasetImplementationService datasetImplementationService,
                             PagedResourcesAssembler<DatasetDto> datasetPagedResourcesAssembler,
                             IDatasetDtoService datasetDtoService,
                             IesiSecurityChecker iesiSecurityChecker) {
        this.datasetDtoModelAssembler = datasetDtoModelAssembler;
        this.datasetService = datasetService;
        this.datasetImplementationService = datasetImplementationService;
        this.datasetDtoPagedResourcesAssembler = datasetPagedResourcesAssembler;
        this.datasetDtoService = datasetDtoService;
        this.iesiSecurityChecker = iesiSecurityChecker;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    public PagedModel<DatasetDto> getAll(Pageable pageable, @RequestParam(required = false, name = "name") String name) {
        Page<DatasetDto> datasetDtoPage = datasetDtoService.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                pageable,
                new DatasetFiltersBuilder()
                        .name(name)
                        .build());
        if (datasetDtoPage.hasContent())
            return datasetDtoPagedResourcesAssembler.toModel(datasetDtoPage, datasetDtoModelAssembler::toModel);
        return (PagedModel<DatasetDto>) datasetDtoPagedResourcesAssembler.toEmptyModel(datasetDtoPage, DatasetDto.class);
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    @PostAuthorize("hasPrivilege('DATASETS_READ', returnObject.securityGroupName)")
    public DatasetDto getByName(@PathVariable String name) {
        return datasetService.getByName(name)
                .map(datasetDtoModelAssembler::toModel)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dataset " + name + " does not exist"));
    }

    @GetMapping("/{uuid}/implementations")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    public List<DatasetImplementationDto> getImplementationsByDatasetUuid(@PathVariable UUID uuid) {
        Optional<Dataset> dataset = datasetService.get(new DatasetKey(uuid));

        if (dataset.isPresent() && !iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(), IESIPrivilege.DATASET_READ.getPrivilege(), dataset.get().getSecurityGroupName())) {
            throw new AccessDeniedException("User is not allowed to retrieve dataset implementation in the dataset : " + dataset.get().getName() + " and ID " + uuid);
        } else if (!dataset.isPresent()) {
            throw new MetadataDoesNotExistException(new DatasetImplementationKey(uuid));
        }
        return datasetDtoService.fetchImplementationsByDatasetUuid(uuid);
    }

    @GetMapping("/{datasetUuid}/implementations/{datasetImplementationUuid}")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    public DatasetImplementationDto getImplementationByUuid(@PathVariable UUID datasetUuid, @PathVariable UUID datasetImplementationUuid) {
        Optional<Dataset> dataset = datasetService.get(new DatasetKey(datasetUuid));
        if (dataset.isPresent() && !iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(), IESIPrivilege.DATASET_READ.getPrivilege(), dataset.get().getSecurityGroupName())) {
            throw new AccessDeniedException("User is not allowed to retrieve dataset implementation in the dataset : " + dataset.get().getName() + " and ID " + datasetUuid);
        } else if (!dataset.isPresent()) {
            throw new MetadataDoesNotExistException(new DatasetImplementationKey(datasetUuid));
        }

        return datasetDtoService.fetchImplementationByUuid(datasetImplementationUuid)
                .orElseThrow(() -> new MetadataDoesNotExistException(new DatasetImplementationKey(datasetImplementationUuid)));
    }


    @PostMapping("")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE', #datasetPostDto.securityGroupName)")
    public ResponseEntity<DatasetDto> create(@RequestBody DatasetPostDto datasetPostDto) {
        Optional<Dataset> dataset = datasetService.getByName(datasetPostDto.getName());
        if (dataset.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Dataset " + datasetPostDto.getName() + " already exists");
        }

        String datasetName = datasetPostDto.getName();
        UUID datasetUuid = UUID.randomUUID();

        SecurityGroup securityGroup = SecurityGroupConfiguration.getInstance().getByName(datasetPostDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("Could not find security group with name + " + datasetPostDto.getSecurityGroupName()));
        Dataset newDataset = new Dataset(
                new DatasetKey(datasetUuid),
                securityGroup.getMetadataKey(),
                securityGroup.getName(),
                datasetPostDto.getName(),
                datasetPostDto.getImplementations().stream()
                        .map(datasetImplementationDto -> {
                            UUID datasetImplementationUuid = UUID.randomUUID();
                            return new InMemoryDatasetImplementation(
                                    new DatasetImplementationKey(datasetImplementationUuid),
                                    new DatasetKey(datasetUuid),
                                    datasetName,
                                    datasetImplementationDto.getLabels().stream()
                                            .map(datasetImplementationLabelDto -> new DatasetImplementationLabel(
                                                    new DatasetImplementationLabelKey(UUID.randomUUID()),
                                                    new DatasetImplementationKey(datasetImplementationUuid),
                                                    datasetImplementationLabelDto.getLabel()))
                                            .collect(Collectors.toSet()),
                                    ((InMemoryDatasetImplementationPostDto) datasetImplementationDto).getKeyValues().stream()
                                            .map(inMemoryDatasetImplementationKeyValuePostDto -> new InMemoryDatasetImplementationKeyValue(
                                                    new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                                    new DatasetImplementationKey(datasetImplementationUuid),
                                                    inMemoryDatasetImplementationKeyValuePostDto.getKey(),
                                                    inMemoryDatasetImplementationKeyValuePostDto.getValue()
                                            ))
                                            .collect(Collectors.toSet())
                            );
                        })
                        .collect(Collectors.toSet())
        );
        datasetService.create(newDataset);
        return ResponseEntity.ok(datasetDtoModelAssembler.toModel(newDataset));
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/{uuid}/implementations")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public HttpEntity<?> addDatasetImplementation(@PathVariable UUID uuid,
                                                  @RequestBody DatasetImplementationPostDto datasetImplementationPostDto) {
        Optional<Dataset> dataset = datasetService.get(new DatasetKey(uuid));
        if (dataset.isPresent()) {
            if (!iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(), IESIPrivilege.DATASET_MODIFY.getPrivilege(), dataset.get().getSecurityGroupName())) {
                throw new AccessDeniedException("User is not allowed to create dataset implementation in the dataset : " + dataset.get().getName() + " and ID " + uuid);
            }
            DatasetImplementation datasetImplementation;
            if (datasetImplementationPostDto instanceof InMemoryDatasetImplementationPostDto) {
                UUID datasetImplementationUuid = UUID.randomUUID();
                datasetImplementation = new InMemoryDatasetImplementation(
                        new DatasetImplementationKey(datasetImplementationUuid),
                        new DatasetKey(uuid),
                        dataset.get().getName(),
                        datasetImplementationPostDto.getLabels().stream()
                                .map(datasetImplementationLabelDto -> new DatasetImplementationLabel(
                                        new DatasetImplementationLabelKey(UUID.randomUUID()),
                                        new DatasetImplementationKey(datasetImplementationUuid),
                                        datasetImplementationLabelDto.getLabel()
                                ))
                                .collect(Collectors.toSet()),
                        ((InMemoryDatasetImplementationPostDto) datasetImplementationPostDto).getKeyValues().stream()
                                .map(keyValue -> new InMemoryDatasetImplementationKeyValue(
                                        new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                        new DatasetImplementationKey(datasetImplementationUuid),
                                        keyValue.getKey(),
                                        keyValue.getValue()))
                                .collect(Collectors.toSet())
                );
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Please specify the correct type of DatasetImplementation");
            }

            datasetImplementationService.create(datasetImplementation);
            return datasetService.get(new DatasetKey(uuid))
                    .map(datasetDtoModelAssembler::toModel)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            throw new MetadataDoesNotExistException(new DatasetKey(uuid));
        }
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE', #datasetPutDto.securityGroupName)")
    public ResponseEntity<DatasetDto> update(@PathVariable UUID uuid, @RequestBody DatasetPutDto datasetPutDto) {
        if (!datasetService.exists(new DatasetKey(uuid))) {
            throw new MetadataDoesNotExistException(new DatasetKey(uuid));
        }

        SecurityGroup securityGroup = SecurityGroupConfiguration.getInstance().getByName(datasetPutDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("Could not find security group with name + " + datasetPutDto.getSecurityGroupName()));

        Dataset dataset = new Dataset(
                new DatasetKey(uuid),
                securityGroup.getMetadataKey(),
                securityGroup.getName(),
                datasetPutDto.getName(),
                datasetPutDto.getImplementations().stream()
                        .map(datasetImplementationDto -> {
                            UUID datasetImplementationUuid = UUID.randomUUID();
                            return new InMemoryDatasetImplementation(
                                    new DatasetImplementationKey(datasetImplementationUuid),
                                    new DatasetKey(uuid),
                                    datasetPutDto.getName(),
                                    datasetImplementationDto.getLabels().stream()
                                            .map(datasetImplementationLabelDto -> new DatasetImplementationLabel(
                                                    new DatasetImplementationLabelKey(UUID.randomUUID()),
                                                    new DatasetImplementationKey(datasetImplementationUuid),
                                                    datasetImplementationLabelDto.getLabel()))
                                            .collect(Collectors.toSet()),
                                    ((InMemoryDatasetImplementationPostDto) datasetImplementationDto).getKeyValues().stream()
                                            .map(inMemoryDatasetImplementationKeyValuePostDto -> new InMemoryDatasetImplementationKeyValue(
                                                    new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                                    new DatasetImplementationKey(datasetImplementationUuid),
                                                    inMemoryDatasetImplementationKeyValuePostDto.getKey(),
                                                    inMemoryDatasetImplementationKeyValuePostDto.getValue()
                                            ))
                                            .collect(Collectors.toSet())
                            );
                        })
                        .collect(Collectors.toSet())
        );

        datasetService.update(dataset);
        return datasetService.get(new DatasetKey(uuid))
                .map(datasetDtoModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{datasetUuid}/implementations/{datasetImplementationUuid}")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public ResponseEntity<Object> deleteImplementationByUuid(@PathVariable UUID datasetUuid, @PathVariable UUID datasetImplementationUuid) {
        Optional<Dataset> dataset = datasetService.get(new DatasetKey(datasetUuid));

        if (dataset.isPresent()) {
            if (!iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(), IESIPrivilege.DATASET_MODIFY.getPrivilege(), dataset.get().getSecurityGroupName())) {
                throw new AccessDeniedException("User is not allowed to delete the dataset implementations in the dataset : " + dataset.get().getName() + " and ID " + datasetUuid);
            }
        } else {
            throw new MetadataDoesNotExistException(new DatasetKey(datasetUuid));
        }

        if (!datasetImplementationService.exists(new DatasetImplementationKey(datasetImplementationUuid))) {
            throw new MetadataDoesNotExistException(new DatasetImplementationKey(datasetImplementationUuid));
        }

        datasetImplementationService.delete(new DatasetImplementationKey(datasetImplementationUuid));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{datasetUuid}/implementations")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public ResponseEntity<Object> deleteImplementationsByDatasetUuid(@PathVariable UUID datasetUuid) {
        Optional<Dataset> dataset = datasetService.get(new DatasetKey(datasetUuid));
        if (dataset.isPresent()) {
            if (!iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(), IESIPrivilege.DATASET_MODIFY.getPrivilege(), dataset.get().getSecurityGroupName())) {
                throw new AccessDeniedException("User is not allowed to delete dataset implementations in the dataset : " + dataset.get().getName() + " and ID " + datasetUuid);
            }
        } else {
            throw new MetadataDoesNotExistException(new DatasetKey(datasetUuid));
        }

        datasetImplementationService.deleteByDatasetId(new DatasetKey(datasetUuid));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public ResponseEntity<Object> delete(@PathVariable UUID uuid) {
        Optional<Dataset> dataset = datasetService.get(new DatasetKey(uuid));

        if (dataset.isPresent()) {
            if (!iesiSecurityChecker.hasPrivilege(SecurityContextHolder.getContext().getAuthentication(), IESIPrivilege.DATASET_MODIFY.getPrivilege(), dataset.get().getSecurityGroupName())) {
                throw new AccessDeniedException("User is not allowed to delete dataset implementations in the dataset : " + dataset.get().getName() + " and ID " + uuid);
            }
        } else {
            throw new MetadataDoesNotExistException(new DatasetKey(uuid));
        }

        datasetService.delete(new DatasetKey(uuid));
        return ResponseEntity.ok().build();
    }

}
