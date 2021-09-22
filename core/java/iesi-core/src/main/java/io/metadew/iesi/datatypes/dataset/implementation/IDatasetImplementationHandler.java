package io.metadew.iesi.datatypes.dataset.implementation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.util.Map;
import java.util.Optional;

public interface IDatasetImplementationHandler extends IDataTypeService<DatasetImplementation> {

    boolean isEmpty(DatasetImplementation datasetImplementation);
    
    void delete(DatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime);

    void setDataItem(DatasetImplementation datasetImplementation, String key, DataType value);

    Optional<DataType> getDataItem(DatasetImplementation datasetImplementation, String dataItem, ExecutionRuntime executionRuntime);

    Map<String, DataType> getDataItems(DatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime);

    void clean(DatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime);

    DataType resolve(DatasetImplementation dataset, String key, ObjectNode jsonNode, ExecutionRuntime executionRuntime);
    
}
