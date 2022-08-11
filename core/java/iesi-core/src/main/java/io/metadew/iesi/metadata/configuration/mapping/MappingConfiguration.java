package io.metadew.iesi.metadata.configuration.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.mapping.Mapping;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

import java.nio.file.Paths;

public class MappingConfiguration {

    private String dataObjectType = "Mapping";

    public Mapping getMapping(String mappingName) {
        String conf = TypeConfigurationOperation.getInstance()
                .getMappingConfigurationFile(this.getDataObjectType(), mappingName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(Paths.get(conf));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                Mapping.class);
    }

    // Getters and Setters
    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}