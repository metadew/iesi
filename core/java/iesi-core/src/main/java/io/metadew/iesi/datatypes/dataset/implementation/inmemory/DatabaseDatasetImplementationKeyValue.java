package io.metadew.iesi.datatypes.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatabaseDatasetImplementationKeyValue extends Metadata<DatabaseDatasetImplementationKeyValueKey> {

    private final DatasetImplementationKey datasetImplementationKey;
    private final String key;
    private String value;

    @Builder
    public DatabaseDatasetImplementationKeyValue(DatabaseDatasetImplementationKeyValueKey metadataKey, DatasetImplementationKey datasetImplementationKey, String key, String value) {
        super(metadataKey);
        this.datasetImplementationKey = datasetImplementationKey;
        this.key = key;
        this.value = value;
    }
}
