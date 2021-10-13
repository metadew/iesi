package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnWebApplication
public class InMemoryDatasetImplementationKeyValueDtoModelAssembler extends RepresentationModelAssemblerSupport<InMemoryDatasetImplementationKeyValue, InMemoryDatasetImplementationKeyValueDto> {

    @Autowired
    public InMemoryDatasetImplementationKeyValueDtoModelAssembler() {
        super(DatasetImplementationController.class, InMemoryDatasetImplementationKeyValueDto.class);
    }

    @Override
    public InMemoryDatasetImplementationKeyValueDto toModel(InMemoryDatasetImplementationKeyValue inMemoryDatasetImplementationKeyValue) {
        InMemoryDatasetImplementationKeyValueDto inMemoryDatasetImplementationKeyValueDto = instantiateModel(inMemoryDatasetImplementationKeyValue);

        inMemoryDatasetImplementationKeyValueDto.setUuid(inMemoryDatasetImplementationKeyValue.getMetadataKey().getUuid());
        inMemoryDatasetImplementationKeyValueDto.setKey(inMemoryDatasetImplementationKeyValue.getKey());
        inMemoryDatasetImplementationKeyValueDto.setValue(inMemoryDatasetImplementationKeyValue.getValue());
        return inMemoryDatasetImplementationKeyValueDto;
    }

}