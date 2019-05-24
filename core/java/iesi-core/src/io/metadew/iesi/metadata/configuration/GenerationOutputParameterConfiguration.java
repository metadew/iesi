package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationOutputParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class GenerationOutputParameterConfiguration {

    private FrameworkExecution frameworkExecution;
    private GenerationOutputParameter generationOutputParameter;

    // Constructors
    public GenerationOutputParameterConfiguration(GenerationOutputParameter generationOutputParameter, FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setgenerationOutputParameter(generationOutputParameter);
    }

    public GenerationOutputParameterConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Insert
    public String getInsertStatement(String generationName, String generationOutputName) {
        String sql = "";

        sql += "INSERT INTO "
                + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputParameters");
        sql += " (GEN_OUT_ID, GEN_OUT_PAR_NM, GEN_OUT_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs"),
                "GEN_OUT_ID",
                "where GEN_OUT_NM = '" + generationOutputName + "' and GEN_ID = ("
                        + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations"),
                        "GEN_ID",
                        "GEN_NM",
                        generationName))
                + "))";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationOutputParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationOutputParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public GenerationOutputParameter getGenerationOutputParameter(long generationOutputId, String generationOutputParameterName) {
        GenerationOutputParameter generationOutputParameter = new GenerationOutputParameter();
        CachedRowSet crsGenerationOutputParameter = null;
        String queryGenerationOutputParameter = "select GEN_OUT_ID, GEN_OUT_PAR_NM, GEN_OUT_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputParameters")
                + " where GEN_OUT_ID = " + generationOutputId + " and GEN_OUT_PAR_NM = '" + generationOutputParameterName + "'";
        crsGenerationOutputParameter = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationOutputParameter, "reader");
        try {
            while (crsGenerationOutputParameter.next()) {
                generationOutputParameter.setName(generationOutputParameterName);
                generationOutputParameter.setValue(crsGenerationOutputParameter.getString("GEN_OUT_PAR_VAL"));
            }
            crsGenerationOutputParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return generationOutputParameter;
    }

    // Getters and Setters
    public GenerationOutputParameter getgenerationOutputParameter() {
        return generationOutputParameter;
    }

    public void setgenerationOutputParameter(GenerationOutputParameter generationOutputParameter) {
        this.generationOutputParameter = generationOutputParameter;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}