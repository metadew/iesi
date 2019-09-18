package io.metadew.iesi.metadata.restore;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.data.definition.DataField;
import io.metadew.iesi.data.definition.DataRow;
import io.metadew.iesi.data.definition.DataTable;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RestoreTableOperation {

    private ExecutionControl executionControl;
    private DataTable dataTable;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public RestoreTableOperation(ExecutionControl executionControl, DataTable dataTable) {
        this.setExecutionControl(executionControl);
        this.setDataTable(dataTable);
    }

    // Methods
    public void execute() {
        for (DataRow dataRow : this.getDataTable().getRows()) {
            // Skip CFG_MTD tables
            if (this.getDataTable().getName().contains("CFG_MTD")) {
                LOGGER.debug("restore.table.skip=" + this.getDataTable().getName());
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
                    // + MetadataControl.getInstance().getDesignMetadataRepository().getMetadataTableConfiguration().getSchemaPrefix()
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTablePrefix()
                    + this.getDataTable().getName();
            sql += " (";
            sql += sqlFields;
            sql += ") VALUES (";
            sql += sqlValues;
            sql += ");";

            // TODO repo redesign
            MetadataControl.getInstance().getDesignMetadataRepository().executeUpdate(sql);
        }

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