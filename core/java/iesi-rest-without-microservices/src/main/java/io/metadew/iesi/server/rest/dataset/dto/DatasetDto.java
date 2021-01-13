package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Relation(value = "dataset", collectionRelation = "datasets")
public class DatasetDto extends RepresentationModel<DatasetDto> {

    private UUID uuid;
    private String name;
    private Set<DatasetImplementationDto> implementations;

    public Dataset convertToEntity() {
        return new Dataset(
                new DatasetKey(uuid),
                name,
                implementations.stream()
                        .map(datasetImplementationDto -> datasetImplementationDto.convertToEntity(uuid, name))
                        .collect(Collectors.toSet())
        );
    }

    public Dataset convertToNewEntity() {
        UUID datasetUuid = uuid == null ? UUID.randomUUID() : uuid;
        return new Dataset(
                new DatasetKey(datasetUuid),
                name,
                implementations.stream()
                        .map(datasetImplementationDto -> datasetImplementationDto.convertToNewEntity(datasetUuid, name))
                        .collect(Collectors.toSet())
        );
    }


}
