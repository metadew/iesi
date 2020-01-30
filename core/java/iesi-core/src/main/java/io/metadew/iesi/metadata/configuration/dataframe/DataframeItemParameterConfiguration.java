package io.metadew.iesi.metadata.configuration.dataframe;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.dataframe.DataframeItemParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class DataframeItemParameterConfiguration {

    private DataframeItemParameter dataframeItemParameter;

    // Constructors
    public DataframeItemParameterConfiguration(DataframeItemParameter dataframeItemParameter) {
        this.setDataframeItemParameter(dataframeItemParameter);
    }

    public DataframeItemParameterConfiguration() {
    }

    // Insert
    public String getInsertStatement(String dataframeName, long dataframeVersionNumber, String dataframeItemName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItemParameters");
        sql += " (DATAFRAME_ITEM_ID, DATAFRAME_ITEM_PAR_NM, DATAFRAME_ITEM_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItems"), "DATAFRAME_ITEM_ID", "where DATAFRAME_ITEM_NM = '" + dataframeItemName + "' and DATAFRAME_ID = (" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews"), "DATAFRAME_ID", "DATAFRAME_NM", dataframeName)) + ") and DATAFRAME_VRS_NB =" + dataframeVersionNumber + ")";
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
        String queryDataframeItemParameter = "select DATAFRAME_ITEM_ID, DATAFRAME_ITEM_PAR_NM, DATAFRAME_ITEM_PAR_VAL from " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItemParameters")
                + " where DATAFRAME_ITEM_ID = " + dataframeItemId + " and DATAFRAME_ITEM_PAR_NM = '" + dataframeItemParameterName + "'";
        crsDataframeItemParameter = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryDataframeItemParameter, "reader");
        try {
            while (crsDataframeItemParameter.next()) {
                dataframeItemParameter.setName(dataframeItemParameterName);
                dataframeItemParameter.setValue(crsDataframeItemParameter.getString("DATAFRAME_ITEM_PAR_VAL"));
            }
            crsDataframeItemParameter.close();
        } catch (SQLException e) {
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

}