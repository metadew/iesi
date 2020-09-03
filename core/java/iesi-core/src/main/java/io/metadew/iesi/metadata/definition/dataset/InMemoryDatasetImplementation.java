package io.metadew.iesi.metadata.definition.dataset;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class InMemoryDatasetImplementation extends DatasetImplementation {

    private List<InMemoryDatasetImplementationKeyValue> keyValues;

    public InMemoryDatasetImplementation(DatasetImplementationKey metadataKey, DatasetKey datasetKey, String name, List<DatasetImplementationLabel> datasetImplementationLabels, List<InMemoryDatasetImplementationKeyValue> keyValues) {
        super(metadataKey, datasetKey, name, datasetImplementationLabels);
        this.keyValues = keyValues;
    }

    public String toString() {
        return super.toString();
    }

}
