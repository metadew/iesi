package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.DataframeItemType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class DataframeItemTypeConfiguration {

    private DataframeItemType dataframeItemType;
    private String dataObjectType = "DataframeItemType";

    // Constructors
    public DataframeItemTypeConfiguration(DataframeItemType dataframeItemType) {
        this.setDataframeItemType(dataframeItemType);
    }

    public DataframeItemTypeConfiguration() {
    }

    public DataframeItemType getDataframeItemType(String dataframeItemTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), dataframeItemTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
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

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}