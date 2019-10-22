package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResultSetDataset extends Dataset {

    public ResultSetDataset(DataType name, DataType labels, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        super(name, labels, executionRuntime);
    }

    public ResultSetDataset(String name, List<String> labels, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        super(name, labels, executionRuntime);
    }

    @Override
    public void clean(ExecutionRuntime executionRuntime) {

    }

    @Override
    protected Database createNewDatasetDatabase(String datasetName, String filename, String tableName, int inventoryId) throws IOException {
        return null;
    }

    @Override
    public Optional<DataType> getDataItem(String dataItem, ExecutionRuntime executionRuntime) {
        return Optional.empty();
    }

    @Override
    public Map<String, DataType> getDataItems(ExecutionRuntime executionRuntime) {
        return null;
    }

    @Override
    public void setDataItem(String key, DataType value) {

    }

    @Override
    public String toString() {
        return null;
    }
}
