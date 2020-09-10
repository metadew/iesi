package io.metadew.iesi.metadata.configuration.execution;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

@Log4j2
public class ExecutionRequestLabelConfiguration extends Configuration<ExecutionRequestLabel, ExecutionRequestLabelKey> {

    private static ExecutionRequestLabelConfiguration INSTANCE;

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
    public Optional<ExecutionRequestLabel> get(ExecutionRequestLabelKey executionRequestLabelKey) {
        try {
            String queryScriptLabel = "select ID, REQUEST_ID, NAME, VALUE from " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels")
                    + " where ID = " + SQLTools.GetStringForSQL(executionRequestLabelKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptLabel, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                log.info(MessageFormat.format("Found multiple implementations for {0}. Returning first implementation", executionRequestLabelKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ExecutionRequestLabel(executionRequestLabelKey,
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
    public void delete(ExecutionRequestLabelKey executionRequestLabelKey) {
        log.trace(MessageFormat.format("Deleting {0}.", executionRequestLabelKey.toString()));
        if (!exists(executionRequestLabelKey)) {
            throw new MetadataDoesNotExistException(executionRequestLabelKey);
        }
        String deleteStatement = deleteStatement(executionRequestLabelKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ExecutionRequestLabelKey executionRequestLabelKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels") +
                " WHERE ID = " + SQLTools.GetStringForSQL(executionRequestLabelKey.getId()) + ";";
    }

    @Override
    public void insert(ExecutionRequestLabel executionRequestLabel) {
        log.trace(MessageFormat.format("Inserting {0}.", executionRequestLabel.toString()));
        if (exists(executionRequestLabel)) {
            throw new MetadataAlreadyExistsException(executionRequestLabel);
        }
        getMetadataRepository().executeUpdate( "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels") +
                " (ID, REQUEST_ID, NAME, VALUE) VALUES (" +
                SQLTools.GetStringForSQL(executionRequestLabel.getMetadataKey().getId()) + "," +
                SQLTools.GetStringForSQL(executionRequestLabel.getExecutionRequestKey().getId()) + "," +
                SQLTools.GetStringForSQL(executionRequestLabel.getName()) + "," +
                SQLTools.GetStringForSQL(executionRequestLabel.getValue()) + ");");

    }

    public boolean exists(ExecutionRequestLabelKey executionRequestLabelKey) {
        String queryScriptParameter = "select ID, REQUEST_ID, NAME, VALUE from " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels")
                + " where ID = " + SQLTools.GetStringForSQL(executionRequestLabelKey.getId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScriptParameter, "reader");
        return cachedRowSet.size() >= 1;
    }

    public void deleteByExecutionRequest(ExecutionRequestKey executionRequestKey) {
        String deleteStatement = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels") +
                " WHERE " +
                " REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public Set<ExecutionRequestLabel> getByExecutionRequest(ExecutionRequestKey executionRequestKey) {
        Set<ExecutionRequestLabel> scriptLabels = new HashSet<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels")
                + " where REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";";
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

    public void update(ExecutionRequestLabel executionRequestLabel) {
        if (!exists(executionRequestLabel.getMetadataKey())) {
            throw new MetadataDoesNotExistException(executionRequestLabel);
        }
        getMetadataRepository().executeUpdate(updateStatement(executionRequestLabel));
    }

    private String updateStatement(ExecutionRequestLabel executionRequestLabel) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ExecutionRequestLabels") + " SET " +
                "REQUEST_ID=" + SQLTools.GetStringForSQL(executionRequestLabel.getExecutionRequestKey().getId()) + "," +
                "NAME=" + SQLTools.GetStringForSQL(executionRequestLabel.getName()) + "," +
                "VALUE=" + SQLTools.GetStringForSQL(executionRequestLabel.getValue()) +
                " WHERE " +
                "REQUEST_ID =" + SQLTools.GetStringForSQL(executionRequestLabel.getMetadataKey().getId()) + ";";
    }
}