package io.metadew.iesi.server.rest.dataset.implementation.inmemory;



import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationController;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDtoModelAssembler;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationType;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.DatabaseDatasetImplementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class InMemoryDatasetImplementationDtoModelAssembler extends RepresentationModelAssemblerSupport<DatabaseDatasetImplementation, InMemoryDatasetImplementationDto> {

    private final InMemoryDatasetImplementationKeyValueDtoModelAssembler inMemoryDatasetImplementationKeyValueDtoModelAssembler;
    private final DatasetImplementationLabelDtoModelAssembler datasetImplementationLabelDtoModelAssembler;

    @Autowired
    public InMemoryDatasetImplementationDtoModelAssembler(InMemoryDatasetImplementationKeyValueDtoModelAssembler inMemoryDatasetImplementationKeyValueDtoModelAssembler, DatasetImplementationLabelDtoModelAssembler datasetImplementationLabelDtoModelAssembler) {
        super(DatasetImplementationController.class, InMemoryDatasetImplementationDto.class);
        this.inMemoryDatasetImplementationKeyValueDtoModelAssembler = inMemoryDatasetImplementationKeyValueDtoModelAssembler;
        this.datasetImplementationLabelDtoModelAssembler = datasetImplementationLabelDtoModelAssembler;
    }

    @Override
    public InMemoryDatasetImplementationDto toModel(DatabaseDatasetImplementation databaseDatasetImplementation) {
        InMemoryDatasetImplementationDto inMemoryDatasetImplementationDto = instantiateModel(databaseDatasetImplementation);

        inMemoryDatasetImplementationDto.setUuid(databaseDatasetImplementation.getMetadataKey().getUuid());
        inMemoryDatasetImplementationDto.setType(DatasetImplementationType.IN_MEMORY.value());
        inMemoryDatasetImplementationDto.setKeyValues(databaseDatasetImplementation.getKeyValues().stream()
                .map(inMemoryDatasetImplementationKeyValueDtoModelAssembler::toModel)
                .collect(Collectors.toSet()));
        inMemoryDatasetImplementationDto.setLabels(databaseDatasetImplementation.getDatasetImplementationLabels().stream()
                .map(datasetImplementationLabelDtoModelAssembler::toModel)
                .collect(Collectors.toSet())
        );
        return inMemoryDatasetImplementationDto;
    }

}