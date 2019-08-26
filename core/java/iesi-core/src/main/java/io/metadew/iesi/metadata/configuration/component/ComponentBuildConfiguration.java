package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.component.ComponentBuild;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ComponentBuildConfiguration {

    private ComponentBuild componentBuild;

    // Constructors
    public ComponentBuildConfiguration(ComponentBuild componentBuild) {
        this.setComponentBuild(componentBuild);
    }

    public ComponentBuildConfiguration() {
    }

    // Insert
    public String getInsertStatement(String componentName, String versionName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersionBuilds");
        sql += " (COMP_ID, COMP_VRS_NM, COMP_BLD_NM, COMP_BLD_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = '" + componentName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(versionName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentBuild().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentBuild().getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }

    public ComponentBuild getComponentBuild(long componentId, String componentVersionName, String componentBuildName) {
        ComponentBuild componentBuild = new ComponentBuild();
        CachedRowSet crsComponentBuild = null;
        String queryComponentBuild = "select COMP_ID, COMP_VRS_NM, COMP_BLD_NM, COMP_BLD_DSC from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersionBuilds")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NM = '" + componentVersionName + "'" + " and COMP_BLD_NM = '" + componentBuildName + "'";
        crsComponentBuild = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentBuild, "reader");
        try {
            while (crsComponentBuild.next()) {
                componentBuild.setName(componentBuildName);
                componentBuild.setDescription(crsComponentBuild.getString("COMP_BLD_DSC"));
            }
            crsComponentBuild.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return componentBuild;
    }

    // Getters and Setters
    public ComponentBuild getComponentBuild() {
        return componentBuild;
    }

    public void setComponentBuild(ComponentBuild componentBuild) {
        this.componentBuild = componentBuild;
    }

}