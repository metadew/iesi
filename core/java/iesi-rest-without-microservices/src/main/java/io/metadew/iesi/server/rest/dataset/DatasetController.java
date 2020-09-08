package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationService;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDto;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDtoModelAssembler;
import io.metadew.iesi.server.rest.dataset.dto.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.dto.DatasetImplementationDtoModelAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Math.min;

@Log4j2
@RestController
@CrossOrigin
@Tag(name = "datasets", description = "Everything about datasets")
@RequestMapping("/datasets")
public class DatasetController {

    private final DatasetDtoModelAssembler datasetDtoModelAssembler;
    private final IDatasetService datasetService;
    private final DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler;
    private final IDatasetImplementationService datasetImplementationService;
    private final PagedResourcesAssembler<Dataset> datasetPagedResourcesAssembler;

    @Autowired
    public DatasetController(DatasetDtoModelAssembler datasetDtoModelAssembler, IDatasetService datasetService,
                             DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler, IDatasetImplementationService datasetImplementationService,
                             PagedResourcesAssembler<Dataset> datasetPagedResourcesAssembler) {
        this.datasetDtoModelAssembler = datasetDtoModelAssembler;
        this.datasetService = datasetService;
        this.datasetImplementationDtoModelAssembler = datasetImplementationDtoModelAssembler;
        this.datasetImplementationService = datasetImplementationService;
        this.datasetPagedResourcesAssembler = datasetPagedResourcesAssembler;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("")
    public PagedModel<DatasetDto> getAll(Pageable pageable) {
        List<Dataset> datasets = datasetService.getAll();
        int minimum = pageable.getPageNumber() * pageable.getPageSize();
        int maximum = (pageable.getPageNumber() + 1) * pageable.getPageSize();
        Page<Dataset> datasetPage;
        if (minimum > datasets.size()) {
            datasetPage = new PageImpl<>(new ArrayList<>(),
                    pageable,
                    datasets.size());
        } else {
            datasetPage = new PageImpl<>(
                    datasets.subList(minimum, min(maximum, datasets.size())),
                    pageable,
                    datasets.size());
        }
        if (datasetPage.hasContent())
            return datasetPagedResourcesAssembler.toModel(datasetPage, datasetDtoModelAssembler);
        return (PagedModel<DatasetDto>) datasetPagedResourcesAssembler.toEmptyModel(datasetPage, DatasetDto.class);
    }

    @PostMapping("")
    public DatasetDto create(@RequestBody DatasetDto datasetDto) {
        Dataset dataset = datasetDto.convertToNewEntity();
        datasetService.create(dataset);
        return datasetDtoModelAssembler.toModel(dataset);
    }

    @GetMapping("/{uuid}")
    public DatasetDto get(@PathVariable UUID uuid) {
        return datasetService.get(new DatasetKey(uuid))
                .map(datasetDtoModelAssembler::toModel)
                .orElseThrow(() -> new MetadataDoesNotExistException(new DatasetKey(uuid)));
    }

    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable UUID uuid) {
        datasetService.delete(new DatasetKey(uuid));
    }

    @PutMapping("/{uuid}")
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
