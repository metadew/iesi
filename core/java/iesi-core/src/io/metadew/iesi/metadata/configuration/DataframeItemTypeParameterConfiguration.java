package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataframeItemType;
import io.metadew.iesi.metadata.definition.DataframeItemTypeParameter;

public class DataframeItemTypeParameterConfiguration {

	private DataframeItemTypeParameter dataframeItemTypeParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public DataframeItemTypeParameterConfiguration(DataframeItemTypeParameter dataframeItemTypeParameter, FrameworkExecution processiongTools) {
		this.setDataframeItemTypeParameter(dataframeItemTypeParameter);
		this.setFrameworkExecution(processiongTools);
	}

	public DataframeItemTypeParameterConfiguration(FrameworkExecution processiongTools) {
		this.setFrameworkExecution(processiongTools);
	}

	// Get DataframeItem Type Parameter
	public DataframeItemTypeParameter getDataframeItemTypeParameter(String dataframeItemTypeName, String dataframeItemTypeParameterName) {
		DataframeItemTypeParameter dataframeItemTypeParameterResult = null;
		DataframeItemTypeConfiguration dataframeItemTypeConfiguration = new DataframeItemTypeConfiguration(this.getFrameworkExecution());
		DataframeItemType dataframeItemType = dataframeItemTypeConfiguration.getDataframeItemType(dataframeItemTypeName);
		for (DataframeItemTypeParameter dataframeItemTypeParameter : dataframeItemType.getParameters()) {
			if (dataframeItemTypeParameter.getName().equalsIgnoreCase(dataframeItemTypeParameterName)) {
				dataframeItemTypeParameterResult = dataframeItemTypeParameter;
				break;
			}
		}
		return dataframeItemTypeParameterResult;
	}
	
	// Getters and Setters
	public DataframeItemTypeParameter getDataframeItemTypeParameter() {
		return dataframeItemTypeParameter;
	}

	public void setDataframeItemTypeParameter(DataframeItemTypeParameter dataframeItemTypeParameter) {
		this.dataframeItemTypeParameter = dataframeItemTypeParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}