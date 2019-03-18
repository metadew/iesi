package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Dataset;
import io.metadew.iesi.metadata.definition.DatasetInstance;
import io.metadew.iesi.metadata.definition.DatasetParameter;

public class DatasetConfiguration {

	private Dataset dataset;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public DatasetConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	public DatasetConfiguration(Dataset dataset, FrameworkExecution frameworkExecution) {
		this.setDataset(dataset);
		this.setFrameworkExecution(frameworkExecution);
	}

	// Delete
	public String getDeleteStatement() {
		String sql = "";

		sql += "DELETE FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstanceLabels");
		sql += " WHERE DST_ID = (";
		sql += "select DST_ID FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Datasets");
		sql += " WHERE DST_NM = " + SQLTools.GetStringForSQL(this.getDataset().getName());
		sql += ")";
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstanceParameters");
		sql += " WHERE DST_ID = (";
		sql += "select DST_ID FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Datasets");
		sql += " WHERE DST_NM = " + SQLTools.GetStringForSQL(this.getDataset().getName());
		sql += ")";
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstances");
		sql += " WHERE DST_ID = (";
		sql += "select DST_ID FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Datasets");
		sql += " WHERE DST_NM = " + SQLTools.GetStringForSQL(this.getDataset().getName());
		sql += ")";
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetParameters");
		sql += " WHERE DST_ID = (";
		sql += "select DST_ID FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Datasets");
		sql += " WHERE DST_NM = " + SQLTools.GetStringForSQL(this.getDataset().getName());
		sql += ")";
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Datasets");
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

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Datasets");
		sql += " (DST_ID, DST_NM, DST_TYP_NM, DST_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Datasets"),
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
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		// add Instances
		String sqlInstances = this.getInstanceInsertStatements();
		if (!sqlInstances.equals("")) {
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
					datasetParameter, this.getFrameworkExecution());
			if (!result.equals(""))
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
					datasetInstance, this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += datasetInstanceConfiguration.getInsertStatement(this.getDataset().getName());
		}

		return result;
	}

	// GEt Dataset
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Dataset getDataset(String datasetName) {
		Dataset dataset = new Dataset();
		CachedRowSet crsDataset = null;
		String queryDataset = "select DST_ID, DST_NM, DST_TYP_NM, DST_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("Datasets")
				+ " where DST_NM = '" + datasetName + "'";
		crsDataset = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
				.executeQuery(queryDataset);
		DatasetParameterConfiguration datasetParameterConfiguration = new DatasetParameterConfiguration(
				this.getFrameworkExecution());
		DatasetInstanceConfiguration datasetInstanceConfiguration = new DatasetInstanceConfiguration(
				this.getFrameworkExecution());
		try {
			while (crsDataset.next()) {
				dataset.setName(datasetName);
				dataset.setId(crsDataset.getLong("DST_ID"));
				dataset.setType(crsDataset.getString("DST_TYP_NM"));
				dataset.setDescription(crsDataset.getString("DST_DSC"));

				// Get parameters
				CachedRowSet crsDatasetParameters = null;
				String queryDatasetParameters = "select DST_ID, DST_PAR_NM, DST_PAR_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("DatasetParameters")
						+ " where DST_ID = " + dataset.getId();
				crsDatasetParameters = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryDatasetParameters);
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
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("DatasetInstances")
						+ " where DST_ID = " + dataset.getId();
				crsDatasetInstances = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryDatasetInstances);
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
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("Datasets")
				+ " where DST_NM = '" + datasetName + "'";
		crsDataset = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
				.executeQuery(queryDataset);
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

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}