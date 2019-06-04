package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationRuleType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationRuleTypeConfiguration {

    private GenerationRuleType generationRuleType;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "GenerationRuleType";


    // Constructors
    public GenerationRuleTypeConfiguration(GenerationRuleType generationRuleType, FrameworkInstance frameworkInstance) {
        this.setgenerationRuleType(generationRuleType);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationRuleTypeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Methods
    public GenerationRuleType getGenerationRuleType(String GenerationRuleTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkInstance(),
                this.getDataObjectType(), GenerationRuleTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        GenerationRuleType GenerationRuleType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                GenerationRuleType.class);
        return GenerationRuleType;
    }


    // Getters and Setters
    public GenerationRuleType getgenerationRuleType() {
        return generationRuleType;
    }

    public void setgenerationRuleType(GenerationRuleType generationRuleType) {
        this.generationRuleType = generationRuleType;
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