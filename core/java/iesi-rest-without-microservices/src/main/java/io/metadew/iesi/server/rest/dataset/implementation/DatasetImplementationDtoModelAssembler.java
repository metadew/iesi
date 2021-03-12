package io.metadew.iesi.server.rest.dataset.implementation;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationDtoModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class DatasetImplementationDtoModelAssembler extends RepresentationModelAssemblerSupport<DatasetImplementation, DatasetImplementationDto> {

    private final InMemoryDatasetImplementationDtoModelAssembler inMemoryDatasetImplementationDtoModelAssembler;

    @Autowired
    public DatasetImplementationDtoModelAssembler(InMemoryDatasetImplementationDtoModelAssembler inMemoryDatasetImplementationDtoModelAssembler) {
        super(DatasetImplementationController.class, DatasetImplementationDto.class);
        this.inMemoryDatasetImplementationDtoModelAssembler = inMemoryDatasetImplementationDtoModelAssembler;
    }

    @Override
    public DatasetImplementationDto toModel(DatasetImplementation datasetImplementation) {
        if (datasetImplementation instanceof InMemoryDatasetImplementation) {
            return inMemoryDatasetImplementationDtoModelAssembler.toModel((InMemoryDatasetImplementation) datasetImplementation);
        } else {
            throw new RuntimeException();
        }
    }

}