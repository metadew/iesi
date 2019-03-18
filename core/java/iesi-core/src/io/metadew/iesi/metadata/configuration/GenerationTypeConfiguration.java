package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationTypeConfiguration {

	private GenerationType generationType;
	private FrameworkExecution frameworkExecution;
	private String dataObjectType = "GenerationType";

	// Constructors
	public GenerationTypeConfiguration(GenerationType generationType, FrameworkExecution frameworkExecution) {
		this.setgenerationType(generationType);
		this.setFrameworkExecution(frameworkExecution);
	}

	public GenerationTypeConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	public GenerationType getGenerationType(String GenerationTypeName) {
		String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
				this.getDataObjectType(), GenerationTypeName);
		DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
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