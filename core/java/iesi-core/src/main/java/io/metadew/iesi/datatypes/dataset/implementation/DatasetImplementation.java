package io.metadew.iesi.datatypes.dataset.implementation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class DatasetImplementation extends Metadata<DatasetImplementationKey> implements DataType {

    private final DatasetKey datasetKey;
    private final String name;
    private final Set<DatasetImplementationLabel> datasetImplementationLabels;
    private Set<DatasetImplementationKeyValue> keyValues;

    public DatasetImplementation(DatasetImplementationKey metadataKey, DatasetKey datasetKey, String name, Set<DatasetImplementationLabel> datasetImplementationLabels, Set<DatasetImplementationKeyValue> keyValues) {
        super(metadataKey);
        this.datasetKey = datasetKey;
        this.name = name;
        this.datasetImplementationLabels = datasetImplementationLabels;
        this.keyValues = keyValues;
    }


    public String toString() {
        return "{{^dataset(" + new Text(name).toString() + ", " +
                new Array(datasetImplementationLabels.stream()
                        .map(DatasetImplementationLabel::getValue)
                        .map(Text::new)
                        .collect(Collectors.toList())).toString() + ")}}";
    }

}
