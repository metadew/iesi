package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DatasetInstanceParameter;

public class DatasetInstanceParameterConfiguration {

	private DatasetInstanceParameter datasetParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public DatasetInstanceParameterConfiguration(DatasetInstanceParameter datasetParameter,
			FrameworkExecution frameworkExecution) {
		this.setDatasetInstanceParameter(datasetParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public DatasetInstanceParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String datasetName, String datasetInstanceName) {
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstanceParameters");
		sql += " (DST_ID, DST_INST_ID, DST_INST_PAR_NM, DST_INST_PAR_VAL) ";
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
								.getMetadataTableConfiguration().getTableName("Datasets"),
						"DST_ID", "where DST_NM = '" + datasetName) + "') and DST_INST_NM = '"
						+ datasetInstanceName)
				+ "')";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getDatasetInstanceParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getDatasetInstanceParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public DatasetInstanceParameter getDatasetInstanceParameter(long datasetId, long datasetInstanceId,
			String datasetInstanceParameterName) {
		DatasetInstanceParameter datasetInstanceParameter = new DatasetInstanceParameter();
		CachedRowSet crsDatasetInstanceParameter = null;
		String queryDatasetInstanceParameter = "select DST_ID, DST_INST_ID, DST_INST_PAR_NM, DST_INST_PAR_VAL from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstanceParameters")
				+ " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstanceId + " and DST_INST_PAR_NM = '" + datasetInstanceParameterName + "'";
		crsDatasetInstanceParameter = this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().executeQuery(queryDatasetInstanceParameter);
		try {
			while (crsDatasetInstanceParameter.next()) {
				datasetInstanceParameter.setName(datasetInstanceParameterName);
				datasetInstanceParameter.setValue(crsDatasetInstanceParameter.getString("DST_INST_PAR_VAL"));
			}
			crsDatasetInstanceParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return datasetInstanceParameter;
	}

	// Getters and Setters
	public DatasetInstanceParameter getDatasetInstanceParameter() {
		return datasetParameter;
	}

	public void setDatasetInstanceParameter(DatasetInstanceParameter datasetParameter) {
		this.datasetParameter = datasetParameter;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}