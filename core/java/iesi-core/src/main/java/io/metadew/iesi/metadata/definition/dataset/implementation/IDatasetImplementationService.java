package io.metadew.iesi.metadata.definition.dataset.implementation;

import io.metadew.iesi.datatypes.DataType;

import java.util.Map;
import java.util.Optional;

public interface IDatasetImplementationService<T extends DatasetImplementation> {

    public void shutdown(T datasetImplementation);

    public void clean(T datasetImplementation);

    public Optional<DataType> getDataItem(T datasetImplementation, String dataItem);

    public Map<String, DataType> getDataItems(T datasetImplementation);

    public void setDataItem(T datasetImplementation, String key, DataType value);

    public Class<T> appliesTo();

}
