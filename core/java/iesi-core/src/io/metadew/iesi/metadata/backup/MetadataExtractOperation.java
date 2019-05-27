package io.metadew.iesi.metadata.backup;

import io.metadew.iesi.connection.tools.OutputTools;
import io.metadew.iesi.data.definition.DataField;
import io.metadew.iesi.data.definition.DataRow;
import io.metadew.iesi.data.definition.DataTable;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.DataObjectConfiguration;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class MetadataExtractOperation {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private Long processId;
    private String dataFileLocation;

    // Constructors
    public MetadataExtractOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
    }

    // Methods
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execute(MetadataTable metadataTable, String outputFilePath) {
        this.getFrameworkExecution().getFrameworkLog().log(
                "metadata.extract.table=" + metadataTable.getName(), Level.INFO);

        // Log Start
        // this.getEoControl().logStart(this);
        // this.setProcessId(this.getEoControl().getProcessId());
        // TODO
        String schemaName = "";
        // this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getRepository().getDatabases().get("reader");
        //		.getMetadataTableConfiguration().getSchemaPrefix();
        String tableNamePrefix = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNamePrefix();
        String tableName = schemaName + tableNamePrefix + metadataTable.getName();
        DataTable dataTable = new DataTable();
        // Setting the table name without instance
        dataTable.setName(metadataTable.getName());

        try {
            String query = "";
            query = "select * from " + tableName;

            CachedRowSet crs = null;
            // TODO repo redesign
            crs = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(query, "reader");
            ResultSetMetaData rsmd = crs.getMetaData();
            int cols = rsmd.getColumnCount();
            int rows = 1;

            List<DataRow> dataRowList = new ArrayList();
            while (crs.next()) {
                DataRow dataRow = new DataRow();
                dataRow.setId(rows);

                List<DataField> dataFieldList = new ArrayList();
                for (int i = 1; i < cols + 1; i++) {
                    DataField dataField = new DataField();
                    dataField.setName(rsmd.getColumnName(i));
                    dataField.setValue(String.valueOf(crs.getObject(i)));
                    dataFieldList.add(dataField);
                }
                dataRow.setFields(dataFieldList);
                dataRowList.add(dataRow);
                rows++;
            }
            crs.close();
            dataTable.setRows(dataRowList);

            DataObjectConfiguration dataObjectConfiguration = new DataObjectConfiguration(this.getFrameworkExecution());

            String fileName = metadataTable.getName() + ".json";

            OutputTools.createOutputFile(fileName,
                    outputFilePath, "",
                    dataObjectConfiguration.getPrettyDataObjectJSON(dataTable), true);
            this.setDataFileLocation(outputFilePath + File.separator + fileName);
            // System.out.println(dataObjectConfiguration.getPrettyDataObjectJSON(dataTable));

        } catch (Exception e) {
            this.getFrameworkExecution().getFrameworkLog().log(
                    "metadata.extract.error", Level.INFO);
        } finally {
            // Log End
            // this.getEoControl().logEnd(this);
        }

    }

    // Getters and Setters
    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getDataFileLocation() {
        return dataFileLocation;
    }

    public void setDataFileLocation(String dataFileLocation) {
        this.dataFileLocation = dataFileLocation;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }
}