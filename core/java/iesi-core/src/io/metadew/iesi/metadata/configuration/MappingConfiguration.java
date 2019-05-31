package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Mapping;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class MappingConfiguration {

    private Mapping mapping;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "Mapping";

    // Constructors
    public MappingConfiguration(Mapping mapping, FrameworkInstance frameworkInstance) {
        this.setMapping(mapping);
        this.setFrameworkInstance(frameworkInstance);
    }

    public MappingConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public Mapping getMapping(String mappingName) {
        String conf = TypeConfigurationOperation.getMappingConfigurationFile(this.getFrameworkInstance(),
                this.getDataObjectType(), mappingName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                Mapping.class);
    }

    // Getters and Setters
    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}