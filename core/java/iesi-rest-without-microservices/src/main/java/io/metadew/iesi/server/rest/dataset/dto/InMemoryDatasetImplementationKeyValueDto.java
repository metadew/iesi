package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InMemoryDatasetImplementationKeyValueDto extends RepresentationModel<InMemoryDatasetImplementationKeyValueDto> {

    private UUID uuid;
    private String key;
    private String value;

    public InMemoryDatasetImplementationKeyValue convertToEntity(UUID datasetImplementationUuid) {
        return new InMemoryDatasetImplementationKeyValue(
                new InMemoryDatasetImplementationKeyValueKey(getUuid()),
                new DatasetImplementationKey(datasetImplementationUuid),
                getKey(),
                getValue()
        );
    }

    public InMemoryDatasetImplementationKeyValue convertToNewEntity(UUID datasetImplementationUuid) {
        return new InMemoryDatasetImplementationKeyValue(
                new InMemoryDatasetImplementationKeyValueKey(getUuid() == null ? UUID.randomUUID() : getUuid()),
                new DatasetImplementationKey(datasetImplementationUuid),
                getKey(),
                getValue()
        );
    }

}
