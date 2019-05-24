package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataframeVersion;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DataframeVersionConfiguration {

    private DataframeVersion dataframeVersion;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public DataframeVersionConfiguration(DataframeVersion dataframeVersion, FrameworkExecution frameworkExecution) {
        this.setDataframeVersion(dataframeVersion);
        this.setFrameworkExecution(frameworkExecution);
    }

    public DataframeVersionConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Insert
    public String getInsertStatement(String dataframeName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("DataframeVersions");
        sql += " (DATAFRAME_ID, DATAFRAME_VRS_NB, DATAFRAME_VRS_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Dataviews"), "DATAFRAME_ID", "where DATAFRAME_NM = '" + dataframeName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataframeVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataframeVersion().getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }


    public String getDefaultInsertStatement(String dataframeName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("DataframeVersions");
        sql += " (DATAFRAME_ID, DATAFRAME_VRS_NB, DATAFRAME_VRS_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Dataviews"), "DATAFRAME_ID", "where DATAFRAME_NM = '" + dataframeName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL("0");
        sql += ",";
        sql += SQLTools.GetStringForSQL("Default dataframeVersion");
        sql += ")";
        sql += ";";

        return sql;
    }

    public DataframeVersion getDataframeVersion(long dataframeId, long dataframeVersionNumber) {
        DataframeVersion dataframeVersion = new DataframeVersion();
        CachedRowSet crsDataframeVersion = null;
        String queryDataframeVersion = "select DATAFRAME_ID, DATAFRAME_VRS_NB, DATAFRAME_VRS_DSC from " + this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("DataframeVersions")
                + " where DATAFRAME_ID = " + dataframeId + " and DATAFRAME_VRS_NB = " + dataframeVersionNumber;
        crsDataframeVersion = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryDataframeVersion, "reader");
        try {
            while (crsDataframeVersion.next()) {
                dataframeVersion.setNumber(dataframeVersionNumber);
                dataframeVersion.setDescription(crsDataframeVersion.getString("DATAFRAME_VRS_DSC"));
            }
            crsDataframeVersion.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return dataframeVersion;
    }

    // Getters and Setters
    public DataframeVersion getDataframeVersion() {
        return dataframeVersion;
    }

    public void setDataframeVersion(DataframeVersion dataframeVersion) {
        this.dataframeVersion = dataframeVersion;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}