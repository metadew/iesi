package io.metadew.iesi.server.rest.dataset.implementation.database;

import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnWebApplication
public class DatabaseDatasetImplementationKeyValueDtoModelAssembler extends RepresentationModelAssemblerSupport<DatabaseDatasetImplementationKeyValue, DatabaseDatasetImplementationKeyValueDto> {

    @Autowired
    public DatabaseDatasetImplementationKeyValueDtoModelAssembler() {
        super(DatasetImplementationController.class, DatabaseDatasetImplementationKeyValueDto.class);
    }

    @Override
    public DatabaseDatasetImplementationKeyValueDto toModel(DatabaseDatasetImplementationKeyValue datasetImplementationKeyValue) {
        DatabaseDatasetImplementationKeyValueDto databaseDatasetImplementationKeyValueDto = instantiateModel(datasetImplementationKeyValue);

        databaseDatasetImplementationKeyValueDto.setUuid(datasetImplementationKeyValue.getMetadataKey().getUuid());
        databaseDatasetImplementationKeyValueDto.setKey(datasetImplementationKeyValue.getKey());
        databaseDatasetImplementationKeyValueDto.setValue(datasetImplementationKeyValue.getValue());
        return databaseDatasetImplementationKeyValueDto;
    }

}