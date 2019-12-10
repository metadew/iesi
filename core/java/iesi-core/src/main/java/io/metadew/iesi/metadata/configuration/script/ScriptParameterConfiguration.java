package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptParameterAlreadyExistsException;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
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

public class ScriptParameterConfiguration extends Configuration<ScriptParameter, ScriptParameterKey> {

    private static ScriptParameterConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ScriptParameterConfiguration getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new ScriptParameterConfiguration();
        }
        return INSTANCE;
    }

    private ScriptParameterConfiguration() {}

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptParameter> get(ScriptParameterKey metadataKey) {
        return getScriptParameter(metadataKey.getScriptId(),
                metadataKey.getScriptVersionNumber(), metadataKey.getParameterName());
    }

    @Override
    public List<ScriptParameter> getAll() {
        List<ScriptParameter> scriptParameters = new ArrayList<>();
        String query = "select * from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ScriptParameters")
                + " order by SCRIPT_ID ASC";
        CachedRowSet crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ScriptParameterKey scriptParameterKey = new ScriptParameterKey(
                        crs.getString("SCRIPT_ID"),
                        crs.getLong("SCRIPT_VRS_NB"),
                        crs.getString("SCRIPT_PAR_NM"));
                scriptParameters.add(new ScriptParameter(
                        scriptParameterKey,
                        crs.getString("SCRIPT_PAR_VAL")));

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
    public void delete(ScriptParameterKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptParameter {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("ScriptParameter", metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptParameterKey metadataKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptParameters") +
                " WHERE " +
                " SCRIPT_ID = " + SQLTools.GetStringForSQL(metadataKey.getScriptId()) + " AND " +
                " SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(metadataKey.getScriptVersionNumber())+ " AND " +
                " SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(metadataKey.getParameterName()) + ";";
    }

    @Override
    public void insert(ScriptParameter metadata) throws MetadataAlreadyExistsException {
        ScriptParameterKey scriptParameterKey = metadata.getMetadataKey();
        insert(scriptParameterKey.getScriptId(), scriptParameterKey.getScriptVersionNumber(), metadata);
    }

    public void insert(String scriptId, long scriptVersionNumber, ScriptParameter scriptParameter) throws ScriptParameterAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptParameter {0}-{1}.", scriptId, scriptVersionNumber));
        if (exists(scriptId, scriptVersionNumber, scriptParameter)) {
            throw new ScriptParameterAlreadyExistsException(MessageFormat.format(
                    "ScriptParameter {0}-{1} already exists", scriptId, scriptVersionNumber));
        }
        getMetadataRepository().executeUpdate(getInsertStatement(scriptId, scriptVersionNumber, scriptParameter));


    }

    private boolean exists(String scriptId, long scriptVersionNumber, ScriptParameter scriptParameter) {
        String queryScriptParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from " + getMetadataRepository().getTableNameByLabel("ScriptParameters")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) + " and SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(scriptParameter.getName()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptParameter, "reader");
        return cachedRowSet.size() >= 1;
    }

    // Insert
    public String getInsertStatement(String scriptId, long scriptVersionNumber, ScriptParameter scriptParameter) {
        return "INSERT INTO " + getMetadataRepository()
                .getTableNameByLabel("ScriptParameters") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + "," +
                scriptVersionNumber + "," +
                SQLTools.GetStringForSQL(scriptParameter.getName()) + "," +
                SQLTools.GetStringForSQL(scriptParameter.getValue()) + ");";
    }


    public Optional<ScriptParameter> getScriptParameter(String scriptId, long scriptVersionNumber, String scriptParameterName) {
        try {
            String queryScriptParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from " + getMetadataRepository().getTableNameByLabel("ScriptParameters")
                    + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) + " and SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(scriptParameterName) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptParameter, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for ScriptParameter {0}-{1}-{2}. Returning first implementation", scriptId, scriptVersionNumber, scriptParameterName));
            }
            cachedRowSet.next();
            ScriptParameterKey scriptParameterKey = new ScriptParameterKey(scriptId, scriptVersionNumber, scriptParameterName);
            return Optional.of(new ScriptParameter(scriptParameterKey, cachedRowSet.getString("ACTION_PAR_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}