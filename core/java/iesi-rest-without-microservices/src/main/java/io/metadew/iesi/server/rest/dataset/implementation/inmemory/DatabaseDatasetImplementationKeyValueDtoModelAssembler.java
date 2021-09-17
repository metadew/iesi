package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKeyValue;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class DatabaseDatasetImplementationKeyValueDtoModelAssembler extends RepresentationModelAssemblerSupport<DatasetImplementationKeyValue, DatabaseDatasetImplementationKeyValueDto> {

    @Autowired
    public DatabaseDatasetImplementationKeyValueDtoModelAssembler() {
        super(DatasetImplementationController.class, DatabaseDatasetImplementationKeyValueDto.class);
    }

    @Override
    public DatabaseDatasetImplementationKeyValueDto toModel(DatasetImplementationKeyValue datasetImplementationKeyValue) {
        DatabaseDatasetImplementationKeyValueDto databaseDatasetImplementationKeyValueDto = instantiateModel(datasetImplementationKeyValue);

        databaseDatasetImplementationKeyValueDto.setUuid(datasetImplementationKeyValue.getMetadataKey().getUuid());
        databaseDatasetImplementationKeyValueDto.setKey(datasetImplementationKeyValue.getKey());
        databaseDatasetImplementationKeyValueDto.setValue(datasetImplementationKeyValue.getValue());
        return databaseDatasetImplementationKeyValueDto;
    }

}