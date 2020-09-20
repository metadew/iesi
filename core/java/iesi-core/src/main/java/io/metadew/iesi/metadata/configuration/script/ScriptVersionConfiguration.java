package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptVersionConfiguration extends Configuration<ScriptVersion, ScriptVersionKey> {

    private static ScriptVersionConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ScriptVersionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptVersionConfiguration();
        }
        return INSTANCE;
    }

    private ScriptVersionConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptVersion> get(ScriptVersionKey scriptVersionKey) {
        String queryScriptVersion = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptKey().getScriptVersion());
        CachedRowSet crsScriptVersion = getMetadataRepository().executeQuery(queryScriptVersion, "reader");
        try {
            if (crsScriptVersion.size() == 0) {
                return Optional.empty();
            } else if (crsScriptVersion.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for script version {0}. Returning first implementation", scriptVersionKey.toString()));
            }
            crsScriptVersion.next();
            ScriptVersion scriptVersion = new ScriptVersion(scriptVersionKey, crsScriptVersion.getString("SCRIPT_VRS_DSC"));
            crsScriptVersion.close();
            return Optional.of(scriptVersion);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }

    }

    @Override
    public List<ScriptVersion> getAll() {
        List<ScriptVersion> scriptVersions = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " order by SCRIPT_ID";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ScriptVersionKey scriptVersionKey = new ScriptVersionKey(
                        new ScriptKey(crs.getString("SCRIPT_ID"),
                        crs.getLong("SCRIPT_VRS_NB")));
                scriptVersions.add(new ScriptVersion(
                        scriptVersionKey,
                        crs.getString("SCRIPT_VRS_DSC")));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return scriptVersions;
    }

    @Override
    public void delete(ScriptVersionKey scriptVersionKey) {
        LOGGER.trace(MessageFormat.format("deleting ScriptVersion {0}", scriptVersionKey.toString()));
        String deleteStatement = deleteStatement(scriptVersionKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public List<ScriptVersion> getByScriptId(String scriptId) {
        List<ScriptVersion> scriptVersions = new ArrayList<>();
        String queryVersionScript = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId);
        CachedRowSet crsVersionScript = getMetadataRepository().executeQuery(queryVersionScript, "reader");
        try {
            while (crsVersionScript.next()) {
                scriptVersions.add(new ScriptVersion(
                        crsVersionScript.getString("SCRIPT_ID"),
                        crsVersionScript.getLong("SCRIPT_VRS_NB"),
                        crsVersionScript.getString("SCRIPT_VRS_DSC")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scriptVersions;
    }

    public Optional<ScriptVersion> getLatestVersionNumber(String scriptId) {
        LOGGER.trace(MessageFormat.format("Fetching latest version for script {0}.", scriptId));
        String queryScriptVersion = "select max(SCRIPT_VRS_NB) as \"MAX_VRS_NB\" from "
                + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " where script_id = " + SQLTools.GetStringForSQL(scriptId) + ";";
        CachedRowSet crsScriptVersion = getMetadataRepository().executeQuery(queryScriptVersion, "reader");
        try {
            if (crsScriptVersion.size() == 0) {
                crsScriptVersion.close();
                return Optional.empty();
            } else {
                crsScriptVersion.next();
                long latestScriptVersion = crsScriptVersion.getLong("MAX_VRS_NB");
                crsScriptVersion.close();
                return get(new ScriptVersionKey(new ScriptKey(scriptId, latestScriptVersion)));
            }
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.info("exception.stacktrace=" + StackTrace);

            return Optional.empty();
        }
    }

    @Override
    public void insert(ScriptVersion scriptVersion) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptVersion {0}-{1}.", scriptVersion.getScriptId(), scriptVersion.getNumber()));
        if (exists(scriptVersion)) {
            throw new MetadataAlreadyExistsException(scriptVersion);
        }
        getMetadataRepository().executeUpdate(getInsertStatement(scriptVersion));
    }

    private String deleteStatement(ScriptVersionKey scriptVersionKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptKey().getScriptVersion()) + ";";
    }

    public boolean exists(ScriptVersionKey scriptVersionKey) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptKey().getScriptVersion()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public String getInsertStatement(ScriptVersion scriptVersion) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptVersion.getMetadataKey().getScriptKey().getScriptId()) + ", " +
                SQLTools.GetStringForSQL(scriptVersion.getMetadataKey().getScriptKey().getScriptVersion()) + ", " +
                SQLTools.GetStringForSQL(scriptVersion.getDescription()) + ");";
    }

}