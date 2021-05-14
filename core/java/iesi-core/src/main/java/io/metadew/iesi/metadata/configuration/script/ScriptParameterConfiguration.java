package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
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

public class ScriptParameterConfiguration extends Configuration<ScriptParameter, ScriptParameterKey> {

    private static ScriptParameterConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ScriptParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptParameterConfiguration();
        }
        return INSTANCE;
    }

    private ScriptParameterConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    @Override
    public Optional<ScriptParameter> get(ScriptParameterKey scriptParameterKey) {
        try {
            String queryScriptParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from " + getMetadataRepository().getTableNameByLabel("ScriptParameters")
                    + " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptParameterKey.getScriptKey().getScriptId()) +
                    " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptParameterKey.getScriptKey().getScriptVersion()) +
                    " and SCRIPT_PAR_NM = " + SQLTools.getStringForSQL(scriptParameterKey.getParameterName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptParameter, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for ScriptParameter {0}. Returning first implementation", scriptParameterKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptParameter(scriptParameterKey,
                    SQLTools.getStringFromSQLClob(cachedRowSet, "SCRIPT_PAR_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptParameter> getAll() {
        List<ScriptParameter> scriptParameters = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptParameters")
                + " order by SCRIPT_ID ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ScriptParameterKey scriptParameterKey = new ScriptParameterKey(
                        new ScriptKey(crs.getString("SCRIPT_ID"),
                                crs.getLong("SCRIPT_VRS_NB")),
                        crs.getString("SCRIPT_PAR_NM"));
                scriptParameters.add(new ScriptParameter(
                        scriptParameterKey,
                        SQLTools.getStringFromSQLClob(crs, "SCRIPT_PAR_VAL")));

            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return scriptParameters;
    }

    @Override
    public void delete(ScriptParameterKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptParameter {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptParameterKey scriptParameterKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(scriptParameterKey.getScriptKey().getScriptId()) + " AND " +
                " SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptParameterKey.getScriptKey().getScriptVersion()) + " AND " +
                " SCRIPT_PAR_NM = " + SQLTools.getStringForSQL(scriptParameterKey.getParameterName()) + ";";
    }

    @Override
    public void insert(ScriptParameter scriptParameter) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptParameter {0}.", scriptParameter.toString()));
        if (exists(scriptParameter)) {
            throw new MetadataAlreadyExistsException(scriptParameter);
        }
        getMetadataRepository().executeUpdate("INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptParameters") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES (" +
                SQLTools.getStringForSQL(scriptParameter.getMetadataKey().getScriptKey().getScriptId()) + "," +
                SQLTools.getStringForSQL(scriptParameter.getMetadataKey().getScriptKey().getScriptVersion()) + "," +
                SQLTools.getStringForSQL(scriptParameter.getMetadataKey().getParameterName()) + "," +
                SQLTools.getStringForSQLClob(scriptParameter.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + ");");

    }

    public boolean exists(ScriptParameterKey scriptParameterKey) {
        String queryScriptParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from " + getMetadataRepository().getTableNameByLabel("ScriptParameters")
                + " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptParameterKey.getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptParameterKey.getScriptKey().getScriptVersion()) +
                " and SCRIPT_PAR_NM = " + SQLTools.getStringForSQL(scriptParameterKey.getParameterName()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptParameter, "reader");
        return cachedRowSet.size() >= 1;
    }

    public void deleteByScript(ScriptKey scriptKey) {
        LOGGER.trace(MessageFormat.format("Deleting script parameters for script {0}", scriptKey.toString()));
        String deleteStatement = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptParameters") +
                " WHERE " +
                " SCRIPT_ID = " + SQLTools.getStringForSQL(scriptKey.getScriptId()) + " AND " +
                " SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptKey.getScriptVersion()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public List<ScriptParameter> getByScript(ScriptKey scriptKey) {
        List<ScriptParameter> scriptParameters = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptParameters")
                + " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptKey.getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptKey.getScriptVersion()) + ";";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ScriptParameterKey scriptParameterKey = new ScriptParameterKey(
                        new ScriptKey(crs.getString("SCRIPT_ID"),
                                crs.getLong("SCRIPT_VRS_NB")),
                        crs.getString("SCRIPT_PAR_NM"));
                scriptParameters.add(new ScriptParameter(
                        scriptParameterKey,
                        SQLTools.getStringFromSQLClob(crs, "SCRIPT_PAR_VAL")));

            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return scriptParameters;
    }
}