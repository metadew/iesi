package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.server.rest.dataset.DatasetController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnWebApplication
public class DatasetDtoModelAssembler extends RepresentationModelAssemblerSupport<Dataset, DatasetDto> {

    @Autowired
    public DatasetDtoModelAssembler() {
        super(DatasetController.class, DatasetDto.class);
    }

    @Override
    public DatasetDto toModel(Dataset dataset) {
        DatasetDto datasetDto = instantiateModel(dataset);

        datasetDto.setUuid(dataset.getMetadataKey().getUuid());
        datasetDto.setName(dataset.getName());
        datasetDto.setSecurityGroupName(dataset.getSecurityGroupName());
        datasetDto.setImplementations(dataset.getDatasetImplementations().stream()
                .map(e -> e.getMetadataKey().getUuid())
                .collect(Collectors.toSet()));

        return datasetDto;
    }

    public List<DatasetDto> toModel(List<Dataset> datasets) {
        return datasets.stream().map(this::toModel).collect(Collectors.toList());
    }

    public DatasetDto toModel(DatasetDto datasetDto) {
        return datasetDto;
    }
}