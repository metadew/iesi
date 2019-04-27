package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.RepositoryInstanceLabel;

public class RepositoryInstanceLabelConfiguration {

	private RepositoryInstanceLabel repositoryLabel;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public RepositoryInstanceLabelConfiguration(RepositoryInstanceLabel repositoryLabel,
			FrameworkExecution frameworkExecution) {
		this.setRepositoryInstanceLabel(repositoryLabel);
		this.setFrameworkExecution(frameworkExecution);
	}

	public RepositoryInstanceLabelConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String repositoryName, String repositoryInstanceName) {
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
						.getTableNameByLabel("RepositoryInstanceLabels");
		sql += " (REPO_ID, REPO_INST_ID, REPO_INST_LBL_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(
				this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
						.getTableNameByLabel("Repositories"),
				"REPO_ID", "where REPO_NM = '" + repositoryName) + "')";
		sql += ",";
		sql += "(" + SQLTools.GetLookupIdStatement(
				this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
						.getTableNameByLabel("RepositoryInstances"),
				"REPO_INST_ID",
				"where REPO_ID = (" + SQLTools.GetLookupIdStatement(
						this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
								.getTableNameByLabel("Repositories"),
						"REPO_ID", "where REPO_NM = '" + repositoryName) + "') and REPO_INST_NM = '"
						+ repositoryInstanceName)
				+ "')";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepositoryInstanceLabel().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public RepositoryInstanceLabel getRepositoryInstanceLabel(long repositoryId, long repositoryInstanceId,
			String repositoryInstanceLabelName) {
		RepositoryInstanceLabel repositoryInstanceLabel = new RepositoryInstanceLabel();
		CachedRowSet crsRepositoryInstanceLabel = null;
		String queryRepositoryInstanceLabel = "select REPO_ID, REPO_INST_ID, REPO_INST_LBL_VAL from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
						.getTableNameByLabel("RepositoryInstanceLabels")
				+ " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstanceId + " and REPO_INST_LBL_VAL = '" + repositoryInstanceLabelName + "'";
		crsRepositoryInstanceLabel = this.getFrameworkExecution().getMetadataControl()
				.getConnectivityMetadataRepository().executeQuery(queryRepositoryInstanceLabel, "reader");
		try {
			while (crsRepositoryInstanceLabel.next()) {
				repositoryInstanceLabel.setValue(repositoryInstanceLabelName);
			}
			crsRepositoryInstanceLabel.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return repositoryInstanceLabel;
	}

	// Getters and Setters
	public RepositoryInstanceLabel getRepositoryInstanceLabel() {
		return repositoryLabel;
	}

	public void setRepositoryInstanceLabel(RepositoryInstanceLabel repositoryLabel) {
		this.repositoryLabel = repositoryLabel;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}