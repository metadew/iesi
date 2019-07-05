package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationRule;
import io.metadew.iesi.metadata.definition.GenerationRuleParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerationRuleConfiguration {

    private GenerationRule generationRule;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public GenerationRuleConfiguration(GenerationRule generationRule, FrameworkInstance frameworkInstance) {
        this.setgenerationRule(generationRule);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationRuleConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String generationName, int generationRuleNumber) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRules");
        sql += " (GEN_ID, GEN_RULE_ID, GEN_RULE_NB, GEN_RULE_TYP_NM, FIELD_NM, GEN_RULE_DSC, BLANK_INJ_FL, BLANK_INJ_UNIT, BLANK_INJ_MEAS, BLANK_INJ_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID", "GEN_NM", generationName) + ")";
        sql += ",";
        sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRules"), "GEN_RULE_ID") + ")";
        sql += ",";
        sql += SQLTools.GetStringForSQL(generationRuleNumber);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationRule().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationRule().getField());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationRule().getDescription());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationRule().getBlankInjectionFlag());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationRule().getBlankInjectionUnit());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationRule().getBlankInjectionMeasure());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationRule().getBlankInjectionValue());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(generationName);
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(String generationName) {
        String result = "";

        for (GenerationRuleParameter generationRuleParameter : this.getgenerationRule().getParameters()) {
            GenerationRuleParameterConfiguration generationRuleParameterConfiguration = new GenerationRuleParameterConfiguration(generationRuleParameter, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += generationRuleParameterConfiguration.getInsertStatement(generationName, this.getgenerationRule().getField());
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GenerationRule getGenerationRule(long generationRuleId) {
        GenerationRule generationRule = new GenerationRule();
        CachedRowSet crsGenerationRule = null;
        String queryGenerationRule = "select GEN_ID, GEN_RULE_ID, GEN_RULE_NB, GEN_RULE_TYP_NM, FIELD_NM, GEN_RULE_DSC, BLANK_INJ_FL, BLANK_INJ_UNIT, BLANK_INJ_MEAS, BLANK_INJ_VAL from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRules") + " where GEN_RULE_ID = " + generationRuleId;
        crsGenerationRule = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationRule, "reader");
        GenerationRuleParameterConfiguration generationRuleParameterConfiguration = new GenerationRuleParameterConfiguration(this.getFrameworkInstance());
        try {
            while (crsGenerationRule.next()) {
                generationRule.setId(generationRuleId);
                generationRule.setNumber(crsGenerationRule.getLong("GEN_RULE_NB"));
                generationRule.setType(crsGenerationRule.getString("GEN_RULE_TYP_NM"));
                generationRule.setField(crsGenerationRule.getString("FIELD_NM"));
                generationRule.setDescription(crsGenerationRule.getString("GEN_RULE_DSC"));
                generationRule.setBlankInjectionFlag(crsGenerationRule.getString("BLANK_INJ_FL"));
                generationRule.setBlankInjectionUnit(crsGenerationRule.getString("BLANK_INJ_UNIT"));
                generationRule.setBlankInjectionMeasure(crsGenerationRule.getLong("BLANK_INJ_MEAS"));
                generationRule.setBlankInjectionValue(crsGenerationRule.getString("BLANK_INJ_VAL"));

                // Get parameters
                CachedRowSet crsGenerationRuleParameters = null;
                String queryGenerationRuleParameters = "select GEN_RULE_ID, GEN_RULE_PAR_NM from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationRuleParameters")
                        + " where GEN_RULE_ID = " + generationRuleId;
                crsGenerationRuleParameters = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationRuleParameters, "reader");
                List<GenerationRuleParameter> generationRuleParameterList = new ArrayList();
                while (crsGenerationRuleParameters.next()) {
                    generationRuleParameterList
                            .add(generationRuleParameterConfiguration.getGenerationRuleParameter(generationRuleId, crsGenerationRuleParameters.getString("GEN_RULE_PAR_NM")));
                }
                generationRule.setParameters(generationRuleParameterList);
                crsGenerationRuleParameters.close();

            }
            crsGenerationRule.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return generationRule;
    }

    // Getters and Setters
    public GenerationRule getgenerationRule() {
        return generationRule;
    }

    public void setgenerationRule(GenerationRule generationRule) {
        this.generationRule = generationRule;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}