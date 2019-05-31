package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.RepositoryInstanceParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class RepositoryInstanceParameterConfiguration {

    private RepositoryInstanceParameter repositoryParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public RepositoryInstanceParameterConfiguration(RepositoryInstanceParameter repositoryParameter,
    		FrameworkInstance frameworkInstance) {
        this.setRepositoryInstanceParameter(repositoryParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public RepositoryInstanceParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String repositoryName, String repositoryInstanceName) {
        String sql = "";

        sql += "INSERT INTO "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstanceParameters");
        sql += " (REPO_ID, REPO_INST_ID, REPO_INST_PAR_NM, REPO_INST_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(
                this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("Repositories"),
                "REPO_ID", "where REPO_NM = '" + repositoryName) + "')";
        sql += ",";
        sql += "(" + SQLTools.GetLookupIdStatement(
                this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("RepositoryInstances"),
                "REPO_INST_ID",
                "where REPO_ID = (" + SQLTools.GetLookupIdStatement(
                        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                                .getTableNameByLabel("Repositories"),
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
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstanceParameters")
                + " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstanceId + " and REPO_INST_PAR_NM = '" + repositoryInstanceParameterName + "'";
        crsRepositoryInstanceParameter = this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().executeQuery(queryRepositoryInstanceParameter, "reader");
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}