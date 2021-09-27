package io.metadew.iesi.server.rest.dataset.implementation.inmemory;



import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationController;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDtoModelAssembler;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationType;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DatabaseDatasetImplementationDtoModelAssembler extends RepresentationModelAssemblerSupport<DatabaseDatasetImplementation, DatabaseDatasetImplementationDto> {

    private final DatabaseDatasetImplementationKeyValueDtoModelAssembler databaseDatasetImplementationKeyValueDtoModelAssembler;
    private final DatasetImplementationLabelDtoModelAssembler datasetImplementationLabelDtoModelAssembler;

    @Autowired
    public DatabaseDatasetImplementationDtoModelAssembler(DatabaseDatasetImplementationKeyValueDtoModelAssembler databaseDatasetImplementationKeyValueDtoModelAssembler, DatasetImplementationLabelDtoModelAssembler datasetImplementationLabelDtoModelAssembler) {
        super(DatasetImplementationController.class, DatabaseDatasetImplementationDto.class);
        this.databaseDatasetImplementationKeyValueDtoModelAssembler = databaseDatasetImplementationKeyValueDtoModelAssembler;
        this.datasetImplementationLabelDtoModelAssembler = datasetImplementationLabelDtoModelAssembler;
    }

    @Override
    public DatabaseDatasetImplementationDto toModel(DatabaseDatasetImplementation databaseDatasetImplementation) {
        DatabaseDatasetImplementationDto databaseDatasetImplementationDto = instantiateModel(databaseDatasetImplementation);

        databaseDatasetImplementationDto.setUuid(databaseDatasetImplementation.getMetadataKey().getUuid());
        databaseDatasetImplementationDto.setType(DatasetImplementationType.IN_MEMORY.value());
        databaseDatasetImplementationDto.setKeyValues(databaseDatasetImplementation.getKeyValues().stream()
                .map(databaseDatasetImplementationKeyValueDtoModelAssembler::toModel)
                .collect(Collectors.toSet()));
        databaseDatasetImplementationDto.setLabels(databaseDatasetImplementation.getDatasetImplementationLabels().stream()
                .map(datasetImplementationLabelDtoModelAssembler::toModel)
                .collect(Collectors.toSet())
        );
        return databaseDatasetImplementationDto;
    }

}