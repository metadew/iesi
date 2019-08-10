package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.GenerationControlType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationControlTypeConfiguration {

    private GenerationControlType generationControlType;
    private String dataObjectType = "GenerationControlType";

    // Constructors
    public GenerationControlTypeConfiguration(GenerationControlType generationControlType) {
        this.setgenerationControlType(generationControlType);
    }

    public GenerationControlTypeConfiguration() {
    }

    // Methods
    public GenerationControlType getGenerationControlType(String GenerationControlTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), GenerationControlTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        GenerationControlType GenerationControlType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                GenerationControlType.class);
        return GenerationControlType;
    }

    // Getters and Setters
    public GenerationControlType getgenerationControlType() {
        return generationControlType;
    }

    public void setgenerationControlType(GenerationControlType generationControlType) {
        this.generationControlType = generationControlType;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}