package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.SubroutineType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class SubroutineTypeConfiguration {

    private SubroutineType subroutineType;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "SubroutineType";

    // Constructors
    public SubroutineTypeConfiguration(SubroutineType subroutineType, FrameworkInstance frameworkInstance) {
        this.setSubroutineType(subroutineType);
        this.setFrameworkInstance(frameworkInstance);
    }

    public SubroutineTypeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public SubroutineType getSubroutineType(String subroutineTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkInstance(),
                this.getDataObjectType(), subroutineTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        SubroutineType subroutineType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                SubroutineType.class);
        return subroutineType;
    }

    // Getters and Setters
    public SubroutineType getSubroutineType() {
        return subroutineType;
    }

    public void setSubroutineType(SubroutineType subroutineType) {
        this.subroutineType = subroutineType;
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