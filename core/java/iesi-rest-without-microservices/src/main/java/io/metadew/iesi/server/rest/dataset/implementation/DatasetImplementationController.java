package io.metadew.iesi.server.rest.dataset.implementation;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationService;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Math.min;

@Log4j2
@RestController
@CrossOrigin
@RequestMapping("/datasetImplementations")
@ConditionalOnWebApplication
public class DatasetImplementationController {

    private final DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler;
    private final IDatasetImplementationService datasetImplementationService;
    private final PagedResourcesAssembler<DatasetImplementation> datasetImplementationPagedResourcesAssembler;


    public DatasetImplementationController(DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler,
                                           IDatasetImplementationService datasetImplementationService,
                                           PagedResourcesAssembler<DatasetImplementation> datasetImplementationPagedResourcesAssembler) {
        this.datasetImplementationDtoModelAssembler = datasetImplementationDtoModelAssembler;
        this.datasetImplementationService = datasetImplementationService;
        this.datasetImplementationPagedResourcesAssembler = datasetImplementationPagedResourcesAssembler;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    public PagedModel<DatasetImplementationDto> getAll(Pageable pageable) {
        List<DatasetImplementation> datasetImplementations = datasetImplementationService.getAll();
        int minimum = pageable.getPageNumber() * pageable.getPageSize();
        int maximum = (pageable.getPageNumber() + 1) * pageable.getPageSize();
        Page<DatasetImplementation> datasetPage;
        if (minimum > datasetImplementations.size()) {
            datasetPage = new PageImpl<>(new ArrayList<>(),
                    pageable,
                    datasetImplementations.size());
        } else {
            datasetPage = new PageImpl<>(
                    datasetImplementations.subList(minimum, min(maximum, datasetImplementations.size())),
                    pageable,
                    datasetImplementations.size());
        }
        if (datasetPage.hasContent())
            return datasetImplementationPagedResourcesAssembler.toModel(datasetPage, datasetImplementationDtoModelAssembler);
        return (PagedModel<DatasetImplementationDto>) datasetImplementationPagedResourcesAssembler.toEmptyModel(datasetPage, DatasetDto.class);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('DATASETS_READ')")
    public DatasetImplementationDto get(@PathVariable UUID uuid) {
        Optional<DatasetImplementation> datasetImplementation = datasetImplementationService.get(new DatasetImplementationKey(uuid));
        return datasetImplementation
                .map(datasetImplementationDtoModelAssembler::toModel)
                .orElseThrow(() -> new MetadataDoesNotExistException(new DatasetImplementationKey(uuid)));
    }


}
