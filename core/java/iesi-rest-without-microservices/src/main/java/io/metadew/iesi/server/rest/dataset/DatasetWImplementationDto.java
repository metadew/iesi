package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.script.dto.NoEmptyLinksRepresentationModel;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Relation(value = "dataset", collectionRelation = "datasets")
public class DatasetWImplementationDto extends NoEmptyLinksRepresentationModel<DatasetDto> {

    private UUID uuid;
    private String name;
    private Map<UUID, DatasetImplementationDto> implementations;

}
