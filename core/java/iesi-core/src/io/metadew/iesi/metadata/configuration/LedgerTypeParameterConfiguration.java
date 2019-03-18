package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.LedgerType;
import io.metadew.iesi.metadata.definition.LedgerTypeParameter;

public class LedgerTypeParameterConfiguration {

	private LedgerTypeParameter ledgerTypeParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public LedgerTypeParameterConfiguration(LedgerTypeParameter ledgerTypeParameter, FrameworkExecution processiongTools) {
		this.setLedgerTypeParameter(ledgerTypeParameter);
		this.setFrameworkExecution(processiongTools);
	}

	public LedgerTypeParameterConfiguration(FrameworkExecution processiongTools) {
		this.setFrameworkExecution(processiongTools);
	}

	// Get Ledger Type Parameter
	public LedgerTypeParameter getLedgerTypeParameter(String ledgerTypeName, String ledgerTypeParameterName) {
		LedgerTypeParameter ledgerTypeParameterResult = null;
		LedgerTypeConfiguration ledgerTypeConfiguration = new LedgerTypeConfiguration(this.getFrameworkExecution());
		LedgerType ledgerType = ledgerTypeConfiguration.getLedgerType(ledgerTypeName);
		for (LedgerTypeParameter ledgerTypeParameter : ledgerType.getParameters()) {
			if (ledgerTypeParameter.getName().equalsIgnoreCase(ledgerTypeParameterName)) {
				ledgerTypeParameterResult = ledgerTypeParameter;
				break;
			}
		}
		return ledgerTypeParameterResult;
	}
	
	// Getters and Setters
	public LedgerTypeParameter getLedgerTypeParameter() {
		return ledgerTypeParameter;
	}

	public void setLedgerTypeParameter(LedgerTypeParameter ledgerTypeParameter) {
		this.ledgerTypeParameter = ledgerTypeParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}