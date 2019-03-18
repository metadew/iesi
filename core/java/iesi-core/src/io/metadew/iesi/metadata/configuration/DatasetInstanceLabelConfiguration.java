package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DatasetInstanceLabel;

public class DatasetInstanceLabelConfiguration {

	private DatasetInstanceLabel datasetLabel;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public DatasetInstanceLabelConfiguration(DatasetInstanceLabel datasetLabel,
			FrameworkExecution frameworkExecution) {
		this.setDatasetInstanceLabel(datasetLabel);
		this.setFrameworkExecution(frameworkExecution);
	}

	public DatasetInstanceLabelConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String datasetName, String datasetInstanceName) {
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstanceLabels");
		sql += " (DST_ID, DST_INST_ID, DST_INST_LBL_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(
				this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("Datasets"),
				"DST_ID", "where DST_NM = '" + datasetName) + "')";
		sql += ",";
		sql += "(" + SQLTools.GetLookupIdStatement(
				this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstances"),
				"DST_INST_ID",
				"where DST_ID = (" + SQLTools.GetLookupIdStatement(
						this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("Repositories"),
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
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstanceLabels")
				+ " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstanceId + " and DST_INST_LBL_VAL = '" + datasetInstanceLabelName + "'";
		crsDatasetInstanceLabel = this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().executeQuery(queryDatasetInstanceLabel);
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

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}