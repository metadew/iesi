package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.LedgerType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

import java.io.File;

public class LedgerTypeConfiguration {

    private LedgerType ledgerType;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "LedgerType";

    // Constructors
    public LedgerTypeConfiguration(LedgerType ledgerType, FrameworkInstance frameworkInstance) {
        this.setLedgerType(ledgerType);
        this.setFrameworkInstance(frameworkInstance);
    }

    public LedgerTypeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public LedgerType getLedgerType(String ledgerTypeName) {
        String conf = this.getFrameworkInstance().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("metadata.conf") + File.separator
                + this.getDataObjectType() + File.separator + ledgerTypeName + ".json";
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        LedgerType ledgerType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                LedgerType.class);
        return ledgerType;
    }

    // Getters and Setters
    public LedgerType getLedgerType() {
        return ledgerType;
    }

    public void setLedgerType(LedgerType ledgerType) {
        this.ledgerType = ledgerType;
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