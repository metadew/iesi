package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ComponentBuild;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ComponentBuildConfiguration {

    private ComponentBuild componentBuild;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ComponentBuildConfiguration(ComponentBuild componentBuild, FrameworkInstance frameworkInstance) {
        this.setComponentBuild(componentBuild);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ComponentBuildConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String componentName, String versionName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersionBuilds");
        sql += " (COMP_ID, COMP_VRS_NM, COMP_BLD_NM, COMP_BLD_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = '" + componentName) + "')";
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
        String queryComponentBuild = "select COMP_ID, COMP_VRS_NM, COMP_BLD_NM, COMP_BLD_DSC from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersionBuilds")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NM = '" + componentVersionName + "'" + " and COMP_BLD_NM = '" + componentBuildName + "'";
        crsComponentBuild = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentBuild, "reader");
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}


}