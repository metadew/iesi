package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationController;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDtoModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class InMemoryDatasetImplementationDtoModelAssembler extends RepresentationModelAssemblerSupport<InMemoryDatasetImplementation, InMemoryDatasetImplementationDto> {

    private final InMemoryDatasetImplementationKeyValueDtoModelAssembler inMemoryDatasetImplementationKeyValueDtoModelAssembler;
    private final DatasetImplementationLabelDtoModelAssembler datasetImplementationLabelDtoModelAssembler;

    @Autowired
    public InMemoryDatasetImplementationDtoModelAssembler(InMemoryDatasetImplementationKeyValueDtoModelAssembler inMemoryDatasetImplementationKeyValueDtoModelAssembler, DatasetImplementationLabelDtoModelAssembler datasetImplementationLabelDtoModelAssembler) {
        super(DatasetImplementationController.class, InMemoryDatasetImplementationDto.class);
        this.inMemoryDatasetImplementationKeyValueDtoModelAssembler = inMemoryDatasetImplementationKeyValueDtoModelAssembler;
        this.datasetImplementationLabelDtoModelAssembler = datasetImplementationLabelDtoModelAssembler;
    }

    @Override
    public InMemoryDatasetImplementationDto toModel(InMemoryDatasetImplementation inMemoryDatasetImplementation) {
        InMemoryDatasetImplementationDto inMemoryDatasetImplementationDto = instantiateModel(inMemoryDatasetImplementation);

        inMemoryDatasetImplementationDto.setUuid(inMemoryDatasetImplementation.getMetadataKey().getUuid());
        inMemoryDatasetImplementationDto.setKeyValues(inMemoryDatasetImplementation.getKeyValues().stream()
                .map(inMemoryDatasetImplementationKeyValueDtoModelAssembler::toModel)
                .collect(Collectors.toSet()));
        inMemoryDatasetImplementationDto.setLabels(inMemoryDatasetImplementation.getDatasetImplementationLabels().stream()
                .map(datasetImplementationLabelDtoModelAssembler::toModel)
                .collect(Collectors.toSet())
        );
        return inMemoryDatasetImplementationDto;
    }

}