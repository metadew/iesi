package io.metadew.iesi.metadata.configuration.execution;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.*;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ExecutionRequestConfiguration extends Configuration<ExecutionRequest, ExecutionRequestKey> {

    private static final Logger LOGGER = LogManager.getLogger();

    private static ExecutionRequestConfiguration INSTANCE;

    public synchronized static ExecutionRequestConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExecutionRequestConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ExecutionRequestConfiguration() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(MetadataRepositoryConfiguration.getInstance()
                .getDesignMetadataRepository()
                .getRepositoryCoordinator()
                .getDatabases().values().stream()
                .findFirst()
                .map(Database::getConnectionPool)
                .orElseThrow(RuntimeException::new));
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    private final static String query = "SELECT EXECUTION_REQUEST.REQUEST_ID AS EXECUTION_REQUEST_REQUEST_ID, EXECUTION_REQUEST.REQUEST_TMS as EXECUTION_REQUEST_REQUEST_TMS, EXECUTION_REQUEST.REQUEST_NM as EXECUTION_REQUEST_REQUEST_NM, " +
            " EXECUTION_REQUEST.REQUEST_DSC as EXECUTION_REQUEST_REQUEST_DSC, EXECUTION_REQUEST.NOTIF_EMAIL as EXECUTION_REQUEST_NOTIF_EMAIL, EXECUTION_REQUEST.SCOPE_NM as EXECUTION_REQUEST_SCOPE_NM, EXECUTION_REQUEST.CONTEXT_NM as EXECUTION_REQUEST_CONTEXT_NM  , EXECUTION_REQUEST.ST_NM as EXECUTION_REQUEST_ST_NM, " +
            " AUTH_EXECUTION_REQUEST.SPACE_NM as AUTH_EXECUTION_REQUEST_SPACE_NM, AUTH_EXECUTION_REQUEST.USER_NM as AUTH_EXECUTION_REQUEST_USER_NM, AUTH_EXECUTION_REQUEST.USER_PASSWORD as AUTH_EXECUTION_REQUEST_USER_PASSWORD , " +
            " AUTH_EXECUTION_REQUEST.REQUEST_ID AS AUTH, " +
            " NON_AUTH_EXECUTION_REQUEST.REQUEST_ID AS NON_AUTH ," +
            " ExecutionRequestLabels.ID as ExecutionRequestLabels_ID, ExecutionRequestLabels.REQUEST_ID as ExecutionRequestLabels_REQUEST_ID, ExecutionRequestLabels.NAME as ExecutionRequestLabels_NAME , ExecutionRequestLabels.VALUE as ExecutionRequestLabels_VALUE " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " EXECUTION_REQUEST " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " AUTH_EXECUTION_REQUEST " +
            " ON EXECUTION_REQUEST.REQUEST_ID = AUTH_EXECUTION_REQUEST.REQUEST_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " NON_AUTH_EXECUTION_REQUEST " +
            " ON EXECUTION_REQUEST.REQUEST_ID = NON_AUTH_EXECUTION_REQUEST.REQUEST_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " ExecutionRequestLabels on EXECUTION_REQUEST.REQUEST_ID = ExecutionRequestLabels.REQUEST_ID" +
            " WHERE EXECUTION_REQUEST.REQUEST_ID = :id ;";
    private final static String getAll = "SELECT EXECUTION_REQUEST.REQUEST_ID AS EXECUTION_REQUEST_REQUEST_ID, EXECUTION_REQUEST.REQUEST_TMS as EXECUTION_REQUEST_REQUEST_TMS, EXECUTION_REQUEST.REQUEST_NM as EXECUTION_REQUEST_REQUEST_NM, " +
            " EXECUTION_REQUEST.REQUEST_DSC as EXECUTION_REQUEST_REQUEST_DSC, EXECUTION_REQUEST.NOTIF_EMAIL as EXECUTION_REQUEST_NOTIF_EMAIL, EXECUTION_REQUEST.SCOPE_NM as EXECUTION_REQUEST_SCOPE_NM, EXECUTION_REQUEST.CONTEXT_NM as EXECUTION_REQUEST_CONTEXT_NM  , EXECUTION_REQUEST.ST_NM as EXECUTION_REQUEST_ST_NM, " +
            " AUTH_EXECUTION_REQUEST.SPACE_NM as AUTH_EXECUTION_REQUEST_SPACE_NM, AUTH_EXECUTION_REQUEST.USER_NM as AUTH_EXECUTION_REQUEST_USER_NM, AUTH_EXECUTION_REQUEST.USER_PASSWORD as AUTH_EXECUTION_REQUEST_USER_PASSWORD , " +
            " AUTH_EXECUTION_REQUEST.REQUEST_ID AS AUTH, " +
            " NON_AUTH_EXECUTION_REQUEST.REQUEST_ID AS NON_AUTH ," +
            " ExecutionRequestLabels.ID as ExecutionRequestLabels_ID, ExecutionRequestLabels.REQUEST_ID as ExecutionRequestLabels_REQUEST_ID, ExecutionRequestLabels.NAME as ExecutionRequestLabels_NAME , ExecutionRequestLabels.VALUE as ExecutionRequestLabels_VALUE " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " EXECUTION_REQUEST " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " AUTH_EXECUTION_REQUEST " +
            " ON EXECUTION_REQUEST.REQUEST_ID = AUTH_EXECUTION_REQUEST.REQUEST_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " NON_AUTH_EXECUTION_REQUEST " +
            " ON EXECUTION_REQUEST.REQUEST_ID = NON_AUTH_EXECUTION_REQUEST.REQUEST_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " ExecutionRequestLabels on EXECUTION_REQUEST.REQUEST_ID = ExecutionRequestLabels.REQUEST_ID" +
            " ;";
    private final static String getAllNew = "SELECT EXECUTION_REQUEST.REQUEST_ID AS EXECUTION_REQUEST_REQUEST_ID, EXECUTION_REQUEST.REQUEST_TMS as EXECUTION_REQUEST_REQUEST_TMS, EXECUTION_REQUEST.REQUEST_NM as EXECUTION_REQUEST_REQUEST_NM, " +
            " EXECUTION_REQUEST.REQUEST_DSC as EXECUTION_REQUEST_REQUEST_DSC, EXECUTION_REQUEST.NOTIF_EMAIL as EXECUTION_REQUEST_NOTIF_EMAIL, EXECUTION_REQUEST.SCOPE_NM as EXECUTION_REQUEST_SCOPE_NM, EXECUTION_REQUEST.CONTEXT_NM as EXECUTION_REQUEST_CONTEXT_NM  , EXECUTION_REQUEST.ST_NM as EXECUTION_REQUEST_ST_NM, " +
            " AUTH_EXECUTION_REQUEST.SPACE_NM as AUTH_EXECUTION_REQUEST_SPACE_NM, AUTH_EXECUTION_REQUEST.USER_NM as AUTH_EXECUTION_REQUEST_USER_NM, AUTH_EXECUTION_REQUEST.USER_PASSWORD as AUTH_EXECUTION_REQUEST_USER_PASSWORD , " +
            " AUTH_EXECUTION_REQUEST.REQUEST_ID AS AUTH, " +
            " NON_AUTH_EXECUTION_REQUEST.REQUEST_ID AS NON_AUTH ," +
            " ExecutionRequestLabels.ID as ExecutionRequestLabels_ID, ExecutionRequestLabels.REQUEST_ID as ExecutionRequestLabels_REQUEST_ID, ExecutionRequestLabels.NAME as ExecutionRequestLabels_NAME , ExecutionRequestLabels.VALUE as ExecutionRequestLabels_VALUE " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " EXECUTION_REQUEST " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " AUTH_EXECUTION_REQUEST " +
            "ON EXECUTION_REQUEST.REQUEST_ID = AUTH_EXECUTION_REQUEST.REQUEST_ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " NON_AUTH_EXECUTION_REQUEST " +
            "ON EXECUTION_REQUEST.REQUEST_ID = NON_AUTH_EXECUTION_REQUEST.REQUEST_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " ExecutionRequestLabels on EXECUTION_REQUEST.REQUEST_ID = ExecutionRequestLabels.REQUEST_ID" +
            " WHERE EXECUTION_REQUEST.ST_NM = :name  ;";
    private static final String deleteExecutionRequests = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " WHERE REQUEST_ID = :id  ;";
    private static final String deleteAuthenticatedExecutionRequests = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " WHERE REQUEST_ID = :id  ;";
    private static final String deleteNonAuthenticatedExecutionRequests = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " WHERE REQUEST_ID = :id  ;";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() +
            " (REQUEST_ID, REQUEST_TMS, REQUEST_NM, REQUEST_DSC, NOTIF_EMAIL, SCOPE_NM, CONTEXT_NM, ST_NM) VALUES (:id, :tm, :name, :desc, :email, :scope, :context, :execution)";
    private final static String insertAuthenticatedExecutionRequest = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() +
            " (REQUEST_ID, SPACE_NM, USER_NM, USER_PASSWORD) VALUES (:id, :space, :user, :password)";
    private final static String insertNonAuthenticatedExecutionRequest = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() +
            " (REQUEST_ID) VALUES (:id)";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() +
            " SET REQUEST_TMS = :tm, REQUEST_NM = :name, REQUEST_DSC = :description, NOTIF_EMAIL = :email, SCOPE_NM = :scope, " +
            "CONTEXT_NM = :context, " +
            "ST_NM = :execution  WHERE SCRIPT_ID = :id ; ";
    private final static String updateAuthenticatedExecutionRequest = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() +
            " SET SPACE_NM = :tm, USER_NM = :name, USER_PASSWORD = :description " + " WHERE REQUEST_ID = :id ; ";

    @Override
    public Optional<ExecutionRequest> get(ExecutionRequestKey executionRequestKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestKey.getId());
        ExecutionRequest executionRequest =
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        query,
                        sqlParameterSource,
                        new ExecutionRequestExtractor()));
        List<ScriptExecutionRequest> scriptExecutionRequests = ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(executionRequestKey);
        executionRequest.setScriptExecutionRequests(scriptExecutionRequests);
        return Optional.ofNullable(executionRequest);
    }

    @Override
    public List<ExecutionRequest> getAll() {
        List<ExecutionRequest> executionRequests = namedParameterJdbcTemplate.query(getAll, new ExecutionRequestExtractor());
        for (ExecutionRequest executionRequest : executionRequests) {
            List<ScriptExecutionRequest> scriptExecutionRequests = ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(executionRequest.getMetadataKey());
            executionRequest.setScriptExecutionRequests(scriptExecutionRequests);
        }
        return executionRequests;
    }

    public List<ExecutionRequest> getAllNew() {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", ExecutionRequestStatus.NEW.value());
        System.out.println(ExecutionRequestStatus.NEW.value());
        List<ExecutionRequest> executionRequests = namedParameterJdbcTemplate.query(
                getAllNew,
                sqlParameterSource,
                new ExecutionRequestExtractor());
        for (ExecutionRequest executionRequest : executionRequests) {
            List<ScriptExecutionRequest> scriptExecutionRequests = ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(executionRequest.getMetadataKey());
            executionRequest.setScriptExecutionRequests(scriptExecutionRequests);
        }
        return executionRequests;
    }

    @Override
    public void delete(ExecutionRequestKey executionRequestKey) {
        LOGGER.trace(MessageFormat.format("Deleting ExecutionRequest {0}.", executionRequestKey.toString()));
        ScriptExecutionRequestConfiguration.getInstance().deleteByExecutionRequest(executionRequestKey);
        ExecutionRequestLabelConfiguration.getInstance().deleteByExecutionRequest(executionRequestKey);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestKey.getId());
        namedParameterJdbcTemplate.update(
                deleteExecutionRequests,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteAuthenticatedExecutionRequests,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteNonAuthenticatedExecutionRequests,
                sqlParameterSource);
    }

    @Override
    public void insert(ExecutionRequest executionRequest) {
        LOGGER.trace(MessageFormat.format("Inserting ExecutionRequest {0}.", executionRequest.toString()));
//        if (exists(executionRequest.getMetadataKey())) {
//            throw new MetadataAlreadyExistsException(executionRequest);
//        }
        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            ScriptExecutionRequestConfiguration.getInstance().insert(scriptExecutionRequest);
        }
        for (ExecutionRequestLabel executionRequestLabel : executionRequest.getExecutionRequestLabels()) {
            ExecutionRequestLabelConfiguration.getInstance().insert(executionRequestLabel);
        }
        SqlParameterSource sqlParameterSourceInsert = new MapSqlParameterSource()
                .addValue("id", executionRequest.getMetadataKey().getId())
                .addValue("tm", executionRequest.getRequestTimestamp())
                .addValue("name", executionRequest.getName())
                .addValue("desc", executionRequest.getDescription())
                .addValue("email", executionRequest.getEmail())
                .addValue("scope", executionRequest.getScope())
                .addValue("context", executionRequest.getContext())
                .addValue("execution", executionRequest.getExecutionRequestStatus().value());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSourceInsert);
        System.out.println(executionRequest.getClass());
        if (executionRequest instanceof AuthenticatedExecutionRequest) {
            SqlParameterSource sqlParameterSourceAuth = new MapSqlParameterSource()
                    .addValue("id", executionRequest.getMetadataKey().getId())
                    .addValue("space", ((AuthenticatedExecutionRequest) executionRequest).getSpace())
                    .addValue("user", ((AuthenticatedExecutionRequest) executionRequest).getUser())
                    .addValue("password", ((AuthenticatedExecutionRequest) executionRequest).getPassword());
            namedParameterJdbcTemplate.update(
                    insertAuthenticatedExecutionRequest,
                    sqlParameterSourceAuth);
        } else if (executionRequest instanceof NonAuthenticatedExecutionRequest) {
            SqlParameterSource sqlParameterSourceNonAuth = new MapSqlParameterSource()
                    .addValue("id", executionRequest.getMetadataKey().getId());
            namedParameterJdbcTemplate.update(
                    insertNonAuthenticatedExecutionRequest,
                    sqlParameterSourceNonAuth);
        }
    }

    @Override
    public void update(ExecutionRequest executionRequest) {
        if (!exists(executionRequest.getMetadataKey())) {
            throw new MetadataDoesNotExistException(executionRequest);
        }
        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);
        }
        for (ExecutionRequestLabel executionRequestLabel : executionRequest.getExecutionRequestLabels()) {
            ExecutionRequestLabelConfiguration.getInstance().update(executionRequestLabel);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("tm", executionRequest.getRequestTimestamp())
                .addValue("name", executionRequest.getName())
                .addValue("desc", executionRequest.getDescription())
                .addValue("email", executionRequest.getEmail())
                .addValue("scope", executionRequest.getScope())
                .addValue("context", executionRequest.getContext())
                .addValue("execution", executionRequest.getExecutionRequestStatus().value());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
        if (executionRequest instanceof AuthenticatedExecutionRequest) {
            SqlParameterSource sqlParameterSourceAuth = new MapSqlParameterSource()
                    .addValue("space", ((AuthenticatedExecutionRequest) executionRequest).getSpace())
                    .addValue("user", ((AuthenticatedExecutionRequest) executionRequest).getUser())
                    .addValue("password", ((AuthenticatedExecutionRequest) executionRequest).getPassword());
            namedParameterJdbcTemplate.update(
                    updateAuthenticatedExecutionRequest,
                    sqlParameterSourceAuth);
        } else if (executionRequest instanceof NonAuthenticatedExecutionRequest) {
            namedParameterJdbcTemplate.update(
                    update,
                    sqlParameterSource);
        }
    }
}
