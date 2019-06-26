package io.metadew.iesi.data.configuration;

import io.metadew.iesi.data.definition.DataTable;
import io.metadew.iesi.framework.execution.FrameworkExecution;

public class DataTableConfiguration {

    private DataTable dataTable;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public DataTableConfiguration(DataTable dataTable, FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setDataTable(dataTable);
    }

    public DataTableConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Create

    // Getters and Setters
    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}