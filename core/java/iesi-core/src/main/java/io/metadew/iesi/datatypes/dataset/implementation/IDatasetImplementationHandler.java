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

public interface IDatasetImplementationHandler extends IDataTypeService<DatasetImplementation> {

    boolean isEmpty(DatasetImplementation datasetImplementation);
    
    void delete(DatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime);

    void setDataItem(DatasetImplementation datasetImplementation, String key, DataType value);

    Optional<DataType> getDataItem(DatasetImplementation datasetImplementation, String dataItem, ExecutionRuntime executionRuntime);

    Map<String, DataType> getDataItems(DatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime);

    /*Optional<DatasetImplementation> getDatasetImplementation(String name, List<String> labels);

    Optional<DatasetImplementation> getDatasetImplementation(DatasetKey datasetKey, List<String> labels);

    DatasetImplementation createNewDatasetImplementation(Dataset dataset, List<String> labels);

    DatasetImplementation createNewDatasetImplementation(DatasetKey datasetKey, String name, List<String> labels);

    DatasetImplementation createNewDatasetImplementation(String name, List<String> labels);*/

    void clean(DatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime);

    DataType resolve(DatasetImplementation dataset, String key, ObjectNode jsonNode, ExecutionRuntime executionRuntime);
    
}
