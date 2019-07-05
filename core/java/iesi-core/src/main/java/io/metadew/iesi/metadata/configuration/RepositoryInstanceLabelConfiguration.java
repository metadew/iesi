package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.RepositoryInstanceLabel;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class RepositoryInstanceLabelConfiguration {

    private RepositoryInstanceLabel repositoryLabel;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public RepositoryInstanceLabelConfiguration(RepositoryInstanceLabel repositoryLabel,
    		FrameworkInstance frameworkInstance) {
        this.setRepositoryInstanceLabel(repositoryLabel);
        this.setFrameworkInstance(frameworkInstance);
    }

    public RepositoryInstanceLabelConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String repositoryName, String repositoryInstanceName) {
        String sql = "";

        sql += "INSERT INTO "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstanceLabels");
        sql += " (REPO_ID, REPO_INST_ID, REPO_INST_LBL_VAL) ";
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
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstanceLabels")
                + " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstanceId + " and REPO_INST_LBL_VAL = '" + repositoryInstanceLabelName + "'";
        crsRepositoryInstanceLabel = this.getFrameworkInstance().getMetadataControl()
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}