package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.RepositoryInstanceParameter;

public class RepositoryInstanceParameterConfiguration {

	private RepositoryInstanceParameter repositoryParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public RepositoryInstanceParameterConfiguration(RepositoryInstanceParameter repositoryParameter,
			FrameworkExecution frameworkExecution) {
		this.setRepositoryInstanceParameter(repositoryParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public RepositoryInstanceParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String repositoryName, String repositoryInstanceName) {
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("RepositoryInstanceParameters");
		sql += " (REPO_ID, REPO_INST_ID, REPO_INST_PAR_NM, REPO_INST_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(
				this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("Repositories"),
				"REPO_ID", "where REPO_NM = '" + repositoryName) + "')";
		sql += ",";
		sql += "(" + SQLTools.GetLookupIdStatement(
				this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("RepositoryInstances"),
				"REPO_INST_ID",
				"where REPO_ID = (" + SQLTools.GetLookupIdStatement(
						this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("Repositories"),
						"REPO_ID", "where REPO_NM = '" + repositoryName) + "') and REPO_INST_NM = '"
						+ repositoryInstanceName)
				+ "')";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepositoryInstanceParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepositoryInstanceParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public RepositoryInstanceParameter getRepositoryInstanceParameter(long repositoryId, long repositoryInstanceId,
			String repositoryInstanceParameterName) {
		RepositoryInstanceParameter repositoryInstanceParameter = new RepositoryInstanceParameter();
		CachedRowSet crsRepositoryInstanceParameter = null;
		String queryRepositoryInstanceParameter = "select REPO_ID, REPO_INST_ID, REPO_INST_PAR_NM, REPO_INST_PAR_VAL from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("RepositoryInstanceParameters")
				+ " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstanceId + " and REPO_INST_PAR_NM = '" + repositoryInstanceParameterName + "'";
		crsRepositoryInstanceParameter = this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().executeQuery(queryRepositoryInstanceParameter);
		try {
			while (crsRepositoryInstanceParameter.next()) {
				repositoryInstanceParameter.setName(repositoryInstanceParameterName);
				repositoryInstanceParameter.setValue(crsRepositoryInstanceParameter.getString("REPO_INST_PAR_VAL"));
			}
			crsRepositoryInstanceParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return repositoryInstanceParameter;
	}

	// Getters and Setters
	public RepositoryInstanceParameter getRepositoryInstanceParameter() {
		return repositoryParameter;
	}

	public void setRepositoryInstanceParameter(RepositoryInstanceParameter repositoryParameter) {
		this.repositoryParameter = repositoryParameter;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}