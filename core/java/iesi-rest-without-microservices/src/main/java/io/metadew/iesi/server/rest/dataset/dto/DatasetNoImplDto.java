package io.metadew.iesi.server.rest.dataset.dto;


import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;


import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Relation(value = "dataset", collectionRelation = "datasets")
public class DatasetNoImplDto extends RepresentationModel<DatasetNoImplDto> {

    private UUID uuid;
    private String name;
    private Set<UUID> implementations;



}
