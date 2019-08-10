package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.exception.action.ActionDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.script.ScriptVersionAlreadyExistsException;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ScriptVersionConfiguration {

    private static final Logger LOGGER = LogManager.getLogger();


    public ScriptVersionConfiguration() {
    }

    public void insert(String scriptId, ScriptVersion scriptVersion) throws ScriptVersionAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptVersion {0}-{1}.", scriptId, scriptVersion.getNumber()));
        if (exists(scriptId, scriptVersion)) {
            throw new ScriptVersionAlreadyExistsException(MessageFormat.format(
                    "ScriptVersion {0}-{1} already exists", scriptId, scriptVersion.getNumber()));
        }
        MetadataControl.getInstance().getDesignMetadataRepository().executeUpdate(getInsertStatement(scriptId, scriptVersion));
    }

    public void delete(String scriptId, long scriptVersionNumber) throws ActionDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptVersion {0}-{1}.", scriptId, scriptVersionNumber));
        if (!exists(scriptId, scriptVersionNumber)) {
            throw new ActionDoesNotExistException(MessageFormat.format("ScriptVersion {0}-{1} does not exists", scriptId, scriptVersionNumber));
        }
        String deleteStatement = deleteStatement(scriptId, scriptVersionNumber);
        MetadataControl.getInstance().getTraceMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(String scriptId, long scriptVersionNumber) {
        return "DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) + ";";

    }

    private boolean exists(String scriptId, ScriptVersion scriptVersion) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + scriptVersion.getNumber() + ";";
        CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }
    private boolean exists(String scriptId, long scriptVersionNumber) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + scriptVersionNumber + ";";
        CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public String getInsertStatement(String scriptId, ScriptVersion scriptVersion) {
        return "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + ", " +
                scriptVersion.getNumber() + ", " +
                SQLTools.GetStringForSQL(scriptVersion.getDescription()) + ");";
    }

    public Optional<ScriptVersion> getScriptVersion(String scriptId, long scriptVersionNumber) {
        String queryScriptVersion = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + scriptVersionNumber;
        CachedRowSet crsScriptVersion = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryScriptVersion, "reader");
        try {
            if (crsScriptVersion.size() == 0) {
                return Optional.empty();
            } else if (crsScriptVersion.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for script version {0}-{1}. Returning first implementation", scriptId, scriptVersionNumber));
            }
            crsScriptVersion.next();
            ScriptVersion scriptVersion = new ScriptVersion(scriptId, scriptVersionNumber, crsScriptVersion.getString("SCRIPT_VRS_DSC"));
            crsScriptVersion.close();
            return Optional.of(scriptVersion);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
    }

}