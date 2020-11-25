package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.server.rest.dataset.DatasetImplementationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DatasetImplementationDtoModelAssembler extends RepresentationModelAssemblerSupport<DatasetImplementation, DatasetImplementationDto> {

    @Autowired
    public DatasetImplementationDtoModelAssembler() {
        super(DatasetImplementationController.class, DatasetImplementationDto.class);
    }

    @Override
    public DatasetImplementationDto toModel(DatasetImplementation datasetImplementation) {
        return convertToDto(datasetImplementation);
    }

    public DatasetImplementationDto convertToDto(DatasetImplementation datasetImplementation) {
        if (datasetImplementation instanceof InMemoryDatasetImplementation) {
            return new InMemoryDatasetImplementationDto(
                    datasetImplementation.getMetadataKey().getUuid(),
                    datasetImplementation.getDatasetImplementationLabels().stream().map(this::convertToDto).collect(Collectors.toList()),
                    ((InMemoryDatasetImplementation) datasetImplementation).getKeyValues().stream().map(this::convertToDto).collect(Collectors.toList())
            );
        } else {
            throw new RuntimeException();
        }
    }

    private DatasetImplementationLabelDto convertToDto(DatasetImplementationLabel datasetImplementationLabel) {
        return new DatasetImplementationLabelDto(
                datasetImplementationLabel.getMetadataKey().getUuid(),
                datasetImplementationLabel.getValue()
        );
    }

    private InMemoryDatasetImplementationKeyValueDto convertToDto(InMemoryDatasetImplementationKeyValue inMemoryDatasetImplementationKeyValue) {
        return new InMemoryDatasetImplementationKeyValueDto(
                inMemoryDatasetImplementationKeyValue.getMetadataKey().getUuid(),
                inMemoryDatasetImplementationKeyValue.getKey(),
                inMemoryDatasetImplementationKeyValue.getValue()
        );
    }
}