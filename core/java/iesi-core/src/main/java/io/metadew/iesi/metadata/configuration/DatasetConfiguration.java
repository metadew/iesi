package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Dataset;
import io.metadew.iesi.metadata.definition.DatasetInstance;
import io.metadew.iesi.metadata.definition.DatasetParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class DatasetConfiguration {

    private Dataset dataset;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public DatasetConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public DatasetConfiguration(Dataset dataset, FrameworkInstance frameworkInstance) {
        this.setDataset(dataset);
        this.setFrameworkInstance(frameworkInstance);
    }

    // Delete
    public String getDeleteStatement() {
        String sql = "";

        sql += "DELETE FROM "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceLabels");
        sql += " WHERE DST_ID = (";
        sql += "select DST_ID FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Datasets");
        sql += " WHERE DST_NM = " + SQLTools.GetStringForSQL(this.getDataset().getName());
        sql += ")";
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstanceParameters");
        sql += " WHERE DST_ID = (";
        sql += "select DST_ID FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Datasets");
        sql += " WHERE DST_NM = " + SQLTools.GetStringForSQL(this.getDataset().getName());
        sql += ")";
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetInstances");
        sql += " WHERE DST_ID = (";
        sql += "select DST_ID FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Datasets");
        sql += " WHERE DST_NM = " + SQLTools.GetStringForSQL(this.getDataset().getName());
        sql += ")";
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("DatasetParameters");
        sql += " WHERE DST_ID = (";
        sql += "select DST_ID FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Datasets");
        sql += " WHERE DST_NM = " + SQLTools.GetStringForSQL(this.getDataset().getName());
        sql += ")";
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Datasets");
        sql += " WHERE DST_NM = " + SQLTools.GetStringForSQL(this.getDataset().getName());
        sql += ";";
        sql += "\n";

        return sql;

    }

    // Insert
    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += this.getDeleteStatement();
        }

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl()
                .getConnectivityMetadataRepository().getTableNameByLabel("Datasets");
        sql += " (DST_ID, DST_NM, DST_TYP_NM, DST_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkInstance().getMetadataControl()
                        .getConnectivityMetadataRepository().getTableNameByLabel("Datasets"),
                "DST_ID") + ")";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataset().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataset().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getDataset().getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements();
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        // add Instances
        String sqlInstances = this.getInstanceInsertStatements();
        if (!sqlInstances.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlInstances;
        }

        return sql;
    }

    private String getParameterInsertStatements() {
        String result = "";

        // Catch null parameters
        if (this.getDataset().getParameters() == null)
            return result;

        for (DatasetParameter datasetParameter : this.getDataset().getParameters()) {
            DatasetParameterConfiguration datasetParameterConfiguration = new DatasetParameterConfiguration(
                    datasetParameter, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += datasetParameterConfiguration.getInsertStatement(this.getDataset().getName());
        }

        return result;
    }

    private String getInstanceInsertStatements() {
        String result = "";

        // Catch null parameters
        if (this.getDataset().getInstances() == null)
            return result;

        for (DatasetInstance datasetInstance : this.getDataset().getInstances()) {
            DatasetInstanceConfiguration datasetInstanceConfiguration = new DatasetInstanceConfiguration(
                    datasetInstance, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += datasetInstanceConfiguration.getInsertStatement(this.getDataset().getName());
        }

        return result;
    }

    // GEt dataset
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Dataset getDataset(String datasetName) {
        Dataset dataset = new Dataset();
        CachedRowSet crsDataset = null;
        String queryDataset = "select DST_ID, DST_NM, DST_TYP_NM, DST_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("Datasets")
                + " where DST_NM = '" + datasetName + "'";
        crsDataset = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .executeQuery(queryDataset, "reader");
        DatasetParameterConfiguration datasetParameterConfiguration = new DatasetParameterConfiguration(
                this.getFrameworkInstance());
        DatasetInstanceConfiguration datasetInstanceConfiguration = new DatasetInstanceConfiguration(
                this.getFrameworkInstance());
        try {
            while (crsDataset.next()) {
                dataset.setName(datasetName);
                dataset.setId(crsDataset.getLong("DST_ID"));
                dataset.setType(crsDataset.getString("DST_TYP_NM"));
                dataset.setDescription(crsDataset.getString("DST_DSC"));

                // Get parameters
                CachedRowSet crsDatasetParameters = null;
                String queryDatasetParameters = "select DST_ID, DST_PAR_NM, DST_PAR_VAL from "
                        + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("DatasetParameters")
                        + " where DST_ID = " + dataset.getId();
                crsDatasetParameters = this.getFrameworkInstance().getMetadataControl()
                        .getConnectivityMetadataRepository().executeQuery(queryDatasetParameters, "reader");
                List<DatasetParameter> datasetParameterList = new ArrayList();
                while (crsDatasetParameters.next()) {
                    datasetParameterList.add(datasetParameterConfiguration.getDatasetParameter(
                            dataset.getId(), crsDatasetParameters.getString("DST_PAR_NM")));
                }
                dataset.setParameters(datasetParameterList);
                crsDatasetParameters.close();

                // Get Instances
                CachedRowSet crsDatasetInstances = null;
                String queryDatasetInstances = "select DST_ID, DST_INST_ID, DST_INST_NM from "
                        + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .getTableNameByLabel("DatasetInstances")
                        + " where DST_ID = " + dataset.getId();
                crsDatasetInstances = this.getFrameworkInstance().getMetadataControl()
                        .getConnectivityMetadataRepository().executeQuery(queryDatasetInstances, "reader");
                List<DatasetInstance> datasetInstanceList = new ArrayList();
                while (crsDatasetInstances.next()) {
                    datasetInstanceList.add(datasetInstanceConfiguration.getDatasetInstance(
                            dataset.getId(), crsDatasetInstances.getString("DST_INST_NM")));
                }
                dataset.setInstances(datasetInstanceList);
                crsDatasetInstances.close();

            }
            crsDataset.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return dataset;
    }

    public long getDatasetId(String datasetName) {
        Dataset dataset = new Dataset();
        CachedRowSet crsDataset = null;
        String queryDataset = "select DST_ID, DST_NM, DST_TYP_NM, DST_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("Datasets")
                + " where DST_NM = '" + datasetName + "'";
        crsDataset = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .executeQuery(queryDataset, "reader");
        try {
            while (crsDataset.next()) {
                dataset.setName(datasetName);
                dataset.setId(crsDataset.getLong("DST_ID"));
                dataset.setType(crsDataset.getString("DST_TYP_NM"));
                dataset.setDescription(crsDataset.getString("DST_DSC"));
            }
            crsDataset.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return dataset.getId();
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}