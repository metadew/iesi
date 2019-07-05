package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationControlRuleParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class GenerationControlRuleParameterConfiguration {

    private GenerationControlRuleParameter generationControlRuleParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public GenerationControlRuleParameterConfiguration(GenerationControlRuleParameter generationControlRuleParameter,
    		FrameworkInstance frameworkInstance) {
        this.setgenerationControlRuleParameter(generationControlRuleParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationControlRuleParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String generationName, String generationControlName, String generationControlRuleName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("GenerationControlRuleParameters");
        sql += " (GEN_CTL_RULE_ID, GEN_CTL_RULE_PAR_NM, GEN_CTL_RULE_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(
                this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRules"),
                "GEN_CTL_RULE_ID",
                "where GEN_CTL_RULE_NM = '" + generationControlRuleName + "' and GEN_CTL_ID = (" + SQLTools.GetLookupIdStatement(
                        this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                                .getTableNameByLabel("GenerationControls"),
                        "GEN_CTL_ID",
                        "where GEN_CTL_NM = '" + generationControlName + "' and GEN_ID = ("
                                + SQLTools.GetLookupIdStatement(
                                this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
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
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("GenerationControlRuleParameters")
                + " where GEN_CTL_RULE_ID = " + generationControlRuleId + " and GEN_CTL_RULE_PAR_NM = '"
                + generationControlRuleParameterName + "'";
        crsGenerationControlRuleParameter = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}