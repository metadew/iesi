package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.DataframeItem;
import io.metadew.iesi.metadata.definition.DataframeItemParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class DataframeItemConfiguration {

    private DataframeItem dataframeItem;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public DataframeItemConfiguration(DataframeItem dataframeItem, FrameworkInstance frameworkInstance) {
        this.setDataframeItem(dataframeItem);
        this.setFrameworkInstance(frameworkInstance);
    }

    public DataframeItemConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String scriptName, long scriptVersionNumber, int dataframeItemNumber) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItems");
        sql += " (DATAFRAME_ID, DATAFRAME_VRS_NB, DATAFRAME_ITEM_ID, DATAFRAME_ITEM_NB, DATAFRAME_ITEM_TYP_NM, DATAFRAME_ITEM_NM, DATAFRAME_ITEM_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews"), "DATAFRAME_ID", "DATAFRAME_NM", scriptName) + ")";
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptVersionNumber);
        sql += ",";
        sql += "(" + SQLTools.GetNextIdStatement(MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItems"), "DATAFRAME_ITEM_ID") + ")";
        sql += ",";
        sql += SQLTools.GetStringForSQL(dataframeItemNumber);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataframeItem().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataframeItem().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataframeItem().getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(scriptName, scriptVersionNumber);
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(String scriptName, long scriptVersionNumber) {
        String result = "";

        for (DataframeItemParameter dataframeItemParameter : this.getDataframeItem().getParameters()) {
            DataframeItemParameterConfiguration dataframeItemParameterConfiguration = new DataframeItemParameterConfiguration(dataframeItemParameter, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += dataframeItemParameterConfiguration.getInsertStatement(scriptName, scriptVersionNumber, this.getDataframeItem().getName());
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public DataframeItem getDataframeItem(long dataframeItemId) {
        DataframeItem dataframeItem = new DataframeItem();
        CachedRowSet crsDataframeItem = null;
        String queryDataframeItem = "select DATAFRAME_ID, DATAFRAME_VRS_NB, DATAFRAME_ITEM_ID, DATAFRAME_ITEM_NB, DATAFRAME_ITEM_TYP_NM, DATAFRAME_ITEM_NM, DATAFRAME_ITEM_DSC from "
                + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItems") + " where DATAFRAME_ITEM_ID = " + dataframeItemId;
        crsDataframeItem = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryDataframeItem, "reader");
        DataframeItemParameterConfiguration dataframeItemParameterConfiguration = new DataframeItemParameterConfiguration(this.getFrameworkInstance());
        try {
            while (crsDataframeItem.next()) {
                dataframeItem.setId(dataframeItemId);
                dataframeItem.setNumber(crsDataframeItem.getLong("DATAFRAME_ITEM_NB"));
                dataframeItem.setType(crsDataframeItem.getString("DATAFRAME_ITEM_TYP_NM"));
                dataframeItem.setName(crsDataframeItem.getString("DATAFRAME_ITEM_NM"));
                dataframeItem.setDescription(crsDataframeItem.getString("DATAFRAME_ITEM_DSC"));

                // Get parameters
                CachedRowSet crsDataframeItemParameters = null;
                String queryDataframeItemParameters = "select DATAFRAME_ITEM_ID, DATAFRAME_ITEM_PAR_NM from " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItemParameters")
                        + " where DATAFRAME_ITEM_ID = " + dataframeItemId;
                crsDataframeItemParameters = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryDataframeItemParameters, "reader");
                List<DataframeItemParameter> dataframeItemParameterList = new ArrayList();
                while (crsDataframeItemParameters.next()) {
                    dataframeItemParameterList
                            .add(dataframeItemParameterConfiguration.getDataframeItemParameter(dataframeItemId, crsDataframeItemParameters.getString("DATAFRAME_ITEM_PAR_NM")));
                }
                dataframeItem.setParameters(dataframeItemParameterList);
                crsDataframeItemParameters.close();

            }
            crsDataframeItem.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return dataframeItem;
    }

    // Getters and Setters
    public DataframeItem getDataframeItem() {
        return dataframeItem;
    }

    public void setDataframeItem(DataframeItem dataframeItem) {
        this.dataframeItem = dataframeItem;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}