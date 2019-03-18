package io.metadew.iesi.metadata.configuration;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.ListObject;

public class ImpersonationConfiguration {

	private Impersonation impersonation;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ImpersonationConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	public ImpersonationConfiguration(Impersonation impersonation, FrameworkExecution frameworkExecution) {
		this.setImpersonation(impersonation);
		this.setFrameworkExecution(frameworkExecution);
	}

	// Delete
	public String getDeleteStatement() {
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations");
		sql += " WHERE IMP_NM = "
				+ SQLTools.GetStringForSQL(this.getImpersonation().getName());
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ImpersonationParameters");
		sql += " WHERE IMP_NM = "
				+ SQLTools.GetStringForSQL(this.getImpersonation().getName());
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

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations");
		sql += " (IMP_NM, IMP_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(this.getImpersonation().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getImpersonation().getDescription());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements(this.getImpersonation().getName());
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getParameterInsertStatements(String impersonationName) {
		String result = "";

		// Catch null parameters
		if (this.getImpersonation().getParameters() == null)
			return result;

		for (ImpersonationParameter impersonationParameter : this.getImpersonation().getParameters()) {
			ImpersonationParameterConfiguration impersonationParameterConfiguration = new ImpersonationParameterConfiguration(impersonationParameter,
					this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += impersonationParameterConfiguration.getInsertStatement(impersonationName);
		}

		return result;
	}

	// GEt Impersonation
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Impersonation getImpersonation(String impersonationName) {
		Impersonation impersonation = new Impersonation();
		CachedRowSet crsImpersonation = null;
		String queryImpersonation = "select IMP_NM, IMP_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations") + " where IMP_NM = '"
				+ impersonationName + "'";
		crsImpersonation = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(queryImpersonation);
		ImpersonationParameterConfiguration impersonationParameterConfiguration = new ImpersonationParameterConfiguration(this.getFrameworkExecution());
		try {
			while (crsImpersonation.next()) {
				impersonation.setName(impersonationName);
				impersonation.setDescription(crsImpersonation.getString("IMP_DSC"));

				// Get parameters
				CachedRowSet crsImpersonationParameters = null;
				String queryImpersonationParameters = "select IMP_NM, CONN_NM from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ImpersonationParameters")
						+ " where IMP_NM = '" + impersonationName + "'";
				crsImpersonationParameters = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.executeQuery(queryImpersonationParameters);
				List<ImpersonationParameter> impersonationParameterList = new ArrayList();
				while (crsImpersonationParameters.next()) {
					impersonationParameterList.add(impersonationParameterConfiguration.getImpersonationParameter(impersonationName,
							crsImpersonationParameters.getString("CONN_NM")));
				}
				impersonation.setParameters(impersonationParameterList);
				crsImpersonationParameters.close();
			}
			crsImpersonation.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return impersonation;
	}

	public ListObject getImpersonations() {
		List<Impersonation> impersonationList = new ArrayList<>();
		CachedRowSet crs = null;
		String query = "select IMP_NM from " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations")
				+ " order by IMP_NM ASC";
		crs = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(query);
		ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(this.getFrameworkExecution());
		try {
			String impersonationName = "";
			while (crs.next()) {
				impersonationName = crs.getString("IMP_NM");
				impersonationList.add(impersonationConfiguration.getImpersonation(impersonationName));
			}
			crs.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return new ListObject(FrameworkObjectConfiguration.getFrameworkObjectType(new Impersonation()), impersonationList);
	}

	public void createImpersonation(String data) {
		DataObjectConfiguration dataObjectConfiguration = new DataObjectConfiguration(this.getFrameworkExecution());
		ObjectMapper objectMapper = new ObjectMapper();
		
		if (dataObjectConfiguration.isJSONArray(data)) {
			for (DataObject doDataObject : dataObjectConfiguration.getDataArray(data)) {

				Impersonation impersonation = objectMapper.convertValue(doDataObject.getData(), Impersonation.class);
				ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, this.getFrameworkExecution());
				String output = impersonationConfiguration.getInsertStatement();

				InputStream inputStream = FileTools
						.convertToInputStream(output, this.getFrameworkExecution().getFrameworkControl());
				this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeScript(inputStream);

			}
		} else {
			Impersonation impersonation = objectMapper.convertValue(dataObjectConfiguration.getDataObject(data).getData(), Impersonation.class);
			ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, this.getFrameworkExecution());
			String output = impersonationConfiguration.getInsertStatement();

			InputStream inputStream = FileTools.convertToInputStream(output,
					this.getFrameworkExecution().getFrameworkControl());
			this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeScript(inputStream);
		}

	}
	
	public void deleteImpersonation(String impersonationName) {
		Impersonation impersonation = this.getImpersonation(impersonationName);
		ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, this.getFrameworkExecution());
		String output = impersonationConfiguration.getDeleteStatement();

		InputStream inputStream = FileTools
				.convertToInputStream(output, this.getFrameworkExecution().getFrameworkControl());
		this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeScript(inputStream);
	}
	
	public void copyImpersonation(String fromImpersonationName, String toImpersonationName) {
		Impersonation impersonation = this.getImpersonation(fromImpersonationName);
		
		// Set new impersonation name
		impersonation.setName(toImpersonationName);
		
		ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, this.getFrameworkExecution());
		String output = impersonationConfiguration.getInsertStatement();

		InputStream inputStream = FileTools.convertToInputStream(output,
				this.getFrameworkExecution().getFrameworkControl());
		this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeScript(inputStream);		
	}

	// Exists
	public boolean exists() {
		return true;
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public Impersonation getImpersonation() {
		return impersonation;
	}

	public void setImpersonation(Impersonation impersonation) {
		this.impersonation = impersonation;
	}

}