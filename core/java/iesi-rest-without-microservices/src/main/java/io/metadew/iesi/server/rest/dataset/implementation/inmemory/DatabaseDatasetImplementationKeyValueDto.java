package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKeyValueKey;
import io.metadew.iesi.server.rest.script.dto.NoEmptyLinksRepresentationModel;
import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatabaseDatasetImplementationKeyValueDto extends NoEmptyLinksRepresentationModel<DatabaseDatasetImplementationKeyValueDto> {

    private UUID uuid;
    private String key;
    private String value;

    public DatasetImplementationKeyValue convertToEntity(UUID datasetImplementationUuid) {
        return new DatasetImplementationKeyValue(
                new DatasetImplementationKeyValueKey(getUuid()),
                new DatasetImplementationKey(datasetImplementationUuid),
                getKey(),
                getValue()
        );
    }

    public DatasetImplementationKeyValue convertToNewEntity(UUID datasetImplementationUuid) {
        return new DatasetImplementationKeyValue(
                new DatasetImplementationKeyValueKey(getUuid() == null ? UUID.randomUUID() : getUuid()),
                new DatasetImplementationKey(datasetImplementationUuid),
                getKey(),
                getValue()
        );
    }

}
