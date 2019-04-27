package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationRuleParameter;

public class GenerationRuleParameterConfiguration {

	private GenerationRuleParameter generationRuleParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public GenerationRuleParameterConfiguration(GenerationRuleParameter generationRuleParameter, FrameworkExecution frameworkExecution) {
		this.setgenerationRuleParameter(generationRuleParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public GenerationRuleParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String generationName, String generationField) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRuleParameters");
		sql += " (GEN_RULE_ID, GEN_RULE_PAR_NM, GEN_RULE_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRules"), "GEN_RULE_ID", "where FIELD_NM = '"+ generationField + "' and GEN_ID = (" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID", "GEN_NM", generationName)) + "))";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationRuleParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationRuleParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public GenerationRuleParameter getGenerationRuleParameter(long generationRuleId, String generationRuleParameterName) {
		GenerationRuleParameter generationRuleParameter = new GenerationRuleParameter();
		CachedRowSet crsGenerationRuleParameter = null;
		String queryGenerationRuleParameter = "select GEN_RULE_ID, GEN_RULE_PAR_NM, GEN_RULE_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRuleParameters")
				+ " where GEN_RULE_ID = " + generationRuleId + " and GEN_RULE_PAR_NM = '" + generationRuleParameterName + "'";
		crsGenerationRuleParameter = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationRuleParameter, "reader");
		try {
			while (crsGenerationRuleParameter.next()) {
				generationRuleParameter.setName(generationRuleParameterName);
				generationRuleParameter.setValue(crsGenerationRuleParameter.getString("GEN_RULE_PAR_VAL"));
			}
			crsGenerationRuleParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return generationRuleParameter;
	}

	// Getters and Setters
	public GenerationRuleParameter getgenerationRuleParameter() {
		return generationRuleParameter;
	}

	public void setgenerationRuleParameter(GenerationRuleParameter generationRuleParameter) {
		this.generationRuleParameter = generationRuleParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}