package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Optional;

public class ComponentVersionConfiguration {

    private final static Logger LOGGER = LogManager.getLogger();

    public ComponentVersionConfiguration() {}


    public Optional<ComponentVersion> getComponentVersion(String componentId, long componentVersionNumber) {
        String queryComponentVersion = "select COMP_ID, COMP_VRS_NB, COMP_VRS_DSC from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NB = " + SQLTools.GetStringForSQL(componentVersionNumber);
        CachedRowSet crsComponentVersion = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentVersion, "reader");
        try {
            if (crsComponentVersion.size() == 0) {
                return Optional.empty();
            } else if (crsComponentVersion.size() > 1) {
                LOGGER.warn(MessageFormat.format("component.version=found multiple descriptions for component id {0} version {1}. " + "Returning first implementation.", componentId, componentVersionNumber));
            }
            crsComponentVersion.next();
            ComponentVersion componentVersion = new ComponentVersion(componentVersionNumber, crsComponentVersion.getString("COMP_VRS_DSC"));
            crsComponentVersion.close();
            return Optional.of(componentVersion);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return Optional.empty();
    }

}