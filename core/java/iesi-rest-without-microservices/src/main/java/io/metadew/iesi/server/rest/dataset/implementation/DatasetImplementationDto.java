package io.metadew.iesi.server.rest.dataset.implementation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Relation(value = "datasetImplementation", collectionRelation = "datasetImplementations")
public abstract class DatasetImplementationDto extends RepresentationModel<DatasetImplementationDto> {

    private UUID uuid;
    private String type;
    private Set<DatasetImplementationLabelDto> labels;

}
