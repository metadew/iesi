package io.metadew.iesi.metadata.configuration.ledger;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.metadata.definition.ListObject;
import io.metadew.iesi.metadata.definition.ledger.Ledger;
import io.metadew.iesi.metadata.definition.ledger.LedgerItem;
import io.metadew.iesi.metadata.definition.ledger.LedgerParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class LedgerConfiguration {

    private Ledger ledger;

    // Constructors
    public LedgerConfiguration() {
    }

    public LedgerConfiguration(Ledger ledger) {
        this.setLedger(ledger);
    }

    // Insert
    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += "DELETE FROM " + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("LedgerItems");
            sql += " WHERE LEDGER_ID = (";
            sql += "select LEDGER_ID FROM " + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("Ledgers");
            sql += " WHERE LEDGER_NM = "
                    + SQLTools.GetStringForSQL(this.getLedger().getName());
            sql += ")";
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("LedgerParameters");
            sql += " WHERE LEDGER_ID = (";
            sql += "select LEDGER_ID FROM " + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("Ledgers");
            sql += " WHERE LEDGER_NM = "
                    + SQLTools.GetStringForSQL(this.getLedger().getName());
            sql += ")";
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("Ledgers");
            sql += " WHERE LEDGER_NM = "
                    + SQLTools.GetStringForSQL(this.getLedger().getName());
            sql += ";";
            sql += "\n";
        }

        sql += "INSERT INTO " + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("Ledgers");
        sql += " (LEDGER_ID, LEDGER_TYP_NM, LEDGER_NM, LEDGER_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetNextIdStatement(
                MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("Ledgers"), "LEDGER_ID") + ")";
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
            LedgerItemConfiguration ledgerItemConfiguration = new LedgerItemConfiguration(ledgerItem);
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
                    ledgerParameter);
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
                + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("Ledgers") + " where LEDGER_NM = '"
                + ledgerName + "'";
        crsLedger = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryLedger, "reader");
        LedgerItemConfiguration ledgerItemConfiguration = new LedgerItemConfiguration();
        LedgerParameterConfiguration ledgerParameterConfiguration = new LedgerParameterConfiguration();
        try {
            while (crsLedger.next()) {
                ledger.setId(crsLedger.getLong("LEDGER_ID"));
                ledger.setType(crsLedger.getString("LEDGER_TYP_NM"));
                ledger.setName(ledgerName);
                ledger.setDescription(crsLedger.getString("LEDGER_DSC"));

                // Get the actions
                List<LedgerItem> ledgerItemList = new ArrayList();
                String queryItems = "select LEDGER_ID, ITEM_NM from "
                        + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("LedgerItems")
                        + " where LEDGER_ID = " + ledger.getId()
                        + " order by ITEM_NM asc ";
                CachedRowSet crsItems = null;
                crsItems = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryItems, "reader");
                while (crsItems.next()) {
                    ledgerItemList.add(ledgerItemConfiguration.getLedgerItem(ledger.getId(), crsItems.getString("ITEM_NM")));
                }
                ledger.setItems(ledgerItemList);
                crsItems.close();

                // Get parameters
                CachedRowSet crsLedgerParameters = null;
                String queryLedgerParameters = "select LEDGER_ID, LEDGER_PAR_NM from "
                        + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("LedgerParameters")
                        + " where LEDGER_ID = " + ledger.getId();
                crsLedgerParameters = MetadataControl.getInstance().getDesignMetadataRepository()
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
                + MetadataControl.getInstance().getLedgerMetadataRepository().getTableNameByLabel("Ledgers") + " order by LEDGER_NM ASC";
        System.out.println(query);
        crs = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
        LedgerConfiguration ledgerConfiguration = new LedgerConfiguration();
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

}