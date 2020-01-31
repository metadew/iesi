package io.metadew.iesi.metadata.restore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.data.definition.DataTable;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class RestoreTargetOperation {

    private ExecutionControl executionControl;
    private Long processId;
    private String dataFileLocation;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public RestoreTargetOperation(ExecutionControl executionControl) {
        this.setExecutionControl(executionControl);
    }

    // Methods
    public void execute(String dataFile) {
        LOGGER.info("restore.file=" + dataFile);
        try {
            // Parse input file
            File file = new File(dataFile);
            ObjectMapper objectMapper = new ObjectMapper();
                DataObject dataObject = objectMapper.readValue(file, new TypeReference<DataObject>() {
                });
                if (dataObject.getType().equalsIgnoreCase("datatable")) {
                    DataTable dataTable = objectMapper.convertValue(dataObject.getData(), DataTable.class);
                    RestoreTableOperation restoreTableOperation = new RestoreTableOperation(this.getExecutionControl(),
                            dataTable);
                    restoreTableOperation.execute();

                } else {
                    LOGGER.error("restore.error.object.type.invalid" + dataFile);
                }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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