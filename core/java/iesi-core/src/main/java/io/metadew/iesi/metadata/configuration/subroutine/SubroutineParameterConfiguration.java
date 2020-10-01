package io.metadew.iesi.metadata.configuration.subroutine;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.subroutine.SubroutineParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

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

        sql += "INSERT INTO " + MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().getTableNameByLabel("SubroutineParameters");
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
        String querySubroutineParameter = "select SRT_NM, SRT_PAR_NM, SRT_PAR_VAL from " + MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().getTableNameByLabel("SubroutineParameters")
                + " where SRT_NM = '" + subroutineName + "' and SRT_PAR_NM = '" + subroutineParameterName + "'";
        crsSubroutineParameter = MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().executeQuery(querySubroutineParameter, "reader");
        try {
            while (crsSubroutineParameter.next()) {
                subroutineParameter.setName(subroutineParameterName);
                subroutineParameter.setValue(crsSubroutineParameter.getString("SRT_PAR_VAL"));
            }
            crsSubroutineParameter.close();
        } catch (SQLException e) {
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