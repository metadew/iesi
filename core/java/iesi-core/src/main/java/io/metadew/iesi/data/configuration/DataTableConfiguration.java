package io.metadew.iesi.data.configuration;

import io.metadew.iesi.data.definition.DataTable;
import io.metadew.iesi.framework.execution.FrameworkExecution;

public class DataTableConfiguration {

    private DataTable dataTable;

    // Constructors
    public DataTableConfiguration(DataTable dataTable) {
        this.setDataTable(dataTable);
    }

    public DataTableConfiguration() {
    }

    // Create

    // Getters and Setters
    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }


}