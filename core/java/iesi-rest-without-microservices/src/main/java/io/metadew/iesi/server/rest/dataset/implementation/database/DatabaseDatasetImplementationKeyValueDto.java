package io.metadew.iesi.server.rest.dataset.implementation.database;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
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
