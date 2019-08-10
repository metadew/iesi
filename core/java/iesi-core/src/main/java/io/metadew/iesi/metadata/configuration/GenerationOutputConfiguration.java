package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationOutput;
import io.metadew.iesi.metadata.definition.GenerationOutputParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerationOutputConfiguration {

    private GenerationOutput generationOutput;

    // Constructors
    public GenerationOutputConfiguration(GenerationOutput generationOutput) {
        this.setgenerationOutput(generationOutput);
    }

    public GenerationOutputConfiguration() {
    }

    // Insert
    public String getInsertStatement(String generationName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs");
        sql += " (GEN_ID, GEN_OUT_ID, GEN_OUT_NM, GEN_OUT_TYP_NM, GEN_OUT_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID", "GEN_NM", generationName) + ")";
        sql += ",";
        sql += "(" + SQLTools.GetNextIdStatement(MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs"), "GEN_OUT_ID") + ")";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationOutput().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationOutput().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getgenerationOutput().getDescription());
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

        if (this.getgenerationOutput().getParameters() == null) return result;

        for (GenerationOutputParameter generationOutputParameter : this.getgenerationOutput().getParameters()) {
            GenerationOutputParameterConfiguration generationOutputParameterConfiguration = new GenerationOutputParameterConfiguration(generationOutputParameter);
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += generationOutputParameterConfiguration.getInsertStatement(generationName, this.getgenerationOutput().getName());
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GenerationOutput getGenerationOutput(long generationOutputId) {
        GenerationOutput generationOutput = new GenerationOutput();
        CachedRowSet crsGenerationOutput = null;
        String queryGenerationOutput = "select GEN_ID, GEN_OUT_ID, GEN_OUT_NM, GEN_OUT_TYP_NM, GEN_OUT_DSC from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs") + " where GEN_OUT_ID = " + generationOutputId;
        crsGenerationOutput = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryGenerationOutput, "reader");
        GenerationOutputParameterConfiguration generationOutputParameterConfiguration = new GenerationOutputParameterConfiguration();
        try {
            while (crsGenerationOutput.next()) {
                generationOutput.setId(generationOutputId);
                generationOutput.setName(crsGenerationOutput.getString("GEN_OUT_NM"));
                generationOutput.setType(crsGenerationOutput.getString("GEN_OUT_TYP_NM"));
                generationOutput.setDescription(crsGenerationOutput.getString("GEN_OUT_DSC"));

                // Get parameters
                CachedRowSet crsGenerationOutputParameters = null;
                String queryGenerationOutputParameters = "select GEN_OUT_ID, GEN_OUT_PAR_NM from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputParameters")
                        + " where GEN_OUT_ID = " + generationOutputId;
                crsGenerationOutputParameters = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryGenerationOutputParameters, "reader");
                List<GenerationOutputParameter> generationOutputParameterList = new ArrayList();
                while (crsGenerationOutputParameters.next()) {
                    generationOutputParameterList
                            .add(generationOutputParameterConfiguration.getGenerationOutputParameter(generationOutputId, crsGenerationOutputParameters.getString("GEN_OUT_PAR_NM")));
                }
                generationOutput.setParameters(generationOutputParameterList);
                crsGenerationOutputParameters.close();

            }
            crsGenerationOutput.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return generationOutput;
    }

    // Getters and Setters
    public GenerationOutput getgenerationOutput() {
        return generationOutput;
    }

    public void setgenerationOutput(GenerationOutput generationOutput) {
        this.generationOutput = generationOutput;
    }

}