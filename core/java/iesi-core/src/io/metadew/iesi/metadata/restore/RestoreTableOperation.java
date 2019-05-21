package io.metadew.iesi.metadata.restore;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.data.definition.DataField;
import io.metadew.iesi.data.definition.DataRow;
import io.metadew.iesi.data.definition.DataTable;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

public class RestoreTableOperation {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private DataTable dataTable;

    // Constructors
    public RestoreTableOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl, DataTable dataTable) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setDataTable(dataTable);
    }

    // Methods
    public void execute() {
        for (DataRow dataRow : this.getDataTable().getRows()) {
            // Skip CFG_MTD tables
            if (this.getDataTable().getName().contains("CFG_MTD")) {
                this.getFrameworkExecution().getFrameworkLog()
                        .log("restore.table.skip=" + this.getDataTable().getName(), Level.DEBUG);
                return;
            }

            String sql = "";
            String sqlFields = "";
            String sqlValues = "";

            for (DataField dataField : dataRow.getFields()) {
                // Skips
                if (dataField.getName().trim().equalsIgnoreCase("load_tms"))
                    continue;

                // Fields
                if (!sqlFields.equalsIgnoreCase(""))
                    sqlFields += ",";
                sqlFields += dataField.getName();

                // Values
                String value = "";
                if (!sqlValues.equalsIgnoreCase(""))
                    sqlValues += ",";

                if (dataField.getValue().trim().equalsIgnoreCase("null")) {
                    value = "null";
                } else {
                    value = SQLTools.GetStringForSQL(dataField.getValue());
                }

                sqlValues += value;

            }

            // Get table name
            // lookup table name based on key
            // DtNow only to the same schema is possible
            // TODO
            sql = "";
            sql += "INSERT INTO "
                    // + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getMetadataTableConfiguration().getSchemaPrefix()
                    + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNamePrefix()
                    + this.getDataTable().getName();
            sql += " (";
            sql += sqlFields;
            sql += ") VALUES (";
            sql += sqlValues;
            sql += ");";

            // TODO repo redesign
            this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeUpdate(sql);
        }

    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }
}