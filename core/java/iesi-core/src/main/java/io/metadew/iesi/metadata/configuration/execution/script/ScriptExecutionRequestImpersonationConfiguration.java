package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestImpersonation;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ScriptExecutionRequestImpersonationConfiguration extends Configuration<ScriptExecutionRequestImpersonation, ScriptExecutionRequestImpersonationKey> {

    private static ScriptExecutionRequestImpersonationConfiguration INSTANCE;

    public synchronized static ScriptExecutionRequestImpersonationConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptExecutionRequestImpersonationConfiguration();
        }
        return INSTANCE;
    }

    private ScriptExecutionRequestImpersonationConfiguration() {}

    // Constructors
    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptExecutionRequestImpersonation> get(ScriptExecutionRequestImpersonationKey scriptExecutionRequestKey) {
        try {
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, IMP_ID FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                    " WHERE ID = " + SQLTools.GetStringForSQL(scriptExecutionRequestKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                log.warn(MessageFormat.format("Found multiple implementations for {0}. Returning first implementation", scriptExecutionRequestKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptExecutionRequestImpersonation(scriptExecutionRequestKey,
                    new ScriptExecutionRequestKey(cachedRowSet.getString("SCRIPT_EXEC_REQ_ID")),
                    new ImpersonationKey(cachedRowSet.getString("IMP_ID"))));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptExecutionRequestImpersonation> getAll() {
        try {
            List<ScriptExecutionRequestImpersonation> scriptExecutionRequests = new ArrayList<>();
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, IMP_ID FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutionRequests.add(new ScriptExecutionRequestImpersonation(
                        new ScriptExecutionRequestImpersonationKey(cachedRowSet.getString("ID")),
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRIPT_EXEC_REQ_ID")),
                        new ImpersonationKey(cachedRowSet.getString("IMP_ID"))));
            }
            return scriptExecutionRequests;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptExecutionRequestImpersonationKey scriptExecutionRequestKey) {
        log.trace(MessageFormat.format("Deleting {0}.", scriptExecutionRequestKey.toString()));
        if (!exists(scriptExecutionRequestKey)) {
            throw new MetadataDoesNotExistException(scriptExecutionRequestKey);
        }
        getMetadataRepository().executeUpdate(deleteStatement(scriptExecutionRequestKey));
    }

    private String deleteStatement(ScriptExecutionRequestImpersonationKey executionRequestKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                " WHERE ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";";
    }

    private String deleteStatement(ScriptExecutionRequestKey executionRequestKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                " WHERE SCRIPT_EXEC_REQ_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";";
    }

    private String deleteStatement(ImpersonationKey executionRequestKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                " WHERE IMP_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getName()) + ";";
    }


    @Override
    public void insert(ScriptExecutionRequestImpersonation scriptExecutionRequest) {
        log.trace(MessageFormat.format("Inserting ScriptExecutionRequest {0}.", scriptExecutionRequest.toString()));
        if (exists(scriptExecutionRequest.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(scriptExecutionRequest);
        }
        getMetadataRepository().executeUpdate(insertStatement(scriptExecutionRequest));
    }

    public String insertStatement(ScriptExecutionRequestImpersonation scriptExecutionRequest) {
        return  "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                " (ID, SCRIPT_EXEC_REQ_ID, IMP_ID) VALUES (" +
                SQLTools.GetStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                SQLTools.GetStringForSQL(scriptExecutionRequest.getScriptExecutionRequestKey().getId()) + ", " +
                SQLTools.GetStringForSQL(scriptExecutionRequest.getImpersonationKey().getName()) + ");";
    }

    public List<ScriptExecutionRequestImpersonation> getByScriptExecutionRequest(ScriptExecutionRequestKey executionRequestKey) {
        try {
            List<ScriptExecutionRequestImpersonation> scriptExecutionRequestParameters = new ArrayList<>();
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, IMP_ID FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                    " WHERE SCRIPT_EXEC_REQ_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutionRequestParameters.add(new ScriptExecutionRequestImpersonation(
                        new ScriptExecutionRequestImpersonationKey(cachedRowSet.getString("ID")),
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRIPT_EXEC_REQ_ID")),
                        new ImpersonationKey(cachedRowSet.getString("IMP_ID"))));
            }
            return scriptExecutionRequestParameters;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ScriptExecutionRequestImpersonation> getByImpersonation(ImpersonationKey executionRequestKey) {
        try {
            List<ScriptExecutionRequestImpersonation> scriptExecutionRequestParameters = new ArrayList<>();
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, IMP_ID FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                    " WHERE IMP_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutionRequestParameters.add(new ScriptExecutionRequestImpersonation(
                        new ScriptExecutionRequestImpersonationKey(cachedRowSet.getString("ID")),
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRIPT_EXEC_REQ_ID")),
                        new ImpersonationKey(cachedRowSet.getString("IMP_ID"))));
            }
            return scriptExecutionRequestParameters;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByScriptExecutionRequest(ScriptExecutionRequestKey executionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequestImpersonation by {0}.", executionRequestKey.toString()));
        getMetadataRepository().executeUpdate(deleteStatement(executionRequestKey));
    }

    public void deleteByImpersonation(ImpersonationKey executionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequestImpersonation by {0}.", executionRequestKey.toString()));
        getMetadataRepository().executeUpdate(deleteStatement(executionRequestKey));
    }

    @Override
    public void update(ScriptExecutionRequestImpersonation executionRequest) {
        if (!exists(executionRequest.getMetadataKey())) {
            throw new MetadataDoesNotExistException(executionRequest);
        }
        getMetadataRepository().executeUpdate(updateStatement(executionRequest));

    }

    public String updateStatement(ScriptExecutionRequestImpersonation scriptExecutionRequest) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") + " SET " +
                "SCRIPT_EXEC_REQ_ID=" + SQLTools.GetStringForSQL(scriptExecutionRequest.getScriptExecutionRequestKey().getId()) + ", " +
                "IMP_ID=" + SQLTools.GetStringForSQL(scriptExecutionRequest.getImpersonationKey().getName()) + "," +
                " WHERE " +
                "ID = " + SQLTools.GetStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + ";";
    }
}
