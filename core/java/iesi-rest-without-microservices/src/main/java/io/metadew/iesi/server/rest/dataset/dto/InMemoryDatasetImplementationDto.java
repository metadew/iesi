package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import lombok.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryDatasetImplementationDto extends DatasetImplementationDto {

    private Set<InMemoryDatasetImplementationKeyValueDto> keyValues;

    @Builder
    public InMemoryDatasetImplementationDto(UUID uuid, Set<DatasetImplementationLabelDto> labels, Set<InMemoryDatasetImplementationKeyValueDto> keyValues) {
        super(uuid, labels);
        this.keyValues = keyValues;
    }

    public InMemoryDatasetImplementation convertToEntity(UUID datasetUuid, String datasetName) {
        return new InMemoryDatasetImplementation(
                new DatasetImplementationKey(getUuid()),
                new DatasetKey(datasetUuid),
                datasetName,
                getLabels().stream()
                        .map(datasetImplementationLabelDto -> datasetImplementationLabelDto.convertToEntity(getUuid()))
                        .collect(Collectors.toSet()),
                keyValues.stream()
                        .map(keyValue -> keyValue.convertToEntity(getUuid()))
                        .collect(Collectors.toSet())
        );
    }

    public InMemoryDatasetImplementation convertToNewEntity(UUID datasetUuid, String datasetName) {
        UUID datasetImplementationUuid = getUuid() == null ? UUID.randomUUID() : getUuid();
        return new InMemoryDatasetImplementation(
                new DatasetImplementationKey(datasetImplementationUuid),
                new DatasetKey(datasetUuid),
                datasetName,
                getLabels().stream()
                        .map(datasetImplementationLabelDto -> datasetImplementationLabelDto.convertToNewEntity(datasetImplementationUuid))
                        .collect(Collectors.toSet()),
                keyValues.stream()
                        .map(keyValue -> keyValue.convertToNewEntity(datasetImplementationUuid))
                        .collect(Collectors.toSet())
        );
    }

}
