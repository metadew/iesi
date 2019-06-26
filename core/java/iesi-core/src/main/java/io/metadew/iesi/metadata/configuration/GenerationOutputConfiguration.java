package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationOutput;
import io.metadew.iesi.metadata.definition.GenerationOutputParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerationOutputConfiguration {

    private GenerationOutput generationOutput;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public GenerationOutputConfiguration(GenerationOutput generationOutput, FrameworkInstance frameworkInstance) {
        this.setgenerationOutput(generationOutput);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationOutputConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String generationName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs");
        sql += " (GEN_ID, GEN_OUT_ID, GEN_OUT_NM, GEN_OUT_TYP_NM, GEN_OUT_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Generations"), "GEN_ID", "GEN_NM", generationName) + ")";
        sql += ",";
        sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs"), "GEN_OUT_ID") + ")";
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
            GenerationOutputParameterConfiguration generationOutputParameterConfiguration = new GenerationOutputParameterConfiguration(generationOutputParameter, this.getFrameworkInstance());
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
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs") + " where GEN_OUT_ID = " + generationOutputId;
        crsGenerationOutput = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationOutput, "reader");
        GenerationOutputParameterConfiguration generationOutputParameterConfiguration = new GenerationOutputParameterConfiguration(this.getFrameworkInstance());
        try {
            while (crsGenerationOutput.next()) {
                generationOutput.setId(generationOutputId);
                generationOutput.setName(crsGenerationOutput.getString("GEN_OUT_NM"));
                generationOutput.setType(crsGenerationOutput.getString("GEN_OUT_TYP_NM"));
                generationOutput.setDescription(crsGenerationOutput.getString("GEN_OUT_DSC"));

                // Get parameters
                CachedRowSet crsGenerationOutputParameters = null;
                String queryGenerationOutputParameters = "select GEN_OUT_ID, GEN_OUT_PAR_NM from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputParameters")
                        + " where GEN_OUT_ID = " + generationOutputId;
                crsGenerationOutputParameters = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryGenerationOutputParameters, "reader");
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}