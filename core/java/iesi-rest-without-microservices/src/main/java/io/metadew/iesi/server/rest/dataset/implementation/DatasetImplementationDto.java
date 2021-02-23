package io.metadew.iesi.server.rest.dataset.implementation;

import com.sun.istack.Nullable;
import io.metadew.iesi.server.rest.script.dto.NoEmptyLinksRepresentationModel;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Relation(value = "datasetImplementation", collectionRelation = "datasetImplementations")
public class DatasetImplementationDto extends NoEmptyLinksRepresentationModel<DatasetImplementationDto> {

    private UUID uuid;
    private String type;
    private Set<DatasetImplementationLabelDto> labels;

}
