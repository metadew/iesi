package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.SubroutineParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class SubroutineParameterConfiguration {

    private SubroutineParameter subroutineParameter;

    // Constructors
    public SubroutineParameterConfiguration(SubroutineParameter subroutineParameter) {
        this.setSubroutineParameter(subroutineParameter);
    }

    public SubroutineParameterConfiguration() {
    }

    // Insert
    public String getInsertStatement(String subroutineName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("SubroutineParameters");
        sql += " (SRT_NM, SRT_PAR_NM, SRT_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(subroutineName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getSubroutineParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getSubroutineParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public SubroutineParameter getSubroutineParameter(String subroutineName, String subroutineParameterName) {
        SubroutineParameter subroutineParameter = new SubroutineParameter();
        CachedRowSet crsSubroutineParameter = null;
        String querySubroutineParameter = "select SRT_NM, SRT_PAR_NM, SRT_PAR_VAL from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("SubroutineParameters")
                + " where SRT_NM = '" + subroutineName + "' and SRT_PAR_NM = '" + subroutineParameterName + "'";
        crsSubroutineParameter = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(querySubroutineParameter, "reader");
        try {
            while (crsSubroutineParameter.next()) {
                subroutineParameter.setName(subroutineParameterName);
                subroutineParameter.setValue(crsSubroutineParameter.getString("SRT_PAR_VAL"));
            }
            crsSubroutineParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return subroutineParameter;
    }

    // Getters and Setters
    public SubroutineParameter getSubroutineParameter() {
        return subroutineParameter;
    }

    public void setSubroutineParameter(SubroutineParameter subroutineParameter) {
        this.subroutineParameter = subroutineParameter;
    }
}