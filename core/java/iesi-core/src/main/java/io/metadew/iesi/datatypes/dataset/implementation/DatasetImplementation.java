package io.metadew.iesi.datatypes.dataset.implementation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class DatasetImplementation extends Metadata<DatasetImplementationKey> implements DataType {

    private final DatasetKey datasetKey;
    private final String name;
    private final List<DatasetImplementationLabel> datasetImplementationLabels;

    public DatasetImplementation(DatasetImplementationKey metadataKey, DatasetKey datasetKey, String name, List<DatasetImplementationLabel> datasetImplementationLabels) {
        super(metadataKey);
        this.datasetKey = datasetKey;
        this.name = name;
        this.datasetImplementationLabels = datasetImplementationLabels;
    }


    public String toString() {
        return "{{^dataset(" + new Text(name).toString() + ", " +
                new Array(datasetImplementationLabels.stream()
                        .map(DatasetImplementationLabel::getValue)
                        .map(Text::new)
                        .collect(Collectors.toList())).toString() + ")}}";
    }

}
