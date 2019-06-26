package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.DataframeItemType;
import io.metadew.iesi.metadata.definition.DataframeItemTypeParameter;

public class DataframeItemTypeParameterConfiguration {

    private DataframeItemTypeParameter dataframeItemTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public DataframeItemTypeParameterConfiguration(DataframeItemTypeParameter dataframeItemTypeParameter, FrameworkInstance frameworkInstance) {
        this.setDataframeItemTypeParameter(dataframeItemTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public DataframeItemTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get DataframeItem Type Parameter
    public DataframeItemTypeParameter getDataframeItemTypeParameter(String dataframeItemTypeName, String dataframeItemTypeParameterName) {
        DataframeItemTypeParameter dataframeItemTypeParameterResult = null;
        DataframeItemTypeConfiguration dataframeItemTypeConfiguration = new DataframeItemTypeConfiguration(this.getFrameworkInstance());
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}