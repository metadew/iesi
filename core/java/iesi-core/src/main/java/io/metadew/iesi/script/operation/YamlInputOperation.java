package io.metadew.iesi.script.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

import java.util.Optional;

/**
 * Operation to manage script execution when a Yaml file has been provided as input.
 *
 * @author peter.billen
 */
public class YamlInputOperation {

    private String fileName;
    private DataObjectOperation dataObjectOperation;


    public YamlInputOperation(String fileName) {
        this.setFileName(fileName);
        this.setDataObjectOperation(new DataObjectOperation(this.getFileName()));
    }

    public Optional<Script> getScript() {
        ObjectMapper objectMapper = new ObjectMapper();
        for (DataObject dataObject : this.getDataObjectOperation().getDataObjectConfiguration().getDataObjects()) {
            // Scripts
            if (dataObject.getType().equalsIgnoreCase("script")) {
                return Optional.of(objectMapper.convertValue(dataObject.getData(), Script.class));
            }
        }
        return Optional.empty();
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DataObjectOperation getDataObjectOperation() {
        return dataObjectOperation;
    }

    public void setDataObjectOperation(DataObjectOperation dataObjectOperation) {
        this.dataObjectOperation = dataObjectOperation;
    }

}