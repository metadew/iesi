package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.generation.GenerationType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationTypeConfiguration {

    private GenerationType generationType;
    private String dataObjectType = "GenerationType";

    // Constructors
    public GenerationTypeConfiguration(GenerationType generationType) {
        this.setgenerationType(generationType);
    }

    public GenerationTypeConfiguration() {
    }

    public GenerationType getGenerationType(String GenerationTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), GenerationTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        GenerationType GenerationType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                GenerationType.class);
        return GenerationType;
    }


    // Getters and Setters
    public GenerationType getgenerationType() {
        return generationType;
    }

    public void setgenerationType(GenerationType generationType) {
        this.generationType = generationType;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}