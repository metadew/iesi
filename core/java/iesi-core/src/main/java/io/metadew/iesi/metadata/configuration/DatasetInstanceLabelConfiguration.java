package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.DatasetInstanceLabel;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DatasetInstanceLabelConfiguration {

    private DatasetInstanceLabel datasetLabel;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public DatasetInstanceLabelConfiguration(DatasetInstanceLabel datasetLabel,
    		FrameworkInstance frameworkInstance) {
        this.setDatasetInstanceLabel(datasetLabel);
        this.setFrameworkInstance(frameworkInstance);
    }

    public DatasetInstanceLabelConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String datasetName, String datasetInstanceName) {
        String sql = "";

        sql += "INSERT INTO "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceLabels");
        sql += " (DST_ID, DST_INST_ID, DST_INST_LBL_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(
                this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("Datasets"),
                "DST_ID", "where DST_NM = '" + datasetName) + "')";
        sql += ",";
        sql += "(" + SQLTools.GetLookupIdStatement(
                this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("DatasetInstances"),
                "DST_INST_ID",
                "where DST_ID = (" + SQLTools.GetLookupIdStatement(
                        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
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
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceLabels")
                + " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstanceId + " and DST_INST_LBL_VAL = '" + datasetInstanceLabelName + "'";
        crsDatasetInstanceLabel = this.getFrameworkInstance().getMetadataControl()
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}