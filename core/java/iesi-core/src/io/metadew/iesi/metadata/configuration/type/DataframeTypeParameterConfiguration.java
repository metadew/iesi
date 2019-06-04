package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.DataframeType;
import io.metadew.iesi.metadata.definition.DataframeTypeParameter;

public class DataframeTypeParameterConfiguration {

    private DataframeTypeParameter dataframeTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public DataframeTypeParameterConfiguration(DataframeTypeParameter dataframeTypeParameter, FrameworkInstance frameworkInstance) {
        this.setDataframeTypeParameter(dataframeTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public DataframeTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get Dataframe Type Parameter
    public DataframeTypeParameter getDataframeTypeParameter(String dataframeTypeName, String dataframeTypeParameterName) {
        DataframeTypeParameter dataframeTypeParameterResult = null;
        DataframeTypeConfiguration dataframeTypeConfiguration = new DataframeTypeConfiguration(this.getFrameworkInstance());
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}