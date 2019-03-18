package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.SubroutineParameter;

public class SubroutineParameterConfiguration {

	private FrameworkExecution frameworkExecution;
	private SubroutineParameter subroutineParameter;

	// Constructors
	public SubroutineParameterConfiguration(SubroutineParameter subroutineParameter, FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setSubroutineParameter(subroutineParameter);
	}

	public SubroutineParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	// Insert
	public String getInsertStatement(String subroutineName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("SubroutineParameters");
		sql += " (SRT_NM, SRT_PAR_NM, SRT_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(subroutineName);
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getSubroutineParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getSubroutineParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}
	
	public SubroutineParameter getSubroutineParameter(String subroutineName, String subroutineParameterName) {
		SubroutineParameter subroutineParameter = new SubroutineParameter();
		CachedRowSet crsSubroutineParameter = null;
		String querySubroutineParameter = "select SRT_NM, SRT_PAR_NM, SRT_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("SubroutineParameters")
				+ " where SRT_NM = '" + subroutineName + "' and SRT_PAR_NM = '" + subroutineParameterName + "'";
		crsSubroutineParameter = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(querySubroutineParameter);
		try {
			while (crsSubroutineParameter.next()) {
				subroutineParameter.setName(subroutineParameterName);
				subroutineParameter.setValue(crsSubroutineParameter.getString("SRT_PAR_VAL"));
			}
			crsSubroutineParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return subroutineParameter;
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public SubroutineParameter getSubroutineParameter() {
		return subroutineParameter;
	}

	public void setSubroutineParameter(SubroutineParameter subroutineParameter) {
		this.subroutineParameter = subroutineParameter;
	}

}