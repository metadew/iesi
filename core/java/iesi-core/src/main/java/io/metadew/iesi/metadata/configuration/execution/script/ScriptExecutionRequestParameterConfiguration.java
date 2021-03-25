package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.service.metadata.MetadataFieldService;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

@Log4j2
public class ScriptExecutionRequestParameterConfiguration extends Configuration<ScriptExecutionRequestParameter, ScriptExecutionRequestParameterKey> {

    private static ScriptExecutionRequestParameterConfiguration INSTANCE;

    public synchronized static ScriptExecutionRequestParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptExecutionRequestParameterConfiguration();
        }
        return INSTANCE;
    }

    private ScriptExecutionRequestParameterConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getExecutionServerMetadataRepository());
    }

    @Override
    public Optional<ScriptExecutionRequestParameter> get(ScriptExecutionRequestParameterKey scriptExecutionRequestKey) {
        try {
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, NAME, VALUE FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestParameters") +
                    " WHERE ID = " + SQLTools.getStringForSQL(scriptExecutionRequestKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                log.warn(MessageFormat.format("Found multiple implementations for {0}. Returning first implementation", scriptExecutionRequestKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptExecutionRequestParameter(scriptExecutionRequestKey,
                    new ScriptExecutionRequestKey(cachedRowSet.getString("SCRIPT_EXEC_REQ_ID")),
                    cachedRowSet.getString("NAME"),
                    SQLTools.getStringFromSQLClob(cachedRowSet, "VALUE")));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptExecutionRequestParameter> getAll() {
        try {
            List<ScriptExecutionRequestParameter> scriptExecutionRequests = new ArrayList<>();
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, NAME, VALUE FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestParameters") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutionRequests.add(new ScriptExecutionRequestParameter(
                        new ScriptExecutionRequestParameterKey(cachedRowSet.getString("ID")),
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRIPT_EXEC_REQ_ID")),
                        cachedRowSet.getString("NAME"),
                        SQLTools.getStringFromSQLClob(cachedRowSet, "VALUE")));
            }

            return scriptExecutionRequests;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptExecutionRequestParameterKey scriptExecutionRequestKey) {
        log.trace(MessageFormat.format("Deleting {0}.", scriptExecutionRequestKey.toString()));
        if (!exists(scriptExecutionRequestKey)) {
            throw new MetadataDoesNotExistException(scriptExecutionRequestKey);
        }
        getMetadataRepository().executeUpdate(deleteStatement(scriptExecutionRequestKey));
    }


    @Override
    public void insert(ScriptExecutionRequestParameter scriptExecutionRequest) {
        log.trace(MessageFormat.format("Inserting ScriptExecutionRequest {0}.", scriptExecutionRequest.toString()));
        if (exists(scriptExecutionRequest.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(scriptExecutionRequest);
        }
        getMetadataRepository().executeUpdate(insertStatement(scriptExecutionRequest));
    }

    public String insertStatement(ScriptExecutionRequestParameter scriptExecutionRequest) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestParameters") +
                " (ID, SCRIPT_EXEC_REQ_ID, NAME, VALUE) VALUES (" +
                SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                SQLTools.getStringForSQL(scriptExecutionRequest.getScriptExecutionRequestKey().getId()) + ", " +
                SQLTools.getStringForSQL(scriptExecutionRequest.getName()) + "," +
                SQLTools.getStringForSQLClob(scriptExecutionRequest.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + ");";
    }

    public Set<ScriptExecutionRequestParameter> getByScriptExecutionRequest(ScriptExecutionRequestKey executionRequestKey) {
        try {
            List<ScriptExecutionRequestParameter> scriptExecutionRequestParameters = new ArrayList<>();
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, NAME, VALUE FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestParameters") +
                    " WHERE SCRIPT_EXEC_REQ_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutionRequestParameters.add(new ScriptExecutionRequestParameter(
                        new ScriptExecutionRequestParameterKey(cachedRowSet.getString("ID")),
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRIPT_EXEC_REQ_ID")),
                        cachedRowSet.getString("NAME"),
                        SQLTools.getStringFromSQLClob(cachedRowSet, "VALUE")));
            }
            return new HashSet<>(scriptExecutionRequestParameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByScriptExecutionRequest(ScriptExecutionRequestKey executionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequest by ExecutionKey {0}.", executionRequestKey.toString()));
        getMetadataRepository().executeUpdate(deleteStatement(executionRequestKey));
    }

    private String deleteStatement(ScriptExecutionRequestKey executionRequestKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestParameters") +
                " WHERE SCRIPT_EXEC_REQ_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";";
    }

    private String deleteStatement(ScriptExecutionRequestParameterKey executionRequestKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestParameters") +
                " WHERE ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";";
    }

    @Override
    public void update(ScriptExecutionRequestParameter executionRequest) {
        if (!exists(executionRequest.getMetadataKey())) {
            throw new MetadataDoesNotExistException(executionRequest);
        }
        getMetadataRepository().executeUpdate(updateStatement(executionRequest));

    }

    public String updateStatement(ScriptExecutionRequestParameter scriptExecutionRequest) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestParameters") + " SET " +
                "NAME=" + SQLTools.getStringForSQL(scriptExecutionRequest.getName()) + "," +
                SQLTools.getStringForSQLClob(scriptExecutionRequest.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) +
                " WHERE ID = " + SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + ";";
    }
}
