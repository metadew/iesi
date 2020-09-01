package io.metadew.iesi.metadata.definition.dataset;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Dataset extends Metadata<DatasetKey> {

    private final String name;
    private final List<DatasetImplementation> datasetImplementations;

    public Dataset(DatasetKey metadataKey, String name, List<DatasetImplementation> datasetImplementations) {
        super(metadataKey);
        this.name = name;
        this.datasetImplementations = datasetImplementations;
    }

}
