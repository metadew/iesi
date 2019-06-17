package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResultSetDataset extends Dataset {

    public ResultSetDataset(DataType name, DataType labels, FrameworkFolderConfiguration frameworkFolderConfiguration, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        super(name, labels, frameworkFolderConfiguration, executionRuntime);
    }

    public ResultSetDataset(String name, List<String> labels, FrameworkFolderConfiguration frameworkFolderConfiguration, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        super(name, labels, frameworkFolderConfiguration, executionRuntime);
    }

    @Override
    public void clean() {

    }

    @Override
    protected Database createNewDatasetDatabase(String datasetName, String filename, String tableName, int inventoryId) throws IOException {
        return null;
    }

    @Override
    public Optional<DataType> getDataItem(String dataItem) {
        return Optional.empty();
    }

    @Override
    public Map<String, DataType> getDataItems() {
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
