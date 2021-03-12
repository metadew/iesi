package io.metadew.iesi.datatypes.dataset.implementation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IDatasetImplementationService<T extends DatasetImplementation> {

    Optional<T> getDatasetImplementation(String name, List<String> labels);

    Optional<T> getDatasetImplementation(DatasetKey datasetKey, List<String> labels);

    T createNewDatasetImplementation(Dataset dataset, List<String> labels);

    T createNewDatasetImplementation(DatasetKey datasetKey, String name, List<String> labels);

    T createNewDatasetImplementation(String name, List<String> labels);

    void clean(T datasetImplementation, ExecutionRuntime executionRuntime);

    void delete(T datasetImplementation, ExecutionRuntime executionRuntime);

    Optional<DataType> getDataItem(T datasetImplementation, String dataItem, ExecutionRuntime executionRuntime);

    Map<String, DataType> getDataItems(T datasetImplementation, ExecutionRuntime executionRuntime);

    void setDataItem(T datasetImplementation, String key, DataType value);

    boolean exists(DatasetImplementationKey datasetImplementationKey);

    boolean exists(String name, List<String> labels);

    Optional<DatasetImplementation> get(DatasetImplementationKey datasetImplementationKey);

    void create(T datasetImplementation);

    void delete(T datasetImplementation);

    void delete(DatasetImplementationKey datasetImplementationKey);

    void update(T datasetImplementation);

    List<DatasetImplementation> getAll();

    boolean isEmpty(T datasetImplementation);

    List<DatasetImplementation> getByDatasetId(DatasetKey datasetKey);

}