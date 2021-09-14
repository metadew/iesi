package io.metadew.iesi.datatypes.dataset.implementation.inmemory;

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
public class InMemoryDatasetImplementation extends DatasetImplementation {

    private Set<InMemoryDatasetImplementationKeyValue> keyValues;

    @Builder
    public InMemoryDatasetImplementation(DatasetImplementationKey metadataKey, DatasetKey datasetKey, String name, Set<DatasetImplementationLabel> datasetImplementationLabels) {
        super(metadataKey, datasetKey, name, datasetImplementationLabels);
    }

    public String toString(){
        return super.toString();
    }
}
