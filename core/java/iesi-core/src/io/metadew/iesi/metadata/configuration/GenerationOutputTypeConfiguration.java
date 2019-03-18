package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationOutputType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationOutputTypeConfiguration {

	private GenerationOutputType generationOutputType;
	private FrameworkExecution frameworkExecution;
	private String dataObjectType = "GenerationOutputType";

	// Constructors
	public GenerationOutputTypeConfiguration(GenerationOutputType generationOutputType,
			FrameworkExecution frameworkExecution) {
		this.setgenerationOutputType(generationOutputType);
		this.setFrameworkExecution(frameworkExecution);
	}

	public GenerationOutputTypeConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Methods
	public GenerationOutputType getGenerationOutputType(String GenerationOutputTypeName) {
		String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
				this.getDataObjectType(), GenerationOutputTypeName);
		DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
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