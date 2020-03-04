package io.metadew.iesi.metadata.configuration.generation;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.generation.GenerationRuleParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class GenerationRuleParameterConfiguration {

    private GenerationRuleParameter generationRuleParameter;

    // Constructors
    public GenerationRuleParameterConfiguration(GenerationRuleParameter generationRuleParameter) {
        this.setgenerationRuleParameter(generationRuleParameter);
    }

    public GenerationRuleParameterConfiguration() {
    }

    // Insert
    public String getInsertStatement(String generationName, String generationField) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationRuleParameters");
        sql += " (GEN_RULE_ID, GEN_RULE_PAR_NM, GEN_RULE_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationRules"), "GEN_RULE_ID", "where FIELD_NM = '" + generationField + "' and GEN_ID = (" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID", "GEN_NM", generationName)) + "))";
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
        String queryGenerationRuleParameter = "select GEN_RULE_ID, GEN_RULE_PAR_NM, GEN_RULE_PAR_VAL from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationRuleParameters")
                + " where GEN_RULE_ID = " + generationRuleId + " and GEN_RULE_PAR_NM = '" + generationRuleParameterName + "'";
        crsGenerationRuleParameter = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryGenerationRuleParameter, "reader");
        try {
            while (crsGenerationRuleParameter.next()) {
                generationRuleParameter.setName(generationRuleParameterName);
                generationRuleParameter.setValue(crsGenerationRuleParameter.getString("GEN_RULE_PAR_VAL"));
            }
            crsGenerationRuleParameter.close();
        } catch (SQLException e) {
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

}