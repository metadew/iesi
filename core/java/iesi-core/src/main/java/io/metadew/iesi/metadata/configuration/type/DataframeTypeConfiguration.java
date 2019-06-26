package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.DataframeType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class DataframeTypeConfiguration {

    private DataframeType dataframeType;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "DataframeType";

    // Constructors
    public DataframeTypeConfiguration(DataframeType dataframeType, FrameworkInstance frameworkInstance) {
        this.setDataframeType(dataframeType);
        this.setFrameworkInstance(frameworkInstance);
    }

    public DataframeTypeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public DataframeType getDataframeType(String dataframeTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkInstance(),
                this.getDataObjectType(), dataframeTypeName);
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}