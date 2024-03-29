package io.metadew.iesi.server.rest.dataset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.IDatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
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
import io.metadew.iesi.server.rest.dataset.implementation.database.DatabaseDatasetImplementationPostDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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
    private final IDatabaseDatasetImplementationService datasetImplementationService;
    private final IesiSecurityChecker iesiSecurityChecker;
    private final ObjectMapper objectMapper;
    private final SecurityGroupConfiguration securityGroupConfiguration;


    @Autowired
    public DatasetController(DatasetDtoModelAssembler datasetDtoModelAssembler,
                             IDatasetService datasetService,
                             IDatabaseDatasetImplementationService datasetImplementationService,
                             PagedResourcesAssembler<DatasetDto> datasetPagedResourcesAssembler,
                             IDatasetDtoService datasetDtoService,
                             IesiSecurityChecker iesiSecurityChecker,
                             ObjectMapper objectMapper,
                             SecurityGroupConfiguration securityGroupConfiguration) {
        this.datasetDtoModelAssembler = datasetDtoModelAssembler;
        this.datasetService = datasetService;
        this.datasetImplementationService = datasetImplementationService;
        this.datasetDtoPagedResourcesAssembler = datasetPagedResourcesAssembler;
        this.datasetDtoService = datasetDtoService;
        this.iesiSecurityChecker = iesiSecurityChecker;
        this.objectMapper = objectMapper;
        this.securityGroupConfiguration = securityGroupConfiguration;
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

    @GetMapping("/{name}/download")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    public ResponseEntity<Resource> getFile(@PathVariable String name) {
        Dataset dataset = datasetService.getByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dataset " + name + " does not exist"));

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(String.format("dataset_%s.json", name))
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDisposition(contentDisposition);

        try {
            String jsonString = objectMapper.writeValueAsString(dataset);
            byte[] data = jsonString.getBytes();
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @PostMapping("")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE', #datasetPostDto.securityGroupName)")
    public ResponseEntity<DatasetDto> create(@RequestBody DatasetPostDto datasetPostDto) {
        Optional<Dataset> dataset = datasetService.getByName(datasetPostDto.getName());
        if (dataset.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Dataset " + datasetPostDto.getName() + " already exists");
        }

        Dataset newDataset = datasetDtoService.convertToEntity(datasetPostDto);
        datasetService.create(newDataset);
        return ResponseEntity.ok(datasetDtoModelAssembler.toModel(newDataset));
    }

    @PostMapping(value= "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DatasetDto>> importDatasets(@RequestParam(value = "file") MultipartFile multipartFile)  {
        try {
            String textPlain = new String(multipartFile.getBytes());
            List<Dataset> datasets = datasetService.importDatasets(textPlain);
            return ResponseEntity.ok(datasetDtoModelAssembler.toModel(datasets));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot process the given file:%s", multipartFile.getOriginalFilename()));
        }
    }

    @PostMapping(value = "/import", consumes = MediaType.TEXT_PLAIN_VALUE )
    public ResponseEntity<List<DatasetDto>> importDatasets(@RequestBody String textPlain) {
        List<Dataset> datasets = datasetService.importDatasets(textPlain);
        return ResponseEntity.ok(datasetDtoModelAssembler.toModel(datasets));
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
            if (datasetImplementationPostDto instanceof DatabaseDatasetImplementationPostDto) {
                UUID datasetImplementationUuid = UUID.randomUUID();
                datasetImplementation = new DatabaseDatasetImplementation(
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
                        ((DatabaseDatasetImplementationPostDto) datasetImplementationPostDto).getKeyValues().stream()
                                .map(keyValue -> new DatabaseDatasetImplementationKeyValue(
                                        new DatabaseDatasetImplementationKeyValueKey(),
                                        new DatasetImplementationKey(datasetImplementationUuid),
                                        keyValue.getKey(),
                                        keyValue.getValue()))
                                .collect(Collectors.toSet())
                );
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Please specify the correct type of DatasetImplementation");
            }

            datasetImplementationService.create((DatabaseDatasetImplementation) datasetImplementation);
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

        SecurityGroup securityGroup = securityGroupConfiguration.getByName(datasetPutDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("Could not find security group with name + " + datasetPutDto.getSecurityGroupName()));

        Dataset dataset = new Dataset(
                new DatasetKey(uuid),
                securityGroup.getMetadataKey(),
                securityGroup.getName(),
                datasetPutDto.getName(),
                datasetPutDto.getImplementations().stream()
                        .map(datasetImplementationDto -> {
                            UUID datasetImplementationUuid = UUID.randomUUID();
                            return new DatabaseDatasetImplementation(
                                    new DatasetImplementationKey(datasetImplementationUuid),
                                    new DatasetKey(uuid),
                                    datasetPutDto.getName(),
                                    datasetImplementationDto.getLabels().stream()
                                            .map(datasetImplementationLabelDto -> new DatasetImplementationLabel(
                                                    new DatasetImplementationLabelKey(UUID.randomUUID()),
                                                    new DatasetImplementationKey(datasetImplementationUuid),
                                                    datasetImplementationLabelDto.getLabel()))
                                            .collect(Collectors.toSet()),
                                    ((DatabaseDatasetImplementationPostDto) datasetImplementationDto).getKeyValues().stream()
                                            .map(inMemoryDatasetImplementationKeyValuePostDto -> new DatabaseDatasetImplementationKeyValue(
                                                    new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID()),
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
