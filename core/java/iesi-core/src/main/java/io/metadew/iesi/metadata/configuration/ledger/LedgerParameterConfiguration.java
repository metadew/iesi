//package io.metadew.iesi.metadata.configuration.ledger;
//
//import io.metadew.iesi.connection.tools.SQLTools;
//import io.metadew.iesi.metadata.definition.ledger.LedgerParameter;
//import io.metadew.iesi.metadata.execution.MetadataControl;
//
//import javax.sql.rowset.CachedRowSet;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//
//public class LedgerParameterConfiguration {
//
//    private LedgerParameter ledgerParameter;
//
//    // Constructors
//    public LedgerParameterConfiguration(LedgerParameter ledgerParameter) {
//        this.setLedgerParameter(ledgerParameter);
//    }
//
//    public LedgerParameterConfiguration() {
//    }
//
//    // Insert
//    public String getInsertStatement(String ledgerName) {
//        String sql = "";
//
//        sql += "INSERT INTO " + MetadataControl.getInstance().getLedgerMetadataRepository()
//                .getTableNameByLabel("LedgerParameters");
//        sql += " (LEDGER_ID, LEDGER_PAR_NM, LEDGER_PAR_VAL) ";
//        sql += "VALUES ";
//        sql += "(";
//        sql += "("
//                + SQLTools.GetLookupIdStatement(
//                MetadataControl.getInstance().getLedgerMetadataRepository()
//                        .getTableNameByLabel("Ledgers"),
//                "LEDGER_ID", "where LEDGER_NM = '" + ledgerName)
//                + "')";
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getLedgerParameter().getName());
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getLedgerParameter().getValue());
//        sql += ")";
//        sql += ";";
//
//        return sql;
//    }
//
//    public LedgerParameter getLedgerParameter(long ledgerId, String ledgerParameterName) {
//        LedgerParameter ledgerParameter = new LedgerParameter();
//        CachedRowSet crsLedgerParameter = null;
//        String queryLedgerParameter = "select LEDGER_ID, LEDGER_PAR_NM, LEDGER_PAR_VAL from "
//                + MetadataControl.getInstance().getLedgerMetadataRepository()
//                .getTableNameByLabel("LedgerParameters")
//                + " where LEDGER_ID = " + ledgerId + " and LEDGER_PAR_NM = '" + ledgerParameterName + "'";
//        crsLedgerParameter = MetadataControl.getInstance().getDesignMetadataRepository()
//                .executeQuery(queryLedgerParameter, "reader");
//        try {
//            while (crsLedgerParameter.next()) {
//                ledgerParameter.setName(ledgerParameterName);
//                ledgerParameter.setValue(crsLedgerParameter.getString("LEDGER_PAR_VAL"));
//            }
//            crsLedgerParameter.close();
//        } catch (Exception e) {
//            StringWriter StackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(StackTrace));
//        }
//        return ledgerParameter;
//    }
//
//    // Getters and Setters
//    public LedgerParameter getLedgerParameter() {
//        return ledgerParameter;
//    }
//
//    public void setLedgerParameter(LedgerParameter ledgerParameter) {
//        this.ledgerParameter = ledgerParameter;
//    }
//
//}