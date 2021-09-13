package io.metadew.iesi.datatypes.dataset.implementation.database;

import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class DatabaseDatasetImplementation extends DatasetImplementation {

    private Set<DatabaseDatasetImplementationKeyValue> keyValues;

    @Builder
    public DatabaseDatasetImplementation(DatasetImplementationKey metadataKey, DatasetKey datasetKey, String name, Set<DatasetImplementationLabel> datasetImplementationLabels, Set<DatabaseDatasetImplementationKeyValue> keyValues) {
        super(metadataKey, datasetKey, name, datasetImplementationLabels);
        this.keyValues = keyValues;
    }

    public String toString() {
        return super.toString();
    }

}
