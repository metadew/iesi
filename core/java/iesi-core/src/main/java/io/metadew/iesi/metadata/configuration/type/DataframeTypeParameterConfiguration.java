package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.DataframeType;
import io.metadew.iesi.metadata.definition.DataframeTypeParameter;

public class DataframeTypeParameterConfiguration {

    private DataframeTypeParameter dataframeTypeParameter;

    // Constructors
    public DataframeTypeParameterConfiguration(DataframeTypeParameter dataframeTypeParameter) {
        this.setDataframeTypeParameter(dataframeTypeParameter);
    }

    public DataframeTypeParameterConfiguration() {
    }

    // Get Dataframe Type Parameter
    public DataframeTypeParameter getDataframeTypeParameter(String dataframeTypeName, String dataframeTypeParameterName) {
        DataframeTypeParameter dataframeTypeParameterResult = null;
        DataframeTypeConfiguration dataframeTypeConfiguration = new DataframeTypeConfiguration();
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

}