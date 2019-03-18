package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataframeType;
import io.metadew.iesi.metadata.definition.DataframeTypeParameter;

public class DataframeTypeParameterConfiguration {

	private DataframeTypeParameter dataframeTypeParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public DataframeTypeParameterConfiguration(DataframeTypeParameter dataframeTypeParameter, FrameworkExecution processiongTools) {
		this.setDataframeTypeParameter(dataframeTypeParameter);
		this.setFrameworkExecution(processiongTools);
	}

	public DataframeTypeParameterConfiguration(FrameworkExecution processiongTools) {
		this.setFrameworkExecution(processiongTools);
	}

	// Get Dataframe Type Parameter
	public DataframeTypeParameter getDataframeTypeParameter(String dataframeTypeName, String dataframeTypeParameterName) {
		DataframeTypeParameter dataframeTypeParameterResult = null;
		DataframeTypeConfiguration dataframeTypeConfiguration = new DataframeTypeConfiguration(this.getFrameworkExecution());
		DataframeType dataframeType = dataframeTypeConfiguration.getDataframeType(dataframeTypeName);
		for (DataframeTypeParameter dataframeTypeParameter : dataframeType.getParameters()) {
			if (dataframeTypeParameter.getName().equalsIgnoreCase(dataframeTypeParameterName)) {
				dataframeTypeParameterResult = dataframeTypeParameter;
				break;
			}
		}
		return dataframeTypeParameterResult;
	}
	
	// Getters and Setters
	public DataframeTypeParameter getDataframeTypeParameter() {
		return dataframeTypeParameter;
	}

	public void setDataframeTypeParameter(DataframeTypeParameter dataframeTypeParameter) {
		this.dataframeTypeParameter = dataframeTypeParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}