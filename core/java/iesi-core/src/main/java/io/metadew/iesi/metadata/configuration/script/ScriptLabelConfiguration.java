package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptLabelConfiguration extends Configuration<ScriptLabel, ScriptLabelKey> {

    private static ScriptLabelConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ScriptLabelConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptLabelConfiguration();
        }
        return INSTANCE;
    }

    private ScriptLabelConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptLabel> get(ScriptLabelKey scriptLabelKey) {
        try {
            String queryScriptLabel = "select ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE from " + getMetadataRepository().getTableNameByLabel("ScriptLabels")
                    + " where ID = " + SQLTools.GetStringForSQL(scriptLabelKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptLabel, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for {0}. Returning first implementation", scriptLabelKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptLabel(scriptLabelKey,
                    new ScriptKey(cachedRowSet.getString("SCRIPT_ID"), cachedRowSet.getLong("SCRIPT_VRS_NB")),
                    cachedRowSet.getString("NAME"),
                    cachedRowSet.getString("VALUE")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptLabel> getAll() {
        List<ScriptLabel> scriptLabels = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptLabels");
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (cachedRowSet.next()) {
                scriptLabels.add(new ScriptLabel(
                        new ScriptLabelKey(cachedRowSet.getString("ID")),
                        new ScriptKey(cachedRowSet.getString("SCRIPT_ID"), cachedRowSet.getLong("SCRIPT_VRS_NB")),
                        cachedRowSet.getString("NAME"),
                        cachedRowSet.getString("VALUE")));

            }
            cachedRowSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return scriptLabels;
    }

    @Override
    public void delete(ScriptLabelKey scriptLabelKey) {
        LOGGER.trace(MessageFormat.format("Deleting {0}.", scriptLabelKey.toString()));
        if (!exists(scriptLabelKey)) {
            throw new MetadataDoesNotExistException(scriptLabelKey);
        }
        String deleteStatement = deleteStatement(scriptLabelKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptLabelKey scriptLabelKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptLabels") +
                " WHERE ID = " + SQLTools.GetStringForSQL(scriptLabelKey.getId()) + ";";
    }

    @Override
    public void insert(ScriptLabel scriptLabel) {
        LOGGER.trace(MessageFormat.format("Inserting {0}.", scriptLabel.toString()));
        if (exists(scriptLabel)) {
            throw new MetadataAlreadyExistsException(scriptLabel);
        }
        getMetadataRepository().executeUpdate( "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptLabels") +
                " (ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE) VALUES (" +
                SQLTools.GetStringForSQL(scriptLabel.getMetadataKey().getId()) + "," +
                SQLTools.GetStringForSQL(scriptLabel.getScriptKey().getScriptId()) + "," +
                SQLTools.GetStringForSQL(scriptLabel.getScriptKey().getScriptVersion()) + "," +
                SQLTools.GetStringForSQL(scriptLabel.getName()) + "," +
                SQLTools.GetStringForSQL(scriptLabel.getValue()) + ");");

    }

    public boolean exists(ScriptLabelKey scriptLabelKey) {
        String queryScriptParameter = "select ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE from " + getMetadataRepository().getTableNameByLabel("ScriptLabels")
                + " where ID = " + SQLTools.GetStringForSQL(scriptLabelKey.getId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptParameter, "reader");
        return cachedRowSet.size() >= 1;
    }

    public void deleteByScript(ScriptKey scriptKey) {
        LOGGER.trace(MessageFormat.format("deleting script labels for script {0}", scriptKey.toString()));
        String deleteStatement = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptLabels") +
                " WHERE " +
                " SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptKey.getScriptId()) + " AND " +
                " SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptKey.getScriptVersion()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public List<ScriptLabel> getByScript(ScriptKey scriptKey) {
        List<ScriptLabel> scriptLabels = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptLabels")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptKey.getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptKey.getScriptVersion()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (cachedRowSet.next()) {
                scriptLabels.add(new ScriptLabel(
                        new ScriptLabelKey(cachedRowSet.getString("ID")),
                        new ScriptKey(cachedRowSet.getString("SCRIPT_ID"), cachedRowSet.getLong("SCRIPT_VRS_NB")),
                        cachedRowSet.getString("NAME"),
                        cachedRowSet.getString("VALUE")));

            }
            cachedRowSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return scriptLabels;
    }
}