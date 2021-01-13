package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.server.rest.dataset.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
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
    private final IDatasetImplementationService datasetImplementationService;
    private final PagedResourcesAssembler<DatasetDto> datasetDtoPagedResourcesAssembler;
    private final IDatasetDtoService datasetDtoService;

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
    public PagedModel<DatasetDto> getAll(Pageable pageable) {
        Page<DatasetDto> datasetDtoPage = datasetDtoService.fetchAll(pageable);
        if (datasetDtoPage.hasContent())
            return datasetDtoPagedResourcesAssembler.toModel(datasetDtoPage, datasetDtoModelAssembler::toModel);
        return (PagedModel<DatasetDto>) datasetDtoPagedResourcesAssembler.toEmptyModel(datasetDtoPage, DatasetDto.class);
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public DatasetDto create(@RequestBody DatasetPostDto datasetPostDto) {
        String datasetName = datasetPostDto.getName();
        UUID datasetUuid = UUID.randomUUID();

        Dataset dataset = new Dataset(
                new DatasetKey(UUID.randomUUID()),
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
        return datasetDtoModelAssembler.toModel(dataset);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    public DatasetDto get(@PathVariable UUID uuid) {
        return datasetService.get(new DatasetKey(uuid))
                .map(datasetDtoModelAssembler::toModel)
                .orElseThrow(() -> new MetadataDoesNotExistException(new DatasetKey(uuid)));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public void delete(@PathVariable UUID uuid) {
        datasetService.delete(new DatasetKey(uuid));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public ResponseEntity<DatasetDto> update(@PathVariable UUID uuid, @RequestBody DatasetDto datasetDto) {
        if (datasetDto.getUuid().equals(uuid)) {
            return ResponseEntity.badRequest().build();
        }
        datasetService.update(datasetDto.convertToEntity());
        return datasetService.get(new DatasetKey(uuid))
                .map(dataset -> ResponseEntity.ok(datasetDtoModelAssembler.toModel(dataset)))
                .orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/{uuid}/datasetImplementation")
    @PreAuthorize("hasPrivilege('DATASETS_WRITE')")
    public ResponseEntity addDatasetImplementation(@PathVariable UUID uuid, @RequestBody DatasetImplementationDto datasetImplementationDto) {
        Optional<Dataset> dataset = datasetService.get(new DatasetKey(uuid));
        if (dataset.isPresent()) {
            datasetImplementationService.create(datasetImplementationDto.convertToNewEntity(dataset.get().getMetadataKey().getUuid(), dataset.get().getName()));
            return datasetService.get(new DatasetKey(uuid))
                    .map(updatedDataset -> ResponseEntity.ok(datasetDtoModelAssembler.toModel(updatedDataset)))
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
