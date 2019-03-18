package io.metadew.iesi.metadata.configuration;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.LedgerType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

public class LedgerTypeConfiguration {

	private LedgerType ledgerType;
	private FrameworkExecution frameworkExecution;
	private String dataObjectType = "LedgerType";

	// Constructors
	public LedgerTypeConfiguration(LedgerType ledgerType, FrameworkExecution frameworkExecution) {
		this.setLedgerType(ledgerType);
		this.setFrameworkExecution(frameworkExecution);
	}

	public LedgerTypeConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	public LedgerType getLedgerType(String ledgerTypeName) {
		String conf = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("metadata.conf") + File.separator
				+ this.getDataObjectType() + File.separator + ledgerTypeName + ".json";
		DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
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
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public String getDataObjectType() {
		return dataObjectType;
	}

	public void setDataObjectType(String dataObjectType) {
		this.dataObjectType = dataObjectType;
	}
	
}