package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.LedgerType;
import io.metadew.iesi.metadata.definition.LedgerTypeParameter;

public class LedgerTypeParameterConfiguration {

    private LedgerTypeParameter ledgerTypeParameter;

    // Constructors
    public LedgerTypeParameterConfiguration(LedgerTypeParameter ledgerTypeParameter) {
        this.setLedgerTypeParameter(ledgerTypeParameter);
    }

    public LedgerTypeParameterConfiguration() {
    }

    // Get Ledger Type Parameter
    public LedgerTypeParameter getLedgerTypeParameter(String ledgerTypeName, String ledgerTypeParameterName) {
        LedgerTypeParameter ledgerTypeParameterResult = null;
        LedgerTypeConfiguration ledgerTypeConfiguration = new LedgerTypeConfiguration();
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

}