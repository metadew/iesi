package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Repository;
import io.metadew.iesi.metadata.definition.RepositoryInstance;
import io.metadew.iesi.metadata.definition.RepositoryParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class RepositoryConfiguration {

    private Repository repository;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public RepositoryConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public RepositoryConfiguration(Repository repository, FrameworkInstance frameworkInstance) {
        this.setRepository(repository);
        this.setFrameworkInstance(frameworkInstance);
    }

    // Delete
    public String getDeleteStatement() {
        String sql = "";

        sql += "DELETE FROM "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstanceLabels");
        sql += " WHERE REPO_ID = (";
        sql += "select REPO_ID FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Repositories");
        sql += " WHERE REPO_NM = " + SQLTools.GetStringForSQL(this.getRepository().getName());
        sql += ")";
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstanceParameters");
        sql += " WHERE REPO_ID = (";
        sql += "select REPO_ID FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Repositories");
        sql += " WHERE REPO_NM = " + SQLTools.GetStringForSQL(this.getRepository().getName());
        sql += ")";
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstances");
        sql += " WHERE REPO_ID = (";
        sql += "select REPO_ID FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Repositories");
        sql += " WHERE REPO_NM = " + SQLTools.GetStringForSQL(this.getRepository().getName());
        sql += ")";
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryParameters");
        sql += " WHERE REPO_ID = (";
        sql += "select REPO_ID FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Repositories");
        sql += " WHERE REPO_NM = " + SQLTools.GetStringForSQL(this.getRepository().getName());
        sql += ")";
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Repositories");
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

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Repositories");
        sql += " (REPO_ID, REPO_NM, REPO_TYP_NM, REPO_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkInstance().getMetadataControl()
                        .getConnectivityMetadataRepository().getTableNameByLabel("Repositories"),
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
                    repositoryParameter, this.getFrameworkInstance());
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
                    repositoryInstance, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += repositoryInstanceConfiguration.getInsertStatement(this.getRepository().getName());
        }

        return result;
    }

    // GEt Repository
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Repository getRepository(String repositoryName) {
        Repository repository = new Repository();
        CachedRowSet crsRepository = null;
        String queryRepository = "select REPO_ID, REPO_NM, REPO_TYP_NM, REPO_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("Repositories")
                + " where REPO_NM = '" + repositoryName + "'";
        crsRepository = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .executeQuery(queryRepository, "reader");
        RepositoryParameterConfiguration repositoryParameterConfiguration = new RepositoryParameterConfiguration(
                this.getFrameworkInstance());
        RepositoryInstanceConfiguration repositoryInstanceConfiguration = new RepositoryInstanceConfiguration(
                this.getFrameworkInstance());
        try {
            while (crsRepository.next()) {
                repository.setName(repositoryName);
                repository.setId(crsRepository.getLong("REPO_ID"));
                repository.setType(crsRepository.getString("REPO_TYP_NM"));
                repository.setDescription(crsRepository.getString("REPO_DSC"));

                // Get parameters
                CachedRowSet crsRepositoryParameters = null;
                String queryRepositoryParameters = "select REPO_ID, REPO_PAR_NM, REPO_PAR_VAL from "
                        + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("RepositoryParameters")
                        + " where REPO_ID = " + repository.getId();
                crsRepositoryParameters = this.getFrameworkInstance().getMetadataControl()
                        .getConnectivityMetadataRepository().executeQuery(queryRepositoryParameters, "reader");
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
                        + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("RepositoryInstances")
                        + " where REPO_ID = " + repository.getId();
                crsRepositoryInstances = this.getFrameworkInstance().getMetadataControl()
                        .getConnectivityMetadataRepository().executeQuery(queryRepositoryInstances, "reader");
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
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("Repositories")
                + " where REPO_NM = '" + repositoryName + "'";
        crsRepository = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .executeQuery(queryRepository, "reader");
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}