package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.RepositoryParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class RepositoryParameterConfiguration {

    private RepositoryParameter repositoryParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public RepositoryParameterConfiguration(RepositoryParameter repositoryParameter, FrameworkInstance frameworkInstance) {
        this.setRepositoryParameter(repositoryParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public RepositoryParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String repositoryName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("RepositoryParameters");
        sql += " (REPO_ID, REPO_PAR_NM, REPO_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Repositories"), "REPO_ID", "where REPO_NM = '" + repositoryName) + "')";
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
        String queryRepositoryParameter = "select REPO_ID, REPO_PAR_NM, REPO_PAR_VAL from " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("RepositoryParameters")
                + " where REPO_ID = " + repositoryId + " and REPO_PAR_NM = '" + repositoryParameterName + "'";
        crsRepositoryParameter = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryRepositoryParameter, "reader");
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}