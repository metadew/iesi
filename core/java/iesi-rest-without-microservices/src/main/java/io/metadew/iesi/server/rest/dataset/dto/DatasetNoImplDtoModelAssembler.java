package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.server.rest.dataset.DatasetController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DatasetNoImplDtoModelAssembler extends RepresentationModelAssemblerSupport<Dataset, DatasetNoImplDto> {

    private final DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler;

    @Autowired
    public DatasetNoImplDtoModelAssembler(DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler) {
        super(DatasetController.class, DatasetNoImplDto.class);
        this.datasetImplementationDtoModelAssembler = datasetImplementationDtoModelAssembler;
    }

    @Override
    public DatasetNoImplDto toModel(Dataset dataset) {
        return convertToDto(dataset);
    }

    public DatasetNoImplDto toModel(DatasetNoImplDto dataset) {
        return dataset;
    }

    private DatasetNoImplDto convertToDto(Dataset dataset) {
        return new DatasetNoImplDto(
                dataset.getMetadataKey().getUuid(),
                dataset.getName(),
                new HashSet<>()
        );
    }

}