package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataframeItemParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DataframeItemParameterConfiguration {

    private DataframeItemParameter dataframeItemParameter;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public DataframeItemParameterConfiguration(DataframeItemParameter dataframeItemParameter, FrameworkExecution frameworkExecution) {
        this.setDataframeItemParameter(dataframeItemParameter);
        this.setFrameworkExecution(frameworkExecution);
    }

    public DataframeItemParameterConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Insert
    public String getInsertStatement(String dataframeName, long dataframeVersionNumber, String dataframeItemName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("DataframeItemParameters");
        sql += " (DATAFRAME_ITEM_ID, DATAFRAME_ITEM_PAR_NM, DATAFRAME_ITEM_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("DataframeItems"), "DATAFRAME_ITEM_ID", "where DATAFRAME_ITEM_NM = '" + dataframeItemName + "' and DATAFRAME_ID = (" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Dataviews"), "DATAFRAME_ID", "DATAFRAME_NM", dataframeName)) + ") and DATAFRAME_VRS_NB =" + dataframeVersionNumber + ")";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataframeItemParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataframeItemParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public DataframeItemParameter getDataframeItemParameter(long dataframeItemId, String dataframeItemParameterName) {
        DataframeItemParameter dataframeItemParameter = new DataframeItemParameter();
        CachedRowSet crsDataframeItemParameter = null;
        String queryDataframeItemParameter = "select DATAFRAME_ITEM_ID, DATAFRAME_ITEM_PAR_NM, DATAFRAME_ITEM_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("DataframeItemParameters")
                + " where DATAFRAME_ITEM_ID = " + dataframeItemId + " and DATAFRAME_ITEM_PAR_NM = '" + dataframeItemParameterName + "'";
        crsDataframeItemParameter = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryDataframeItemParameter, "reader");
        try {
            while (crsDataframeItemParameter.next()) {
                dataframeItemParameter.setName(dataframeItemParameterName);
                dataframeItemParameter.setValue(crsDataframeItemParameter.getString("DATAFRAME_ITEM_PAR_VAL"));
            }
            crsDataframeItemParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return dataframeItemParameter;
    }

    // Getters and Setters
    public DataframeItemParameter getDataframeItemParameter() {
        return dataframeItemParameter;
    }

    public void setDataframeItemParameter(DataframeItemParameter dataframeItemParameter) {
        this.dataframeItemParameter = dataframeItemParameter;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}