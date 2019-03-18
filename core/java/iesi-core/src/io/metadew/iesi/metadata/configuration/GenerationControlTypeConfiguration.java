package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationControlTypeConfiguration {

	private GenerationControlType generationControlType;
	private FrameworkExecution frameworkExecution;
	private String dataObjectType = "GenerationControlType";

	// Constructors
	public GenerationControlTypeConfiguration(GenerationControlType generationControlType, FrameworkExecution frameworkExecution) {
		this.setgenerationControlType(generationControlType);
		this.setFrameworkExecution(frameworkExecution);
	}

	public GenerationControlTypeConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	// Methods
	public GenerationControlType getGenerationControlType(String GenerationControlTypeName) {
		String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
				this.getDataObjectType(), GenerationControlTypeName);
		DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
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
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public String getDataObjectType() {
		return dataObjectType;
	}

	public void setDataObjectType(String dataObjectType) {
		this.dataObjectType = dataObjectType;
	}
	
}