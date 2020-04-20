package io.metadew.iesi.metadata.definition.dataset.implementation;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.dataset.DatasetKey;
import io.metadew.iesi.metadata.definition.dataset.implementation.usage.DatasetUsageStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class DatasetImplementation extends Metadata<DatasetKey> {

    public final DatasetUsageStrategy datasetUsageStrategy;

    public DatasetImplementation(DatasetKey metadataKey, DatasetUsageStrategy datasetUsageStrategy) {
        super(metadataKey);
        this.datasetUsageStrategy = datasetUsageStrategy;
    }

}
