package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.LedgerType;
import io.metadew.iesi.metadata.definition.LedgerTypeParameter;

public class LedgerTypeParameterConfiguration {

    private LedgerTypeParameter ledgerTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public LedgerTypeParameterConfiguration(LedgerTypeParameter ledgerTypeParameter, FrameworkInstance frameworkInstance) {
        this.setLedgerTypeParameter(ledgerTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public LedgerTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get Ledger Type Parameter
    public LedgerTypeParameter getLedgerTypeParameter(String ledgerTypeName, String ledgerTypeParameterName) {
        LedgerTypeParameter ledgerTypeParameterResult = null;
        LedgerTypeConfiguration ledgerTypeConfiguration = new LedgerTypeConfiguration(this.getFrameworkInstance());
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}