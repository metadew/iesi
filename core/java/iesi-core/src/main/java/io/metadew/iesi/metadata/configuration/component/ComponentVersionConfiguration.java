package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class ComponentVersionConfiguration {

    public ComponentVersionConfiguration() {}

    // Insert
    public String getInsertStatement(String componentName, long version) {
        return "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions") +
                " (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) VALUES ((" +
                SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = " + SQLTools.GetStringForSQL(componentName)) + ")" + "," +
                SQLTools.GetStringForSQL(version) + "," + SQLTools.GetStringForSQL(version) + ");";
    }

    public boolean exists(String componentId, long componentVersion) {
        String queryComponentVersion = "select COMP_ID, COMP_VRS_NB, COMP_VRS_DSC from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NB = " + SQLTools.GetStringForSQL(componentVersion);
        CachedRowSet crsComponentVersion = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentVersion, "reader");
        return crsComponentVersion.size() > 0;
    }


    public Optional<ComponentVersion> getComponentVersion(String componentId, long componentVersionNumber) {
        ComponentVersion componentVersion;
        String queryComponentVersion = "select COMP_ID, COMP_VRS_NB, COMP_VRS_DSC from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NB = " + SQLTools.GetStringForSQL(componentVersionNumber);
        CachedRowSet crsComponentVersion = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentVersion, "reader");
        try {
            if (crsComponentVersion.size() == 0) {
                return Optional.empty();
            } else if (crsComponentVersion.size() == 1) {
                crsComponentVersion.next();
                componentVersion = new ComponentVersion(componentVersionNumber, crsComponentVersion.getString("COMP_VRS_DSC"));
            } else {
            	//TODO fix logging
                //frameworkExecution.getFrameworkLog().log(MessageFormat.format("component.version=found multiple descriptions for component id {0} version {1}. " + "Returning first implementation.", componentId, componentVersion), Level.WARN);
                crsComponentVersion.next();
                componentVersion = new ComponentVersion(componentVersionNumber, crsComponentVersion.getString("COMP_VRS_DSC"));
            }
            crsComponentVersion.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
        return Optional.of(componentVersion);
    }

}