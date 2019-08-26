package io.metadew.iesi.metadata.configuration.dataframe;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.metadata.definition.*;
import io.metadew.iesi.metadata.definition.dataframe.Dataframe;
import io.metadew.iesi.metadata.definition.dataframe.DataframeItem;
import io.metadew.iesi.metadata.definition.dataframe.DataframeParameter;
import io.metadew.iesi.metadata.definition.dataframe.DataframeVersion;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class DataframeConfiguration {

    private Dataframe dataframe;
    private MetadataRepositoryOperation metadataRepositoryOperation;

    // Constructors
    public DataframeConfiguration() {
    }

    public DataframeConfiguration(Dataframe dataframe, MetadataRepositoryOperation metadataRepositoryOperation) {
        this.setDataframe(dataframe);
        this.setMetadataRepositoryOperation(metadataRepositoryOperation);
        this.verifyVersionExists();
    }

    // Checks
    private void verifyVersionExists() {
        if (this.getDataframe().getVersion() == null) {
            this.getDataframe().setVersion(new DataframeVersion());
            this.getDataframe().getVersion().setNumber(0);
            this.getDataframe().getVersion().setDescription("Default version");
        }
    }

    private boolean verifyDataframeConfigurationExists(String dataframeName) {
        Dataframe dataframe = new Dataframe();
        CachedRowSet crsDataframe = null;
        String queryDataframe = "select DATAFRAME_ID, DATAFRAME_TYP_NM, DATAFRAME_NM, DATAFRAME_DSC from "
                + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews") + " where DATAFRAME_NM = '"
                + dataframeName + "'";
        crsDataframe = this.getMetadataRepositoryOperation().getMetadataRepository().executeQuery(queryDataframe, "reader");
        // Metadew repository change - replicate across

        try {
            while (crsDataframe.next()) {
                dataframe.setId(crsDataframe.getLong("DATAFRAME_ID"));
                dataframe.setType(crsDataframe.getString("DATAFRAME_TYP_NM"));
                dataframe.setName(dataframeName);
                dataframe.setDescription(crsDataframe.getString("DATAFRAME_DSC"));
            }
            crsDataframe.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (dataframe.getName() == null || dataframe.getName().equalsIgnoreCase("")) {
            return false;
        } else {
            return true;
        }
    }

    // Insert
    public String getInsertStatement() {
        String sql = "";
        if (this.exists()) {
            sql += "DELETE FROM " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItemParameters");
            sql += " WHERE DATAFRAME_ITEM_ID in (";
            sql += "select DATAFRAME_ITEM_ID FROM " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItems");
            sql += " WHERE DATAFRAME_ID = (";
            sql += "select DATAFRAME_ID FROM " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews");
            sql += " WHERE DATAFRAME_NM = "
                    + SQLTools.GetStringForSQL(this.getDataframe().getName());
            sql += ")";
            sql += " AND DATAFRAME_VRS_NB = " + this.getDataframe().getVersion().getNumber();
            sql += ")";
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItems");
            sql += " WHERE DATAFRAME_ID = (";
            sql += "select DATAFRAME_ID FROM " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews");
            sql += " WHERE DATAFRAME_NM = "
                    + SQLTools.GetStringForSQL(this.getDataframe().getName());
            sql += ")";
            sql += " AND DATAFRAME_VRS_NB = " + this.getDataframe().getVersion().getNumber();
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeVersions");
            sql += " WHERE DATAFRAME_ID = (";
            sql += "select DATAFRAME_ID FROM " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews");
            sql += " WHERE DATAFRAME_NM = "
                    + SQLTools.GetStringForSQL(this.getDataframe().getName());
            sql += ")";
            sql += " AND DATAFRAME_VRS_NB = " + this.getDataframe().getVersion().getNumber();
            sql += ";";

            /*
             * Remove delete option for any version of a dataframe sql += "\n"; sql +=
             * "DELETE FROM " +
             * MetadataControl.getInstance().getDesignMetadataRepository().getMetadataTableConfiguration().getCFG_DATAFRAME();
             * sql += " WHERE DATAFRAME_NM = " +
             * this.getFrameworkInstance().getSqlTools().GetStringForSQL(this.getDataframe().
             * getScriptName()); sql += ";";
             */

            sql += "\n";
        }

        if (!this.verifyDataframeConfigurationExists(this.getDataframe().getName())) {
            sql += "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews");
            sql += " (DATAFRAME_ID, DATAFRAME_TYP_NM, DATAFRAME_NM, DATAFRAME_DSC) ";
            sql += "VALUES ";
            sql += "(";
            sql += "("
                    + SQLTools.GetNextIdStatement(
                    MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews"), "DATAFRAME_ID")
                    + ")";
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getDataframe().getType());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getDataframe().getName());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getDataframe().getDescription());
            sql += ")";
            sql += ";";
        }

        // add DataframeVersion
        String sqlVersion = this.getVersionInsertStatements();
        if (!sqlVersion.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlVersion;
        }

        // add Items
        String sqlItems = this.getDataframeItemInsertStatements();
        if (!sqlItems.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlItems;
        }

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements();
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getVersionInsertStatements() {
        String result = "";

        if (this.getDataframe().getVersion() == null)
            return result;

        DataframeVersionConfiguration dataframeVersionConfiguration = new DataframeVersionConfiguration(
                this.getDataframe().getVersion());
        result += dataframeVersionConfiguration.getInsertStatement(this.getDataframe().getName());

        return result;
    }

    private String getDataframeItemInsertStatements() {
        String result = "";
        int counter = 0;

        if (this.getDataframe().getItems() == null)
            return result;

        for (DataframeItem dataframeItem : this.getDataframe().getItems()) {
            counter++;
            DataframeItemConfiguration dataframeItemConfiguration = new DataframeItemConfiguration(dataframeItem);
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += dataframeItemConfiguration.getInsertStatement(this.getDataframe().getName(),
                    this.getDataframe().getVersion().getNumber(), counter);
        }

        return result;
    }

    private String getParameterInsertStatements() {
        String result = "";

        if (this.getDataframe().getParameters() == null)
            return result;

        for (DataframeParameter dataframeParameter : this.getDataframe().getParameters()) {
            DataframeParameterConfiguration dataframeParameterConfiguration = new DataframeParameterConfiguration(
                    this.getDataframe().getVersion(), dataframeParameter);
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += dataframeParameterConfiguration.getInsertStatement(this.getDataframe().getName());
        }

        return result;
    }

    private long getLatestVersion(String dataframeName) {
        long dataframeVersionNumber = -1;
        CachedRowSet crsDataframeVersion = null;
        String queryDataframeVersion = "select max(DATAFRAME_VRS_NB) as \"MAX_VRS_NB\" from "
                + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeVersions") + " a inner join "
                + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews")
                + " b on a.dataframe_id = b.dataframe_id where b.dataframe_nm = '" + dataframeName + "'";
        crsDataframeVersion = this.getMetadataRepositoryOperation().getMetadataRepository().executeQuery(queryDataframeVersion, "reader");
        try {
            while (crsDataframeVersion.next()) {
                dataframeVersionNumber = crsDataframeVersion.getLong("MAX_VRS_NB");
            }
            crsDataframeVersion.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (dataframeVersionNumber == -1) {
            throw new RuntimeException("No dataframe version found for Dataframe (NAME) " + dataframeName);
        }

        return dataframeVersionNumber;
    }

    public Dataframe getDataframe(String dataframeName) {
        return this.getDataframe(dataframeName, this.getLatestVersion(dataframeName));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Dataframe getDataframe(String dataframeName, long dataframeVersionNumber) {
        Dataframe dataframe = new Dataframe();
        CachedRowSet crsDataframe = null;
        String queryDataframe = "select DATAFRAME_ID, DATAFRAME_TYP_NM, DATAFRAME_NM, DATAFRAME_DSC from "
                + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews") + " where DATAFRAME_NM = '"
                + dataframeName + "'";
        crsDataframe = this.getMetadataRepositoryOperation().getMetadataRepository().executeQuery(queryDataframe, "reader");
        DataframeItemConfiguration dataframeItemConfiguration = new DataframeItemConfiguration();
        DataframeParameterConfiguration dataframeParameterConfiguration = new DataframeParameterConfiguration();
        DataframeVersionConfiguration dataframeVersionConfiguration = new DataframeVersionConfiguration();
        try {
            while (crsDataframe.next()) {
                dataframe.setId(crsDataframe.getLong("DATAFRAME_ID"));
                dataframe.setType(crsDataframe.getString("DATAFRAME_TYP_NM"));
                dataframe.setName(dataframeName);
                dataframe.setDescription(crsDataframe.getString("DATAFRAME_DSC"));

                // Get the version
                DataframeVersion dataframeVersion = dataframeVersionConfiguration.getDataframeVersion(dataframe.getId(), dataframeVersionNumber);
                dataframe.setVersion(dataframeVersion);

                // Get the dataframeItems
                List<DataframeItem> dataframeItemList = new ArrayList();
                String queryItems = "select DATAFRAME_ID, DATAFRAME_VRS_NB, DATAFRAME_ITEM_ID, DATAFRAME_ITEM_NB from "
                        + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeItems")
                        + " where DATAFRAME_ID = " + dataframe.getId() + " and DATAFRAME_VRS_NB = " + dataframeVersionNumber
                        + " order by DATAFRAME_ITEM_NB asc ";
                CachedRowSet crsItems = null;
                crsItems = this.getMetadataRepositoryOperation().getMetadataRepository().executeQuery(queryItems, "reader");
                while (crsItems.next()) {
                    dataframeItemList.add(dataframeItemConfiguration.getDataframeItem(crsItems.getLong("DATAFRAME_ITEM_ID")));
                }
                dataframe.setItems(dataframeItemList);
                crsItems.close();

                // Get parameters
                CachedRowSet crsDataframeParameters = null;
                String queryDataframeParameters = "select DATAFRAME_ID, DATAFRAME_VRS_NB, DATAFRAME_PAR_NM from "
                        + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("DataframeParameters")
                        + " where DATAFRAME_ID = " + dataframe.getId() + " and DATAFRAME_VRS_NB = " + dataframeVersionNumber;
                crsDataframeParameters = this.getMetadataRepositoryOperation().getMetadataRepository()
                        .executeQuery(queryDataframeParameters, "reader");
                List<DataframeParameter> dataframeParameterList = new ArrayList();
                while (crsDataframeParameters.next()) {
                    dataframeParameterList.add(dataframeParameterConfiguration.getDataframeParameter(dataframe.getId(),
                            dataframeVersionNumber, crsDataframeParameters.getString("DATAFRAME_PAR_NM")));
                }
                dataframe.setParameters(dataframeParameterList);
                crsDataframeParameters.close();

            }
            crsDataframe.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (dataframe.getName() == null || dataframe.getName().equalsIgnoreCase("")) {
            throw new RuntimeException("Dataframe (NAME) " + dataframeName + " does not exist");
        }

        return dataframe;
    }

    // Get
    public ListObject getDataframes() {
        List<Dataframe> dataframeList = new ArrayList<>();
        CachedRowSet crs = null;
        String query = "select DATAFRAME_NM, DATAFRAME_DSC from "
                + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Dataviews") + " order by DATAFRAME_NM ASC";
        crs = this.getMetadataRepositoryOperation().getMetadataRepository().executeQuery(query, "reader");
        DataframeConfiguration dataframeConfiguration = new DataframeConfiguration();
        try {
            String dataframeName = "";
            while (crs.next()) {
                dataframeName = crs.getString("DATAFRAME_NM");
                dataframeList.add(dataframeConfiguration.getDataframe(dataframeName));
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return new ListObject(
                FrameworkObjectConfiguration.getFrameworkObjectType(new Dataframe()),
                dataframeList);
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Dataframe getDataframe() {
        return dataframe;
    }

    public void setDataframe(Dataframe dataframe) {
        this.dataframe = dataframe;
    }

    public MetadataRepositoryOperation getMetadataRepositoryOperation() {
        return metadataRepositoryOperation;
    }

    public void setMetadataRepositoryOperation(MetadataRepositoryOperation metadataRepositoryOperation) {
        this.metadataRepositoryOperation = metadataRepositoryOperation;
    }

}