package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.GenerationControlRule;
import io.metadew.iesi.metadata.definition.GenerationControlRuleParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerationControlRuleConfiguration {

    private GenerationControlRule generationControlRule;

    // Constructors
    public GenerationControlRuleConfiguration(GenerationControlRule generationControlRule) {
        this.setgenerationControlRule(generationControlRule);
    }

    public GenerationControlRuleConfiguration() {
    }

    // Insert
    public String getInsertStatement(String generationName, String generationControlName, int generationControlRuleNumber) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRules");
        sql += " (GEN_CTL_ID, GEN_CTL_RULE_ID, GEN_CTL_RULE_NB, GEN_CTL_RULE_TYP_NM, GEN_CTL_RULE_NM, GEN_CTL_RULE_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationControls"), "GEN_CTL_ID", "where GEN_CTL_NM = '" + generationControlName + "' and GEN_ID = (" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID", "GEN_NM", generationName) + ")") + ")";
        sql += ",";
        sql += "(" + SQLTools.GetNextIdStatement(MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRules"), "GEN_CTL_RULE_ID") + ")";
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
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(String generationName, String generationControlName) {
        String result = "";

        for (GenerationControlRuleParameter generationControlRuleParameter : this.getgenerationControlRule().getParameters()) {
            GenerationControlRuleParameterConfiguration generationControlRuleParameterConfiguration = new GenerationControlRuleParameterConfiguration(generationControlRuleParameter);
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += generationControlRuleParameterConfiguration.getInsertStatement(generationName, generationControlName, this.getgenerationControlRule().getName());
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GenerationControlRule getGenerationControlRule(long generationControlRuleId) {
        GenerationControlRule generationControlRule = new GenerationControlRule();
        CachedRowSet crsGenerationControlRule = null;
        String queryGenerationControlRule = "select GEN_CTL_ID, GEN_CTL_RULE_ID, GEN_CTL_RULE_NB, GEN_CTL_RULE_TYP_NM, GEN_CTL_RULE_NM, GEN_CTL_RULE_DSC from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRules") + " where GEN_CTL_RULE_ID = " + generationControlRuleId;
        crsGenerationControlRule = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryGenerationControlRule, "reader");
        GenerationControlRuleParameterConfiguration generationControlRuleParameterConfiguration = new GenerationControlRuleParameterConfiguration();
        try {
            while (crsGenerationControlRule.next()) {
                generationControlRule.setId(generationControlRuleId);
                generationControlRule.setNumber(crsGenerationControlRule.getLong("GEN_CTL_RULE_NB"));
                generationControlRule.setType(crsGenerationControlRule.getString("GEN_CTL_RULE_TYP_NM"));
                generationControlRule.setName(crsGenerationControlRule.getString("GEN_CTL_RULE_NM"));
                generationControlRule.setDescription(crsGenerationControlRule.getString("GEN_CTL_RULE_DSC"));

                // Get parameters
                CachedRowSet crsGenerationControlRuleParameters = null;
                String queryGenerationControlRuleParameters = "select GEN_CTL_RULE_ID, GEN_CTL_RULE_PAR_NM from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRuleParameters")
                        + " where GEN_CTL_RULE_ID = " + generationControlRuleId;
                crsGenerationControlRuleParameters = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryGenerationControlRuleParameters, "reader");
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

}