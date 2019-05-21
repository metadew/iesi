package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DatasetParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DatasetParameterConfiguration {

    private DatasetParameter datasetParameter;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public DatasetParameterConfiguration(DatasetParameter datasetParameter, FrameworkExecution frameworkExecution) {
        this.setDatasetParameter(datasetParameter);
        this.setFrameworkExecution(frameworkExecution);
    }

    public DatasetParameterConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Insert
    public String getInsertStatement(String datasetName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("DatasetParameters");
        sql += " (DST_ID, DST_PAR_NM, DST_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Repositories"), "DST_ID", "where DST_NM = '" + datasetName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDatasetParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDatasetParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public DatasetParameter getDatasetParameter(long datasetId, String datasetParameterName) {
        DatasetParameter datasetParameter = new DatasetParameter();
        CachedRowSet crsDatasetParameter = null;
        String queryDatasetParameter = "select DST_ID, DST_PAR_NM, DST_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("DatasetParameters")
                + " where DST_ID = " + datasetId + " and DST_PAR_NM = '" + datasetParameterName + "'";
        crsDatasetParameter = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryDatasetParameter, "reader");
        try {
            while (crsDatasetParameter.next()) {
                datasetParameter.setName(datasetParameterName);
                datasetParameter.setValue(crsDatasetParameter.getString("DST_PAR_VAL"));
            }
            crsDatasetParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return datasetParameter;
    }

    // Getters and Setters
    public DatasetParameter getDatasetParameter() {
        return datasetParameter;
    }

    public void setDatasetParameter(DatasetParameter datasetParameter) {
        this.datasetParameter = datasetParameter;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}