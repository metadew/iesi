package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.DataframeType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class DataframeTypeConfiguration {

    private DataframeType dataframeType;
    private String dataObjectType = "DataframeType";

    // Constructors
    public DataframeTypeConfiguration(DataframeType dataframeType) {
        this.setDataframeType(dataframeType);
    }

    public DataframeTypeConfiguration() {
    }

    public DataframeType getDataframeType(String dataframeTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), dataframeTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
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

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}