package io.metadew.iesi.metadata.definition.dataset.implementation.usage;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.definition.dataset.implementation.DatasetImplementation;

import java.util.Map;
import java.util.Optional;

public interface IDatasetUsageHandler<T extends DatasetUsageStrategy> {

    public void clean(T datasetUsageStrategy, DatasetImplementation datasetImplementation);

    public void setDataItem(T datasetUsageStrategy, DatasetImplementation datasetImplementation, String key, DataType value);

    public Optional<DataType> getDataItem(T datasetUsageStrategy, DatasetImplementation datasetImplementation, String dataItem);

    public Map<String, DataType> getDataItems(T datasetUsageStrategy, DatasetImplementation datasetImplementation);

}
