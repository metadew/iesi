package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.DataframeItemType;
import io.metadew.iesi.metadata.definition.DataframeItemTypeParameter;

public class DataframeItemTypeParameterConfiguration {

    private DataframeItemTypeParameter dataframeItemTypeParameter;

    // Constructors
    public DataframeItemTypeParameterConfiguration(DataframeItemTypeParameter dataframeItemTypeParameter) {
        this.setDataframeItemTypeParameter(dataframeItemTypeParameter);
    }

    public DataframeItemTypeParameterConfiguration() {
    }

    // Get DataframeItem Type Parameter
    public DataframeItemTypeParameter getDataframeItemTypeParameter(String dataframeItemTypeName, String dataframeItemTypeParameterName) {
        DataframeItemTypeParameter dataframeItemTypeParameterResult = null;
        DataframeItemTypeConfiguration dataframeItemTypeConfiguration = new DataframeItemTypeConfiguration();
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

}