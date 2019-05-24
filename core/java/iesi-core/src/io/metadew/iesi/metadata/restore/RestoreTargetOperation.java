package io.metadew.iesi.metadata.restore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.data.definition.DataTable;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import java.io.File;

public class RestoreTargetOperation {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private Long processId;
    private String dataFileLocation;

    // Constructors
    public RestoreTargetOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
    }

    // Methods
    public void execute(String dataFile) {
        this.getFrameworkExecution().getFrameworkLog()
                .log("restore.file=" + dataFile, Level.INFO);

        try {
            // Parse input file
            File file = new File(dataFile);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                DataObject dataObject = objectMapper.readValue(file, new TypeReference<DataObject>() {
                });
                if (dataObject.getType().equalsIgnoreCase("datatable")) {
                    DataTable dataTable = objectMapper.convertValue(dataObject.getData(), DataTable.class);
                    RestoreTableOperation restoreTableOperation = new RestoreTableOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                            dataTable);
                    restoreTableOperation.execute();

                } else {
                    this.getFrameworkExecution().getFrameworkLog().log("restore.error.object.type.invalid" + dataFile, Level.ERROR);
                }

            } catch (Exception e) {
                this.getFrameworkExecution().getFrameworkLog().log("restore.error.file.read" + dataFile, Level.ERROR);
            }
        } catch (Exception e) {
            this.getFrameworkExecution().getFrameworkLog().log("restore.error.file.parse" + dataFile, Level.ERROR);
        } finally {
            // Log End
            // this.getEoControl().logEnd(this);
        }

    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

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
}