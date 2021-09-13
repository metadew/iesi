package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.DatabaseDatasetImplementationKeyValueKey;
import io.metadew.iesi.server.rest.script.dto.NoEmptyLinksRepresentationModel;
import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InMemoryDatasetImplementationKeyValueDto extends NoEmptyLinksRepresentationModel<InMemoryDatasetImplementationKeyValueDto> {

    private UUID uuid;
    private String key;
    private String value;

    public DatabaseDatasetImplementationKeyValue convertToEntity(UUID datasetImplementationUuid) {
        return new DatabaseDatasetImplementationKeyValue(
                new DatabaseDatasetImplementationKeyValueKey(getUuid()),
                new DatasetImplementationKey(datasetImplementationUuid),
                getKey(),
                getValue()
        );
    }

    public DatabaseDatasetImplementationKeyValue convertToNewEntity(UUID datasetImplementationUuid) {
        return new DatabaseDatasetImplementationKeyValue(
                new DatabaseDatasetImplementationKeyValueKey(getUuid() == null ? UUID.randomUUID() : getUuid()),
                new DatasetImplementationKey(datasetImplementationUuid),
                getKey(),
                getValue()
        );
    }

}
