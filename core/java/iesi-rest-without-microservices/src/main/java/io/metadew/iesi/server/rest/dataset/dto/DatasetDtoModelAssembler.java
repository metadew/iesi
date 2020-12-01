package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.server.rest.dataset.DatasetController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DatasetDtoModelAssembler extends RepresentationModelAssemblerSupport<Dataset, DatasetDto> {

    private final DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler;

    @Autowired
    public DatasetDtoModelAssembler(DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler) {
        super(DatasetController.class, DatasetDto.class);
        this.datasetImplementationDtoModelAssembler = datasetImplementationDtoModelAssembler;
    }

    @Override
    public DatasetDto toModel(Dataset dataset) {
        return convertToDto(dataset);
    }

    private DatasetDto convertToDto(Dataset dataset) {
        return new DatasetDto(
                dataset.getMetadataKey().getUuid(),
                dataset.getName(),
                dataset.getDatasetImplementations().stream().map(datasetImplementationDtoModelAssembler::convertToDto).collect(Collectors.toList()));
    }

}