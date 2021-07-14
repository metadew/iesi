package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
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

    public static synchronized ScriptLabelConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptLabelConfiguration();
        }
        return INSTANCE;
    }

    private ScriptLabelConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    @Override
    public Optional<ScriptLabel> get(ScriptLabelKey scriptLabelKey) {
        try {
            String queryScriptLabel = "select ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE from " + getMetadataRepository().getTableNameByLabel("ScriptLabels")
                    + " where ID = " + SQLTools.getStringForSQL(scriptLabelKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptLabel, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for {0}. Returning first implementation", scriptLabelKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptLabel(scriptLabelKey,
                    new ScriptVersionKey(new ScriptKey(cachedRowSet.getString("SCRIPT_ID")),
                            cachedRowSet.getLong("SCRIPT_VRS_NB"),
                            cachedRowSet.getString("DELETED_AT")),
                    cachedRowSet.getString("NAME"),
                    SQLTools.getStringFromSQLClob(cachedRowSet, "VALUE")
            ));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptLabel> getAll() {
        List<ScriptLabel> scriptLabels = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptLabels") + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (cachedRowSet.next()) {
                scriptLabels.add(new ScriptLabel(
                        new ScriptLabelKey(cachedRowSet.getString("ID")),
                        new ScriptVersionKey(new ScriptKey(cachedRowSet.getString("SCRIPT_ID")),
                                cachedRowSet.getLong("SCRIPT_VRS_NB"),
                                cachedRowSet.getString("DELETED_AT")),
                        cachedRowSet.getString("NAME"),
                        SQLTools.getStringFromSQLClob(cachedRowSet, "VALUE")
                ));

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
                " WHERE ID = " + SQLTools.getStringForSQL(scriptLabelKey.getId()) + ";";
    }

    @Override
    public void insert(ScriptLabel scriptLabel) {
        LOGGER.trace(MessageFormat.format("Inserting {0}.", scriptLabel.toString()));
        if (exists(scriptLabel)) {
            throw new MetadataAlreadyExistsException(scriptLabel);
        }
        getMetadataRepository().executeUpdate( "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptLabels") +
                " (ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE, DELETED_AT) VALUES (" +
                SQLTools.getStringForSQL(scriptLabel.getMetadataKey().getId()) + "," +
                SQLTools.getStringForSQL(scriptLabel.getScriptVersionKey().getScriptKey().getScriptId()) + "," +
                SQLTools.getStringForSQL(scriptLabel.getScriptVersionKey().getScriptVersion()) + "," +
                SQLTools.getStringForSQL(scriptLabel.getName()) + "," +
                SQLTools.getStringForSQLClob(scriptLabel.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + "," +
                SQLTools.getStringForSQL(scriptLabel.getScriptVersionKey().getDeletedAt()) + ");");
    }

    public boolean exists(ScriptLabelKey scriptLabelKey) {
        String queryScriptParameter = "select ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE from " + getMetadataRepository().getTableNameByLabel("ScriptLabels")
                + " where ID = " + SQLTools.getStringForSQL(scriptLabelKey.getId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptParameter, "reader");
        return cachedRowSet.size() >= 1;
    }

    public void softDeleteByScriptVersion(ScriptVersionKey scriptKey, String timeStamp) {
        LOGGER.trace(MessageFormat.format("deleting script labels for script {0}", scriptKey.toString()));
        String deleteStatement = "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptLabels") +
                " SET DELETED_AT = " + SQLTools.getStringForSQL(timeStamp) +
                " WHERE " +
                " SCRIPT_ID = " + SQLTools.getStringForSQL(scriptKey.getScriptKey().getScriptId()) + " AND " +
                " SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptKey.getScriptVersion()) +
                " AND DELETED_AT = " + SQLTools.getStringForSQL(scriptKey.getDeletedAt()) + " ;";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public void deleteByScriptVersion(ScriptVersionKey scriptKey) {
        LOGGER.trace(MessageFormat.format("deleting script labels for script {0}", scriptKey.toString()));
        String deleteStatement = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptLabels") +
                " WHERE " +
                " SCRIPT_ID = " + SQLTools.getStringForSQL(scriptKey.getScriptKey().getScriptId()) + " AND " +
                " SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptKey.getScriptVersion()) +
                " AND DELETED_AT = " + SQLTools.getStringForSQL(scriptKey.getDeletedAt()) + " ;";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public List<ScriptLabel> getByScriptVersion(ScriptVersionKey scriptKey) {
        List<ScriptLabel> scriptLabels = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptLabels")
                + " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptKey.getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptKey.getScriptVersion()) +
                " and DELETED_AT = " + SQLTools.getStringForSQL(scriptKey.getDeletedAt()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (cachedRowSet.next()) {
                scriptLabels.add(new ScriptLabel(
                        new ScriptLabelKey(cachedRowSet.getString("ID")),
                        new ScriptVersionKey(new ScriptKey(cachedRowSet.getString("SCRIPT_ID")),
                                cachedRowSet.getLong("SCRIPT_VRS_NB"),
                                cachedRowSet.getString("DELETED_AT")),
                        cachedRowSet.getString("NAME"),
                        SQLTools.getStringFromSQLClob(cachedRowSet, "VALUE")
                ));

            }
            cachedRowSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return scriptLabels;
    }

}