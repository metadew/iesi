package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDtoModelAssembler;
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
        DatasetDto datasetDto = instantiateModel(dataset);

        datasetDto.setUuid(dataset.getMetadataKey().getUuid());
        datasetDto.setName(dataset.getName());
        datasetDto.setImplementations(dataset.getDatasetImplementations().stream()
                .map(datasetImplementationDtoModelAssembler::toModel)
                .collect(Collectors.toSet()));

        return datasetDto;
    }

    public DatasetDto toModel(DatasetDto datasetDto) {
        return datasetDto;
    }

}