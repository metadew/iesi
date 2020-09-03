package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDto;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDtoModelAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Log4j2
@RestController
@CrossOrigin
@Tag(name = "datasets", description = "Everything about datasets")
@RequestMapping("/datasets")
public class DatasetController {

    private final DatasetDtoModelAssembler datasetDtoModelAssembler;
    private final IDatasetService datasetService;
    private final PagedResourcesAssembler<Dataset> datasetPagedResourcesAssembler;

    @Autowired
    public DatasetController(DatasetDtoModelAssembler datasetDtoModelAssembler, IDatasetService datasetService, PagedResourcesAssembler<Dataset> datasetPagedResourcesAssembler) {
        this.datasetDtoModelAssembler = datasetDtoModelAssembler;
        this.datasetService = datasetService;
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

    @GetMapping("/{uuid}")
    public DatasetDto get(@RequestParam UUID uuid) {
        return datasetService.get(new DatasetKey(uuid))
                .map(datasetDtoModelAssembler::toModel)
                .orElseThrow(() -> new MetadataDoesNotExistException(new DatasetKey(uuid)));
    }

}
