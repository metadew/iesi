package io.metadew.iesi.datatypes.dataset.implementation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IDatasetImplementationService<T extends DatasetImplementation> extends IDataTypeService<T> {

    boolean isEmpty(T datasetImplementation);

    void delete(T datasetImplementation, ExecutionRuntime executionRuntime);

    void setDataItem(T datasetImplementation, String key, DataType value);

    Optional<DataType> getDataItem(T datasetImplementation, String dataItem, ExecutionRuntime executionRuntime);

    Map<String, DataType> getDataItems(T datasetImplementation, ExecutionRuntime executionRuntime);

    T createNewDatasetImplementation(Dataset dataset, List<String> labels, ExecutionRuntime executionRuntime);

    T createNewDatasetImplementation(DatasetKey datasetKey, String name, List<String> labels, ExecutionRuntime executionRuntime);

    T createNewDatasetImplementation(String name, List<String> labels, ExecutionRuntime executionRuntime);

    void clean(T datasetImplementation, ExecutionRuntime executionRuntime);

    DataType resolve(T dataset, String key, ObjectNode jsonNode, ExecutionRuntime executionRuntime);

}