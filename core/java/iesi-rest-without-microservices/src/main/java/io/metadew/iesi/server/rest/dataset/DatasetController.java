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
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationPostDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationPostDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RestController
@CrossOrigin
@Tag(name = "datasets", description = "Everything about datasets")
@RequestMapping("/datasets")
public class DatasetController {

    private final DatasetDtoModelAssembler datasetDtoModelAssembler;
    private final IDatasetService datasetService;
    private final PagedResourcesAssembler<DatasetDto> datasetDtoPagedResourcesAssembler;
    private final IDatasetDtoService datasetDtoService;
    private final IDatasetImplementationService datasetImplementationService;


    @Autowired
    public DatasetController(DatasetDtoModelAssembler datasetDtoModelAssembler,
                             IDatasetService datasetService,
                             IDatasetImplementationService datasetImplementationService,
                             PagedResourcesAssembler<DatasetDto> datasetPagedResourcesAssembler,
                             IDatasetDtoService datasetDtoService) {
        this.datasetDtoModelAssembler = datasetDtoModelAssembler;
        this.datasetService = datasetService;
        this.datasetImplementationService = datasetImplementationService;
        this.datasetDtoPagedResourcesAssembler = datasetPagedResourcesAssembler;
        this.datasetDtoService = datasetDtoService;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    public PagedModel<DatasetDto> getAll(Pageable pageable,
                                         @RequestParam(required = false, name = "name") String name) {
        Page<DatasetDto> datasetDtoPage = datasetDtoService.fetchAll(
                pageable,
                new DatasetFiltersBuilder()
                        .name(name)
                        .build());
        if (datasetDtoPage.hasContent())
            return datasetDtoPagedResourcesAssembler.toModel(datasetDtoPage, datasetDtoModelAssembler::toModel);
        return (PagedModel<DatasetDto>) datasetDtoPagedResourcesAssembler.toEmptyModel(datasetDtoPage, DatasetDto.class);
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public ResponseEntity<DatasetDto> create(@RequestBody DatasetPostDto datasetPostDto) {
        if (datasetService.exists(datasetPostDto.getName())) {
            return ResponseEntity.badRequest().build();
        }

        String datasetName = datasetPostDto.getName();
        UUID datasetUuid = UUID.randomUUID();

        Dataset dataset = new Dataset(
                new DatasetKey(datasetUuid),
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
        datasetService.create(dataset);
        return ResponseEntity.ok(datasetDtoModelAssembler.toModel(dataset));
    }

    @GetMapping("/{uuid}/implementations")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    public Set<DatasetImplementationDto> get(@PathVariable UUID uuid) {
        return datasetService.get(new DatasetKey(uuid))
                .map(datasetDtoModelAssembler::toList)
                .orElseThrow(() -> new MetadataDoesNotExistException(new DatasetKey(uuid)));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public ResponseEntity<Object> delete(@PathVariable UUID uuid) {
        if (!datasetService.exists(new DatasetKey(uuid))) {
            return ResponseEntity.notFound().build();
        }
        datasetService.delete(new DatasetKey(uuid));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public ResponseEntity<DatasetDto> update(@PathVariable UUID uuid, @RequestBody DatasetPutDto datasetPutDto) {
        if (!datasetPutDto.getUuid().equals(uuid)) {
            return ResponseEntity.badRequest().build();
        } else if (!datasetService.exists(new DatasetKey(datasetPutDto.getUuid()))) {
            return ResponseEntity.notFound().build();
        }

        Dataset dataset = new Dataset(
                new DatasetKey(datasetPutDto.getUuid()),
                datasetPutDto.getName(),
                datasetPutDto.getImplementations().stream()
                        .map(datasetImplementationDto -> {
                            UUID datasetImplementationUuid = UUID.randomUUID();
                            return new InMemoryDatasetImplementation(
                                    new DatasetImplementationKey(datasetImplementationUuid),
                                    new DatasetKey(datasetPutDto.getUuid()),
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

    @SuppressWarnings("unchecked")
    @PostMapping("/{uuid}/implementations")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public HttpEntity<?> addDatasetImplementation(@PathVariable UUID uuid,
                                                  @RequestBody DatasetImplementationPostDto datasetImplementationPostDto) {
        Optional<Dataset> dataset = datasetService.get(new DatasetKey(uuid));
        if (dataset.isPresent()) {
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
                return ResponseEntity.badRequest().build();
            }

            datasetImplementationService.create(datasetImplementation);
            return datasetService.get(new DatasetKey(uuid))
                    .map(datasetDtoModelAssembler::toModel)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
