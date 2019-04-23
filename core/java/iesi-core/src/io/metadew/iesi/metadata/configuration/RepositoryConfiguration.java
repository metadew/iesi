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
import io.metadew.iesi.metadata.definition.RepositoryParameter;

public class RepositoryConfiguration {

	private Repository repository;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public RepositoryConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	public RepositoryConfiguration(Repository repository, FrameworkExecution frameworkExecution) {
		this.setRepository(repository);
		this.setFrameworkExecution(frameworkExecution);
	}

	// Delete
	public String getDeleteStatement() {
		String sql = "";

		sql += "DELETE FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("RepositoryInstanceLabels");
		sql += " WHERE REPO_ID = (";
		sql += "select REPO_ID FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Repositories");
		sql += " WHERE REPO_NM = " + SQLTools.GetStringForSQL(this.getRepository().getName());
		sql += ")";
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("RepositoryInstanceParameters");
		sql += " WHERE REPO_ID = (";
		sql += "select REPO_ID FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Repositories");
		sql += " WHERE REPO_NM = " + SQLTools.GetStringForSQL(this.getRepository().getName());
		sql += ")";
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("RepositoryInstances");
		sql += " WHERE REPO_ID = (";
		sql += "select REPO_ID FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Repositories");
		sql += " WHERE REPO_NM = " + SQLTools.GetStringForSQL(this.getRepository().getName());
		sql += ")";
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("RepositoryParameters");
		sql += " WHERE REPO_ID = (";
		sql += "select REPO_ID FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Repositories");
		sql += " WHERE REPO_NM = " + SQLTools.GetStringForSQL(this.getRepository().getName());
		sql += ")";
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Repositories");
		sql += " WHERE REPO_NM = " + SQLTools.GetStringForSQL(this.getRepository().getName());
		sql += ";";
		sql += "\n";

		return sql;

	}

	// Insert
	public String getInsertStatement() {
		String sql = "";

		if (this.exists()) {
			sql += this.getDeleteStatement();
		}

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Repositories");
		sql += " (REPO_ID, REPO_NM, REPO_TYP_NM, REPO_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Repositories"),
				"REPO_ID") + ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepository().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepository().getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getRepository().getDescription());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements();
		if (!sqlParameters.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		// add Instances
		String sqlInstances = this.getInstanceInsertStatements();
		if (!sqlInstances.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlInstances;
		}

		return sql;
	}

	private String getParameterInsertStatements() {
		String result = "";

		// Catch null parameters
		if (this.getRepository().getParameters() == null)
			return result;

		for (RepositoryParameter repositoryParameter : this.getRepository().getParameters()) {
			RepositoryParameterConfiguration repositoryParameterConfiguration = new RepositoryParameterConfiguration(
					repositoryParameter, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += repositoryParameterConfiguration.getInsertStatement(this.getRepository().getName());
		}

		return result;
	}

	private String getInstanceInsertStatements() {
		String result = "";

		// Catch null parameters
		if (this.getRepository().getInstances() == null)
			return result;

		for (RepositoryInstance repositoryInstance : this.getRepository().getInstances()) {
			RepositoryInstanceConfiguration repositoryInstanceConfiguration = new RepositoryInstanceConfiguration(
					repositoryInstance, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += repositoryInstanceConfiguration.getInsertStatement(this.getRepository().getName());
		}

		return result;
	}

	// GEt Repository
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Repository getRepository(String repositoryName) {
		Repository repository = new Repository();
		CachedRowSet crsRepository = null;
		String queryRepository = "select REPO_ID, REPO_NM, REPO_TYP_NM, REPO_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("Repositories")
				+ " where REPO_NM = '" + repositoryName + "'";
		crsRepository = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
				.executeQuery(queryRepository);
		RepositoryParameterConfiguration repositoryParameterConfiguration = new RepositoryParameterConfiguration(
				this.getFrameworkExecution());
		RepositoryInstanceConfiguration repositoryInstanceConfiguration = new RepositoryInstanceConfiguration(
				this.getFrameworkExecution());
		try {
			while (crsRepository.next()) {
				repository.setName(repositoryName);
				repository.setId(crsRepository.getLong("REPO_ID"));
				repository.setType(crsRepository.getString("REPO_TYP_NM"));
				repository.setDescription(crsRepository.getString("REPO_DSC"));

				// Get parameters
				CachedRowSet crsRepositoryParameters = null;
				String queryRepositoryParameters = "select REPO_ID, REPO_PAR_NM, REPO_PAR_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("RepositoryParameters")
						+ " where REPO_ID = " + repository.getId();
				crsRepositoryParameters = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryRepositoryParameters);
				List<RepositoryParameter> repositoryParameterList = new ArrayList();
				while (crsRepositoryParameters.next()) {
					repositoryParameterList.add(repositoryParameterConfiguration.getRepositoryParameter(
							repository.getId(), crsRepositoryParameters.getString("REPO_PAR_NM")));
				}
				repository.setParameters(repositoryParameterList);
				crsRepositoryParameters.close();
				
				// Get Instances
				CachedRowSet crsRepositoryInstances = null;
				String queryRepositoryInstances = "select REPO_ID, REPO_INST_ID, REPO_INST_NM from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("RepositoryInstances")
						+ " where REPO_ID = " + repository.getId();
				crsRepositoryInstances = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryRepositoryInstances);
				List<RepositoryInstance> repositoryInstanceList = new ArrayList();
				while (crsRepositoryInstances.next()) {
					repositoryInstanceList.add(repositoryInstanceConfiguration.getRepositoryInstance(
							repository.getId(), crsRepositoryInstances.getString("REPO_INST_NM")));
				}
				repository.setInstances(repositoryInstanceList);
				crsRepositoryInstances.close();

			}
			crsRepository.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return repository;
	}
	
	public long getRepositoryId(String repositoryName) {
		Repository repository = new Repository();
		CachedRowSet crsRepository = null;
		String queryRepository = "select REPO_ID, REPO_NM, REPO_TYP_NM, REPO_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("Repositories")
				+ " where REPO_NM = '" + repositoryName + "'";
		crsRepository = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
				.executeQuery(queryRepository);
		try {
			while (crsRepository.next()) {
				repository.setName(repositoryName);
				repository.setId(crsRepository.getLong("REPO_ID"));
				repository.setType(crsRepository.getString("REPO_TYP_NM"));
				repository.setDescription(crsRepository.getString("REPO_DSC"));
			}
			crsRepository.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return repository.getId();
	}

	// Exists
	public boolean exists() {
		return true;
	}

	// Getters and Setters
	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}