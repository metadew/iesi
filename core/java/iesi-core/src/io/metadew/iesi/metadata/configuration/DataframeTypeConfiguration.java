package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataframeType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class DataframeTypeConfiguration {

	private DataframeType dataframeType;
	private FrameworkExecution frameworkExecution;
	private String dataObjectType = "DataframeType";

	// Constructors
	public DataframeTypeConfiguration(DataframeType dataframeType, FrameworkExecution frameworkExecution) {
		this.setDataframeType(dataframeType);
		this.setFrameworkExecution(frameworkExecution);
	}

	public DataframeTypeConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	public DataframeType getDataframeType(String dataframeTypeName) {
		String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
				this.getDataObjectType(), dataframeTypeName);
		DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
		ObjectMapper objectMapper = new ObjectMapper();
		DataframeType dataframeType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
				DataframeType.class);
		return dataframeType;
	}
	
	// Getters and Setters
	public DataframeType getDataframeType() {
		return dataframeType;
	}

	public void setDataframeType(DataframeType dataframeType) {
		this.dataframeType = dataframeType;
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