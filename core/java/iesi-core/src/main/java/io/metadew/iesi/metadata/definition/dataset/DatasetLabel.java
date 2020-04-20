package io.metadew.iesi.metadata.definition.dataset;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetLabel extends Metadata<DatasetLabelKey> {

    public DatasetLabel(DatasetLabelKey metadataKey, String value) {
        super(metadataKey);
        this.value = value;
    }

    public DatasetLabel(String value) {
        super(new DatasetLabelKey());
        this.value = value;
    }

    private final String value;
}
