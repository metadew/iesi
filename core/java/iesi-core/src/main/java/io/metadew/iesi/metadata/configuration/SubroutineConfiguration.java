package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Subroutine;
import io.metadew.iesi.metadata.definition.SubroutineParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class SubroutineConfiguration {

    private FrameworkInstance frameworkInstance;
    private Subroutine subroutine;

    // Constructors
    public SubroutineConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public SubroutineConfiguration(Subroutine subroutine, FrameworkInstance frameworkInstance) {
        this.setSubroutine(subroutine);
        this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("SubroutineParameters");
            sql += " WHERE SRT_NM = " + SQLTools.GetStringForSQL(this.getSubroutine().getName());
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Subroutines");
            sql += " WHERE SRT_NM = " + SQLTools.GetStringForSQL(this.getSubroutine().getName());
            sql += ";";
            sql += "\n";
        }

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Subroutines");
        sql += " (SRT_NM, SRT_TYP_NM, SRT_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(this.getSubroutine().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getSubroutine().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getSubroutine().getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements();
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements() {
        String result = "";

        for (SubroutineParameter subroutineParameter : this.getSubroutine().getParameters()) {
            SubroutineParameterConfiguration subroutineParameterConfiguration = new SubroutineParameterConfiguration(
                    subroutineParameter, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += subroutineParameterConfiguration.getInsertStatement(this.getSubroutine().getName());
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Subroutine getSubroutine(String subroutineName) {
        Subroutine subroutine = new Subroutine();
        CachedRowSet crsSubroutine = null;
        String querySubroutine = "select SRT_NM, SRT_TYP_NM, SRT_DSC from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Subroutines") + " where SRT_NM = '" + subroutineName + "'";
        crsSubroutine = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(querySubroutine, "reader");
        SubroutineParameterConfiguration subroutineParameterConfiguration = new SubroutineParameterConfiguration(this.getFrameworkInstance());
        try {
            while (crsSubroutine.next()) {
                subroutine.setName(subroutineName);
                subroutine.setType(crsSubroutine.getString("SRT_TYP_NM"));
                subroutine.setDescription(crsSubroutine.getString("SRT_DSC"));

                // Get parameters
                CachedRowSet crsSubroutineParameters = null;
                String querySubroutineParameters = "select SRT_NM, SRT_PAR_NM, SRT_PAR_VAL from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("SubroutineParameters")
                        + " where SRT_NM = '" + subroutineName + "'";
                crsSubroutineParameters = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(querySubroutineParameters, "reader");
                List<SubroutineParameter> subroutineParameterList = new ArrayList();
                while (crsSubroutineParameters.next()) {
                    subroutineParameterList
                            .add(subroutineParameterConfiguration.getSubroutineParameter(subroutineName, crsSubroutineParameters.getString("SRT_PAR_NM")));
                }
                subroutine.setParameters(subroutineParameterList);
                crsSubroutineParameters.close();
            }
            crsSubroutine.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return subroutine;
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Subroutine getSubroutine() {
        return subroutine;
    }

    public void setSubroutine(Subroutine subroutine) {
        this.subroutine = subroutine;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}
}