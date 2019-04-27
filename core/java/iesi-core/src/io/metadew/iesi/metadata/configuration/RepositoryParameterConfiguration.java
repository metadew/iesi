package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.RepositoryParameter;

public class RepositoryParameterConfiguration {

	private RepositoryParameter repositoryParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public RepositoryParameterConfiguration(RepositoryParameter repositoryParameter, FrameworkExecution frameworkExecution) {
		this.setRepositoryParameter(repositoryParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public RepositoryParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String repositoryName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("RepositoryParameters");
		sql += " (REPO_ID, REPO_PAR_NM, REPO_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Repositories"), "REPO_ID", "where REPO_NM = '"+ repositoryName) + "')";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepositoryParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepositoryParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public RepositoryParameter getRepositoryParameter(long repositoryId, String repositoryParameterName) {
		RepositoryParameter repositoryParameter = new RepositoryParameter();
		CachedRowSet crsRepositoryParameter = null;
		String queryRepositoryParameter = "select REPO_ID, REPO_PAR_NM, REPO_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("RepositoryParameters")
				+ " where REPO_ID = " + repositoryId + " and REPO_PAR_NM = '" + repositoryParameterName + "'";
		crsRepositoryParameter = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryRepositoryParameter, "reader");
		try {
			while (crsRepositoryParameter.next()) {
				repositoryParameter.setName(repositoryParameterName);
				repositoryParameter.setValue(crsRepositoryParameter.getString("REPO_PAR_VAL"));
			}
			crsRepositoryParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return repositoryParameter;
	}

	// Getters and Setters
	public RepositoryParameter getRepositoryParameter() {
		return repositoryParameter;
	}

	public void setRepositoryParameter(RepositoryParameter repositoryParameter) {
		this.repositoryParameter = repositoryParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}