package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationOutputType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationOutputTypeConfiguration {

    private GenerationOutputType generationOutputType;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "GenerationOutputType";

    // Constructors
    public GenerationOutputTypeConfiguration(GenerationOutputType generationOutputType,
    		FrameworkInstance frameworkInstance) {
        this.setgenerationOutputType(generationOutputType);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationOutputTypeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Methods
    public GenerationOutputType getGenerationOutputType(String GenerationOutputTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkInstance(),
                this.getDataObjectType(), GenerationOutputTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        GenerationOutputType GenerationOutputType = objectMapper
                .convertValue(dataObjectOperation.getDataObject().getData(), GenerationOutputType.class);
        return GenerationOutputType;
    }

    // Getters and Setters
    public GenerationOutputType getgenerationOutputType() {
        return generationOutputType;
    }

    public void setgenerationOutputType(GenerationOutputType generationOutputType) {
        this.generationOutputType = generationOutputType;
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