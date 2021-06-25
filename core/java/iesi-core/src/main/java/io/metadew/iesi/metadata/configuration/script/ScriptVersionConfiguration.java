package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.relational.core.sql.SQL;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
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
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    @Override
    public Optional<ScriptVersion> get(ScriptVersionKey scriptVersionKey) {
        String queryScriptVersion = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC, CREATED_BY, CREATED_AT, LAST_MODIFIED_BY, LAST_MODIFIED_AT, DELETED_AT from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where DELETED_AT = 'NA' and SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptVersion());
        CachedRowSet crsScriptVersion = getMetadataRepository().executeQuery(queryScriptVersion, "reader");
        try {
            if (crsScriptVersion.size() == 0) {
                return Optional.empty();
            } else if (crsScriptVersion.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for script version {0}. Returning first implementation", scriptVersionKey.toString()));
            }
            crsScriptVersion.next();
            scriptVersionKey.getScriptKey().setDeletedAt(crsScriptVersion.getString("DELETED_AT"));
            ScriptVersion scriptVersion = new ScriptVersion(
                    scriptVersionKey,
                    crsScriptVersion.getString("SCRIPT_VRS_DSC"),
                    crsScriptVersion.getString("CREATED_BY"),
                    crsScriptVersion.getString("CREATED_AT"),
                    crsScriptVersion.getString("LAST_MODIFIED_BY"),
                    crsScriptVersion.getString("LAST_MODIFIED_AT"));
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
                + " where DELETED_AT = 'NA' ";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ScriptVersionKey scriptVersionKey = new ScriptVersionKey(
                        new ScriptKey(crs.getString("SCRIPT_ID"),
                                crs.getLong("SCRIPT_VRS_NB"),
                                crs.getString("DELETED_AT")));
                scriptVersions.add(new ScriptVersion(
                        scriptVersionKey,
                        crs.getString("SCRIPT_VRS_DSC"),
                        crs.getString("CREATED_BY"),
                        crs.getString("CREATED_AT"),
                        crs.getString("LAST_MODIFIED_BY"),
                        crs.getString("LAST_MODIFIED_AT")));
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
        String deleteStatement = markInactiveScriptVersion(scriptVersionKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public List<ScriptVersion> getByScriptId(String scriptId) {
        List<ScriptVersion> scriptVersions = new ArrayList<>();
        String queryVersionScript = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " WHERE DELETED_AT = 'NA' AND SCRIPT_ID = " + SQLTools.getStringForSQL(scriptId);
        CachedRowSet crsVersionScript = getMetadataRepository().executeQuery(queryVersionScript, "reader");
        try {
            while (crsVersionScript.next()) {
                scriptVersions.add(new ScriptVersion(
                        crsVersionScript.getString("SCRIPT_ID"),
                        crsVersionScript.getLong("SCRIPT_VRS_NB"),
                        crsVersionScript.getString("SCRIPT_VRS_DSC"),
                        crsVersionScript.getString("LAST_MODIFIED_BY"),
                        crsVersionScript.getString("LAST_MODIFIED_AT"),
                        crsVersionScript.getString("DELETED_AT")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scriptVersions;
    }

    public List<ScriptVersion> getDeletedByScriptId(String scriptId) {
        List<ScriptVersion> scriptVersions = new ArrayList<>();
        String queryVersionScript = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " WHERE DELETED_AT != 'NA' AND SCRIPT_ID = " + SQLTools.getStringForSQL(scriptId);
        CachedRowSet crsVersionScript = getMetadataRepository().executeQuery(queryVersionScript, "reader");
        try {
            while (crsVersionScript.next()) {
                scriptVersions.add(new ScriptVersion(
                        crsVersionScript.getString("SCRIPT_ID"),
                        crsVersionScript.getLong("SCRIPT_VRS_NB"),
                        crsVersionScript.getString("SCRIPT_VRS_DSC"),
                        crsVersionScript.getString("CREATED_BY"),
                        crsVersionScript.getString("CREATED_AT"),
                        crsVersionScript.getString("LAST_MODIFIED_BY"),
                        crsVersionScript.getString("LAST_MODIFIED_AT"),
                        crsVersionScript.getString("DELETED_AT")));
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
                " where script_id = " + SQLTools.getStringForSQL(scriptId) +
                " and DELETED_AT = 'NA' " + " ;";
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

    @Override
    public void update(ScriptVersion scriptVersion){
        LOGGER.trace(MessageFormat.format("Updating ScriptVersion {0}-{1}.", scriptVersion.getScriptId(), scriptVersion.getNumber()));
        if (!exists(scriptVersion)) {
            throw new MetadataDoesNotExistException(scriptVersion);
        }
        getMetadataRepository().executeUpdate(updateStatement(scriptVersion));
    }

    public void restoreDeletedScriptVersion(ScriptVersionKey scriptVersionKey) {
        LOGGER.trace(MessageFormat.format("Restoring deleted ScriptVersion {0}", scriptVersionKey.getScriptKey().getScriptId()));
        if (exists(scriptVersionKey)) {
            throw new MetadataAlreadyExistsException(scriptVersionKey.getScriptKey());
        } else if (!existsDeleted(scriptVersionKey)) {
            throw new MetadataDoesNotExistException(scriptVersionKey.getScriptKey());
        }
        getMetadataRepository().executeUpdate(markActiveScriptVersion(scriptVersionKey));
        LOGGER.trace(MessageFormat.format("Successfully Restored deleted ScriptVersion {0}", scriptVersionKey.getScriptKey()));
    }

    private String markInactiveScriptVersion(ScriptVersionKey scriptVersionKey) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptVersions") + " SET " +
                " DELETED_AT = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getDeletedAt()) +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptVersion()) +
                " AND DELETED_AT = 'NA' ;";
    }

    private String markActiveScriptVersion(ScriptVersionKey scriptVersionKey) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptVersions") + " SET " +
                " DELETED_AT = 'NA' " +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptVersion()) +
                " AND DELETED_AT = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getDeletedAt()) + ";";
    }

    private String updateStatement(ScriptVersion scriptVersion){
        return "UPDATE "+ getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " SET LAST_MODIFIED_BY = " + SQLTools.getStringForSQL(scriptVersion.getLastModifiedBy()) + ", " +
                " LAST_MODIFIED_AT = " + SQLTools.getStringForSQL(scriptVersion.getLastModifiedAt()) + ", " +
                " SCRIPT_VRS_DSC = " + SQLTools.getStringForSQL(scriptVersion.getDescription()) +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getScriptKey().getScriptVersion()) + ";";
    }

    public boolean exists(ScriptVersionKey scriptVersionKey) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptVersion()) +
                " and DELETED_AT = 'NA' ;";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public boolean existsDeleted(ScriptVersionKey scriptVersionKey) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptVersion()) +
                " and DELETED_AT = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getDeletedAt()) + " ;";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public String getInsertStatement(ScriptVersion scriptVersion) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC, CREATED_BY, CREATED_AT, DELETED_AT) VALUES (" +
                SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getScriptKey().getScriptId()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getScriptKey().getScriptVersion()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getDescription()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getCreatedBy()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getCreatedAt()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getScriptKey().getDeletedAt()) + ");";
    }
}