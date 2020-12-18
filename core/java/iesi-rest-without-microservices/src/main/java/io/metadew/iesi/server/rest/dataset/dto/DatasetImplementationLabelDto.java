package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DatasetImplementationLabelDto extends RepresentationModel<DatasetImplementationLabelDto> {

    private UUID uuid;
    private String label;

    public DatasetImplementationLabel convertToEntity(UUID datasetImplementationUuid) {
        return new DatasetImplementationLabel(
                new DatasetImplementationLabelKey(uuid),
                new DatasetImplementationKey(datasetImplementationUuid),
                label);
    }

    public DatasetImplementationLabel convertToNewEntity(UUID datasetImplementationUuid) {
        return new DatasetImplementationLabel(
                new DatasetImplementationLabelKey(uuid == null ? UUID.randomUUID() : uuid),
                new DatasetImplementationKey(datasetImplementationUuid),
                label);
    }

}