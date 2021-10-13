package io.metadew.iesi.server.rest.dataset.implementation;

import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnWebApplication
public class DatasetImplementationLabelDtoModelAssembler extends RepresentationModelAssemblerSupport<DatasetImplementationLabel, DatasetImplementationLabelDto> {

    @Autowired
    public DatasetImplementationLabelDtoModelAssembler() {
        super(DatasetImplementationController.class, DatasetImplementationLabelDto.class);
    }

    @Override
    public DatasetImplementationLabelDto toModel(DatasetImplementationLabel datasetImplementationLabel) {
        DatasetImplementationLabelDto datasetImplementationLabelDto = instantiateModel(datasetImplementationLabel);

        datasetImplementationLabelDto.setUuid(datasetImplementationLabel.getMetadataKey().getUuid());
        datasetImplementationLabelDto.setLabel(datasetImplementationLabel.getValue());

        return datasetImplementationLabelDto;
    }

}