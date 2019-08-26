package io.metadew.iesi.metadata.configuration.dataset;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.dataset.DatasetInstanceLabel;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DatasetInstanceLabelConfiguration {

    private DatasetInstanceLabel datasetLabel;

    // Constructors
    public DatasetInstanceLabelConfiguration(DatasetInstanceLabel datasetLabel) {
        this.setDatasetInstanceLabel(datasetLabel);
    }

    public DatasetInstanceLabelConfiguration() {
    }

    // Insert
    public String getInsertStatement(String datasetName, String datasetInstanceName) {
        String sql = "";

        sql += "INSERT INTO "
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceLabels");
        sql += " (DST_ID, DST_INST_ID, DST_INST_LBL_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(
                MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("Datasets"),
                "DST_ID", "where DST_NM = '" + datasetName) + "')";
        sql += ",";
        sql += "(" + SQLTools.GetLookupIdStatement(
                MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("DatasetInstances"),
                "DST_INST_ID",
                "where DST_ID = (" + SQLTools.GetLookupIdStatement(
                        MetadataControl.getInstance().getConnectivityMetadataRepository()
                                .getTableNameByLabel("Repositories"),
                        "DST_ID", "where DST_NM = '" + datasetName) + "') and DST_INST_NM = '"
                        + datasetInstanceName)
                + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDatasetInstanceLabel().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public DatasetInstanceLabel getDatasetInstanceLabel(long datasetId, long datasetInstanceId,
                                                        String datasetInstanceLabelName) {
        DatasetInstanceLabel datasetInstanceLabel = new DatasetInstanceLabel();
        CachedRowSet crsDatasetInstanceLabel = null;
        String queryDatasetInstanceLabel = "select DST_ID, DST_INST_ID, DST_INST_LBL_VAL from "
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceLabels")
                + " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstanceId + " and DST_INST_LBL_VAL = '" + datasetInstanceLabelName + "'";
        crsDatasetInstanceLabel = MetadataControl.getInstance()
                .getConnectivityMetadataRepository().executeQuery(queryDatasetInstanceLabel, "reader");
        try {
            while (crsDatasetInstanceLabel.next()) {
                datasetInstanceLabel.setValue(datasetInstanceLabelName);
            }
            crsDatasetInstanceLabel.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return datasetInstanceLabel;
    }

    // Getters and Setters
    public DatasetInstanceLabel getDatasetInstanceLabel() {
        return datasetLabel;
    }

    public void setDatasetInstanceLabel(DatasetInstanceLabel datasetLabel) {
        this.datasetLabel = datasetLabel;
    }

}