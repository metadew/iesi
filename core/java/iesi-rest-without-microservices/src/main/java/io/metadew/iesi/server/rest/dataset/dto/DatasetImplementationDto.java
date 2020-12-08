package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Relation(value = "datasetImplementation", collectionRelation = "datasetImplementations")
public abstract class DatasetImplementationDto extends RepresentationModel<DatasetImplementationDto> {

    private UUID uuid;
    private List<DatasetImplementationLabelDto> labels;

    public abstract DatasetImplementation convertToEntity(UUID datasetUuid, String datasetName);

    public abstract DatasetImplementation convertToNewEntity(UUID datasetUuid, String datasetName);

}
