package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlRule;
import io.metadew.iesi.metadata.definition.GenerationControlRuleParameter;

public class GenerationControlRuleConfiguration {

	private GenerationControlRule generationControlRule;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public GenerationControlRuleConfiguration(GenerationControlRule generationControlRule, FrameworkExecution frameworkExecution) {
		this.setgenerationControlRule(generationControlRule);
		this.setFrameworkExecution(frameworkExecution);
	}

	public GenerationControlRuleConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String generationName, String generationControlName, int generationControlRuleNumber) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControlRules");
		sql += " (GEN_CTL_ID, GEN_CTL_RULE_ID, GEN_CTL_RULE_NB, GEN_CTL_RULE_TYP_NM, GEN_CTL_RULE_NM, GEN_CTL_RULE_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControls"), "GEN_CTL_ID", "where GEN_CTL_NM = '" + generationControlName + "' and GEN_ID = (" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Generations"),"GEN_ID", "GEN_NM",generationName)  +")") + ")";
		sql += ",";
		sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControlRules"), "GEN_CTL_RULE_ID") + ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(generationControlRuleNumber);
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControlRule().getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControlRule().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControlRule().getDescription());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements(generationName, generationControlName);
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getParameterInsertStatements(String generationName, String generationControlName) {
		String result = "";

		for (GenerationControlRuleParameter generationControlRuleParameter : this.getgenerationControlRule().getParameters()) {
			GenerationControlRuleParameterConfiguration generationControlRuleParameterConfiguration = new GenerationControlRuleParameterConfiguration(generationControlRuleParameter, this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += generationControlRuleParameterConfiguration.getInsertStatement(generationName, generationControlName, this.getgenerationControlRule().getName());
		}

		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GenerationControlRule getGenerationControlRule(long generationControlRuleId) {
		GenerationControlRule generationControlRule = new GenerationControlRule();
		CachedRowSet crsGenerationControlRule = null;
		String queryGenerationControlRule = "select GEN_CTL_ID, GEN_CTL_RULE_ID, GEN_CTL_RULE_NB, GEN_CTL_RULE_TYP_NM, GEN_CTL_RULE_NM, GEN_CTL_RULE_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControlRules") + " where GEN_CTL_RULE_ID = " + generationControlRuleId;
		crsGenerationControlRule = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryGenerationControlRule);
		GenerationControlRuleParameterConfiguration generationControlRuleParameterConfiguration = new GenerationControlRuleParameterConfiguration(this.getFrameworkExecution());
		try {
			while (crsGenerationControlRule.next()) {
				generationControlRule.setId(generationControlRuleId);
				generationControlRule.setNumber(crsGenerationControlRule.getLong("GEN_CTL_RULE_NB"));
				generationControlRule.setType(crsGenerationControlRule.getString("GEN_CTL_RULE_TYP_NM"));
				generationControlRule.setName(crsGenerationControlRule.getString("GEN_CTL_RULE_NM"));
				generationControlRule.setDescription(crsGenerationControlRule.getString("GEN_CTL_RULE_DSC"));
				
				// Get parameters
				CachedRowSet crsGenerationControlRuleParameters = null;
				String queryGenerationControlRuleParameters = "select GEN_CTL_RULE_ID, GEN_CTL_RULE_PAR_NM from " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControlRuleParameters")
						+ " where GEN_CTL_RULE_ID = " + generationControlRuleId;
				crsGenerationControlRuleParameters = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryGenerationControlRuleParameters);
				List<GenerationControlRuleParameter> generationControlRuleParameterList = new ArrayList();
				while (crsGenerationControlRuleParameters.next()) {
					generationControlRuleParameterList
							.add(generationControlRuleParameterConfiguration.getGenerationControlRuleParameter(generationControlRuleId, crsGenerationControlRuleParameters.getString("GEN_CTL_RULE_PAR_NM")));
				}
				generationControlRule.setParameters(generationControlRuleParameterList);
				crsGenerationControlRuleParameters.close();

			}
			crsGenerationControlRule.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return generationControlRule;
	}

	// Getters and Setters
	public GenerationControlRule getgenerationControlRule() {
		return generationControlRule;
	}

	public void setgenerationControlRule(GenerationControlRule generationControlRule) {
		this.generationControlRule = generationControlRule;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
	
}