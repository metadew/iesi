package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationControlParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class GenerationControlParameterConfiguration {

    private FrameworkInstance frameworkInstance;
    private GenerationControlParameter generationControlParameter;

    // Constructors
    public GenerationControlParameterConfiguration(GenerationControlParameter generationControlParameter, FrameworkInstance frameworkInstance) {
        this.setgenerationControlParameter(generationControlParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationControlParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String generationName, String generationControlName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlParameters");
        sql += " (GEN_CTL_ID, GEN_CTL_PAR_NM, GEN_CTL_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControls"), "GEN_CTL_ID", "where GEN_CTL_NM = '" + generationControlName + "' and GEN_ID = (" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID", "GEN_NM", generationName)) + "))";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationControlParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationControlParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public GenerationControlParameter getGenerationControlParameter(long generationControlId, String generationControlParameterName) {
        GenerationControlParameter generationControlParameter = new GenerationControlParameter();
        CachedRowSet crsGenerationControlParameter = null;
        String queryGenerationControlParameter = "select GEN_CTL_ID, GEN_CTL_PAR_NM, GEN_CTL_PAR_VAL from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationControlParameters")
                + " where GEN_CTL_ID = " + generationControlId + " and GEN_CTL_PAR_NM = '" + generationControlParameterName + "'";
        crsGenerationControlParameter = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationControlParameter, "reader");
        try {
            while (crsGenerationControlParameter.next()) {
                generationControlParameter.setName(generationControlParameterName);
                generationControlParameter.setValue(crsGenerationControlParameter.getString("GEN_CTL_PAR_VAL"));
            }
            crsGenerationControlParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return generationControlParameter;
    }

    // Getters and Setters
    public GenerationControlParameter getgenerationControlParameter() {
        return generationControlParameter;
    }

    public void setgenerationControlParameter(GenerationControlParameter generationControlParameter) {
        this.generationControlParameter = generationControlParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}