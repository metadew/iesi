package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Repository;
import io.metadew.iesi.metadata.definition.RepositoryInstance;
import io.metadew.iesi.metadata.definition.RepositoryInstanceLabel;
import io.metadew.iesi.metadata.definition.RepositoryInstanceParameter;

public class RepositoryInstanceConfiguration {

	private RepositoryInstance repositoryInstance;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public RepositoryInstanceConfiguration(RepositoryInstance repositoryInstance,
			FrameworkExecution frameworkExecution) {
		this.setRepositoryInstance(repositoryInstance);
		this.setFrameworkExecution(frameworkExecution);
	}

	public RepositoryInstanceConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String repositoryName) {
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("RepositoryInstances");
		sql += " (REPO_ID, REPO_INST_ID, REPO_INST_NM, REPO_INST_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(
				this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("Repositories"),
				"REPO_ID", "where REPO_NM = '" + repositoryName) + "')";
		sql += ",";
		sql += "("
				+ SQLTools.GetNextIdStatement(
						this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("RepositoryInstances"),
						"REPO_INST_ID")
				+ ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepositoryInstance().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepositoryInstance().getDescription());
		sql += ")";
		sql += ";";
		
		// add Parameters
		String sqlParameters = this.getParameterInsertStatements(repositoryName);
		if (!sqlParameters.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		// add Lables
		String sqlLabels = this.getLabelInsertStatements(repositoryName);
		if (!sqlLabels.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlLabels;
		}

		return sql;
	}

	private String getParameterInsertStatements(String repositoryName) {
		String result = "";

		// Catch null parameters
		if (this.getRepositoryInstance().getParameters() == null)
			return result;

		for (RepositoryInstanceParameter repositoryInstanceParameter : this.getRepositoryInstance().getParameters()) {
			RepositoryInstanceParameterConfiguration repositoryInstanceParameterConfiguration = new RepositoryInstanceParameterConfiguration(
					repositoryInstanceParameter, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += repositoryInstanceParameterConfiguration.getInsertStatement(repositoryName, this.getRepositoryInstance().getName());
		}

		return result;
	}

	
	private String getLabelInsertStatements(String repositoryName) {
		String result = "";

		// Catch null labels
		if (this.getRepositoryInstance().getLabels() == null)
			return result;

		for (RepositoryInstanceLabel repositoryInstanceLabel : this.getRepositoryInstance().getLabels()) {
			RepositoryInstanceLabelConfiguration repositoryInstanceLabelConfiguration = new RepositoryInstanceLabelConfiguration(
					repositoryInstanceLabel, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += repositoryInstanceLabelConfiguration.getInsertStatement(repositoryName, this.getRepositoryInstance().getName());
		}

		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RepositoryInstance getRepositoryInstance(long repositoryId, String repositoryInstanceName) {
		RepositoryInstance repositoryInstance = new RepositoryInstance();
		CachedRowSet crsRepositoryInstance = null;
		String queryRepositoryInstance = "select REPO_ID, REPO_INST_ID, REPO_INST_NM, REPO_INST_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("RepositoryInstances")
				+ " where REPO_ID = " + repositoryId + " and REPO_INST_NM = '" + repositoryInstanceName + "'";
		crsRepositoryInstance = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
				.executeQuery(queryRepositoryInstance);
		RepositoryInstanceParameterConfiguration repositoryInstanceParameterConfiguration = new RepositoryInstanceParameterConfiguration(
				this.getFrameworkExecution());
		RepositoryInstanceLabelConfiguration repositoryInstanceLabelConfiguration = new RepositoryInstanceLabelConfiguration(
				this.getFrameworkExecution());
		try {
			while (crsRepositoryInstance.next()) {
				repositoryInstance.setName(repositoryInstanceName);
				repositoryInstance.setId(crsRepositoryInstance.getLong("REPO_INST_ID"));
				repositoryInstance.setDescription(crsRepositoryInstance.getString("REPO_INST_DSC"));
				
				// Get parameters
				CachedRowSet crsRepositoryInstanceParameters = null;
				String queryRepositoryInstanceParameters = "select REPO_ID, REPO_INST_ID, REPO_INST_PAR_NM, REPO_INST_PAR_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("RepositoryInstanceParameters")
						+ " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstance.getId();
				crsRepositoryInstanceParameters = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryRepositoryInstanceParameters);
				List<RepositoryInstanceParameter> repositoryInstanceParameterList = new ArrayList();
				while (crsRepositoryInstanceParameters.next()) {
					repositoryInstanceParameterList.add(repositoryInstanceParameterConfiguration.getRepositoryInstanceParameter(
							repositoryId, repositoryInstance.getId(), crsRepositoryInstanceParameters.getString("REPO_INST_PAR_NM")));
				}
				repositoryInstance.setParameters(repositoryInstanceParameterList);
				crsRepositoryInstanceParameters.close();
				
				// Get labels
				CachedRowSet crsRepositoryInstanceLabels = null;
				String queryRepositoryInstanceLabels = "select REPO_ID, REPO_INST_ID, REPO_INST_LBL_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("RepositoryInstanceLabels")
						+ " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstance.getId();
				crsRepositoryInstanceLabels = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryRepositoryInstanceLabels);
				List<RepositoryInstanceLabel> repositoryInstanceLabelList = new ArrayList();
				while (crsRepositoryInstanceLabels.next()) {
					repositoryInstanceLabelList.add(repositoryInstanceLabelConfiguration.getRepositoryInstanceLabel(
							repositoryId, repositoryInstance.getId(), crsRepositoryInstanceLabels.getString("REPO_INST_LBL_VAL")));
				}
				repositoryInstance.setLabels(repositoryInstanceLabelList);
				crsRepositoryInstanceLabels.close();
			}
			crsRepositoryInstance.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return repositoryInstance;
	}

	public RepositoryInstance getRepositoryInstance(String repositoryName, String repositoryInstanceName) {
		RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration(this.getFrameworkExecution());
		return this.getRepositoryInstance(repositoryConfiguration.getRepositoryId(repositoryName), repositoryInstanceName);
	}
	
	public RepositoryInstance getRepositoryInstance(Repository repository, String repositoryInstanceName) {
		RepositoryInstance repositoryInstanceResult = null;
		for (RepositoryInstance repositoryInstance : repository.getInstances()) {
			if (repositoryInstance.getName().equalsIgnoreCase(repositoryInstanceName)) {
				repositoryInstanceResult = repositoryInstance;
				break;
			}
		}
		
		return repositoryInstanceResult;
	}

	// Getters and Setters
	public RepositoryInstance getRepositoryInstance() {
		return repositoryInstance;
	}

	public void setRepositoryInstance(RepositoryInstance repositoryInstance) {
		this.repositoryInstance = repositoryInstance;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}