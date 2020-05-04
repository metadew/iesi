package io.metadew.iesi.metadata.configuration.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.mapping.Mapping;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class MappingConfiguration {

    private String dataObjectType = "Mapping";

    // Constructors
    public MappingConfiguration() {
    }

    public Mapping getMapping(String mappingName) {
        String conf = TypeConfigurationOperation.getMappingConfigurationFile(this.getDataObjectType(), mappingName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
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