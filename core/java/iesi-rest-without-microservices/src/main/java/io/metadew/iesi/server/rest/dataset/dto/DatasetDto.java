package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Relation(value = "dataset", collectionRelation = "datasets")
public class DatasetDto extends RepresentationModel<DatasetDto> {

    private UUID uuid;
    private String name;
    private List<DatasetImplementationDto> implementations;


    public Dataset convertToEntity() {
        return new Dataset(
                new DatasetKey(uuid),
                name,
                implementations.stream()
                        .map(datasetImplementationDto -> datasetImplementationDto.convertToEntity(uuid, name))
                        .collect(Collectors.toList())
        );
    }

    public Dataset convertToNewEntity() {
        UUID datasetUuid = uuid == null ? UUID.randomUUID() : uuid;
        return new Dataset(
                new DatasetKey(datasetUuid),
                name,
                implementations.stream()
                        .map(datasetImplementationDto -> datasetImplementationDto.convertToNewEntity(datasetUuid, name))
                        .collect(Collectors.toList())
        );
    }


}
