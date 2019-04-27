package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationParameter;

public class GenerationParameterConfiguration {

	private GenerationParameter generationParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public GenerationParameterConfiguration(GenerationParameter generationParameter, FrameworkExecution frameworkExecution) {
		this.setgenerationParameter(generationParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public GenerationParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String generationName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationParameters");
		sql += " (GEN_ID, GEN_PAR_NM, GEN_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID", "where GEN_NM = '"+ generationName) + "')";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public GenerationParameter getGenerationParameter(long generationId, String generationParameterName) {
		GenerationParameter generationParameter = new GenerationParameter();
		CachedRowSet crsGenerationParameter = null;
		String queryGenerationParameter = "select GEN_ID, GEN_PAR_NM, GEN_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationParameters")
				+ " where GEN_ID = " + generationId + " and GEN_PAR_NM = '" + generationParameterName + "'";
		crsGenerationParameter = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationParameter, "reader");
		try {
			while (crsGenerationParameter.next()) {
				generationParameter.setName(generationParameterName);
				generationParameter.setValue(crsGenerationParameter.getString("GEN_PAR_VAL"));
			}
			crsGenerationParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return generationParameter;
	}

	// Getters and Setters
	public GenerationParameter getgenerationParameter() {
		return generationParameter;
	}

	public void setgenerationParameter(GenerationParameter generationParameter) {
		this.generationParameter = generationParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}