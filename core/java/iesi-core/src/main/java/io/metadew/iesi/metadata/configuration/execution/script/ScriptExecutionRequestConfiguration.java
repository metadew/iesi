package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.script.exception.ScriptExecutionRequestAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.execution.script.exception.ScriptExecutionRequestDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptFileExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptExecutionRequestConfiguration extends Configuration<ScriptExecutionRequest, ScriptExecutionRequestKey> {

    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public ScriptExecutionRequestConfiguration() {
        super();
    }


    @Override
    public Optional<ScriptExecutionRequest> get(ScriptExecutionRequestKey scriptExecutionRequestKey) throws SQLException {
        String query = "SELECT SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID, SCRIPT_EXEC_REQ.EXEC_REQUEST_ID, SCRIPT_EXEC_REQ.EXIT, SCRIPT_EXEC_REQ.IMPERSONATION, " +
                "SCRIPT_EXEC_REQ.ENVIRONMENT, SCRIPT_EXEC_REQ.PARAMETERS, SCRIPT_EXEC_REQ.IMPERSONATIONS, SCRIPT_EXEC_REQ.ST_NM, " +
                "SCRIPT_FILE_EXEC_REQ.SCRPT_FILENAME, " +
                "SCRIPT_NAME_EXEC_REQ.SCRPT_NAME, SCRIPT_NAME_EXEC_REQ.SCRPT_VRS, " +
                // Replace with case in case of Spring
                // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                // "END AS clazz " +
                "SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID AS FILE, " +
                "SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID AS NAME " +
                "FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") + " SCRIPT_EXEC_REQ " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") + " SCRIPT_FILE_EXEC_REQ " +
                "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID AND SCRIPT_EXEC_REQ.EXEC_REQUEST_ID = SCRIPT_FILE_EXEC_REQ.EXEC_REQUEST_ID " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") + " SCRIPT_NAME_EXEC_REQ " +
                "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID AND SCRIPT_EXEC_REQ.EXEC_REQUEST_ID = SCRIPT_NAME_EXEC_REQ.EXEC_REQUEST_ID " +
                "WHERE SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = " + SQLTools.GetStringForSQL(scriptExecutionRequestKey.getId()) + ";";

        CachedRowSet cachedRowSet = getMetadataControl().getExecutionServerMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptExecutionRequest {0}. Returning first implementation", scriptExecutionRequestKey.toString()));
        }
        cachedRowSet.next();
        if (cachedRowSet.getString("FILE") != null) {
            return Optional.of(new ScriptFileExecutionRequest(scriptExecutionRequestKey,
                    new ExecutionRequestKey(cachedRowSet.getString("EXEC_REQUEST_ID")),
                    cachedRowSet.getString("SCRPT_FILENAME"),
                    cachedRowSet.getString("ENVIRONMENT"),
                    new ArrayList<>(),
                    SQLTools.getBooleanFromSql(cachedRowSet.getString("EXIT")),
                    cachedRowSet.getString("IMPERSONATION"),
                    SQLTools.getMapFromSql(cachedRowSet.getString("IMPERSONATIONS")),
                    SQLTools.getMapFromSql(cachedRowSet.getString("PARAMETERS")), ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM"))));
        } else if (cachedRowSet.getString("NAME") != null) {
            return Optional.of(new ScriptNameExecutionRequest(scriptExecutionRequestKey,
                    new ExecutionRequestKey(cachedRowSet.getString("EXEC_REQUEST_ID")),
                    cachedRowSet.getString("SCRPT_NAME"),
                    cachedRowSet.getLong("SCRPT_VRS"),
                    cachedRowSet.getString("ENVIRONMENT"),
                    new ArrayList<>(),
                    SQLTools.getBooleanFromSql(cachedRowSet.getString("EXIT")),
                    cachedRowSet.getString("IMPERSONATION"),
                    SQLTools.getMapFromSql(cachedRowSet.getString("IMPERSONATIONS")),
                    SQLTools.getMapFromSql(cachedRowSet.getString("PARAMETERS")), ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM"))));
        } else {
            LOGGER.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", scriptExecutionRequestKey.toString()));
            return Optional.empty();

        }
    }

    @Override
    public List<ScriptExecutionRequest> getAll() throws SQLException {
        List<ScriptExecutionRequest> scriptExecutionRequests = new ArrayList<>();
        String query = "SELECT SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID, SCRIPT_EXEC_REQ.EXEC_REQUEST_ID, SCRIPT_EXEC_REQ.EXIT, SCRIPT_EXEC_REQ.IMPERSONATION, " +
                "SCRIPT_EXEC_REQ.ENVIRONMENT, SCRIPT_EXEC_REQ.PARAMETERS, SCRIPT_EXEC_REQ.IMPERSONATIONS, SCRIPT_EXEC_REQ.ST_NM, SCRIPT_EXEC_REQ.ST_NM, " +
                "SCRIPT_FILE_EXEC_REQ.SCRPT_FILENAME, " +
                "SCRIPT_NAME_EXEC_REQ.SCRPT_NAME, SCRIPT_NAME_EXEC_REQ.SCRPT_VRS, " +
                // Replace with case in case of Spring
                // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                // "END AS clazz " +
                "SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID AS FILE, " +
                "SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID AS NAME " +
                "FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") + " SCRIPT_EXEC_REQ " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") + " SCRIPT_FILE_EXEC_REQ " +
                "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID AND SCRIPT_EXEC_REQ.EXEC_REQUEST_ID = SCRIPT_FILE_EXEC_REQ.EXEC_REQUEST_ID " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") + " SCRIPT_NAME_EXEC_REQ " +
                "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID AND SCRIPT_EXEC_REQ.EXEC_REQUEST_ID = SCRIPT_NAME_EXEC_REQ.EXEC_REQUEST_ID " + ";";

        CachedRowSet cachedRowSet = getMetadataControl().getExecutionServerMetadataRepository().executeQuery(query, "reader");
        while (cachedRowSet.next()) {
            if (cachedRowSet.getString("FILE") != null) {
                scriptExecutionRequests.add(new ScriptFileExecutionRequest(
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")
                        ),
                        new ExecutionRequestKey(cachedRowSet.getString("EXEC_REQUEST_ID")),
                        cachedRowSet.getString("SCRPT_FILENAME"),
                        cachedRowSet.getString("ENVIRONMENT"),
                        new ArrayList<>(),
                        SQLTools.getBooleanFromSql(cachedRowSet.getString("EXIT")),
                        cachedRowSet.getString("IMPERSONATION"),
                        SQLTools.getMapFromSql(cachedRowSet.getString("IMPERSONATIONS")),
                        SQLTools.getMapFromSql(cachedRowSet.getString("PARAMETERS")), ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM"))));
            } else if (cachedRowSet.getString("NAME") != null) {
                scriptExecutionRequests.add(new ScriptNameExecutionRequest(
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")
                        ),
                        new ExecutionRequestKey(cachedRowSet.getString("EXEC_REQUEST_ID")),
                        cachedRowSet.getString("SCRPT_NAME"),
                        cachedRowSet.getLong("SCRPT_VRS"),
                        cachedRowSet.getString("ENVIRONMENT"),
                        new ArrayList<>(),
                        SQLTools.getBooleanFromSql(cachedRowSet.getString("EXIT")),
                        cachedRowSet.getString("IMPERSONATION"),
                        SQLTools.getMapFromSql(cachedRowSet.getString("IMPERSONATIONS")),
                        SQLTools.getMapFromSql(cachedRowSet.getString("PARAMETERS")), ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM"))));
            } else {
                LOGGER.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", cachedRowSet.getString("SCRPT_REQUEST_ID")));

            }
        }
        return scriptExecutionRequests;
    }

    @Override
    public void delete(ScriptExecutionRequestKey scriptExecutionRequestKey) throws MetadataDoesNotExistException, SQLException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptExecutionRequest {0}.", scriptExecutionRequestKey.toString()));
        if (!exists(scriptExecutionRequestKey)) {
            throw new ScriptExecutionRequestDoesNotExistException(MessageFormat.format(
                    "ScriptExecutionRequest {0} does not exists", scriptExecutionRequestKey.toString()));
        }
        List<String> deleteStatement = deleteStatement(scriptExecutionRequestKey);
        getMetadataControl().getExecutionServerMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ScriptExecutionRequestKey executionRequestKey) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") +
                " WHERE " +
                " SCRPT_REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") +
                " WHERE " +
                " SCRPT_REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") +
                " WHERE " +
                " SCRPT_REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";");
        return queries;
    }

    @Override
    public void insert(ScriptExecutionRequest scriptExecutionRequest) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptExecutionRequest {0}.", scriptExecutionRequest.toString()));
        if (exists(scriptExecutionRequest.getMetadataKey())) {
            throw new ScriptExecutionRequestAlreadyExistsException(MessageFormat.format(
                    "ScriptExecutionRequest {0} already exists", scriptExecutionRequest.getMetadataKey().toString()));
        }
        List<String> insertStatement = insertStatement(scriptExecutionRequest);
        getMetadataControl().getExecutionServerMetadataRepository().executeBatch(insertStatement);
    }

    private List<String> insertStatement(ScriptExecutionRequest scriptExecutionRequest) {
        List<String> queries = new ArrayList<>();
        if (scriptExecutionRequest instanceof ScriptFileExecutionRequest) {
            queries.add("INSERT INTO " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") +
                    " (SCRPT_REQUEST_ID, EXEC_REQUEST_ID, EXIT, IMPERSONATION, ENVIRONMENT, PARAMETERS, IMPERSONATIONS, ST_NM) VALUES (" +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getExecutionRequestKey().getId()) + ", " +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.isExit()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getImpersonation().orElse(null)) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getEnvironment()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getParameters()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getImpersonations().orElse(null)) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getScriptExecutionRequestStatus().value()) + ");");
            queries.add("INSERT INTO " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") +
                    " (SCRPT_REQUEST_ID, EXEC_REQUEST_ID, SCRPT_FILENAME) VALUES (" +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getExecutionRequestKey().getId()) + ", " +
                    SQLTools.GetStringForSQL(((ScriptFileExecutionRequest) scriptExecutionRequest).getFileName()) + ");");
            return queries;
        } else if (scriptExecutionRequest instanceof ScriptNameExecutionRequest) {
            queries.add("INSERT INTO " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") +
                    " (SCRPT_REQUEST_ID, EXEC_REQUEST_ID, EXIT, IMPERSONATION, ENVIRONMENT, PARAMETERS, IMPERSONATIONS, ST_NM) VALUES (" +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getExecutionRequestKey().getId()) + ", " +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.isExit()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getImpersonation().orElse(null)) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getEnvironment()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getParameters()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getImpersonations().orElse(null)) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getScriptExecutionRequestStatus().value()) + ");");
            queries.add("INSERT INTO " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") +
                    " (SCRPT_REQUEST_ID, EXEC_REQUEST_ID, SCRPT_NAME, SCRPT_VRS) VALUES (" +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.GetStringForSQL(scriptExecutionRequest.getExecutionRequestKey().getId()) + ", " +
                    SQLTools.GetStringForSQL(((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptName()) + ", " +
                    SQLTools.GetStringForSQL(((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptVersion().orElse(null)) + ");");
            return queries;
        } else {
            LOGGER.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", scriptExecutionRequest.toString()));
        }
        return queries;
    }

    public List<ScriptExecutionRequest> getByExecutionRequest(ExecutionRequestKey executionRequestKey) throws SQLException {
        List<ScriptExecutionRequest> scriptExecutionRequests = new ArrayList<>();
        String query = "SELECT SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID, SCRIPT_EXEC_REQ.EXEC_REQUEST_ID, SCRIPT_EXEC_REQ.EXIT, SCRIPT_EXEC_REQ.IMPERSONATION, " +
                "SCRIPT_EXEC_REQ.ENVIRONMENT, SCRIPT_EXEC_REQ.PARAMETERS, SCRIPT_EXEC_REQ.IMPERSONATIONS, SCRIPT_EXEC_REQ.ST_NM, " +
                "SCRIPT_FILE_EXEC_REQ.SCRPT_FILENAME, " +
                "SCRIPT_NAME_EXEC_REQ.SCRPT_NAME, SCRIPT_NAME_EXEC_REQ.SCRPT_VRS, " +
                // Replace with case in case of Spring
                // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                // "END AS clazz " +
                "SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID AS FILE, " +
                "SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID AS NAME " +
                "FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") + " SCRIPT_EXEC_REQ " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") + " SCRIPT_FILE_EXEC_REQ " +
                "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_FILE_EXEC_REQ.SCRPT_REQUEST_ID AND SCRIPT_EXEC_REQ.EXEC_REQUEST_ID = SCRIPT_FILE_EXEC_REQ.EXEC_REQUEST_ID " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") + " SCRIPT_NAME_EXEC_REQ " +
                "ON SCRIPT_EXEC_REQ.SCRPT_REQUEST_ID = SCRIPT_NAME_EXEC_REQ.SCRPT_REQUEST_ID AND SCRIPT_EXEC_REQ.EXEC_REQUEST_ID = SCRIPT_NAME_EXEC_REQ.EXEC_REQUEST_ID " +
                "WHERE SCRIPT_EXEC_REQ.EXEC_REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) +";";

        CachedRowSet cachedRowSet = getMetadataControl().getExecutionServerMetadataRepository().executeQuery(query, "reader");
        while (cachedRowSet.next()) {
            if (cachedRowSet.getString("FILE") != null) {
                scriptExecutionRequests.add(new ScriptFileExecutionRequest(
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")
                        ),
                        executionRequestKey,
                        cachedRowSet.getString("SCRPT_FILENAME"),
                        cachedRowSet.getString("ENVIRONMENT"),
                        new ArrayList<>(),
                        SQLTools.getBooleanFromSql(cachedRowSet.getString("EXIT")),
                        cachedRowSet.getString("IMPERSONATION"),
                        SQLTools.getMapFromSql(cachedRowSet.getString("IMPERSONATIONS")),
                        SQLTools.getMapFromSql(cachedRowSet.getString("PARAMETERS")), ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM"))));
            } else if (cachedRowSet.getString("NAME") != null) {
                scriptExecutionRequests.add(new ScriptNameExecutionRequest(
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")),
                        executionRequestKey,
                        cachedRowSet.getString("SCRPT_NAME"),
                        cachedRowSet.getLong("SCRPT_VRS"),
                        cachedRowSet.getString("ENVIRONMENT"),
                        new ArrayList<>(),
                        SQLTools.getBooleanFromSql(cachedRowSet.getString("EXIT")),
                        cachedRowSet.getString("IMPERSONATION"),
                        SQLTools.getMapFromSql(cachedRowSet.getString("IMPERSONATIONS")),
                        SQLTools.getMapFromSql(cachedRowSet.getString("PARAMETERS")), ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM"))));
            } else {
                LOGGER.warn(MessageFormat.format("ScriptExecutionRequest {0} does not have a certain class", cachedRowSet.getString("SCRPT_REQUEST_ID")));

            }
        }
        return scriptExecutionRequests;
    }

    public void deleteByExecutionKey(ExecutionRequestKey executionRequestKey) throws MetadataDoesNotExistException, SQLException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptExecutionRequest by ExecutionKey {0}.", executionRequestKey.toString()));
        List<String> deleteStatement = deleteStatement(executionRequestKey);
        getMetadataControl().getExecutionServerMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ExecutionRequestKey executionRequestKey) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutionRequests") +
                " WHERE " +
                " EXEC_REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptFileExecutionRequests") +
                " WHERE " +
                " EXEC_REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptNameExecutionRequests") +
                " WHERE " +
                " EXEC_REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";");
        return queries;
    }
}
