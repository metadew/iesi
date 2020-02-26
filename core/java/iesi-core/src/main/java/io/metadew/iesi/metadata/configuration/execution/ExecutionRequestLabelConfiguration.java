package io.metadew.iesi.metadata.configuration.execution;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExecutionRequestLabelConfiguration extends Configuration<ExecutionRequestLabel, ExecutionRequestLabelKey> {

    private static ExecutionRequestLabelConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ExecutionRequestLabelConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExecutionRequestLabelConfiguration();
        }
        return INSTANCE;
    }

    private ExecutionRequestLabelConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ExecutionRequestLabel> get(ExecutionRequestLabelKey scriptLabelKey) {
        try {
            String queryScriptLabel = "select ID, REQUEST_ID, NAME, VALUE from " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels")
                    + " where ID = " + SQLTools.GetStringForSQL(scriptLabelKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptLabel, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for {0}. Returning first implementation", scriptLabelKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ExecutionRequestLabel(scriptLabelKey,
                    new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
                    cachedRowSet.getString("NAME"),
                    cachedRowSet.getString("VALUE")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExecutionRequestLabel> getAll() {
        List<ExecutionRequestLabel> scriptLabels = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels");
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (cachedRowSet.next()) {
                scriptLabels.add(new ExecutionRequestLabel(
                        new ExecutionRequestLabelKey(cachedRowSet.getString("ID")),
                        new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
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
    public void delete(ExecutionRequestLabelKey scriptLabelKey) {
        LOGGER.trace(MessageFormat.format("Deleting {0}.", scriptLabelKey.toString()));
        if (!exists(scriptLabelKey)) {
            throw new MetadataDoesNotExistException(scriptLabelKey);
        }
        String deleteStatement = deleteStatement(scriptLabelKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ExecutionRequestLabelKey scriptLabelKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels") +
                " WHERE ID = " + SQLTools.GetStringForSQL(scriptLabelKey.getId()) + ";";
    }

    @Override
    public void insert(ExecutionRequestLabel scriptLabel) {
        LOGGER.trace(MessageFormat.format("Inserting {0}.", scriptLabel.toString()));
        if (exists(scriptLabel)) {
            throw new MetadataAlreadyExistsException(scriptLabel);
        }
        getMetadataRepository().executeUpdate( "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels") +
                " (ID, REQUEST_ID, NAME, VALUE) VALUES (" +
                SQLTools.GetStringForSQL(scriptLabel.getMetadataKey().getId()) + "," +
                SQLTools.GetStringForSQL(scriptLabel.getExecutionRequestKey().getId()) + "," +
                SQLTools.GetStringForSQL(scriptLabel.getName()) + "," +
                SQLTools.GetStringForSQL(scriptLabel.getValue()) + ");");

    }

    public boolean exists(ExecutionRequestLabelKey executionRequestLabelKey) {
        String queryScriptParameter = "select ID, REQUEST_ID, NAME, VALUE from " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels")
                + " where ID = " + SQLTools.GetStringForSQL(executionRequestLabelKey.getId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptParameter, "reader");
        return cachedRowSet.size() >= 1;
    }

    public void deleteByExecutionRequest(ExecutionRequestKey scriptKey) {
        String deleteStatement = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels") +
                " WHERE " +
                " REQUEST_ID = " + SQLTools.GetStringForSQL(scriptKey.getId()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public List<ExecutionRequestLabel> getByExecutionRequest(ExecutionRequestKey scriptKey) {
        List<ExecutionRequestLabel> scriptLabels = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels")
                + " where REQUEST_ID = " + SQLTools.GetStringForSQL(scriptKey.getId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (cachedRowSet.next()) {
                scriptLabels.add(new ExecutionRequestLabel(
                        new ExecutionRequestLabelKey(cachedRowSet.getString("ID")),
                        new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
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