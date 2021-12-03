package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptFileExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
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

    private ScriptExecutionRequestConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getExecutionServerMetadataRepository());
    }

    @Override
    public Optional<ScriptExecutionRequest> get(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        try {
            String query = "SELECT SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID, SCRIPT_EXEC_REQ.ID," +
                    "SCRIPT_EXEC_REQ.ENVIRONMENT, SCRIPT_EXEC_REQ.ST_NM, " +
                    "SCRIPT_FILE_EXEC_REQ.SCRPT_FILENAME, " +
                    "SCRIPT_NAME_EXEC_REQ.SCRPT_NAME, SCRIPT_NAME_EXEC_REQ.SCRPT_VRS, " +
                    // Replace with case in case of Spring
                    // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                    // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                    // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                    // "END AS clazz " +
                    "SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID AS FILE_REQ, " +
                    "SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID AS NAME_REQ " +
                    "FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") + " SCRIPT_EXEC_REQ " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") + " SCRIPT_FILE_EXEC_REQ " +
                    "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") + " SCRIPT_NAME_EXEC_REQ " +
                    "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID " +
                    "WHERE SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = " + SQLTools.getStringForSQL(scriptExecutionRequestKey.getId()) + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                log.warn(MessageFormat.format("Found multiple implementations for ScriptExecutionRequest {0}. Returning first implementation", scriptExecutionRequestKey.toString()));
            }
            cachedRowSet.next();
            if (cachedRowSet.getString("FILE_REQ") != null) {
                return Optional.of(new ScriptFileExecutionRequest(scriptExecutionRequestKey,
                        new ExecutionRequestKey(cachedRowSet.getString("ID")),
                        cachedRowSet.getString("SCRPT_FILENAME"),
                        cachedRowSet.getString("ENVIRONMENT"),
                        ScriptExecutionRequestImpersonationConfiguration.getInstance()
                                .getByScriptExecutionRequest(scriptExecutionRequestKey),
                        ScriptExecutionRequestParameterConfiguration.getInstance()
                                .getByScriptExecutionRequest(scriptExecutionRequestKey),
                        ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM"))));
            } else if (cachedRowSet.getString("NAME_REQ") != null) {
                return Optional.of(new ScriptNameExecutionRequest(scriptExecutionRequestKey,
                        new ExecutionRequestKey(
                                cachedRowSet.getString("ID")),
                        cachedRowSet.getString("ENVIRONMENT"),
                        ScriptExecutionRequestImpersonationConfiguration.getInstance().getByScriptExecutionRequest(scriptExecutionRequestKey),
                        ScriptExecutionRequestParameterConfiguration.getInstance().getByScriptExecutionRequest(scriptExecutionRequestKey),
                        ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        cachedRowSet.getString("SCRPT_NAME"),
                        cachedRowSet.getLong("SCRPT_VRS")
                ));
            } else {
                log.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", scriptExecutionRequestKey.toString()));
                return Optional.empty();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptExecutionRequest> getAll() {
        try {
            List<ScriptExecutionRequest> scriptExecutionRequests = new ArrayList<>();
            String query = "SELECT SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID, SCRIPT_EXEC_REQ.ID," +
                    "SCRIPT_EXEC_REQ.ENVIRONMENT, SCRIPT_EXEC_REQ.ST_NM, SCRIPT_EXEC_REQ.ST_NM, " +
                    "SCRIPT_FILE_EXEC_REQ.SCRPT_FILENAME, " +
                    "SCRIPT_NAME_EXEC_REQ.SCRPT_NAME, SCRIPT_NAME_EXEC_REQ.SCRPT_VRS, " +
                    // Replace with case in case of Spring
                    // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                    // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                    // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                    // "END AS clazz " +
                    "SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID AS FILE_REQ, " +
                    "SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID AS NAME_REQ " +
                    "FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") + " SCRIPT_EXEC_REQ " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") + " SCRIPT_FILE_EXEC_REQ " +
                    "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") + " SCRIPT_NAME_EXEC_REQ " +
                    "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID " + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                if (cachedRowSet.getString("FILE_REQ") != null) {
                    scriptExecutionRequests.add(new ScriptFileExecutionRequest(
                            new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")),
                            new ExecutionRequestKey(cachedRowSet.getString("ID")),
                            cachedRowSet.getString("SCRPT_FILENAME"),
                            cachedRowSet.getString("ENVIRONMENT"),
                            ScriptExecutionRequestImpersonationConfiguration.getInstance()
                                    .getByScriptExecutionRequest(new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID"))),
                            ScriptExecutionRequestParameterConfiguration.getInstance()
                                    .getByScriptExecutionRequest(new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID"))),
                            ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM"))));
                } else if (cachedRowSet.getString("NAME_REQ") != null) {
                    scriptExecutionRequests.add(new ScriptNameExecutionRequest(
                            new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")),
                            new ExecutionRequestKey(cachedRowSet.getString("ID")),
                            cachedRowSet.getString("ENVIRONMENT"), ScriptExecutionRequestImpersonationConfiguration.getInstance()
                            .getByScriptExecutionRequest(new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID"))), ScriptExecutionRequestParameterConfiguration.getInstance()
                            .getByScriptExecutionRequest(new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID"))), ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")), cachedRowSet.getString("SCRPT_NAME"),
                            cachedRowSet.getLong("SCRPT_VRS")
                    ));
                } else {
                    log.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", cachedRowSet.getString("SCRPT_REQUEST_ID")));

                }
            }
            return scriptExecutionRequests;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequest {0}.", scriptExecutionRequestKey.toString()));
        if (!exists(scriptExecutionRequestKey)) {
            throw new MetadataDoesNotExistException(scriptExecutionRequestKey);
        }
        ScriptExecutionRequestParameterConfiguration.getInstance().deleteByScriptExecutionRequest(scriptExecutionRequestKey);
        ScriptExecutionRequestImpersonationConfiguration.getInstance().deleteByScriptExecutionRequest(scriptExecutionRequestKey);
        List<String> deleteStatement = deleteStatement(scriptExecutionRequestKey);
        getMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ScriptExecutionRequestKey executionRequestKey) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") +
                " WHERE " +
                " SCRPT_REQUEST_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") +
                " WHERE " +
                " SCRPT_REQUEST_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") +
                " WHERE " +
                " SCRPT_REQUEST_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";");
        return queries;
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
        List<String> insertStatement = insertStatement(scriptExecutionRequest);
        getMetadataRepository().executeBatch(insertStatement);
    }

    public List<String> insertStatement(ScriptExecutionRequest scriptExecutionRequest) {
        List<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") +
                " (SCRPT_REQUEST_ID, ID, ENVIRONMENT, ST_NM) VALUES (" +
                SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                SQLTools.getStringForSQL(scriptExecutionRequest.getExecutionRequestKey().getId()) + ", " +
                SQLTools.getStringForSQL(scriptExecutionRequest.getEnvironment()) + "," +
                SQLTools.getStringForSQL(scriptExecutionRequest.getScriptExecutionRequestStatus().value()) + ");");
        if (scriptExecutionRequest instanceof ScriptFileExecutionRequest) {
            queries.add("INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") +
                    " (SCRPT_REQUEST_ID, SCRPT_FILENAME) VALUES (" +
                    SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.getStringForSQL(((ScriptFileExecutionRequest) scriptExecutionRequest).getFileName()) + ");");
            return queries;
        } else if (scriptExecutionRequest instanceof ScriptNameExecutionRequest) {
            queries.add("INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") +
                    " (SCRPT_REQUEST_ID, SCRPT_NAME, SCRPT_VRS) VALUES (" +
                    SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.getStringForSQL(((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptName()) + ", " +
                    SQLTools.getStringForSQL(((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptVersion().orElse(null)) + ");");
            return queries;
        } else {
            log.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", scriptExecutionRequest.toString()));
        }
        return queries;
    }

    public List<ScriptExecutionRequest> getByExecutionRequest(ExecutionRequestKey executionRequestKey) {
        try {
            List<ScriptExecutionRequest> scriptExecutionRequests = new ArrayList<>();
            String query = "SELECT SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID, SCRIPT_EXEC_REQ.ID, " +
                    "SCRIPT_EXEC_REQ.ENVIRONMENT, SCRIPT_EXEC_REQ.ST_NM, " +
                    "SCRIPT_FILE_EXEC_REQ.SCRPT_FILENAME, " +
                    "SCRIPT_NAME_EXEC_REQ.SCRPT_NAME, SCRIPT_NAME_EXEC_REQ.SCRPT_VRS, " +
                    // Replace with case in case of Spring
                    // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                    // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                    // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                    // "END AS clazz " +
                    "SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID AS FILE_REQ, " +
                    "SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID AS NAME_REQ " +
                    "FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") + " SCRIPT_EXEC_REQ " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") + " SCRIPT_FILE_EXEC_REQ " +
                    "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") + " SCRIPT_NAME_EXEC_REQ " +
                    "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID " +
                    "WHERE SCRIPT_EXEC_REQ.ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                if (cachedRowSet.getString("FILE_REQ") != null) {
                    scriptExecutionRequests.add(new ScriptFileExecutionRequest(
                            new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")
                            ),
                            executionRequestKey,
                            cachedRowSet.getString("SCRPT_FILENAME"),
                            cachedRowSet.getString("ENVIRONMENT"),
                            ScriptExecutionRequestImpersonationConfiguration.getInstance()
                                    .getByScriptExecutionRequest(new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID"))),
                            ScriptExecutionRequestParameterConfiguration.getInstance()
                                    .getByScriptExecutionRequest(new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID"))),
                            ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM"))));
                } else if (cachedRowSet.getString("NAME_REQ") != null) {
                    scriptExecutionRequests.add(new ScriptNameExecutionRequest(
                            new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")),
                            executionRequestKey,
                            cachedRowSet.getString("ENVIRONMENT"), ScriptExecutionRequestImpersonationConfiguration.getInstance()
                            .getByScriptExecutionRequest(new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID"))), ScriptExecutionRequestParameterConfiguration.getInstance()
                            .getByScriptExecutionRequest(new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID"))), ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")), cachedRowSet.getString("SCRPT_NAME"),
                            cachedRowSet.getLong("SCRPT_VRS")
                    ));
                } else {
                    log.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", cachedRowSet.getString("SCRPT_REQUEST_ID")));

                }
            }
            return scriptExecutionRequests;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByExecutionRequest(ExecutionRequestKey executionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequest by ExecutionKey {0}.", executionRequestKey.toString()));
        List<String> deleteStatement = deleteStatement(executionRequestKey);
        getMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ExecutionRequestKey executionRequestKey) {
        List<String> queries = new ArrayList<>();
        String subQuery = String.format(
                "SELECT SCRPT_REQUEST_ID FROM %s WHERE ID=%s",
                getMetadataRepository().getTableNameByLabel("ScriptExecutionRequests"),
                SQLTools.getStringForSQL(executionRequestKey.getId()));
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") +
                " WHERE " +
                " SCRPT_REQUEST_ID in (" + subQuery + ");");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") +
                " WHERE " +
                " SCRPT_REQUEST_ID in (" + subQuery + ");");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") +
                " WHERE " +
                " ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";");

        return queries;
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
        List<String> updateStatement = updateStatement(executionRequest);
        getMetadataRepository().executeBatch(updateStatement);

    }

    public List<String> updateStatement(ScriptExecutionRequest scriptExecutionRequest) {
        List<String> queries = new ArrayList<>();
        queries.add("UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") + " SET " +
                "ID=" + SQLTools.getStringForSQL(scriptExecutionRequest.getExecutionRequestKey().getId()) + ", " +
                "ENVIRONMENT=" + SQLTools.getStringForSQL(scriptExecutionRequest.getEnvironment()) + "," +
                "ST_NM=" + SQLTools.getStringForSQL(scriptExecutionRequest.getScriptExecutionRequestStatus().value()) + " WHERE " +
                "SCRPT_REQUEST_ID = " + SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + ";");
        if (scriptExecutionRequest instanceof ScriptFileExecutionRequest) {
            queries.add("UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") + " SET " +
                    "SCRPT_FILENAME=" + SQLTools.getStringForSQL(((ScriptFileExecutionRequest) scriptExecutionRequest).getFileName()) + " WHERE " +
                    "SCRPT_REQUEST_ID = " + SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + ";");
            return queries;
        } else if (scriptExecutionRequest instanceof ScriptNameExecutionRequest) {
            queries.add("UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") + " SET " +
                    "SCRPT_NAME=" + SQLTools.getStringForSQL(((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptName()) + "," +
                    "SCRPT_VRS=" + SQLTools.getStringForSQL(((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptVersion().orElse(null)) + " WHERE " +
                    "SCRPT_REQUEST_ID = " + SQLTools.getStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + ";");
            return queries;
        } else {
            log.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", scriptExecutionRequest.toString()));
        }
        return queries;
    }

    public Long getTotalExecutionRequests(String scriptName, long version, String environment) {
        try {
            String query = "SELECT COUNT(*) as total_requests FROM (" +
                    "SELECT SCRPT_REQUEST_ID from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests") +
                    " WHERE SCRPT_NAME=" + SQLTools.getStringForSQL(scriptName) + " AND " +
                    " SCRPT_VRS = " + SQLTools.getStringForSQL(version) + ") named_script_exec_reqs inner join " +
                    "SELECT SCRPT_REQUEST_ID from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests") +
                    " WHERE ENVIRONMENT=" + SQLTools.getStringForSQL(environment) + ") script_exec_reqs on " +
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
                " WHERE SCRPT_NAME=" + SQLTools.getStringForSQL(scriptName) + " AND " +
                " SCRPT_VRS = " + SQLTools.getStringForSQL(version) + ") named_script_exec_reqs inner join " +
                "SELECT SCRPT_REQUEST_ID from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests") +
                " WHERE ENVIRONMENT=" + SQLTools.getStringForSQL(environment) + ") script_exec_reqs on " +
                " named_script_exec_reqs.SCRPT_REQUEST_ID = script_exec_reqs.SCRPT_REQUEST_ID";
        return null;
    }

}
