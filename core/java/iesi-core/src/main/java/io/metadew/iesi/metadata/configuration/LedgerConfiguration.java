package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Ledger;
import io.metadew.iesi.metadata.definition.LedgerItem;
import io.metadew.iesi.metadata.definition.LedgerParameter;
import io.metadew.iesi.metadata.definition.ListObject;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class LedgerConfiguration {

    private Ledger ledger;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public LedgerConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public LedgerConfiguration(Ledger ledger, FrameworkInstance frameworkInstance) {
        this.setLedger(ledger);
        this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("LedgerItems");
            sql += " WHERE LEDGER_ID = (";
            sql += "select LEDGER_ID FROM " + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("Ledgers");
            sql += " WHERE LEDGER_NM = "
                    + SQLTools.GetStringForSQL(this.getLedger().getName());
            sql += ")";
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("LedgerParameters");
            sql += " WHERE LEDGER_ID = (";
            sql += "select LEDGER_ID FROM " + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("Ledgers");
            sql += " WHERE LEDGER_NM = "
                    + SQLTools.GetStringForSQL(this.getLedger().getName());
            sql += ")";
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("Ledgers");
            sql += " WHERE LEDGER_NM = "
                    + SQLTools.GetStringForSQL(this.getLedger().getName());
            sql += ";";
            sql += "\n";
        }

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("Ledgers");
        sql += " (LEDGER_ID, LEDGER_TYP_NM, LEDGER_NM, LEDGER_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetNextIdStatement(
                this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("Ledgers"), "LEDGER_ID") + ")";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getLedger().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getLedger().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getLedger().getDescription());
        sql += ")";
        sql += ";";

        // add Items
        String sqlItems = this.getItemInsertStatements();
        if (!sqlItems.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlItems;
        }

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements();
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getItemInsertStatements() {
        String result = "";

        if (this.getLedger().getItems() == null)
            return result;

        for (LedgerItem ledgerItem : this.getLedger().getItems()) {
            LedgerItemConfiguration ledgerItemConfiguration = new LedgerItemConfiguration(ledgerItem, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += ledgerItemConfiguration.getInsertStatement(this.getLedger().getName());
        }

        return result;
    }

    private String getParameterInsertStatements() {
        String result = "";

        if (this.getLedger().getParameters() == null)
            return result;

        for (LedgerParameter ledgerParameter : this.getLedger().getParameters()) {
            LedgerParameterConfiguration ledgerParameterConfiguration = new LedgerParameterConfiguration(
                    ledgerParameter, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += ledgerParameterConfiguration.getInsertStatement(this.getLedger().getName());
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Ledger getLedger(String ledgerName) {
        Ledger ledger = new Ledger();
        CachedRowSet crsLedger = null;
        String queryLedger = "select LEDGER_ID, LEDGER_TYP_NM, LEDGER_NM, LEDGER_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("Ledgers") + " where LEDGER_NM = '"
                + ledgerName + "'";
        crsLedger = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryLedger, "reader");
        LedgerItemConfiguration ledgerItemConfiguration = new LedgerItemConfiguration(this.getFrameworkInstance());
        LedgerParameterConfiguration ledgerParameterConfiguration = new LedgerParameterConfiguration(
                this.getFrameworkInstance());
        try {
            while (crsLedger.next()) {
                ledger.setId(crsLedger.getLong("LEDGER_ID"));
                ledger.setType(crsLedger.getString("LEDGER_TYP_NM"));
                ledger.setName(ledgerName);
                ledger.setDescription(crsLedger.getString("LEDGER_DSC"));

                // Get the actions
                List<LedgerItem> ledgerItemList = new ArrayList();
                String queryItems = "select LEDGER_ID, ITEM_NM from "
                        + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("LedgerItems")
                        + " where LEDGER_ID = " + ledger.getId()
                        + " order by ITEM_NM asc ";
                CachedRowSet crsItems = null;
                crsItems = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryItems, "reader");
                while (crsItems.next()) {
                    ledgerItemList.add(ledgerItemConfiguration.getLedgerItem(ledger.getId(), crsItems.getString("ITEM_NM")));
                }
                ledger.setItems(ledgerItemList);
                crsItems.close();

                // Get parameters
                CachedRowSet crsLedgerParameters = null;
                String queryLedgerParameters = "select LEDGER_ID, LEDGER_PAR_NM from "
                        + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("LedgerParameters")
                        + " where LEDGER_ID = " + ledger.getId();
                crsLedgerParameters = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                        .executeQuery(queryLedgerParameters, "reader");
                List<LedgerParameter> ledgerParameterList = new ArrayList();
                while (crsLedgerParameters.next()) {
                    ledgerParameterList.add(ledgerParameterConfiguration.getLedgerParameter(ledger.getId(),
                            crsLedgerParameters.getString("LEDGER_PAR_NM")));
                }
                ledger.setParameters(ledgerParameterList);
                crsLedgerParameters.close();

            }
            crsLedger.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (ledger.getName() == null || ledger.getName().equalsIgnoreCase("")) {
            throw new RuntimeException("Ledger (NAME) " + ledgerName + " does not exist");
        }

        return ledger;
    }

    // Get
    public ListObject getLedgers() {
        List<Ledger> ledgerList = new ArrayList<>();
        CachedRowSet crs = null;
        String query = "select LEDGER_NM, LEDGER_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getLedgerMetadataRepository().getTableNameByLabel("Ledgers") + " order by LEDGER_NM ASC";
        System.out.println(query);
        crs = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(query, "reader");
        LedgerConfiguration ledgerConfiguration = new LedgerConfiguration(this.getFrameworkInstance());
        try {
            String ledgerName = "";
            while (crs.next()) {
                ledgerName = crs.getString("LEDGER_NM");
                ledgerList.add(ledgerConfiguration.getLedger(ledgerName));
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return new ListObject(
                FrameworkObjectConfiguration.getFrameworkObjectType(new Ledger()),
                ledgerList);
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Ledger getLedger() {
        return ledger;
    }

    public void setLedger(Ledger ledger) {
        this.ledger = ledger;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}