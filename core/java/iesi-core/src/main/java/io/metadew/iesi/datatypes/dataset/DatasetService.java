package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DatasetService<T extends Dataset> extends DataTypeService<T> {

    public void clean(T dataset, ExecutionRuntime executionRuntime);

    public Optional<DataType> getDataItem(T dataset, String dataItem, ExecutionRuntime executionRuntime);

    public Map<String, DataType> getDataItems(T dataset, ExecutionRuntime executionRuntime);

    public void setDataItem(T dataset, String key, DataType value);

    public KeyValueDataset getByNameAndLabels(String name, List<String> labels, ExecutionRuntime executionRuntime) throws IOException;

    public void shutdown(T dataset);

}
