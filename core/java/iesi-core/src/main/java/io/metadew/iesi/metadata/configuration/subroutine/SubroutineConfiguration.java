package io.metadew.iesi.metadata.configuration.subroutine;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.subroutine.Subroutine;
import io.metadew.iesi.metadata.definition.subroutine.SubroutineParameter;
import org.springframework.stereotype.Component;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SubroutineConfiguration {

    private Subroutine subroutine;
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public SubroutineConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }


    // Insert
    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += "DELETE FROM " + metadataRepositoryConfiguration.getDesignMetadataRepository().getTableNameByLabel("SubroutineParameters");
            sql += " WHERE SRT_NM = " + SQLTools.getStringForSQL(this.getSubroutine().getName());
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + metadataRepositoryConfiguration.getDesignMetadataRepository().getTableNameByLabel("Subroutines");
            sql += " WHERE SRT_NM = " + SQLTools.getStringForSQL(this.getSubroutine().getName());
            sql += ";";
            sql += "\n";
        }

        sql += "INSERT INTO " + metadataRepositoryConfiguration.getDesignMetadataRepository().getTableNameByLabel("Subroutines");
        sql += " (SRT_NM, SRT_TYP_NM, SRT_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.getStringForSQL(this.getSubroutine().getName());
        sql += ",";
        sql += SQLTools.getStringForSQL(this.getSubroutine().getType());
        sql += ",";
        sql += SQLTools.getStringForSQL(this.getSubroutine().getDescription());
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
                    subroutineParameter);
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
        String querySubroutine = "select SRT_NM, SRT_TYP_NM, SRT_DSC from " + metadataRepositoryConfiguration.getDesignMetadataRepository().getTableNameByLabel("Subroutines") + " where SRT_NM = '" + subroutineName + "'";
        crsSubroutine = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(querySubroutine, "reader");
        SubroutineParameterConfiguration subroutineParameterConfiguration = new SubroutineParameterConfiguration();
        try {
            while (crsSubroutine.next()) {
                subroutine.setName(subroutineName);
                subroutine.setType(crsSubroutine.getString("SRT_TYP_NM"));
                subroutine.setDescription(crsSubroutine.getString("SRT_DSC"));

                // Get parameters
                CachedRowSet crsSubroutineParameters = null;
                String querySubroutineParameters = "select SRT_NM, SRT_PAR_NM, SRT_PAR_VAL from " + metadataRepositoryConfiguration.getDesignMetadataRepository().getTableNameByLabel("SubroutineParameters")
                        + " where SRT_NM = '" + subroutineName + "'";
                crsSubroutineParameters = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(querySubroutineParameters, "reader");
                List<SubroutineParameter> subroutineParameterList = new ArrayList();
                while (crsSubroutineParameters.next()) {
                    subroutineParameterList
                            .add(subroutineParameterConfiguration.getSubroutineParameter(subroutineName, crsSubroutineParameters.getString("SRT_PAR_NM")));
                }
                subroutine.setParameters(subroutineParameterList);
                crsSubroutineParameters.close();
            }
            crsSubroutine.close();
        } catch (SQLException e) {
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

}