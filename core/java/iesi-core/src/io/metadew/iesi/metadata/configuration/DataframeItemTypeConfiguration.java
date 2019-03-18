package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataframeItemType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class DataframeItemTypeConfiguration {

	private DataframeItemType dataframeItemType;
	private FrameworkExecution frameworkExecution;
	private String dataObjectType = "DataframeItemType";

	// Constructors
	public DataframeItemTypeConfiguration(DataframeItemType dataframeItemType, FrameworkExecution frameworkExecution) {
		this.setDataframeItemType(dataframeItemType);
		this.setFrameworkExecution(frameworkExecution);
	}

	public DataframeItemTypeConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	public DataframeItemType getDataframeItemType(String dataframeItemTypeName) {
		String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
				this.getDataObjectType(), dataframeItemTypeName);
		DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
		ObjectMapper objectMapper = new ObjectMapper();
		DataframeItemType dataframeItemType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
				DataframeItemType.class);
		return dataframeItemType;
	}

	// Getters and Setters
	public DataframeItemType getDataframeItemType() {
		return dataframeItemType;
	}

	public void setDataframeItemType(DataframeItemType dataframeItemType) {
		this.dataframeItemType = dataframeItemType;
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