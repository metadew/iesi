package io.metadew.iesi.metadata.definition.dataset;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IDatasetImplementationService<T extends DatasetImplementation> {

    public Optional<T> getDatasetImplementation(String name, List<String> labels);

    public T createNewDatasetImplementation(String name, List<String> labels);

    public void clean(T datasetImplementation, ExecutionRuntime executionRuntime);

    public Optional<DataType> getDataItem(T datasetImplementation, String dataItem, ExecutionRuntime executionRuntime);

    public Map<String, DataType> getDataItems(T datasetImplementation, ExecutionRuntime executionRuntime);

    public void setDataItem(T datasetImplementation, String key, DataType value);

}