package io.metadew.iesi.metadata.configuration.repository;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.repository.RepositoryInstanceLabel;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class RepositoryInstanceLabelConfiguration {

    private RepositoryInstanceLabel repositoryLabel;

    // Constructors
    public RepositoryInstanceLabelConfiguration(RepositoryInstanceLabel repositoryLabel) {
        this.setRepositoryInstanceLabel(repositoryLabel);
    }

    public RepositoryInstanceLabelConfiguration() {
    }

    // Insert
    public String getInsertStatement(String repositoryName, String repositoryInstanceName) {
        String sql = "";

        sql += "INSERT INTO "
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstanceLabels");
        sql += " (REPO_ID, REPO_INST_ID, REPO_INST_LBL_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(
                MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("Repositories"),
                "REPO_ID", "where REPO_NM = '" + repositoryName) + "')";
        sql += ",";
        sql += "(" + SQLTools.GetLookupIdStatement(
                MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("RepositoryInstances"),
                "REPO_INST_ID",
                "where REPO_ID = (" + SQLTools.GetLookupIdStatement(
                        MetadataControl.getInstance().getConnectivityMetadataRepository()
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
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstanceLabels")
                + " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstanceId + " and REPO_INST_LBL_VAL = '" + repositoryInstanceLabelName + "'";
        crsRepositoryInstanceLabel = MetadataControl.getInstance()
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

}