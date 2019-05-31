package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.LedgerParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LedgerParameterConfiguration {

    private LedgerParameter ledgerParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public LedgerParameterConfiguration(LedgerParameter ledgerParameter, FrameworkInstance frameworkInstance) {
        this.setLedgerParameter(ledgerParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public LedgerParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String ledgerName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository()
                .getTableNameByLabel("LedgerParameters");
        sql += " (LEDGER_ID, LEDGER_PAR_NM, LEDGER_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "("
                + SQLTools.GetLookupIdStatement(
                this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository()
                        .getTableNameByLabel("Ledgers"),
                "LEDGER_ID", "where LEDGER_NM = '" + ledgerName)
                + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getLedgerParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getLedgerParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public LedgerParameter getLedgerParameter(long ledgerId, String ledgerParameterName) {
        LedgerParameter ledgerParameter = new LedgerParameter();
        CachedRowSet crsLedgerParameter = null;
        String queryLedgerParameter = "select LEDGER_ID, LEDGER_PAR_NM, LEDGER_PAR_VAL from "
                + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository()
                .getTableNameByLabel("LedgerParameters")
                + " where LEDGER_ID = " + ledgerId + " and LEDGER_PAR_NM = '" + ledgerParameterName + "'";
        crsLedgerParameter = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .executeQuery(queryLedgerParameter, "reader");
        try {
            while (crsLedgerParameter.next()) {
                ledgerParameter.setName(ledgerParameterName);
                ledgerParameter.setValue(crsLedgerParameter.getString("LEDGER_PAR_VAL"));
            }
            crsLedgerParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return ledgerParameter;
    }

    // Getters and Setters
    public LedgerParameter getLedgerParameter() {
        return ledgerParameter;
    }

    public void setLedgerParameter(LedgerParameter ledgerParameter) {
        this.ledgerParameter = ledgerParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}