package io.metadew.iesi.datatypes;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata_repository.repository.database.connection.DatabaseConnection;

import java.util.List;

public class Dataset {

    private final String name;
    private final List<String> labels;

    public Dataset(String name, List<String> labels) {
        this.name = name;
        this.labels = labels;
    }

    public DataType getDatasetItem(FrameworkExecution frameworkExecution, String DatasetItemName) {
        DatabaseConnection databaseConnection = this.getDatasetConnection(frameworkExecution);
        return null;
    }

    private DatabaseConnection getDatasetConnection(FrameworkExecution frameworkExecution) {
        return null;
    }
}
