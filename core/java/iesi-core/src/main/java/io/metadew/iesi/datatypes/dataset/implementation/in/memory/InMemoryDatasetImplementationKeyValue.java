package io.metadew.iesi.datatypes.dataset.implementation.in.memory;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InMemoryDatasetImplementationKeyValue extends Metadata<InMemoryDatasetImplementationKeyValueKey> {

    private final DatasetImplementationKey datasetImplementationKey;
    private final String key;
    private DataType value;

    @Builder
    public InMemoryDatasetImplementationKeyValue(InMemoryDatasetImplementationKeyValueKey metadataKey, DatasetImplementationKey datasetImplementationKey, String key, DataType value) {
        super(metadataKey);
        this.datasetImplementationKey = datasetImplementationKey;
        this.key = key;
        this.value = value;
    }
}
