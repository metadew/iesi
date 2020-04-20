package io.metadew.iesi.metadata.definition.dataset.implementation;

import io.metadew.iesi.metadata.definition.dataset.DatasetKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RepositoryDatasetImplementation extends DatasetImplementation {

    public final Map<String, String> keyValuePairs;

    public RepositoryDatasetImplementation(DatasetKey metadataKey, Map<String, String> keyValuePairs) {
        super(metadataKey);
        this.keyValuePairs = keyValuePairs;
    }
}
