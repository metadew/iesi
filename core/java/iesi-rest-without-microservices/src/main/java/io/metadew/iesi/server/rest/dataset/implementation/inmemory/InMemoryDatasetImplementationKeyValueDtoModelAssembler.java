package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.inmemory.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class InMemoryDatasetImplementationKeyValueDtoModelAssembler extends RepresentationModelAssemblerSupport<DatabaseDatasetImplementationKeyValue, InMemoryDatasetImplementationKeyValueDto> {

    @Autowired
    public InMemoryDatasetImplementationKeyValueDtoModelAssembler() {
        super(DatasetImplementationController.class, InMemoryDatasetImplementationKeyValueDto.class);
    }

    @Override
    public InMemoryDatasetImplementationKeyValueDto toModel(DatabaseDatasetImplementationKeyValue databaseDatasetImplementationKeyValue) {
        InMemoryDatasetImplementationKeyValueDto inMemoryDatasetImplementationKeyValueDto = instantiateModel(databaseDatasetImplementationKeyValue);

        inMemoryDatasetImplementationKeyValueDto.setUuid(databaseDatasetImplementationKeyValue.getMetadataKey().getUuid());
        inMemoryDatasetImplementationKeyValueDto.setKey(databaseDatasetImplementationKeyValue.getKey());
        inMemoryDatasetImplementationKeyValueDto.setValue(databaseDatasetImplementationKeyValue.getValue());
        return inMemoryDatasetImplementationKeyValueDto;
    }

}