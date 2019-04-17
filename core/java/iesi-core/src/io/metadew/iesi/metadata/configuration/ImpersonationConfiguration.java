package io.metadew.iesi.metadata.configuration;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationDoesNotExistException;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.ListObject;
import org.apache.logging.log4j.Level;

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<Impersonation> getImpersonation(String impersonationName) {
		// TODO: Make it return an Optional. value if impersonation can be found, else return an empty
		Impersonation impersonation = null;
		String queryImpersonation = "select IMP_NM, IMP_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations")
				+ " where IMP_NM = '" + impersonationName + "'";
		CachedRowSet crsImpersonation = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(queryImpersonation);
		try {
			while (crsImpersonation.next()) {
				String description = crsImpersonation.getString("IMP_DSC");

				// Get parameters
				String queryImpersonationParameters = "select IMP_NM, CONN_NM from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ImpersonationParameters")
						+ " where IMP_NM = '" + impersonationName + "'";
				CachedRowSet crsImpersonationParameters = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.executeQuery(queryImpersonationParameters);
				List<ImpersonationParameter> impersonationParameterList = new ArrayList();
				while (crsImpersonationParameters.next()) {
					impersonationParameterList.add(new ImpersonationParameterConfiguration(this.getFrameworkExecution())
							.getImpersonationParameter(impersonationName, crsImpersonationParameters.getString("CONN_NM")));
				}
				crsImpersonationParameters.close();
				impersonation = new Impersonation(impersonationName, description, impersonationParameterList);
			}
			crsImpersonation.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.ofNullable(impersonation);
	}

	public List<Impersonation> getAllImpersonations() {
		frameworkExecution.getFrameworkLog().log("Getting all impersonations {0}.", Level.TRACE);
		List<Impersonation> impersonations = new ArrayList<>();
		String query = "select IMP_NM from " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations")
				+ " order by IMP_NM ASC";
		CachedRowSet crs = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(query);
		try {
			while (crs.next()) {
				String impersonationName = crs.getString("IMP_NM");
				getImpersonation(impersonationName).ifPresent(impersonations::add);
			}
			crs.close();
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return impersonations;
	}

	public boolean exists(Impersonation impersonation) {
		String queryImpersonation = "select * from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations")
				+ " where IMP_NM = '"
				+ impersonation.getName() + "'";

		CachedRowSet crsEnvironment = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(queryImpersonation);
		return crsEnvironment.size() == 1;
	}

	public void deleteImpersonation(Impersonation impersonation) throws ImpersonationDoesNotExistException {
		frameworkExecution.getFrameworkLog().log(MessageFormat.format(
				"Deleting impersonation {0}.", impersonation.getName()), Level.TRACE);
		if (!exists(impersonation)) {
			throw new ImpersonationDoesNotExistException(
					MessageFormat.format("Impersonation {0} is not present in the repository so cannot be updated",
							impersonation.getName()));

		}
		String query = getDeleteStatement(impersonation);
		this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeUpdate(query);
	}

	public void deleteAllEnvironments() {
		frameworkExecution.getFrameworkLog().log("Deleting all impersonations", Level.TRACE);
		String query = getDeleteAllStatement();
		this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeUpdate(query);
	}

	private String getDeleteAllStatement() {
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations");
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ImpersonationParameters");
		sql += ";";
		sql += "\n";

		return sql;
	}

	public void insertImpersonation(Impersonation impersonation) throws ImpersonationAlreadyExistsException {
		frameworkExecution.getFrameworkLog().log(MessageFormat.format(
				"Inserting impersonation {0}.", impersonation.getName()), Level.TRACE);
		if (exists(impersonation)) {
			throw new ImpersonationAlreadyExistsException(MessageFormat.format("Impersonation {0} already exists",impersonation.getName()));
		}
		String query = getInsertStatement(impersonation);
		this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeUpdate(query);
	}

	public void updateImpersonation(Impersonation impersonation) throws ImpersonationDoesNotExistException {
		frameworkExecution.getFrameworkLog().log(MessageFormat.format(
				"Updating impersonation {0}.", impersonation.getName()), Level.TRACE);
		try {
			deleteImpersonation(impersonation);
			insertImpersonation(impersonation);
		} catch (ImpersonationDoesNotExistException e) {
			frameworkExecution.getFrameworkLog().log(MessageFormat.format(
					"Impersonation {0} is not present in the repository so cannot be updated", impersonation.getName()),
					Level.TRACE);
			throw new ImpersonationDoesNotExistException(MessageFormat.format(
					"Impersonation {0} is not present in the repository so cannot be updated", impersonation.getName()));

		} catch (ImpersonationAlreadyExistsException e) {
			frameworkExecution.getFrameworkLog().log(MessageFormat.format(
					"Environment {0} is not deleted correctly during update. {1}", impersonation.getName(), e.toString()),
					Level.WARN);
		}
	}

	public String getDeleteStatement(Impersonation impersonation) {
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations");
		sql += " WHERE IMP_NM = "
				+ SQLTools.GetStringForSQL(impersonation.getName());
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ImpersonationParameters");
		sql += " WHERE IMP_NM = "
				+ SQLTools.GetStringForSQL(impersonation.getName());
		sql += ";";
		sql += "\n";

		return sql;
	}

	public String getInsertStatement(Impersonation impersonation) {
		String sql = "";

		if (this.exists()) {
			sql += this.getDeleteStatement();
		}

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Impersonations");
		sql += " (IMP_NM, IMP_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(impersonation.getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(impersonation.getDescription());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements(impersonation);
		if (!sqlParameters.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getParameterInsertStatements(Impersonation impersonation) {
		String result = "";

		// Catch null parameters
		if (this.getImpersonation().getParameters() == null)
			return result;

		for (ImpersonationParameter impersonationParameter : impersonation.getParameters()) {
			ImpersonationParameterConfiguration impersonationParameterConfiguration = new ImpersonationParameterConfiguration(
					this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += impersonationParameterConfiguration.getInsertStatement(impersonation.getName(), impersonationParameter);
		}

		return result;
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
		if (!sqlParameters.equalsIgnoreCase("")) {
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
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += impersonationParameterConfiguration.getInsertStatement(impersonationName);
		}

		return result;
	}
	// GEt Impersonation

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
				impersonationConfiguration.getImpersonation(impersonationName).ifPresent(impersonationList::add);
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
		this.getImpersonation(impersonationName).ifPresent(impersonation -> {
			ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, this.getFrameworkExecution());
			String output = impersonationConfiguration.getDeleteStatement();

			InputStream inputStream = FileTools
					.convertToInputStream(output, this.getFrameworkExecution().getFrameworkControl());
			this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeScript(inputStream);
		});

	}
	
	public void copyImpersonation(String fromImpersonationName, String toImpersonationName) {
		// TODO: check optionallity of impersonation
		Impersonation impersonation = this.getImpersonation(fromImpersonationName).get();
		
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