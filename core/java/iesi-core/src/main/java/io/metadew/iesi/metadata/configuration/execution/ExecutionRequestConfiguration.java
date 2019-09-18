package io.metadew.iesi.metadata.configuration.execution;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.exception.ExecutionRequestAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.execution.exception.ExecutionRequestDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExecutionRequestConfiguration extends Configuration<ExecutionRequest, ExecutionRequestKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ScriptExecutionRequestConfiguration scriptExecutionRequestConfiguration;

    // Constructors
    public ExecutionRequestConfiguration() {
        super();
        this.scriptExecutionRequestConfiguration = new ScriptExecutionRequestConfiguration();
    }



    @Override
    public Optional<ExecutionRequest> get(ExecutionRequestKey executionRequestKey)  {
        try {
        String query = "SELECT EXECUTION_REQUEST.REQUEST_ID, EXECUTION_REQUEST.REQUEST_TMS, EXECUTION_REQUEST.REQUEST_NM, " +
                "EXECUTION_REQUEST.REQUEST_DSC, EXECUTION_REQUEST.NOTIF_EMAIL, EXECUTION_REQUEST.SCOPE_NM, EXECUTION_REQUEST.CONTEXT_NM, EXECUTION_REQUEST.ST_NM, " +
                "AUTH_EXECUTION_REQUEST.SPACE_NM, AUTH_EXECUTION_REQUEST.USER_NM, AUTH_EXECUTION_REQUEST.USER_PASSWORD, " +
                // Replace with case in case of Spring
                // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                // "END AS clazz " +
                "AUTH_EXECUTION_REQUEST.REQUEST_ID AS AUTH, " +
                "NON_AUTH_EXECUTION_REQUEST.REQUEST_ID AS NON_AUTH " +
                "FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ExecutionRequests") + " EXECUTION_REQUEST " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") + " AUTH_EXECUTION_REQUEST " +
                "ON EXECUTION_REQUEST.REQUEST_ID = AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") + " NON_AUTH_EXECUTION_REQUEST " +
                "ON EXECUTION_REQUEST.REQUEST_ID = NON_AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                "WHERE EXECUTION_REQUEST.REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";";

        CachedRowSet cachedRowSet = getMetadataControl().getExecutionServerMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for ExecutionRequest {0}. Returning first implementation", executionRequestKey.toString()));
        }
        cachedRowSet.next();
        if (cachedRowSet.getString("AUTH") != null) {
            return Optional.of(new AuthenticatedExecutionRequest(executionRequestKey,
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                    cachedRowSet.getString("REQUEST_NM"),
                    cachedRowSet.getString("REQUEST_DSC"),
                    cachedRowSet.getString("NOTIF_EMAIL"),
                    cachedRowSet.getString("SCOPE_NM"),
                    cachedRowSet.getString("CONTEXT_NM"),
                    ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                    scriptExecutionRequestConfiguration.getByExecutionRequest(executionRequestKey),
                    cachedRowSet.getString("SPACE_NM"),
                    cachedRowSet.getString("USER_NM"), cachedRowSet.getString("USER_PASSWORD")));
        } else if (cachedRowSet.getString("NON_AUTH") != null) {
            return Optional.of(new NonAuthenticatedExecutionRequest(executionRequestKey,
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                    cachedRowSet.getString("REQUEST_NM"),
                    cachedRowSet.getString("REQUEST_DSC"),
                    cachedRowSet.getString("NOTIF_EMAIL"),
                    cachedRowSet.getString("SCOPE_NM"),
                    cachedRowSet.getString("CONTEXT_NM"),
                    ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                    scriptExecutionRequestConfiguration.getByExecutionRequest(executionRequestKey)));
        } else {
            LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", executionRequestKey.toString()));
            return Optional.empty();

        }

//        switch (cachedRowSet.getInt("clazz")) {
//            case 1:
//                return Optional.of(new AuthenticatedExecutionRequest(executionRequestKey,
//                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
//                        cachedRowSet.getString("REQUEST_NM"),
//                        cachedRowSet.getString("REQUEST_DSC"),
//                        cachedRowSet.getString("NOTIF_EMAIL"),
//                        cachedRowSet.getString("SCOPE_NM"),
//                        cachedRowSet.getString("CONTEXT_NM"),
//                        new ArrayList<>(),
//                        cachedRowSet.getString("SPACE_NM"),
//                        cachedRowSet.getString("USER_NM"),
//                        cachedRowSet.getString("USER_PASSWORD")));
//            case 2:
//                return Optional.of(new NonAuthenticatedExecutionRequest(executionRequestKey,
//                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
//                        cachedRowSet.getString("REQUEST_NM"),
//                        cachedRowSet.getString("REQUEST_DSC"),
//                        cachedRowSet.getString("NOTIF_EMAIL"),
//                        cachedRowSet.getString("SCOPE_NM"),
//                        cachedRowSet.getString("CONTEXT_NM"),
//                        new ArrayList<>()));
//            default:
//                LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", executionRequestKey.toString()));
//                return Optional.empty();
//        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExecutionRequest> getAll()  {
        try {
        List<ExecutionRequest> executionRequests = new ArrayList<>();
        String query = "SELECT EXECUTION_REQUEST.REQUEST_ID, EXECUTION_REQUEST.REQUEST_TMS, EXECUTION_REQUEST.REQUEST_NM, " +
                "EXECUTION_REQUEST.REQUEST_DSC, EXECUTION_REQUEST.NOTIF_EMAIL, EXECUTION_REQUEST.SCOPE_NM, EXECUTION_REQUEST.CONTEXT_NM, EXECUTION_REQUEST.ST_NM, " +
                "AUTH_EXECUTION_REQUEST.SPACE_NM, AUTH_EXECUTION_REQUEST.USER_NM, AUTH_EXECUTION_REQUEST.USER_PASSWORD, " +
                // Replace with case in case of Spring
                // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                // "END AS clazz " +
                "AUTH_EXECUTION_REQUEST.REQUEST_ID AS AUTH, " +
                "NON_AUTH_EXECUTION_REQUEST.REQUEST_ID AS NON_AUTH " +
                "FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ExecutionRequests") + " EXECUTION_REQUEST " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") + " AUTH_EXECUTION_REQUEST " +
                "ON EXECUTION_REQUEST.REQUEST_ID = AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") + " NON_AUTH_EXECUTION_REQUEST " +
                "ON EXECUTION_REQUEST.REQUEST_ID = NON_AUTH_EXECUTION_REQUEST.REQUEST_ID;";

        CachedRowSet cachedRowSet = getMetadataControl().getExecutionServerMetadataRepository().executeQuery(query, "reader");
        while(cachedRowSet.next()) {

            if (cachedRowSet.getString("AUTH") != null) {
                executionRequests.add(new AuthenticatedExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                        cachedRowSet.getString("REQUEST_NM"),
                        cachedRowSet.getString("REQUEST_DSC"),
                        cachedRowSet.getString("NOTIF_EMAIL"),
                        cachedRowSet.getString("SCOPE_NM"),
                        cachedRowSet.getString("CONTEXT_NM"),
                        ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        scriptExecutionRequestConfiguration.getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID"))),
                        cachedRowSet.getString("SPACE_NM"),
                        cachedRowSet.getString("USER_NM"), cachedRowSet.getString("USER_PASSWORD")));
            } else if (cachedRowSet.getString("NON_AUTH") != null) {
                executionRequests.add(new NonAuthenticatedExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                        cachedRowSet.getString("REQUEST_NM"),
                        cachedRowSet.getString("REQUEST_DSC"),
                        cachedRowSet.getString("NOTIF_EMAIL"),
                        cachedRowSet.getString("SCOPE_NM"),
                        cachedRowSet.getString("CONTEXT_NM"),
                        ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        scriptExecutionRequestConfiguration.getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")))));
            } else {
                LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", cachedRowSet.getString("REQUEST_ID")));

            }
//            switch (cachedRowSet.getInt("clazz")) {
//                case 1:
//                    executionRequests.add(new AuthenticatedExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
//                            SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
//                            cachedRowSet.getString("REQUEST_NM"),
//                            cachedRowSet.getString("REQUEST_DSC"),
//                            cachedRowSet.getString("NOTIF_EMAIL"),
//                            cachedRowSet.getString("SCOPE_NM"),
//                            cachedRowSet.getString("CONTEXT_NM"),
//                            new ArrayList<>(),
//                            cachedRowSet.getString("SPACE_NM"),
//                            cachedRowSet.getString("USER_NM"),
//                            cachedRowSet.getString("USER_PASSWORD")));
//                case 2:
//                    executionRequests.add(new NonAuthenticatedExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
//                            SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
//                            cachedRowSet.getString("REQUEST_NM"),
//                            cachedRowSet.getString("REQUEST_DSC"),
//                            cachedRowSet.getString("NOTIF_EMAIL"),
//                            cachedRowSet.getString("SCOPE_NM"),
//                            cachedRowSet.getString("CONTEXT_NM"),
//                            new ArrayList<>()));
//                default:
//                    LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", cachedRowSet.getString("REQUEST_ID")));
        }
        return executionRequests;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ExecutionRequest> getAllNew()  {
        try {
        List<ExecutionRequest> executionRequests = new ArrayList<>();
        String query = "SELECT EXECUTION_REQUEST.REQUEST_ID, EXECUTION_REQUEST.REQUEST_TMS, EXECUTION_REQUEST.REQUEST_NM, " +
                "EXECUTION_REQUEST.REQUEST_DSC, EXECUTION_REQUEST.NOTIF_EMAIL, EXECUTION_REQUEST.SCOPE_NM, EXECUTION_REQUEST.CONTEXT_NM, EXECUTION_REQUEST.ST_NM, " +
                "AUTH_EXECUTION_REQUEST.SPACE_NM, AUTH_EXECUTION_REQUEST.USER_NM, AUTH_EXECUTION_REQUEST.USER_PASSWORD, " +
                // Replace with case in case of Spring
                // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                // "END AS clazz " +
                "AUTH_EXECUTION_REQUEST.REQUEST_ID AS AUTH, " +
                "NON_AUTH_EXECUTION_REQUEST.REQUEST_ID AS NON_AUTH " +
                "FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ExecutionRequests") + " EXECUTION_REQUEST " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") + " AUTH_EXECUTION_REQUEST " +
                "ON EXECUTION_REQUEST.REQUEST_ID = AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                "LEFT OUTER JOIN " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") + " NON_AUTH_EXECUTION_REQUEST " +
                "ON EXECUTION_REQUEST.REQUEST_ID = NON_AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                "WHERE ST_NM = " + SQLTools.GetStringForSQL(ExecutionRequestStatus.NEW.value()) + ";";

        CachedRowSet cachedRowSet = getMetadataControl().getExecutionServerMetadataRepository().executeQuery(query, "reader");
        while(cachedRowSet.next()) {

            if (cachedRowSet.getString("AUTH") != null) {
                executionRequests.add(new AuthenticatedExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                        cachedRowSet.getString("REQUEST_NM"),
                        cachedRowSet.getString("REQUEST_DSC"),
                        cachedRowSet.getString("NOTIF_EMAIL"),
                        cachedRowSet.getString("SCOPE_NM"),
                        cachedRowSet.getString("CONTEXT_NM"),
                        ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        scriptExecutionRequestConfiguration.getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID"))),
                        cachedRowSet.getString("SPACE_NM"),
                        cachedRowSet.getString("USER_NM"), cachedRowSet.getString("USER_PASSWORD")));
            } else if (cachedRowSet.getString("NON_AUTH") != null) {
                executionRequests.add(new NonAuthenticatedExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                        cachedRowSet.getString("REQUEST_NM"),
                        cachedRowSet.getString("REQUEST_DSC"),
                        cachedRowSet.getString("NOTIF_EMAIL"),
                        cachedRowSet.getString("SCOPE_NM"),
                        cachedRowSet.getString("CONTEXT_NM"),
                        ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        scriptExecutionRequestConfiguration.getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")))));
            } else {
                LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", cachedRowSet.getString("REQUEST_ID")));

            }
//            switch (cachedRowSet.getInt("clazz")) {
//                case 1:
//                    executionRequests.add(new AuthenticatedExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
//                            SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
//                            cachedRowSet.getString("REQUEST_NM"),
//                            cachedRowSet.getString("REQUEST_DSC"),
//                            cachedRowSet.getString("NOTIF_EMAIL"),
//                            cachedRowSet.getString("SCOPE_NM"),
//                            cachedRowSet.getString("CONTEXT_NM"),
//                            new ArrayList<>(),
//                            cachedRowSet.getString("SPACE_NM"),
//                            cachedRowSet.getString("USER_NM"),
//                            cachedRowSet.getString("USER_PASSWORD")));
//                case 2:
//                    executionRequests.add(new NonAuthenticatedExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
//                            SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
//                            cachedRowSet.getString("REQUEST_NM"),
//                            cachedRowSet.getString("REQUEST_DSC"),
//                            cachedRowSet.getString("NOTIF_EMAIL"),
//                            cachedRowSet.getString("SCOPE_NM"),
//                            cachedRowSet.getString("CONTEXT_NM"),
//                            new ArrayList<>()));
//                default:
//                    LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", cachedRowSet.getString("REQUEST_ID")));
        }
        return executionRequests;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ExecutionRequestKey executionRequestKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ExecutionRequest {0}.", executionRequestKey.toString()));
        if (!exists(executionRequestKey)) {
            throw new ExecutionRequestDoesNotExistException(MessageFormat.format(
                    "ExecutionRequest {0} does not exists", executionRequestKey.toString()));
        }
        scriptExecutionRequestConfiguration.deleteByExecutionKey(executionRequestKey);
        List<String> deleteStatement = deleteStatement(executionRequestKey);
        getMetadataControl().getExecutionServerMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ExecutionRequestKey executionRequestKey) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ExecutionRequests") +
                " WHERE " +
                " REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") +
                " WHERE " +
                " REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") +
                " WHERE " +
                " REQUEST_ID = " + SQLTools.GetStringForSQL(executionRequestKey.getId()) + ";");
        return queries;
    }

    @Override
    public void insert(ExecutionRequest executionRequest) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ExecutionRequest {0}.", executionRequest.toString()));
        if (exists(executionRequest.getMetadataKey())) {
            throw new ExecutionRequestAlreadyExistsException(MessageFormat.format(
                    "ExecutionRequest {0} already exists", executionRequest.getMetadataKey().toString()));
        }
        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            scriptExecutionRequestConfiguration.insert(scriptExecutionRequest);
        }
        List<String> insertStatement = insertStatement(executionRequest);
        getMetadataControl().getExecutionServerMetadataRepository().executeBatch(insertStatement);
    }

    private List<String> insertStatement(ExecutionRequest executionRequest) {
        List<String> queries = new ArrayList<>();
        if (executionRequest instanceof AuthenticatedExecutionRequest) {

            queries.add("INSERT INTO " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ExecutionRequests") +
                    " (REQUEST_ID, REQUEST_TMS, REQUEST_NM, REQUEST_DSC, NOTIF_EMAIL, SCOPE_NM, CONTEXT_NM, ST_NM) VALUES (" +
                    SQLTools.GetStringForSQL(executionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getRequestTimestamp()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getName()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getDescription()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getEmail()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getScope()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getContext()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getExecutionRequestStatus().value()) + ");");
            queries.add("INSERT INTO " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("AuthenticationExecutionRequests") +
                    " (REQUEST_ID, SPACE_NM, USER_NM, USER_PASSWORD) VALUES (" +
                    SQLTools.GetStringForSQL(executionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.GetStringForSQL(((AuthenticatedExecutionRequest) executionRequest).getSpace()) + "," +
                    SQLTools.GetStringForSQL(((AuthenticatedExecutionRequest) executionRequest).getUser()) + "," +
                    SQLTools.GetStringForSQL(((AuthenticatedExecutionRequest) executionRequest).getPassword()) + ");");
            return queries;
        } else if (executionRequest instanceof NonAuthenticatedExecutionRequest) {
            queries.add("INSERT INTO " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ExecutionRequests") +
                    " (REQUEST_ID, REQUEST_TMS, REQUEST_NM, REQUEST_DSC, NOTIF_EMAIL, SCOPE_NM, CONTEXT_NM, ST_NM) VALUES (" +
                    SQLTools.GetStringForSQL(executionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getRequestTimestamp()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getName()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getDescription()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getEmail()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getScope()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getContext()) + "," +
                    SQLTools.GetStringForSQL(executionRequest.getExecutionRequestStatus().value()) + ");");
            queries.add("INSERT INTO " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") +
                    " (REQUEST_ID) VALUES (" +
                    SQLTools.GetStringForSQL(executionRequest.getMetadataKey().getId()) + ");");
            return queries;
        } else {
            LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", executionRequest.toString()));
        }
        return queries;
    }
}
