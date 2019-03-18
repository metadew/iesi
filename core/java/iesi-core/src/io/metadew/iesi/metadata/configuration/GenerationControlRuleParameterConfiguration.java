package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlRuleParameter;

public class GenerationControlRuleParameterConfiguration
{

	private GenerationControlRuleParameter generationControlRuleParameter;

	private FrameworkExecution frameworkExecution;

	// Constructors
	public GenerationControlRuleParameterConfiguration(GenerationControlRuleParameter generationControlRuleParameter,
				FrameworkExecution frameworkExecution)
	{
		this.setgenerationControlRuleParameter(generationControlRuleParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public GenerationControlRuleParameterConfiguration(FrameworkExecution frameworkExecution)
	{
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String generationName, String generationControlName, String generationControlRuleName)
	{
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration()
					.getMetadataTableConfiguration().getTableName("GenerationControlRuleParameters");
		sql += " (GEN_CTL_RULE_ID, GEN_CTL_RULE_PAR_NM, GEN_CTL_RULE_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(
					this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration()
								.getTableName("GenerationControlRules"),
					"GEN_CTL_RULE_ID",
					"where GEN_CTL_RULE_NM = '" + generationControlRuleName + "' and GEN_CTL_ID = (" + SQLTools.GetLookupIdStatement(
								this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration()
											.getMetadataTableConfiguration().getTableName("GenerationControls"),
								"GEN_CTL_ID",
								"where GEN_CTL_NM = '" + generationControlName + "' and GEN_ID = ("
											+ SQLTools.GetLookupIdStatement(
														this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration()
																	.getMetadataTableConfiguration().getTableName("Generations"),
														"GEN_ID", "GEN_NM", generationName)
											+ ")"))
					+ "))";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControlRuleParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControlRuleParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public GenerationControlRuleParameter getGenerationControlRuleParameter(long generationControlRuleId,
				String generationControlRuleParameterName)
	{
		GenerationControlRuleParameter generationControlRuleParameter = new GenerationControlRuleParameter();
		CachedRowSet crsGenerationControlRuleParameter = null;
		String queryGenerationControlRuleParameter = "select GEN_CTL_RULE_ID, GEN_CTL_RULE_PAR_NM, GEN_CTL_RULE_PAR_VAL from "
					+ this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("GenerationControlRuleParameters")
					+ " where GEN_CTL_RULE_ID = " + generationControlRuleId + " and GEN_CTL_RULE_PAR_NM = '"
					+ generationControlRuleParameterName + "'";
		crsGenerationControlRuleParameter = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration()
					.executeQuery(queryGenerationControlRuleParameter);
		try
		{
			while (crsGenerationControlRuleParameter.next())
			{
				generationControlRuleParameter.setName(generationControlRuleParameterName);
				generationControlRuleParameter.setValue(crsGenerationControlRuleParameter.getString("GEN_CTL_RULE_PAR_VAL"));
			}
			crsGenerationControlRuleParameter.close();
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return generationControlRuleParameter;
	}

	// Getters and Setters
	public GenerationControlRuleParameter getgenerationControlRuleParameter()
	{
		return generationControlRuleParameter;
	}

	public void setgenerationControlRuleParameter(GenerationControlRuleParameter generationControlRuleParameter)
	{
		this.generationControlRuleParameter = generationControlRuleParameter;
	}

	public FrameworkExecution getFrameworkExecution()
	{
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution)
	{
		this.frameworkExecution = frameworkExecution;
	}

}