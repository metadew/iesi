package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationControl;
import io.metadew.iesi.metadata.definition.GenerationControlParameter;
import io.metadew.iesi.metadata.definition.GenerationControlRule;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerationControlConfiguration {

    private GenerationControl generationControl;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public GenerationControlConfiguration(GenerationControl generationControl, FrameworkInstance frameworkInstance) {
        this.setgenerationControl(generationControl);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationControlConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String generationName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControls");
        sql += " (GEN_ID, GEN_CTL_ID, GEN_CTL_NM, GEN_CTL_TYP_NM, GEN_CTL_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID", "GEN_NM", generationName) + ")";
        sql += ",";
        sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControls"), "GEN_CTL_ID") + ")";
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
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        // add rules
        String sqlRules = this.getRuleInsertStatements(generationName, this.getgenerationControl().getName());
        if (!sqlRules.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlRules;
        }


        return sql;
    }

    private String getParameterInsertStatements(String generationName) {
        String result = "";

        if (this.getgenerationControl().getParameters() == null) return result;

        for (GenerationControlParameter generationControlParameter : this.getgenerationControl().getParameters()) {
            GenerationControlParameterConfiguration generationControlParameterConfiguration = new GenerationControlParameterConfiguration(generationControlParameter, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
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
            GenerationControlRuleConfiguration generationControlRuleConfiguration = new GenerationControlRuleConfiguration(generationControlRule, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += generationControlRuleConfiguration.getInsertStatement(generationName, generationControlName, counter);
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GenerationControl getGenerationControl(long generationControlId) {
        GenerationControl generationControl = new GenerationControl();
        CachedRowSet crsGenerationControl = null;
        String queryGenerationControl = "select GEN_ID, GEN_CTL_ID, GEN_CTL_NM, GEN_CTL_TYP_NM, GEN_CTL_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControls") + " where GEN_CTL_ID = " + generationControlId;
        crsGenerationControl = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationControl, "reader");
        GenerationControlParameterConfiguration generationControlParameterConfiguration = new GenerationControlParameterConfiguration(this.getFrameworkInstance());
        GenerationControlRuleConfiguration generationControlRuleConfiguration = new GenerationControlRuleConfiguration(this.getFrameworkInstance());
        try {
            while (crsGenerationControl.next()) {
                generationControl.setId(generationControlId);
                generationControl.setName(crsGenerationControl.getString("GEN_CTL_NM"));
                generationControl.setType(crsGenerationControl.getString("GEN_CTL_TYP_NM"));
                generationControl.setDescription(crsGenerationControl.getString("GEN_CTL_DSC"));

                // Get parameters
                CachedRowSet crsGenerationControlParameters = null;
                String queryGenerationControlParameters = "select GEN_CTL_ID, GEN_CTL_PAR_NM from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlParameters")
                        + " where GEN_CTL_ID = " + generationControlId;
                crsGenerationControlParameters = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationControlParameters, "reader");
                List<GenerationControlParameter> generationControlParameterList = new ArrayList();
                while (crsGenerationControlParameters.next()) {
                    generationControlParameterList
                            .add(generationControlParameterConfiguration.getGenerationControlParameter(generationControlId, crsGenerationControlParameters.getString("GEN_CTL_PAR_NM")));
                }
                generationControl.setParameters(generationControlParameterList);
                crsGenerationControlParameters.close();

                // Get rules
                CachedRowSet crsGenerationControlRules = null;
                String queryGenerationControlRules = "select GEN_CTL_RULE_ID, GEN_CTL_RULE_NM from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlRules")
                        + " where GEN_CTL_ID = " + generationControlId;
                crsGenerationControlRules = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationControlRules, "reader");
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}