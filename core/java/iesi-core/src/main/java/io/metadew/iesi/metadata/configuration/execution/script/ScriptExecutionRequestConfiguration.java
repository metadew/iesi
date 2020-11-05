package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptFileExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ScriptExecutionRequestConfiguration extends Configuration<ScriptExecutionRequest, ScriptExecutionRequestKey> {

    private static ScriptExecutionRequestConfiguration INSTANCE;

    public synchronized static ScriptExecutionRequestConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptExecutionRequestConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ScriptExecutionRequestConfiguration() {
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

    private final static String query = "SELECT ScriptExecutionRequests.ID as ScriptExecutionRequests_ID, ScriptExecutionRequests.SCRPT_REQUEST_ID as ScriptExecutionRequests_SCRPT_REQUEST_ID," +
            " ScriptExecutionRequests.EXIT as ScriptExecutionRequests_EXIT, ScriptExecutionRequests.ENVIRONMENT as ScriptExecutionRequests_ENVIRONMENT, ScriptExecutionRequests.ST_NM as ScriptExecutionRequests_ST_NM, " +
            " ScriptFileExecutionRequests.SCRPT_FILENAME as ScriptFileExecutionRequests_SCRPT_FILENAME ,ScriptNameExecutionRequests.SCRPT_NAME as ScriptNameExecutionRequests_SCRPT_NAME, ScriptNameExecutionRequests.SCRPT_VRS as ScriptNameExecutionRequests_SCRPT_VRS ,ScriptFileExecutionRequests.SCRPT_REQUEST_ID AS FILE_REQ, ScriptNameExecutionRequests.SCRPT_REQUEST_ID AS NAME_REQ," +
            " ScriptExecutionRequestImpersonations.ID as ScriptExecutionRequestImpersonations_ID, ScriptExecutionRequestImpersonations.SCRIPT_EXEC_REQ_ID as ScriptExecutionRequestImpersonations_SCRIPT_EXEC_REQ_ID, ScriptExecutionRequestImpersonations.IMP_ID as ScriptExecutionRequestImpersonations_IMP_ID," +
            " ScriptExecutionRequestParameters.ID as ScriptExecutionRequestParameters_ID, ScriptExecutionRequestParameters.SCRIPT_EXEC_REQ_ID as ScriptExecutionRequestParameters_SCRIPT_EXEC_REQ_ID , ScriptExecutionRequestParameters.NAME as ScriptExecutionRequestParameters_NAME, ScriptExecutionRequestParameters.VALUE as ScriptExecutionRequestParameters_VALUE " +
            " FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() +
            " ScriptExecutionRequests " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " ScriptFileExecutionRequests " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptFileExecutionRequests.SCRPT_REQUEST_ID AND ScriptExecutionRequests.ID = ScriptFileExecutionRequests.ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " ScriptNameExecutionRequests " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptNameExecutionRequests.SCRPT_REQUEST_ID AND ScriptExecutionRequests.ID = ScriptNameExecutionRequests.ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() + " ScriptExecutionRequestImpersonations " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptExecutionRequestImpersonations.SCRIPT_EXEC_REQ_ID AND ScriptExecutionRequests.ID = ScriptExecutionRequestImpersonations.ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " ScriptExecutionRequestParameters  " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptExecutionRequestParameters.SCRIPT_EXEC_REQ_ID AND ScriptExecutionRequests.ID = ScriptExecutionRequestParameters.ID " +
            "WHERE ScriptExecutionRequests.SCRPT_REQUEST_ID = :id ;";
    private final static String getAll = "SELECT ScriptExecutionRequests.ID as ScriptExecutionRequests_ID, ScriptExecutionRequests.SCRPT_REQUEST_ID as ScriptExecutionRequests_SCRPT_REQUEST_ID," +
            " ScriptExecutionRequests.EXIT as ScriptExecutionRequests_EXIT, ScriptExecutionRequests.ENVIRONMENT as ScriptExecutionRequests_ENVIRONMENT, ScriptExecutionRequests.ST_NM as ScriptExecutionRequests_ST_NM, " +
            " ScriptFileExecutionRequests.SCRPT_FILENAME as ScriptFileExecutionRequests_SCRPT_FILENAME ,ScriptNameExecutionRequests.SCRPT_NAME as ScriptNameExecutionRequests_SCRPT_NAME, ScriptNameExecutionRequests.SCRPT_VRS as ScriptNameExecutionRequests_SCRPT_VRS ,ScriptFileExecutionRequests.SCRPT_REQUEST_ID AS FILE_REQ, ScriptNameExecutionRequests.SCRPT_REQUEST_ID AS NAME_REQ," +
            " ScriptExecutionRequestImpersonations.ID as ScriptExecutionRequestImpersonations_ID, ScriptExecutionRequestImpersonations.SCRIPT_EXEC_REQ_ID as ScriptExecutionRequestImpersonations_SCRIPT_EXEC_REQ_ID, ScriptExecutionRequestImpersonations.IMP_ID as ScriptExecutionRequestImpersonations_IMP_ID," +
            " ScriptExecutionRequestParameters.ID as ScriptExecutionRequestParameters_ID, ScriptExecutionRequestParameters.SCRIPT_EXEC_REQ_ID as ScriptExecutionRequestParameters_SCRIPT_EXEC_REQ_ID , ScriptExecutionRequestParameters.NAME as ScriptExecutionRequestParameters_NAME, ScriptExecutionRequestParameters.VALUE as ScriptExecutionRequestParameters_VALUE " +
            " FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() +
            " ScriptExecutionRequests " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " ScriptFileExecutionRequests " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptFileExecutionRequests.SCRPT_REQUEST_ID AND ScriptExecutionRequests.ID = ScriptFileExecutionRequests.ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " ScriptNameExecutionRequests " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptNameExecutionRequests.SCRPT_REQUEST_ID AND ScriptExecutionRequests.ID = ScriptNameExecutionRequests.ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() + " ScriptExecutionRequestImpersonations " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptExecutionRequestImpersonations.SCRIPT_EXEC_REQ_ID AND ScriptExecutionRequests.ID = ScriptExecutionRequestImpersonations.ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " ScriptExecutionRequestParameters  " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptExecutionRequestParameters.SCRIPT_EXEC_REQ_ID AND ScriptExecutionRequests.ID = ScriptExecutionRequestParameters.ID " +
            " ;";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() +
            " (SCRPT_REQUEST_ID, ID, EXIT, ENVIRONMENT, ST_NM) VALUES (:requestId, :id, :exit, :env, :status)";
    private final static String insertScriptFileExecutionRequest = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() +
            " (SCRPT_REQUEST_ID, ID, SCRPT_FILENAME) VALUES (:requestId, :id, :filename)";
    private final static String insertScriptNameExecutionRequest = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() +
            " (SCRPT_REQUEST_ID, ID, SCRPT_NAME, SCRPT_VRS) VALUES (:requestId, :id, :name, :version)";
    private final static String getByExecutionRequest = "SELECT " +
            "ScriptExecutionRequests.ID as ScriptExecutionRequests_ID, ScriptExecutionRequests.SCRPT_REQUEST_ID as ScriptExecutionRequests_SCRPT_REQUEST_ID," +
            " ScriptExecutionRequests.EXIT as ScriptExecutionRequests_EXIT, ScriptExecutionRequests.ENVIRONMENT as ScriptExecutionRequests_ENVIRONMENT, ScriptExecutionRequests.ST_NM as ScriptExecutionRequests_ST_NM, " +
            " ScriptFileExecutionRequests.SCRPT_FILENAME as ScriptFileExecutionRequests_SCRPT_FILENAME ,ScriptNameExecutionRequests.SCRPT_NAME as ScriptNameExecutionRequests_SCRPT_NAME, ScriptNameExecutionRequests.SCRPT_VRS as ScriptNameExecutionRequests_SCRPT_VRS ,ScriptFileExecutionRequests.SCRPT_REQUEST_ID AS FILE_REQ, ScriptNameExecutionRequests.SCRPT_REQUEST_ID AS NAME_REQ," +
            " ScriptExecutionRequestImpersonations.ID as ScriptExecutionRequestImpersonations_ID, ScriptExecutionRequestImpersonations.SCRIPT_EXEC_REQ_ID as ScriptExecutionRequestImpersonations_SCRIPT_EXEC_REQ_ID, ScriptExecutionRequestImpersonations.IMP_ID as ScriptExecutionRequestImpersonations_IMP_ID," +
            " ScriptExecutionRequestParameters.ID as ScriptExecutionRequestParameters_ID, ScriptExecutionRequestParameters.SCRIPT_EXEC_REQ_ID as ScriptExecutionRequestParameters_SCRIPT_EXEC_REQ_ID , ScriptExecutionRequestParameters.NAME as ScriptExecutionRequestParameters_NAME, ScriptExecutionRequestParameters.VALUE as ScriptExecutionRequestParameters_VALUE " +
            " FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() +
            " ScriptExecutionRequests " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " ScriptFileExecutionRequests " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptFileExecutionRequests.SCRPT_REQUEST_ID AND ScriptExecutionRequests.ID = ScriptFileExecutionRequests.ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " ScriptNameExecutionRequests " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptNameExecutionRequests.SCRPT_REQUEST_ID AND ScriptExecutionRequests.ID = ScriptNameExecutionRequests.ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() + " ScriptExecutionRequestImpersonations " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptExecutionRequestImpersonations.SCRIPT_EXEC_REQ_ID AND ScriptExecutionRequests.ID = ScriptExecutionRequestImpersonations.ID " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " ScriptExecutionRequestParameters  " +
            "ON ScriptExecutionRequests.SCRPT_REQUEST_ID = ScriptExecutionRequestParameters.SCRIPT_EXEC_REQ_ID AND ScriptExecutionRequests.ID = ScriptExecutionRequestParameters.ID " +
            "WHERE ScriptExecutionRequests.ID = :id ;";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() +
            " SET ID = :id, EXIT = :exit, ENVIRONMENT = :env, ST_NM = :value " +
            "ST_NM = :execution  WHERE SCRPT_REQUEST_ID = :id ; ";
    private final static String updateScriptFileExecutionRequests = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() +
            " SET ID = :id, SCRPT_FILENAME = :filename " +
            " WHERE SCRPT_REQUEST_ID = :id ; ";
    private final static String updateScriptNameExecutionRequests = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() +
            " SET ID = :id, SCRPT_NAME = :name, SCRPT_VRS= :version  " +
            " WHERE SCRPT_REQUEST_ID = :id ; ";
    private static final String deleteScriptExecutionRequests = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " WHERE REQUEST_ID = :id  ;";
    private static final String deleteScriptFileExecutionRequests = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " WHERE REQUEST_ID = :id  ;";
    private static final String deleteNonScriptNameExecutionRequests = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " WHERE REQUEST_ID = :id  ;";
    private static final String deleteScriptExecutionRequestsId = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " WHERE ID  = :id  ;";
    private static final String deleteScriptFileExecutionRequestsId = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " WHERE ID  = :id  ;";
    private static final String deleteNonScriptNameExecutionRequestsId = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " WHERE ID  = :id  ;";


    @Override
    public Optional<ScriptExecutionRequest> get(ScriptExecutionRequestKey scriptExecutionRequestKey) {

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionRequestKey.getId());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        query,
                        sqlParameterSource,
                        new ScriptExecutionRequestExtractor())));
    }

    @Override
    public List<ScriptExecutionRequest> getAll() {
        return namedParameterJdbcTemplate.query(getAll, new ScriptExecutionRequestExtractor());
    }

    @Override
    public void delete(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequest {0}.", scriptExecutionRequestKey.toString()));
        if (!exists(scriptExecutionRequestKey)) {
            throw new MetadataDoesNotExistException(scriptExecutionRequestKey);
        }
        ScriptExecutionRequestParameterConfiguration.getInstance().deleteByScriptExecutionRequest(scriptExecutionRequestKey);
        ScriptExecutionRequestImpersonationConfiguration.getInstance().deleteByScriptExecutionRequest(scriptExecutionRequestKey);

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionRequestKey.getId());
        namedParameterJdbcTemplate.update(
                deleteScriptExecutionRequests,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteScriptFileExecutionRequests,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteNonScriptNameExecutionRequests,
                sqlParameterSource);
    }

    @Override
    public void insert(ScriptExecutionRequest scriptExecutionRequest) {
        log.trace(MessageFormat.format("Inserting ScriptExecutionRequest {0}.", scriptExecutionRequest.toString()));
        if (exists(scriptExecutionRequest.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(scriptExecutionRequest);
        }
        scriptExecutionRequest.getImpersonations()
                .forEach(scriptExecutionRequestImpersonation -> ScriptExecutionRequestImpersonationConfiguration.getInstance().insert(scriptExecutionRequestImpersonation));
        scriptExecutionRequest.getParameters()
                .forEach(scriptExecutionRequestParameter -> ScriptExecutionRequestParameterConfiguration.getInstance().insert(scriptExecutionRequestParameter));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("requestId", scriptExecutionRequest.getMetadataKey().getId())
                .addValue("id", scriptExecutionRequest.getExecutionRequestKey().getId())
                .addValue("exit", scriptExecutionRequest.isExit())
                .addValue("env", scriptExecutionRequest.getEnvironment())
                .addValue("status", scriptExecutionRequest.getScriptExecutionRequestStatus().value());

        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
        if (scriptExecutionRequest instanceof ScriptFileExecutionRequest) {
            SqlParameterSource sqlParameterSourceScriptFileExecutionRequest = new MapSqlParameterSource()
                    .addValue("requestId", scriptExecutionRequest.getMetadataKey().getId())
                    .addValue("id", scriptExecutionRequest.getExecutionRequestKey().getId())
                    .addValue("filename", ((ScriptFileExecutionRequest) scriptExecutionRequest).getFileName());
            namedParameterJdbcTemplate.update(
                    insertScriptFileExecutionRequest,
                    sqlParameterSourceScriptFileExecutionRequest);
        } else if (scriptExecutionRequest instanceof ScriptNameExecutionRequest) {
            SqlParameterSource sqlParameterSourceScriptNameExecutionRequest = new MapSqlParameterSource()
                    .addValue("requestId", scriptExecutionRequest.getMetadataKey().getId())
                    .addValue("id", scriptExecutionRequest.getExecutionRequestKey().getId())
                    .addValue("name", ((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptName())
                    .addValue("version", ((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptVersion().orElse(null));
            namedParameterJdbcTemplate.update(
                    insertScriptNameExecutionRequest,
                    sqlParameterSourceScriptNameExecutionRequest);
        }
    }

    public List<ScriptExecutionRequest> getByExecutionRequest(ExecutionRequestKey executionRequestKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestKey.getId());
        return namedParameterJdbcTemplate.query(
                getByExecutionRequest,
                sqlParameterSource,
                new ScriptExecutionRequestExtractor());
    }

    public void deleteByExecutionRequest(ExecutionRequestKey executionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequest by ExecutionKey {0}.", executionRequestKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestKey.getId());
        namedParameterJdbcTemplate.update(
                deleteScriptExecutionRequestsId,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteScriptFileExecutionRequestsId,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteNonScriptNameExecutionRequestsId,
                sqlParameterSource);
    }

    @Override
    public void update(ScriptExecutionRequest executionRequest) {
        if (!exists(executionRequest.getMetadataKey())) {
            throw new MetadataDoesNotExistException(executionRequest);
        }
        executionRequest.getImpersonations()
                .forEach(scriptExecutionRequestImpersonation -> ScriptExecutionRequestImpersonationConfiguration.getInstance().update(scriptExecutionRequestImpersonation));
        executionRequest.getParameters()
                .forEach(scriptExecutionRequestParameter -> ScriptExecutionRequestParameterConfiguration.getInstance().update(scriptExecutionRequestParameter));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequest.getExecutionRequestKey().getId())
                .addValue("exit", executionRequest.isExit())
                .addValue("env", executionRequest.getEnvironment())
                .addValue("value", executionRequest.getScriptExecutionRequestStatus().value())
                .addValue("requestId", executionRequest.getMetadataKey().getId());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
        if (executionRequest instanceof ScriptFileExecutionRequest) {
            SqlParameterSource sqlParameterSourceScriptFileExecutionRequest = new MapSqlParameterSource()
                    .addValue("id", executionRequest.getExecutionRequestKey().getId())
                    .addValue("filename", ((ScriptFileExecutionRequest) executionRequest).getFileName())
                    .addValue("requestId", executionRequest.getMetadataKey().getId());
            namedParameterJdbcTemplate.update(
                    updateScriptFileExecutionRequests,
                    sqlParameterSourceScriptFileExecutionRequest);
        } else if (executionRequest instanceof ScriptNameExecutionRequest) {
            SqlParameterSource sqlParameterSourceScriptNameExecutionRequest = new MapSqlParameterSource()
                    .addValue("id", executionRequest.getExecutionRequestKey().getId())
                    .addValue("name", ((ScriptNameExecutionRequest) executionRequest).getScriptName())
                    .addValue("version", ((ScriptNameExecutionRequest) executionRequest).getScriptVersion().orElse(null))
                    .addValue("requestId", executionRequest.getMetadataKey().getId());
            namedParameterJdbcTemplate.update(
                    updateScriptNameExecutionRequests,
                    sqlParameterSourceScriptNameExecutionRequest);
        } else {
            log.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", executionRequest.toString()));
        }
    }

    public Long getTotalExecutionRequests(String scriptName, long version, String environment) {
        try {
            String query = "SELECT COUNT(*) as total_requests FROM (" +
                    "SELECT SCRPT_REQUEST_ID from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests") +
                    " WHERE SCRPT_NAME=" + SQLTools.GetStringForSQL(scriptName) + " AND " +
                    " SCRPT_VRS = " + SQLTools.GetStringForSQL(version) + ") named_script_exec_reqs inner join " +
                    "SELECT SCRPT_REQUEST_ID from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests") +
                    " WHERE ENVIRONMENT=" + SQLTools.GetStringForSQL(environment) + ") script_exec_reqs on " +
                    " named_script_exec_reqs.SCRPT_REQUEST_ID = script_exec_reqs.SCRPT_REQUEST_ID";
            CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
            if (crs.next()) {
                return Long.parseLong(crs.getString("total_executions"));
            } else {
                return 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ScriptExecutionRequest getMostRecentExecutionRequests(String scriptName, long version, String environment) {
        String query = "SELECT COUNT(*) as total_requests FROM (" +
                "SELECT SCRPT_REQUEST_ID from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests") +
                " WHERE SCRPT_NAME=" + SQLTools.GetStringForSQL(scriptName) + " AND " +
                " SCRPT_VRS = " + SQLTools.GetStringForSQL(version) + ") named_script_exec_reqs inner join " +
                "SELECT SCRPT_REQUEST_ID from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests") +
                " WHERE ENVIRONMENT=" + SQLTools.GetStringForSQL(environment) + ") script_exec_reqs on " +
                " named_script_exec_reqs.SCRPT_REQUEST_ID = script_exec_reqs.SCRPT_REQUEST_ID";
        return null;
    }

}
