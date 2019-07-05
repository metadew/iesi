package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.LedgerItem;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LedgerItemConfiguration {

    private LedgerItem ledgerItem;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public LedgerItemConfiguration(LedgerItem ledgerItem, FrameworkInstance frameworkInstance) {
        this.setLedgerItem(ledgerItem);
        this.setFrameworkInstance(frameworkInstance);
    }

    public LedgerItemConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String ledgerName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository()
                .getTableNameByLabel("LedgerItems");
        sql += " (LEDGER_ID, ITEM_NM, ITEM_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "("
                + SQLTools.GetLookupIdStatement(
                this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository()
                        .getTableNameByLabel("Ledgers"),
                "LEDGER_ID", "where LEDGER_NM = '" + ledgerName)
                + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getLedgerItem().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getLedgerItem().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public LedgerItem getLedgerItem(long ledgerId, String ledgerItemName) {
        LedgerItem ledgerItem = new LedgerItem();
        CachedRowSet crsLedgerItem = null;
        String queryLedgerItem = "select LEDGER_ID, ITEM_NM, ITEM_VAL from "
                + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository()
                .getTableNameByLabel("LedgerItems")
                + " where LEDGER_ID = " + ledgerId + " and ITEM_NM = '" + ledgerItemName + "'";
        crsLedgerItem = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .executeQuery(queryLedgerItem, "reader");
        try {
            while (crsLedgerItem.next()) {
                ledgerItem.setName(ledgerItemName);
                ledgerItem.setValue(crsLedgerItem.getString("LEDGER_ITEM_VAL"));
            }
            crsLedgerItem.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return ledgerItem;
    }

    // Getters and Setters
    public LedgerItem getLedgerItem() {
        return ledgerItem;
    }

    public void setLedgerItem(LedgerItem ledgerItem) {
        this.ledgerItem = ledgerItem;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}