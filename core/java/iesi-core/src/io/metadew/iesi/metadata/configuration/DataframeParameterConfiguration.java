package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataframeParameter;
import io.metadew.iesi.metadata.definition.DataframeVersion;

public class DataframeParameterConfiguration {

	private DataframeVersion dataframeVersion;
	private DataframeParameter dataframeParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public DataframeParameterConfiguration(DataframeVersion dataframeVersion, DataframeParameter dataframeParameter, FrameworkExecution frameworkExecution) {
		this.setDataframeVersion(dataframeVersion);
		this.setDataframeParameter(dataframeParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public DataframeParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String dataframeName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration().getMetadataTableConfiguration().getTableName("DataframeParameters");
		sql += " (DATAFRAME_ID, DATAFRAME_VRS_NB, DATAFRAME_PAR_NM, DATAFRAME_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Dataviews"), "DATAFRAME_ID", "where DATAFRAME_NM = '"+ dataframeName) + "')";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getDataframeVersion().getNumber());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getDataframeParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getDataframeParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public DataframeParameter getDataframeParameter(long dataframeId, long dataframeVersionNumber, String dataframeParameterName) {
		DataframeParameter dataframeParameter = new DataframeParameter();
		CachedRowSet crsDataframeParameter = null;
		String queryDataframeParameter = "select DATAFRAME_ID, DATAFRAME_VRS_NB, DATAFRAME_PAR_NM, DATAFRAME_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration().getMetadataTableConfiguration().getTableName("DataframeParameters")
				+ " where DATAFRAME_ID = " + dataframeId + " and DATAFRAME_VRS_NB = " + dataframeVersionNumber + " and DATAFRAME_PAR_NM = '" + dataframeParameterName + "'";
		crsDataframeParameter = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryDataframeParameter);
		try {
			while (crsDataframeParameter.next()) {
				dataframeParameter.setName(dataframeParameterName);
				dataframeParameter.setValue(crsDataframeParameter.getString("DATAFRAME_PAR_VAL"));
			}
			crsDataframeParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return dataframeParameter;
	}

	// Getters and Setters
	public DataframeParameter getDataframeParameter() {
		return dataframeParameter;
	}

	public void setDataframeParameter(DataframeParameter dataframeParameter) {
		this.dataframeParameter = dataframeParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public DataframeVersion getDataframeVersion() {
		return dataframeVersion;
	}

	public void setDataframeVersion(DataframeVersion dataframeVersion) {
		this.dataframeVersion = dataframeVersion;
	}

}