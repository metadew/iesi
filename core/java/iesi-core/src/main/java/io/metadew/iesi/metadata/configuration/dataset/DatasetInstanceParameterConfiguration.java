package io.metadew.iesi.metadata.configuration.dataset;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.dataset.DatasetInstanceParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DatasetInstanceParameterConfiguration {

    private DatasetInstanceParameter datasetParameter;

    // Constructors
    public DatasetInstanceParameterConfiguration(DatasetInstanceParameter datasetParameter) {
        this.setDatasetInstanceParameter(datasetParameter);
    }

    public DatasetInstanceParameterConfiguration() {
    }

    // Insert
    public String getInsertStatement(String datasetName, String datasetInstanceName) {
        String sql = "";

        sql += "INSERT INTO "
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceParameters");
        sql += " (DST_ID, DST_INST_ID, DST_INST_PAR_NM, DST_INST_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(
                MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("Datasets"),
                "DST_ID", "where DST_NM = '" + datasetName) + "')";
        sql += ",";
        sql += "(" + SQLTools.GetLookupIdStatement(
                MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("DatasetInstances"),
                "DST_INST_ID",
                "where DST_ID = (" + SQLTools.GetLookupIdStatement(
                        MetadataControl.getInstance().getConnectivityMetadataRepository()
                                .getTableNameByLabel("Datasets"),
                        "DST_ID", "where DST_NM = '" + datasetName) + "') and DST_INST_NM = '"
                        + datasetInstanceName)
                + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDatasetInstanceParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDatasetInstanceParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public DatasetInstanceParameter getDatasetInstanceParameter(long datasetId, long datasetInstanceId,
                                                                String datasetInstanceParameterName) {
        DatasetInstanceParameter datasetInstanceParameter = new DatasetInstanceParameter();
        CachedRowSet crsDatasetInstanceParameter = null;
        String queryDatasetInstanceParameter = "select DST_ID, DST_INST_ID, DST_INST_PAR_NM, DST_INST_PAR_VAL from "
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceParameters")
                + " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstanceId + " and DST_INST_PAR_NM = '" + datasetInstanceParameterName + "'";
        crsDatasetInstanceParameter = MetadataControl.getInstance()
                .getConnectivityMetadataRepository().executeQuery(queryDatasetInstanceParameter, "reader");
        try {
            while (crsDatasetInstanceParameter.next()) {
                datasetInstanceParameter.setName(datasetInstanceParameterName);
                datasetInstanceParameter.setValue(crsDatasetInstanceParameter.getString("DST_INST_PAR_VAL"));
            }
            crsDatasetInstanceParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return datasetInstanceParameter;
    }

    // Getters and Setters
    public DatasetInstanceParameter getDatasetInstanceParameter() {
        return datasetParameter;
    }

    public void setDatasetInstanceParameter(DatasetInstanceParameter datasetParameter) {
        this.datasetParameter = datasetParameter;
    }

}