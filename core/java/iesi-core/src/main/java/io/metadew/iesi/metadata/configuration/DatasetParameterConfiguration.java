package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.DatasetParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DatasetParameterConfiguration {

    private DatasetParameter datasetParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public DatasetParameterConfiguration(DatasetParameter datasetParameter, FrameworkInstance frameworkInstance) {
        this.setDatasetParameter(datasetParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public DatasetParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String datasetName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("DatasetParameters");
        sql += " (DST_ID, DST_PAR_NM, DST_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Repositories"), "DST_ID", "where DST_NM = '" + datasetName) + "')";
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
        String queryDatasetParameter = "select DST_ID, DST_PAR_NM, DST_PAR_VAL from " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("DatasetParameters")
                + " where DST_ID = " + datasetId + " and DST_PAR_NM = '" + datasetParameterName + "'";
        crsDatasetParameter = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryDatasetParameter, "reader");
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}