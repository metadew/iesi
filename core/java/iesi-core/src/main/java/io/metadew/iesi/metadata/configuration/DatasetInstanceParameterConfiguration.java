package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.DatasetInstanceParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DatasetInstanceParameterConfiguration {

    private DatasetInstanceParameter datasetParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public DatasetInstanceParameterConfiguration(DatasetInstanceParameter datasetParameter,
    		FrameworkInstance frameworkInstance) {
        this.setDatasetInstanceParameter(datasetParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public DatasetInstanceParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String datasetName, String datasetInstanceName) {
        String sql = "";

        sql += "INSERT INTO "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceParameters");
        sql += " (DST_ID, DST_INST_ID, DST_INST_PAR_NM, DST_INST_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(
                this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("Datasets"),
                "DST_ID", "where DST_NM = '" + datasetName) + "')";
        sql += ",";
        sql += "(" + SQLTools.GetLookupIdStatement(
                this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("DatasetInstances"),
                "DST_INST_ID",
                "where DST_ID = (" + SQLTools.GetLookupIdStatement(
                        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
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
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceParameters")
                + " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstanceId + " and DST_INST_PAR_NM = '" + datasetInstanceParameterName + "'";
        crsDatasetInstanceParameter = this.getFrameworkInstance().getMetadataControl()
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}