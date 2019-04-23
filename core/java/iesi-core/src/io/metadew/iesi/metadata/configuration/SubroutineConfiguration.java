package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Subroutine;
import io.metadew.iesi.metadata.definition.SubroutineParameter;

public class SubroutineConfiguration {

	private FrameworkExecution frameworkExecution;
	private Subroutine subroutine;

	// Constructors
	public SubroutineConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	public SubroutineConfiguration(Subroutine subroutine, FrameworkExecution frameworkExecution) {
		this.setSubroutine(subroutine);
		this.setFrameworkExecution(frameworkExecution);
	}
	
	// Insert
	public String getInsertStatement() {
		String sql = "";

		if (this.exists()) {
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("SubroutineParameters");
			sql += " WHERE SRT_NM = " + SQLTools.GetStringForSQL(this.getSubroutine().getName());
			sql += ";";
			sql += "\n";			
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Subroutines");
			sql += " WHERE SRT_NM = " + SQLTools.GetStringForSQL(this.getSubroutine().getName());
			sql += ";";
			sql += "\n";
		}
		
		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Subroutines");
		sql += " (SRT_NM, SRT_TYP_NM, SRT_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(this.getSubroutine().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getSubroutine().getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getSubroutine().getDescription());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements();
		if (!sqlParameters.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getParameterInsertStatements() {
		String result = "";

		for (SubroutineParameter subroutineParameter : this.getSubroutine().getParameters()) {
			SubroutineParameterConfiguration subroutineParameterConfiguration = new SubroutineParameterConfiguration(
					subroutineParameter, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += subroutineParameterConfiguration.getInsertStatement(this.getSubroutine().getName());
		}

		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Subroutine getSubroutine(String subroutineName) {
		Subroutine subroutine = new Subroutine();
		CachedRowSet crsSubroutine = null;
		String querySubroutine = "select SRT_NM, SRT_TYP_NM, SRT_DSC from " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Subroutines") + " where SRT_NM = '" + subroutineName + "'";
		crsSubroutine = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(querySubroutine);
		SubroutineParameterConfiguration subroutineParameterConfiguration = new SubroutineParameterConfiguration(this.getFrameworkExecution());
		try {
			while (crsSubroutine.next()) {
				subroutine.setName(subroutineName);
				subroutine.setType(crsSubroutine.getString("SRT_TYP_NM"));
				subroutine.setDescription(crsSubroutine.getString("SRT_DSC"));
				
				// Get parameters
				CachedRowSet crsSubroutineParameters = null;
				String querySubroutineParameters = "select SRT_NM, SRT_PAR_NM, SRT_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("SubroutineParameters")
						+ " where SRT_NM = '" + subroutineName + "'";
				crsSubroutineParameters = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(querySubroutineParameters);
				List<SubroutineParameter> subroutineParameterList = new ArrayList();
				while (crsSubroutineParameters.next()) {
					subroutineParameterList
							.add(subroutineParameterConfiguration.getSubroutineParameter(subroutineName, crsSubroutineParameters.getString("SRT_PAR_NM")));
				}
				subroutine.setParameters(subroutineParameterList);
				crsSubroutineParameters.close();
			}
			crsSubroutine.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return subroutine;
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

	public Subroutine getSubroutine() {
		return subroutine;
	}

	public void setSubroutine(Subroutine subroutine) {
		this.subroutine = subroutine;
	}
}