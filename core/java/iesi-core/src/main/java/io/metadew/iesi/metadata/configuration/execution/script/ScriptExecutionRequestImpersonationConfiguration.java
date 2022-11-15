package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestImpersonation;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

@Log4j2
@Component
public class ScriptExecutionRequestImpersonationConfiguration extends Configuration<ScriptExecutionRequestImpersonation, ScriptExecutionRequestImpersonationKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public ScriptExecutionRequestImpersonationConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getExecutionServerMetadataRepository());
    }

    @Override
    public Optional<ScriptExecutionRequestImpersonation> get(ScriptExecutionRequestImpersonationKey scriptExecutionRequestKey) {
        try {
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, IMP_ID FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                    " WHERE ID = " + SQLTools.getStringForSQL(scriptExecutionRequestKey.getId()) + ";";
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
                " WHERE ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";";
    }

    private String deleteStatement(ScriptExecutionRequestKey executionRequestKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                " WHERE SCRIPT_EXEC_REQ_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";";
    }

    private String deleteStatement(ImpersonationKey executionRequestKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                " WHERE IMP_ID = " + SQLTools.getStringForSQL(executionRequestKey.getName()) + ";";
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
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                " (ID, SCRIPT_EXEC_REQ_ID, IMP_ID) VALUES (" +
                SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                SQLTools.getStringForSQL(scriptExecutionRequest.getScriptExecutionRequestKey().getId()) + ", " +
                SQLTools.getStringForSQL(scriptExecutionRequest.getImpersonationKey().getName()) + ");";
    }

    public Set<ScriptExecutionRequestImpersonation> getByScriptExecutionRequest(ScriptExecutionRequestKey executionRequestKey) {
        try {
            List<ScriptExecutionRequestImpersonation> scriptExecutionRequestParameters = new ArrayList<>();
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, IMP_ID FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                    " WHERE SCRIPT_EXEC_REQ_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutionRequestParameters.add(new ScriptExecutionRequestImpersonation(
                        new ScriptExecutionRequestImpersonationKey(cachedRowSet.getString("ID")),
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRIPT_EXEC_REQ_ID")),
                        new ImpersonationKey(cachedRowSet.getString("IMP_ID"))));
            }
            return new HashSet<>(scriptExecutionRequestParameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ScriptExecutionRequestImpersonation> getByImpersonation(ImpersonationKey executionRequestKey) {
        try {
            List<ScriptExecutionRequestImpersonation> scriptExecutionRequestParameters = new ArrayList<>();
            String query = "SELECT ID, SCRIPT_EXEC_REQ_ID, IMP_ID FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutionRequestImpersonations") +
                    " WHERE IMP_ID = " + SQLTools.getStringForSQL(executionRequestKey.getName()) + ";";
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
                "SCRIPT_EXEC_REQ_ID=" + SQLTools.getStringForSQL(scriptExecutionRequest.getScriptExecutionRequestKey().getId()) + ", " +
                "IMP_ID=" + SQLTools.getStringForSQL(scriptExecutionRequest.getImpersonationKey().getName()) + "," +
                " WHERE " +
                "ID = " + SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + ";";
    }
}
