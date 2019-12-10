package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptVersionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptVersionDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
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

    public synchronized static ScriptVersionConfiguration getInstance(){
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
    public Optional<ScriptVersion> get(ScriptVersionKey metadataKey) {
        return getScriptVersion(metadataKey.getScriptId(), metadataKey.getVersionNumber());
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
                        crs.getString("SCRIPT_ID"),
                        crs.getLong("SCRIPT_VRS_NB"));
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
    public void delete(ScriptVersionKey metadataKey) throws MetadataDoesNotExistException {
        delete(metadataKey.getScriptId(), metadataKey.getVersionNumber());
    }

    @Override
    public void insert(ScriptVersion metadata) throws MetadataAlreadyExistsException {
        insert(metadata.getMetadataKey().getScriptId(), metadata);
    }

    public List<ScriptVersion> getAllVersionsOfScript(String scriptId){
        List<ScriptVersion> scriptVersions = new ArrayList<>();
        String queryVersionScript = "select * from "
                + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " WHERE SCRIPT_ID = " + scriptId;
        CachedRowSet crsVersionScript = getMetadataRepository().executeQuery(queryVersionScript, "reader");
        try{
            while (crsVersionScript.next()){
                Optional<ScriptVersion> scriptVersionOpt = createScriptVersion(crsVersionScript);
                scriptVersionOpt.ifPresent(scriptVersions::add);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return scriptVersions;

    }

    private Optional<ScriptVersion> createScriptVersion(CachedRowSet crsVersionScript){
        try{
            ScriptVersion scriptVersion = new ScriptVersion(
                    crsVersionScript.getString("SCRIPT_ID"),
                    crsVersionScript.getLong("SCRIPT_VRS_NB"),
                    crsVersionScript.getString("SCRIPT_VRS_DSC"));
            return Optional.of(scriptVersion);
        }catch(SQLException e){
            return Optional.empty();
        }
    }

    public void insert(String scriptId, ScriptVersion scriptVersion) throws ScriptVersionAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptVersion {0}-{1}.", scriptId, scriptVersion.getNumber()));
        if (exists(scriptId, scriptVersion)) {
            throw new ScriptVersionAlreadyExistsException(MessageFormat.format(
                    "ScriptVersion {0}-{1} already exists", scriptId, scriptVersion.getNumber()));
        }
        getMetadataRepository().executeUpdate(getInsertStatement(scriptId, scriptVersion));
    }

    public void delete(String scriptId, long scriptVersionNumber) throws ScriptVersionDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptVersion {0}-{1}.", scriptId, scriptVersionNumber));
        if (!exists(scriptId, scriptVersionNumber)) {
            throw new ScriptVersionDoesNotExistException(MessageFormat.format("ScriptVersion {0}-{1} does not exists", scriptId, scriptVersionNumber));
        }
        String deleteStatement = deleteStatement(scriptId, scriptVersionNumber);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(String scriptId, long scriptVersionNumber) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) + ";";

    }

    private boolean exists(String scriptId, ScriptVersion scriptVersion) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + scriptVersion.getNumber() + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }
    private boolean exists(String scriptId, long scriptVersionNumber) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + scriptVersionNumber + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public String getInsertStatement(String scriptId, ScriptVersion scriptVersion) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + ", " +
                scriptVersion.getNumber() + ", " +
                SQLTools.GetStringForSQL(scriptVersion.getDescription()) + ");";
    }

    public Optional<ScriptVersion> getScriptVersion(String scriptId, long scriptVersionNumber) {
        String queryScriptVersion = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + scriptVersionNumber;
        CachedRowSet crsScriptVersion = getMetadataRepository().executeQuery(queryScriptVersion, "reader");
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