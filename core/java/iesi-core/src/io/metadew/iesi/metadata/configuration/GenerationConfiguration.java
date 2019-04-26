package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Generation;
import io.metadew.iesi.metadata.definition.GenerationControl;
import io.metadew.iesi.metadata.definition.GenerationOutput;
import io.metadew.iesi.metadata.definition.GenerationParameter;
import io.metadew.iesi.metadata.definition.GenerationRule;
import io.metadew.iesi.metadata.definition.ListObject;

public class GenerationConfiguration {

	private Generation generation;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public GenerationConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	public GenerationConfiguration(Generation generation, FrameworkExecution frameworkExecution) {
		this.setGeneration(generation);
		this.setFrameworkExecution(frameworkExecution);
	}
	
	// Insert
	public String getInsertStatement() {
		String sql = "";
		
		if (this.exists()) {
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputParameters");
			sql += " WHERE GEN_OUT_ID in (";
			sql += "select GEN_OUT_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs");
			sql += " WHERE GEN_ID = (";
			sql += "select GEN_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ")";
			sql += ")";
			sql += ";";
			sql += "\n";
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs");
			sql += " WHERE GEN_ID = (";
			sql += "select GEN_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ")";
			sql += ";";
			sql += "\n";			
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRuleParameters");
			sql += " WHERE GEN_CTL_RULE_ID in (";
			sql += "select GEN_CTL_RULE_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRules");
			sql += " WHERE GEN_CTL_ID in (";
			sql += "select GEN_CTL_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControls");
			sql += " WHERE GEN_ID in (";
			sql += "select GEN_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ")";
			sql += ")";
			sql += ")";
			sql += ";";
			sql += "\n";
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRules");
			sql += " WHERE GEN_CTL_RULE_ID in (";
			sql += "select GEN_CTL_RULE_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRules");
			sql += " WHERE GEN_CTL_ID in (";
			sql += "select GEN_CTL_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControls");
			sql += " WHERE GEN_ID in (";
			sql += "select GEN_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ")";
			sql += ")";
			sql += ")";
			sql += ";";
			sql += "\n";			
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlParameters");
			sql += " WHERE GEN_CTL_ID in (";
			sql += "select GEN_CTL_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControls");
			sql += " WHERE GEN_ID in (";
			sql += "select GEN_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ")";
			sql += ")";
			sql += ";";
			sql += "\n";
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControls");
			sql += " WHERE GEN_ID in (";
			sql += "select GEN_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ")";
			sql += ";";
			sql += "\n";			
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRuleParameters");
			sql += " WHERE GEN_RULE_ID in (";
			sql += "select GEN_RULE_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRules");
			sql += " WHERE GEN_ID in (";
			sql += "select GEN_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ")";
			sql += ")";
			sql += ";";
			sql += "\n";
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRules");
			sql += " WHERE GEN_ID in (";
			sql += "select GEN_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ")";
			sql += ";";
			sql += "\n";			
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationParameters");
			sql += " WHERE GEN_ID in (";
			sql += "select GEN_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ")";
			sql += ";";
			sql += "\n";			
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
			sql += " WHERE GEN_NM = " + SQLTools.GetStringForSQL(this.getGeneration().getName());
			sql += ";";
			sql += "\n";
		}
	
		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations");
		sql += " (GEN_ID, GEN_TYP_NM, GEN_NM, GEN_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID") + ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getGeneration().getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getGeneration().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getGeneration().getDescription());
		sql += ")";
		sql += ";";

		// add Rules
		String sqlRules = this.getGenerationRuleInsertStatements();
		if (!sqlRules.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlRules;
		}

		// add Outputs
		String sqlOutputs = this.getGenerationOutputInsertStatements();
		if (!sqlOutputs.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlOutputs;
		}

		// add Controls
		String sqlControls = this.getGenerationControlInsertStatements();
		if (!sqlControls.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlControls;
		}
		
		// add Parameters
		String sqlParameters = this.getParameterInsertStatements();
		if (!sqlParameters.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getGenerationRuleInsertStatements() {
		String result = "";
		int counter = 0;
		
		if (this.getGeneration().getRules() == null) return result;
		
		for (GenerationRule generationRule : this.getGeneration().getRules()) {
			counter++;
			GenerationRuleConfiguration generationRuleConfiguration = new GenerationRuleConfiguration(generationRule, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";		
			result += generationRuleConfiguration.getInsertStatement(this.getGeneration().getName(), counter);
		}

		return result;
	}
	
	private String getGenerationOutputInsertStatements() {
		String result = "";
		
		if (this.getGeneration().getOutputs() == null) return result;
		
		for (GenerationOutput generationOutput : this.getGeneration().getOutputs()) {
			GenerationOutputConfiguration generationOutputConfiguration = new GenerationOutputConfiguration(generationOutput, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";		
			result += generationOutputConfiguration.getInsertStatement(this.getGeneration().getName());
		}

		return result;
	}

	private String getGenerationControlInsertStatements() {
		String result = "";
		
		if (this.getGeneration().getControls() == null) return result;
		
		for (GenerationControl generationControl : this.getGeneration().getControls()) {
			GenerationControlConfiguration generationControlConfiguration = new GenerationControlConfiguration(generationControl, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";		
			result += generationControlConfiguration.getInsertStatement(this.getGeneration().getName());
		}

		return result;
	}
	
	private String getParameterInsertStatements() {
		String result = "";

		if (this.getGeneration().getParameters() == null) return result;
		
		for (GenerationParameter generationParameter : this.getGeneration().getParameters()) {
			GenerationParameterConfiguration generationParameterConfiguration = new GenerationParameterConfiguration(generationParameter, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += generationParameterConfiguration.getInsertStatement(this.getGeneration().getName());
		}

		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Generation getGeneration(String generationName) {
		Generation generation = new Generation();
		CachedRowSet crsGeneration = null;
		String queryGeneration = "select GEN_ID, GEN_TYP_NM, GEN_NM, GEN_DSC from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations") + " where GEN_NM = '" + generationName +"'";
		crsGeneration = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGeneration, "reader");
		GenerationRuleConfiguration generationRuleConfiguration = new GenerationRuleConfiguration(this.getFrameworkExecution());
		GenerationOutputConfiguration generationOutputConfiguration = new GenerationOutputConfiguration(this.getFrameworkExecution());
		GenerationControlConfiguration generationControlConfiguration = new GenerationControlConfiguration(this.getFrameworkExecution());
		GenerationParameterConfiguration generationParameterConfiguration = new GenerationParameterConfiguration(this.getFrameworkExecution());
		try {
			while (crsGeneration.next()) {
				generation.setId(crsGeneration.getLong("GEN_ID"));
				generation.setType(crsGeneration.getString("GEN_TYP_NM"));
				generation.setName(generationName);
				generation.setDescription(crsGeneration.getString("GEN_DSC"));
				
				// Get the generationRules
				List<GenerationRule> generationRuleList = new ArrayList();
				String queryRules = "select GEN_ID, GEN_RULE_ID, GEN_RULE_NB from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRules")
						+ " where GEN_ID = " + generation.getId() + " order by GEN_RULE_NB asc ";
				CachedRowSet crsRules = null;
				crsRules = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryRules, "reader");
				while (crsRules.next()) {
					generationRuleList.add(generationRuleConfiguration.getGenerationRule(crsRules.getLong("GEN_RULE_ID")));
				}
				generation.setRules(generationRuleList);
				crsRules.close();
				
				// Get the generationOutputs
				List<GenerationOutput> generationOutputList = new ArrayList();
				String queryOutputs = "select GEN_ID, GEN_OUT_ID from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs")
						+ " where GEN_ID = " + generation.getId() + " order by GEN_OUT_ID asc ";
				CachedRowSet crsOutputs = null;
				crsOutputs = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryOutputs, "reader");
				while (crsOutputs.next()) {
					generationOutputList.add(generationOutputConfiguration.getGenerationOutput(crsOutputs.getLong("GEN_OUT_ID")));
				}
				generation.setOutputs(generationOutputList);
				crsOutputs.close();

				// Get the generationControls
				List<GenerationControl> generationControlList = new ArrayList();
				String queryControls = "select GEN_ID, GEN_CTL_ID from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControls")
						+ " where GEN_ID = " + generation.getId() + " order by GEN_CTL_ID asc ";
				CachedRowSet crsControls = null;
				crsControls = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryControls, "reader");
				while (crsControls.next()) {
					generationControlList.add(generationControlConfiguration.getGenerationControl(crsControls.getLong("GEN_CTL_ID")));
				}
				generation.setControls(generationControlList);
				crsControls.close();

				// Get parameters
				CachedRowSet crsGenerationParameters = null;
				String queryGenerationParameters = "select GEN_ID, GEN_PAR_NM from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationParameters")
						+ " where GEN_ID = " + generation.getId();
				crsGenerationParameters = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationParameters, "reader");
				List<GenerationParameter> generationParameterList = new ArrayList();
				while (crsGenerationParameters.next()) {
					generationParameterList
							.add(generationParameterConfiguration.getGenerationParameter(generation.getId(), crsGenerationParameters.getString("GEN_PAR_NM")));
				}
				generation.setParameters(generationParameterList);
				crsGenerationParameters.close();

			}
			crsGeneration.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		
		if (generation.getName() == null || generation.getName().equalsIgnoreCase("")) {
			throw new RuntimeException("Generation (NAME) " + generationName + " does not exist");
		}
		
		return generation;
	}
	
	// Get
	public ListObject getGenerations() {
		List<Generation> generationList = new ArrayList<>();
		CachedRowSet crs = null;
		String query = "select GEN_NM, GEN_DSC from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations")
				+ " order by GEN_NM ASC";
		crs = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(query, "reader");
		GenerationConfiguration generationConfiguration = new GenerationConfiguration(this.getFrameworkExecution());
		try {
			String generationName = "";
			while (crs.next()) {
				generationName = crs.getString("GEN_NM");
				generationList.add(generationConfiguration.getGeneration(generationName));
			}
			crs.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return new ListObject(FrameworkObjectConfiguration.getFrameworkObjectType(new Generation()), generationList);
	}

	// Exists
	public boolean exists() {
		return true;
	}

	// Getters and Setters
	public Generation getGeneration() {
		return generation;
	}

	public void setGeneration(Generation generation) {
		this.generation = generation;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}


}