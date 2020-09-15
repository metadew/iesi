package io.metadew.iesi.datatypes.dataset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = DatasetJsonComponent.Deserializer.class)
@JsonSerialize(using = DatasetJsonComponent.Serializer.class)
public class Dataset extends Metadata<DatasetKey> {

    private final String name;
    private final List<DatasetImplementation> datasetImplementations;

    @Builder
    public Dataset(DatasetKey metadataKey, String name, List<DatasetImplementation> datasetImplementations) {
        super(metadataKey);
        this.name = name;
        this.datasetImplementations = datasetImplementations;
    }

}
