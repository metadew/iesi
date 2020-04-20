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

    public Dataset(DatasetKey metadataKey, String name, List<DatasetLabel> labels) {
        super(metadataKey);
        this.name = name;
        this.labels = labels;
    }

    public Dataset(String name, List<DatasetLabel> labels) {
        super(new DatasetKey());
        this.name = name;
        this.labels = labels;
    }

    private final String name;
    private final List<DatasetLabel> labels;

}
