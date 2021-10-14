package io.metadew.iesi.server.rest.dataset.implementation;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.server.rest.dataset.implementation.database.DatabaseDatasetImplementationDtoModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class DatasetImplementationDtoModelAssembler extends RepresentationModelAssemblerSupport<DatasetImplementation, DatasetImplementationDto> {

    private final DatabaseDatasetImplementationDtoModelAssembler databaseDatasetImplementationDtoModelAssembler;

    @Autowired
    public DatasetImplementationDtoModelAssembler(DatabaseDatasetImplementationDtoModelAssembler databaseDatasetImplementationDtoModelAssembler) {
        super(DatasetImplementationController.class, DatasetImplementationDto.class);
        this.databaseDatasetImplementationDtoModelAssembler = databaseDatasetImplementationDtoModelAssembler;
    }

    @Override
    public DatasetImplementationDto toModel(DatasetImplementation datasetImplementation) {
        if (datasetImplementation instanceof DatabaseDatasetImplementation) {
            return databaseDatasetImplementationDtoModelAssembler.toModel((DatabaseDatasetImplementation) datasetImplementation);
        } else {
            throw new RuntimeException();
        }
    }

}