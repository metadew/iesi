package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.GenerationControlRuleParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class GenerationControlRuleParameterConfiguration {

    private GenerationControlRuleParameter generationControlRuleParameter;

    // Constructors
    public GenerationControlRuleParameterConfiguration(GenerationControlRuleParameter generationControlRuleParameter) {
        this.setgenerationControlRuleParameter(generationControlRuleParameter);
    }

    public GenerationControlRuleParameterConfiguration() {
    }

    // Insert
    public String getInsertStatement(String generationName, String generationControlName, String generationControlRuleName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
                .getTableNameByLabel("GenerationControlRuleParameters");
        sql += " (GEN_CTL_RULE_ID, GEN_CTL_RULE_PAR_NM, GEN_CTL_RULE_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(
                MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRules"),
                "GEN_CTL_RULE_ID",
                "where GEN_CTL_RULE_NM = '" + generationControlRuleName + "' and GEN_CTL_ID = (" + SQLTools.GetLookupIdStatement(
                        MetadataControl.getInstance().getDesignMetadataRepository()
                                .getTableNameByLabel("GenerationControls"),
                        "GEN_CTL_ID",
                        "where GEN_CTL_NM = '" + generationControlName + "' and GEN_ID = ("
                                + SQLTools.GetLookupIdStatement(
                                MetadataControl.getInstance().getDesignMetadataRepository()
                                        .getTableNameByLabel("Generations"),
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
                                                                            String generationControlRuleParameterName) {
        GenerationControlRuleParameter generationControlRuleParameter = new GenerationControlRuleParameter();
        CachedRowSet crsGenerationControlRuleParameter = null;
        String queryGenerationControlRuleParameter = "select GEN_CTL_RULE_ID, GEN_CTL_RULE_PAR_NM, GEN_CTL_RULE_PAR_VAL from "
                + MetadataControl.getInstance().getDesignMetadataRepository()
                .getTableNameByLabel("GenerationControlRuleParameters")
                + " where GEN_CTL_RULE_ID = " + generationControlRuleId + " and GEN_CTL_RULE_PAR_NM = '"
                + generationControlRuleParameterName + "'";
        crsGenerationControlRuleParameter = MetadataControl.getInstance().getDesignMetadataRepository()
                .executeQuery(queryGenerationControlRuleParameter, "reader");
        try {
            while (crsGenerationControlRuleParameter.next()) {
                generationControlRuleParameter.setName(generationControlRuleParameterName);
                generationControlRuleParameter.setValue(crsGenerationControlRuleParameter.getString("GEN_CTL_RULE_PAR_VAL"));
            }
            crsGenerationControlRuleParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return generationControlRuleParameter;
    }

    // Getters and Setters
    public GenerationControlRuleParameter getgenerationControlRuleParameter() {
        return generationControlRuleParameter;
    }

    public void setgenerationControlRuleParameter(GenerationControlRuleParameter generationControlRuleParameter) {
        this.generationControlRuleParameter = generationControlRuleParameter;
    }

}