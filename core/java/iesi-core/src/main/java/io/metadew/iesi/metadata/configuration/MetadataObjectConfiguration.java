package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataObject;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

import java.util.HashMap;

public class MetadataObjectConfiguration {
    private HashMap<String, String> objectMap;

    public MetadataObjectConfiguration(MetadataRepositoryCategoryConfiguration metadataRepositoryCategoryConfiguration) {
        this.getObjectConfig(metadataRepositoryCategoryConfiguration);
    }

    private void getObjectConfig(MetadataRepositoryCategoryConfiguration metadataRepositoryCategoryConfiguration) {
        this.setObjectMap(new HashMap<String, String>());

        DataObjectOperation dataObjectOperation = new DataObjectOperation();
        dataObjectOperation.setInputFile(metadataRepositoryCategoryConfiguration.getObjectDefinitionFilePath());
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();
        //
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadataobject")) {
                MetadataObject metadataObject = objectMapper.convertValue(dataObject.getData(), MetadataObject.class);
                this.getObjectMap().put(metadataObject.getLabel(), metadataObject.getName());
            }
        }

    }

    public boolean exists(String objectName) {
        return this.getObjectMap().containsKey(objectName);
    }

    // Getters and setters
    public HashMap<String, String> getObjectMap() {
        return objectMap;
    }

    public void setObjectMap(HashMap<String, String> objectMap) {
        this.objectMap = objectMap;
    }

}