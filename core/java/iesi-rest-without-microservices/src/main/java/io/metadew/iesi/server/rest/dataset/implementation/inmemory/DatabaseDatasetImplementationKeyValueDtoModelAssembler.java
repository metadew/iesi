package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class DatabaseDatasetImplementationKeyValueDtoModelAssembler extends RepresentationModelAssemblerSupport<DatabaseDatasetImplementationKeyValue, DatabaseDatasetImplementationKeyValueDto> {

    @Autowired
    public DatabaseDatasetImplementationKeyValueDtoModelAssembler() {
        super(DatasetImplementationController.class, DatabaseDatasetImplementationKeyValueDto.class);
    }

    @Override
    public DatabaseDatasetImplementationKeyValueDto toModel(DatabaseDatasetImplementationKeyValue databaseDatasetImplementationKeyValue) {
        DatabaseDatasetImplementationKeyValueDto databaseDatasetImplementationKeyValueDto = instantiateModel(databaseDatasetImplementationKeyValue);

        databaseDatasetImplementationKeyValueDto.setUuid(databaseDatasetImplementationKeyValue.getMetadataKey().getUuid());
        databaseDatasetImplementationKeyValueDto.setKey(databaseDatasetImplementationKeyValue.getKey());
        databaseDatasetImplementationKeyValueDto.setValue(databaseDatasetImplementationKeyValue.getValue());
        return databaseDatasetImplementationKeyValueDto;
    }

}