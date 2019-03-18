package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControl;
import io.metadew.iesi.metadata.definition.GenerationControlParameter;
import io.metadew.iesi.metadata.definition.GenerationControlRule;

public class GenerationControlConfiguration {

	private GenerationControl generationControl;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public GenerationControlConfiguration(GenerationControl generationControl, FrameworkExecution frameworkExecution) {
		this.setgenerationControl(generationControl);
		this.setFrameworkExecution(frameworkExecution);
	}

	public GenerationControlConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String generationName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControls");
		sql += " (GEN_ID, GEN_CTL_ID, GEN_CTL_NM, GEN_CTL_TYP_NM, GEN_CTL_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Generations"), "GEN_ID", "GEN_NM", generationName) + ")";
		sql += ",";
		sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControls"), "GEN_CTL_ID") + ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControl().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControl().getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getgenerationControl().getDescription());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements(generationName);
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}
		
		// add rules
		String sqlRules = this.getRuleInsertStatements(generationName, this.getgenerationControl().getName());
		if (!sqlRules.equals("")) {
			sql += "\n";
			sql += sqlRules;
		}
		

		return sql;
	}

	private String getParameterInsertStatements(String generationName) {
		String result = "";
		
		if (this.getgenerationControl().getParameters() == null) return result;

		for (GenerationControlParameter generationControlParameter : this.getgenerationControl().getParameters()) {
			GenerationControlParameterConfiguration generationControlParameterConfiguration = new GenerationControlParameterConfiguration(generationControlParameter, this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += generationControlParameterConfiguration.getInsertStatement(generationName, this.getgenerationControl().getName());
		}

		return result;
	}

	private String getRuleInsertStatements(String generationName, String generationControlName) {
		String result = "";
		int counter = 0;
		
		if (this.getgenerationControl().getRules() == null) return result;

		for (GenerationControlRule generationControlRule : this.getgenerationControl().getRules()) {
			counter++;
			GenerationControlRuleConfiguration generationControlRuleConfiguration = new GenerationControlRuleConfiguration(generationControlRule, this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += generationControlRuleConfiguration.getInsertStatement(generationName, generationControlName, counter);
		}

		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GenerationControl getGenerationControl(long generationControlId) {
		GenerationControl generationControl = new GenerationControl();
		CachedRowSet crsGenerationControl = null;
		String queryGenerationControl = "select GEN_ID, GEN_CTL_ID, GEN_CTL_NM, GEN_CTL_TYP_NM, GEN_CTL_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControls") + " where GEN_CTL_ID = " + generationControlId;
		crsGenerationControl = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryGenerationControl);
		GenerationControlParameterConfiguration generationControlParameterConfiguration = new GenerationControlParameterConfiguration(this.getFrameworkExecution());
		GenerationControlRuleConfiguration generationControlRuleConfiguration = new GenerationControlRuleConfiguration(this.getFrameworkExecution());
		try {
			while (crsGenerationControl.next()) {
				generationControl.setId(generationControlId);
				generationControl.setName(crsGenerationControl.getString("GEN_CTL_NM"));
				generationControl.setType(crsGenerationControl.getString("GEN_CTL_TYP_NM"));
				generationControl.setDescription(crsGenerationControl.getString("GEN_CTL_DSC"));
				
				// Get parameters
				CachedRowSet crsGenerationControlParameters = null;
				String queryGenerationControlParameters = "select GEN_CTL_ID, GEN_CTL_PAR_NM from " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControlParameters")
						+ " where GEN_CTL_ID = " + generationControlId;
				crsGenerationControlParameters = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryGenerationControlParameters);
				List<GenerationControlParameter> generationControlParameterList = new ArrayList();
				while (crsGenerationControlParameters.next()) {
					generationControlParameterList
							.add(generationControlParameterConfiguration.getGenerationControlParameter(generationControlId, crsGenerationControlParameters.getString("GEN_CTL_PAR_NM")));
				}
				generationControl.setParameters(generationControlParameterList);
				crsGenerationControlParameters.close();

				// Get rules
				CachedRowSet crsGenerationControlRules = null;
				String queryGenerationControlRules = "select GEN_CTL_RULE_ID, GEN_CTL_RULE_NM from " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("GenerationControlRules")
						+ " where GEN_CTL_ID = " + generationControlId;
				crsGenerationControlRules = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryGenerationControlRules);
				List<GenerationControlRule> generationControlRuleList = new ArrayList();
				while (crsGenerationControlRules.next()) {
					generationControlRuleList
							.add(generationControlRuleConfiguration.getGenerationControlRule(crsGenerationControlRules.getLong("GEN_CTL_RULE_ID")));
				}
				generationControl.setRules(generationControlRuleList);
				crsGenerationControlRules.close();
			}
			crsGenerationControl.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return generationControl;
	}

	// Getters and Setters
	public GenerationControl getgenerationControl() {
		return generationControl;
	}

	public void setgenerationControl(GenerationControl generationControl) {
		this.generationControl = generationControl;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
	
}