//package io.metadew.iesi.metadata.configuration.dataset;
//
//import io.metadew.iesi.connection.tools.SQLTools;
//import io.metadew.iesi.metadata.definition.dataset.Dataset;
//import io.metadew.iesi.metadata.definition.dataset.DatasetInstance;
//import io.metadew.iesi.metadata.definition.dataset.DatasetInstanceLabel;
//import io.metadew.iesi.metadata.definition.dataset.DatasetInstanceParameter;
//import io.metadew.iesi.metadata.execution.MetadataControl;
//
//import javax.sql.rowset.CachedRowSet;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DatasetInstanceConfiguration {
//
//    private DatasetInstance datasetInstance;
//
//    // Constructors
//    public DatasetInstanceConfiguration(DatasetInstance datasetInstance) {
//        this.setDatasetInstance(datasetInstance);
//    }
//
//    public DatasetInstanceConfiguration() {
//    }
//
//    // Insert
//    public String getInsertStatement(String datasetName) {
//        String sql = "";
//
//        sql += "INSERT INTO "
//                + MetadataControl.getInstance().getConnectivityMetadataRepository()
//                .getTableNameByLabel("DatasetInstances");
//        sql += " (DST_ID, DST_INST_ID, DST_INST_NM, DST_INST_DSC) ";
//        sql += "VALUES ";
//        sql += "(";
//        sql += "(" + SQLTools.GetLookupIdStatement(
//                MetadataControl.getInstance().getConnectivityMetadataRepository()
//                        .getTableNameByLabel("Datasets"),
//                "DST_ID", "where DST_NM = '" + datasetName) + "')";
//        sql += ",";
//        sql += "(" + SQLTools.GetNextIdStatement(
//                MetadataControl.getInstance().getConnectivityMetadataRepository()
//                        .getTableNameByLabel("DatasetInstances"),
//                "DST_INST_ID") + ")";
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getDatasetInstance().getName());
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getDatasetInstance().getDescription());
//        sql += ")";
//        sql += ";";
//
//        // add Parameters
//        String sqlParameters = this.getParameterInsertStatements(datasetName);
//        if (!sqlParameters.equalsIgnoreCase("")) {
//            sql += "\n";
//            sql += sqlParameters;
//        }
//
//        // add Lables
//        String sqlLabels = this.getLabelInsertStatements(datasetName);
//        if (!sqlLabels.equalsIgnoreCase("")) {
//            sql += "\n";
//            sql += sqlLabels;
//        }
//
//        return sql;
//    }
//
//    private String getParameterInsertStatements(String datasetName) {
//        String result = "";
//
//        // Catch null parameters
//        if (this.getDatasetInstance().getParameters() == null)
//            return result;
//
//        for (DatasetInstanceParameter datasetInstanceParameter : this.getDatasetInstance().getParameters()) {
//            DatasetInstanceParameterConfiguration datasetInstanceParameterConfiguration = new DatasetInstanceParameterConfiguration(
//                    datasetInstanceParameter);
//            if (!result.equalsIgnoreCase(""))
//                result += "\n";
//            result += datasetInstanceParameterConfiguration.getInsertStatement(datasetName,
//                    this.getDatasetInstance().getName());
//        }
//
//        return result;
//    }
//
//    private String getLabelInsertStatements(String datasetName) {
//        String result = "";
//
//        // Catch null labels
//        if (this.getDatasetInstance().getLabels() == null)
//            return result;
//
//        for (DatasetInstanceLabel datasetInstanceLabel : this.getDatasetInstance().getLabels()) {
//            DatasetInstanceLabelConfiguration datasetInstanceLabelConfiguration = new DatasetInstanceLabelConfiguration(
//                    datasetInstanceLabel);
//            if (!result.equalsIgnoreCase(""))
//                result += "\n";
//            result += datasetInstanceLabelConfiguration.getInsertStatement(datasetName,
//                    this.getDatasetInstance().getName());
//        }
//
//        return result;
//    }
//
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public DatasetInstance getDatasetInstance(long datasetId, String datasetInstanceName) {
//        DatasetInstance datasetInstance = new DatasetInstance();
//        CachedRowSet crsDatasetInstance = null;
//        String queryDatasetInstance = "select DST_ID, DST_INST_ID, DST_INST_NM, DST_INST_DSC from "
//                + MetadataControl.getInstance().getConnectivityMetadataRepository()
//                .getTableNameByLabel("DatasetInstances")
//                + " where DST_ID = " + datasetId + " and DST_INST_NM = '" + datasetInstanceName + "'";
//        crsDatasetInstance = MetadataControl.getInstance().getConnectivityMetadataRepository()
//                .executeQuery(queryDatasetInstance, "reader");
//        DatasetInstanceParameterConfiguration datasetInstanceParameterConfiguration = new DatasetInstanceParameterConfiguration();
//        DatasetInstanceLabelConfiguration datasetInstanceLabelConfiguration = new DatasetInstanceLabelConfiguration();
//        try {
//            while (crsDatasetInstance.next()) {
//                datasetInstance.setName(datasetInstanceName);
//                datasetInstance.setId(crsDatasetInstance.getLong("DST_INST_ID"));
//                datasetInstance.setDescription(crsDatasetInstance.getString("DST_INST_DSC"));
//
//                // Get parameters
//                CachedRowSet crsDatasetInstanceParameters = null;
//                String queryDatasetInstanceParameters = "select DST_ID, DST_INST_ID, DST_INST_PAR_NM, DST_INST_PAR_VAL from "
//                        + MetadataControl.getInstance().getConnectivityMetadataRepository()
//                        .getTableNameByLabel("DatasetInstanceParameters")
//                        + " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstance.getId();
//                crsDatasetInstanceParameters = MetadataControl.getInstance()
//                        .getConnectivityMetadataRepository().executeQuery(queryDatasetInstanceParameters, "reader");
//                List<DatasetInstanceParameter> datasetInstanceParameterList = new ArrayList();
//                while (crsDatasetInstanceParameters.next()) {
//                    datasetInstanceParameterList.add(datasetInstanceParameterConfiguration.getDatasetInstanceParameter(
//                            datasetId, datasetInstance.getId(),
//                            crsDatasetInstanceParameters.getString("DST_INST_PAR_NM")));
//                }
//                datasetInstance.setParameters(datasetInstanceParameterList);
//                crsDatasetInstanceParameters.close();
//
//                // Get labels
//                CachedRowSet crsDatasetInstanceLabels = null;
//                String queryDatasetInstanceLabels = "select DST_ID, DST_INST_ID, DST_INST_LBL_VAL from "
//                        + MetadataControl.getInstance().getConnectivityMetadataRepository()
//                        .getTableNameByLabel("DatasetInstanceLabels")
//                        + " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstance.getId();
//                crsDatasetInstanceLabels = MetadataControl.getInstance()
//                        .getConnectivityMetadataRepository().executeQuery(queryDatasetInstanceLabels, "reader");
//                List<DatasetInstanceLabel> datasetInstanceLabelList = new ArrayList();
//                while (crsDatasetInstanceLabels.next()) {
//                    datasetInstanceLabelList.add(datasetInstanceLabelConfiguration.getDatasetInstanceLabel(datasetId,
//                            datasetInstance.getId(), crsDatasetInstanceLabels.getString("DST_INST_LBL_VAL")));
//                }
//                datasetInstance.setLabels(datasetInstanceLabelList);
//                crsDatasetInstanceLabels.close();
//            }
//            crsDatasetInstance.close();
//        } catch (Exception e) {
//            StringWriter StackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(StackTrace));
//        }
//        return datasetInstance;
//    }
//
//    public DatasetInstance getDatasetInstance(String datasetName, String datasetInstanceName) {
//        DatasetConfiguration datasetConfiguration = new DatasetConfiguration();
//        return this.getDatasetInstance(datasetConfiguration.getDatasetId(datasetName), datasetInstanceName);
//    }
//
//    public DatasetInstance getDatasetInstance(Dataset dataset, String datasetInstanceName) {
//        DatasetInstance datasetInstanceResult = null;
//        for (DatasetInstance datasetInstance : dataset.getInstances()) {
//            if (datasetInstance.getName().equalsIgnoreCase(datasetInstanceName)) {
//                datasetInstanceResult = datasetInstance;
//                break;
//            }
//        }
//
//        return datasetInstanceResult;
//    }
//
//    // Getters and Setters
//    public DatasetInstance getDatasetInstance() {
//        return datasetInstance;
//    }
//
//    public void setDatasetInstance(DatasetInstance datasetInstance) {
//        this.datasetInstance = datasetInstance;
//    }
//
//}