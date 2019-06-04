package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationTypeConfiguration {

    private GenerationType generationType;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "GenerationType";

    // Constructors
    public GenerationTypeConfiguration(GenerationType generationType, FrameworkInstance frameworkInstance) {
        this.setgenerationType(generationType);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationTypeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationType getGenerationType(String GenerationTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkInstance(),
                this.getDataObjectType(), GenerationTypeName);
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}