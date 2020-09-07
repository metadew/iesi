package io.metadew.iesi.datatypes.dataset.implementation.label;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetImplementationLabel extends Metadata<DatasetImplementationLabelKey> {

    private final DatasetImplementationKey datasetImplementationKey;
    private final String value;

    public DatasetImplementationLabel(DatasetImplementationLabelKey metadataKey, DatasetImplementationKey datasetImplementationKey, String value) {
        super(metadataKey);
        this.datasetImplementationKey = datasetImplementationKey;
        this.value = value;
    }
}