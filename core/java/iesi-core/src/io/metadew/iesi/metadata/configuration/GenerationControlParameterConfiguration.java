package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlParameter;

public class GenerationControlParameterConfiguration {

	private FrameworkExecution frameworkExecution;
	private GenerationControlParameter generationControlParameter;

	// Constructors
	public GenerationControlParameterConfiguration(GenerationControlParameter generationControlParameter, FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setgenerationControlParameter(generationControlParameter);
	}

	public GenerationControlParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	// Insert
	public String getInsertStatement(String generationName, String generationControlName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControlParameters");
		sql += " (GEN_CTL_ID, GEN_CTL_PAR_NM, GEN_CTL_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControls"), "GEN_CTL_ID", "where GEN_CTL_NM = '"+ generationControlName + "' and GEN_ID = (" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Generations"), "GEN_ID", "GEN_NM", generationName)) + "))";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControlParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControlParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}
	
	public GenerationControlParameter getGenerationControlParameter(long generationControlId,String generationControlParameterName) {
		GenerationControlParameter generationControlParameter = new GenerationControlParameter();
		CachedRowSet crsGenerationControlParameter = null;
		String queryGenerationControlParameter = "select GEN_CTL_ID, GEN_CTL_PAR_NM, GEN_CTL_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControlParameters")
				+ " where GEN_CTL_ID = " + generationControlId + " and GEN_CTL_PAR_NM = '" + generationControlParameterName + "'";
		crsGenerationControlParameter = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryGenerationControlParameter);
		try {
			while (crsGenerationControlParameter.next()) {
				generationControlParameter.setName(generationControlParameterName);
				generationControlParameter.setValue(crsGenerationControlParameter.getString("GEN_CTL_PAR_VAL"));
			}
			crsGenerationControlParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return generationControlParameter;
	}

	// Getters and Setters
	public GenerationControlParameter getgenerationControlParameter() {
		return generationControlParameter;
	}

	public void setgenerationControlParameter(GenerationControlParameter generationControlParameter) {
		this.generationControlParameter = generationControlParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}