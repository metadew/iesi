package io.metadew.iesi.metadata.configuration.execution;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.*;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.repository.MetadataRepository;
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

    private static ExecutionRequestConfiguration instance;

    public static synchronized ExecutionRequestConfiguration getInstance() {
        if (instance == null) {
            instance = new ExecutionRequestConfiguration();
        }
        return instance;
    }

    private ExecutionRequestConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getExecutionServerMetadataRepository());
    }

    //TODO : Remove NM
    @Override
    public Optional<ExecutionRequest> get(ExecutionRequestKey executionRequestKey) {
        try {
            String query = "SELECT EXECUTION_REQUEST.REQUEST_ID, EXECUTION_REQUEST.REQUEST_TMS, EXECUTION_REQUEST.REQUEST_NM, " +
                    "EXECUTION_REQUEST.REQUEST_DSC, EXECUTION_REQUEST.NOTIF_EMAIL, EXECUTION_REQUEST.SCOPE_NM, EXECUTION_REQUEST.CONTEXT_NM, EXECUTION_REQUEST.ST_NM, " +
                    "AUTH_EXECUTION_REQUEST.USER_ID_NM, AUTH_EXECUTION_REQUEST.USERNAME_NM, " +
                    // Replace with case in case of Spring
                    // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                    // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                    // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                    // "END AS clazz " +
                    "AUTH_EXECUTION_REQUEST.REQUEST_ID AS AUTH, " +
                    "NON_AUTH_EXECUTION_REQUEST.REQUEST_ID AS NON_AUTH " +
                    "FROM " + getMetadataRepository().getTableNameByLabel("ExecutionRequests") + " EXECUTION_REQUEST " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") + " AUTH_EXECUTION_REQUEST " +
                    "ON EXECUTION_REQUEST.REQUEST_ID = AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") + " NON_AUTH_EXECUTION_REQUEST " +
                    "ON EXECUTION_REQUEST.REQUEST_ID = NON_AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                    "WHERE EXECUTION_REQUEST.REQUEST_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ExecutionRequest {0}. Returning first implementation", executionRequestKey.toString()));
            }
            cachedRowSet.next();
            if (cachedRowSet.getString("AUTH") != null) {
                return Optional.of(new AuthenticatedExecutionRequest(
                        executionRequestKey,
                        // new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("SECURITY_GROUP_ID"))),
                        // cachedRowSet.getString("SECURITY_GROUP_NAME"),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                        cachedRowSet.getString("REQUEST_NM"),
                        cachedRowSet.getString("REQUEST_DSC"),
                        cachedRowSet.getString("NOTIF_EMAIL"),
                        cachedRowSet.getString("SCOPE_NM"),
                        cachedRowSet.getString("CONTEXT_NM"),
                        ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(executionRequestKey),
                        ExecutionRequestLabelConfiguration.getInstance().getByExecutionRequest(executionRequestKey),
                        cachedRowSet.getString("USER_ID_NM"),
                        cachedRowSet.getString("USERNAME_NM")));
            } else if (cachedRowSet.getString("NON_AUTH") != null) {
                return Optional.of(new NonAuthenticatedExecutionRequest(
                        executionRequestKey,
                        // new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("SECURITY_GROUP_ID"))),
                        // cachedRowSet.getString("SECURITY_GROUP_NAME"),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                        cachedRowSet.getString("REQUEST_NM"),
                        cachedRowSet.getString("REQUEST_DSC"),
                        cachedRowSet.getString("NOTIF_EMAIL"),
                        cachedRowSet.getString("SCOPE_NM"),
                        cachedRowSet.getString("CONTEXT_NM"),
                        ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(executionRequestKey),
                        ExecutionRequestLabelConfiguration.getInstance().getByExecutionRequest(executionRequestKey)));
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

    //TODO : Remove NM
    @Override
    public List<ExecutionRequest> getAll() {
        try {
            List<ExecutionRequest> executionRequests = new ArrayList<>();
            String query = "SELECT EXECUTION_REQUEST.REQUEST_ID, EXECUTION_REQUEST.REQUEST_TMS, EXECUTION_REQUEST.REQUEST_NM, " +
                    "EXECUTION_REQUEST.REQUEST_DSC, EXECUTION_REQUEST.NOTIF_EMAIL, EXECUTION_REQUEST.SCOPE_NM, EXECUTION_REQUEST.CONTEXT_NM, EXECUTION_REQUEST.ST_NM, " +
                    "AUTH_EXECUTION_REQUEST.USER_ID_NM, AUTH_EXECUTION_REQUEST.USERNAME_NM, " +
                    // Replace with case in case of Spring
                    // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                    // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                    // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                    // "END AS clazz " +
                    "AUTH_EXECUTION_REQUEST.REQUEST_ID AS AUTH, " +
                    "NON_AUTH_EXECUTION_REQUEST.REQUEST_ID AS NON_AUTH " +
                    "FROM " + getMetadataRepository().getTableNameByLabel("ExecutionRequests") + " EXECUTION_REQUEST " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") + " AUTH_EXECUTION_REQUEST " +
                    "ON EXECUTION_REQUEST.REQUEST_ID = AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") + " NON_AUTH_EXECUTION_REQUEST " +
                    "ON EXECUTION_REQUEST.REQUEST_ID = NON_AUTH_EXECUTION_REQUEST.REQUEST_ID;";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {

                if (cachedRowSet.getString("AUTH") != null) {
                    executionRequests.add(new AuthenticatedExecutionRequest(
                            new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
                            // new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("SECURITY_GROUP_ID"))),
                            // cachedRowSet.getString("SECURITY_GROUP_NAME"),
                            SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                            cachedRowSet.getString("REQUEST_NM"),
                            cachedRowSet.getString("REQUEST_DSC"),
                            cachedRowSet.getString("NOTIF_EMAIL"),
                            cachedRowSet.getString("SCOPE_NM"),
                            cachedRowSet.getString("CONTEXT_NM"),
                            ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                            ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID"))),
                            ExecutionRequestLabelConfiguration.getInstance().getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID"))),
                            cachedRowSet.getString("USER_ID_NM"),
                            cachedRowSet.getString("USERNAME_NM")));
                } else if (cachedRowSet.getString("NON_AUTH") != null) {
                    executionRequests.add(new NonAuthenticatedExecutionRequest(
                            new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
                            // new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("SECURITY_GROUP_ID"))),
                            // cachedRowSet.getString("SECURITY_GROUP_NAME"),
                            SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                            cachedRowSet.getString("REQUEST_NM"),
                            cachedRowSet.getString("REQUEST_DSC"),
                            cachedRowSet.getString("NOTIF_EMAIL"),
                            cachedRowSet.getString("SCOPE_NM"),
                            cachedRowSet.getString("CONTEXT_NM"),
                            ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                            ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID"))),
                            ExecutionRequestLabelConfiguration.getInstance().getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")))));
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

    //TODO : Remove NM
    public List<ExecutionRequest> getAllNew() {
        try {
            List<ExecutionRequest> executionRequests = new ArrayList<>();
            String query = "SELECT EXECUTION_REQUEST.REQUEST_ID, EXECUTION_REQUEST.REQUEST_TMS, EXECUTION_REQUEST.REQUEST_NM, " +
                    "EXECUTION_REQUEST.REQUEST_DSC, EXECUTION_REQUEST.NOTIF_EMAIL, EXECUTION_REQUEST.SCOPE_NM, EXECUTION_REQUEST.CONTEXT_NM, EXECUTION_REQUEST.ST_NM, " +
                    "AUTH_EXECUTION_REQUEST.USER_ID_NM, AUTH_EXECUTION_REQUEST.USERNAME, " +
                    // Replace with case in case of Spring
                    // "CASE WHEN AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 1 " +
                    // "WHEN NON_AUTH_EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 2 " +
                    // "WHEN EXECUTION_REQUEST.REQUEST_ID IS NOT NULL THEN 0 " +
                    // "END AS clazz " +
                    "AUTH_EXECUTION_REQUEST.REQUEST_ID AS AUTH, " +
                    "NON_AUTH_EXECUTION_REQUEST.REQUEST_ID AS NON_AUTH " +
                    "FROM " + getMetadataRepository().getTableNameByLabel("ExecutionRequests") + " EXECUTION_REQUEST " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") + " AUTH_EXECUTION_REQUEST " +
                    "ON EXECUTION_REQUEST.REQUEST_ID = AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                    "LEFT OUTER JOIN " + getMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") + " NON_AUTH_EXECUTION_REQUEST " +
                    "ON EXECUTION_REQUEST.REQUEST_ID = NON_AUTH_EXECUTION_REQUEST.REQUEST_ID " +
                    "WHERE ST_NM = " + SQLTools.getStringForSQL(ExecutionRequestStatus.NEW.value()) + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {

                if (cachedRowSet.getString("AUTH") != null) {
                    executionRequests.add(new AuthenticatedExecutionRequest(
                            new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
                            // new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("SECURITY_GROUP_ID"))),
                            // cachedRowSet.getString("SECURITY_GROUP_NAME"),
                            SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                            cachedRowSet.getString("REQUEST_NM"),
                            cachedRowSet.getString("REQUEST_DSC"),
                            cachedRowSet.getString("NOTIF_EMAIL"),
                            cachedRowSet.getString("SCOPE_NM"),
                            cachedRowSet.getString("CONTEXT_NM"),
                            ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                            ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID"))),
                            ExecutionRequestLabelConfiguration.getInstance().getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID"))),
                            cachedRowSet.getString("USER_ID_NM"),
                            cachedRowSet.getString("USERNAME_NM")));
                } else if (cachedRowSet.getString("NON_AUTH") != null) {
                    executionRequests.add(new NonAuthenticatedExecutionRequest(
                            new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")),
                            // new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("SECURITY_GROUP_ID"))),
                            // cachedRowSet.getString("SECURITY_GROUP_NAME"),
                            SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
                            cachedRowSet.getString("REQUEST_NM"),
                            cachedRowSet.getString("REQUEST_DSC"),
                            cachedRowSet.getString("NOTIF_EMAIL"),
                            cachedRowSet.getString("SCOPE_NM"),
                            cachedRowSet.getString("CONTEXT_NM"),
                            ExecutionRequestStatus.valueOf(cachedRowSet.getString("ST_NM")),
                            ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID"))),
                            ExecutionRequestLabelConfiguration.getInstance().getByExecutionRequest(new ExecutionRequestKey(cachedRowSet.getString("REQUEST_ID")))));
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
    public void delete(ExecutionRequestKey executionRequestKey) {
        LOGGER.trace(MessageFormat.format("Deleting ExecutionRequest {0}.", executionRequestKey.toString()));
        if (!exists(executionRequestKey)) {
            throw new MetadataDoesNotExistException(executionRequestKey);
        }
        ScriptExecutionRequestConfiguration.getInstance().deleteByExecutionRequest(executionRequestKey);
        ExecutionRequestLabelConfiguration.getInstance().deleteByExecutionRequest(executionRequestKey);
        List<String> deleteStatement = deleteStatement(executionRequestKey);
        getMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ExecutionRequestKey executionRequestKey) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ExecutionRequests") +
                " WHERE " +
                " REQUEST_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") +
                " WHERE " +
                " REQUEST_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") +
                " WHERE " +
                " REQUEST_ID = " + SQLTools.getStringForSQL(executionRequestKey.getId()) + ";");
        return queries;
    }

    @Override
    public void insert(ExecutionRequest executionRequest) {
        LOGGER.trace(MessageFormat.format("Inserting ExecutionRequest {0}.", executionRequest.toString()));
        if (exists(executionRequest.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(executionRequest);
        }
        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            ScriptExecutionRequestConfiguration.getInstance().insert(scriptExecutionRequest);
        }
        for (ExecutionRequestLabel executionRequestLabel : executionRequest.getExecutionRequestLabels()) {
            ExecutionRequestLabelConfiguration.getInstance().insert(executionRequestLabel);
        }
        List<String> insertStatement = insertStatement(executionRequest);
        getMetadataRepository().executeBatch(insertStatement);
    }

    //TODO : Remove NM
    private List<String> insertStatement(ExecutionRequest executionRequest) {
        List<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + getMetadataRepository().getTableNameByLabel("ExecutionRequests") +
                " (REQUEST_ID, " +
                //"SECURITY_GROUP_ID, SECURITY_GROUP_NAME, " +
                "REQUEST_TMS, REQUEST_NM, REQUEST_DSC, NOTIF_EMAIL, SCOPE_NM, CONTEXT_NM, ST_NM) VALUES (" +
                SQLTools.getStringForSQL(executionRequest.getMetadataKey().getId()) + "," +
                // SQLTools.getStringForSQL(executionRequest.getSecurityGroupKey().getUuid()) + ", " +
                // SQLTools.getStringForSQL(executionRequest.getSecurityGroupName()) + ", " +
                SQLTools.getStringForSQL(executionRequest.getRequestTimestamp()) + "," +
                SQLTools.getStringForSQL(executionRequest.getName()) + "," +
                SQLTools.getStringForSQL(executionRequest.getDescription()) + "," +
                SQLTools.getStringForSQL(executionRequest.getEmail()) + "," +
                SQLTools.getStringForSQL(executionRequest.getScope()) + "," +
                SQLTools.getStringForSQL(executionRequest.getContext()) + "," +
                SQLTools.getStringForSQL(executionRequest.getExecutionRequestStatus().value()) + ");");
        if (executionRequest instanceof AuthenticatedExecutionRequest) {
            queries.add("INSERT INTO " + getMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") +
                    " (REQUEST_ID, USER_ID_NM, USERNAME) VALUES (" +
                    SQLTools.getStringForSQL(executionRequest.getMetadataKey().getId()) + "," +
                    SQLTools.getStringForSQL(((AuthenticatedExecutionRequest) executionRequest).getUserID()) + "," +
                    SQLTools.getStringForSQL(((AuthenticatedExecutionRequest) executionRequest).getUsername()) + ");");
            return queries;
        } else if (executionRequest instanceof NonAuthenticatedExecutionRequest) {
            queries.add("INSERT INTO " + getMetadataRepository().getTableNameByLabel("NonAuthenticatedExecutionRequests") +
                    " (REQUEST_ID) VALUES (" +
                    SQLTools.getStringForSQL(executionRequest.getMetadataKey().getId()) + ");");
            return queries;
        } else {
            LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", executionRequest.toString()));
        }
        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            queries.addAll(ScriptExecutionRequestConfiguration.getInstance().insertStatement(scriptExecutionRequest));
        }
        return queries;
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
        List<String> updateStatement = updateStatement(executionRequest);
        getMetadataRepository().executeBatch(updateStatement);
    }

    //TODO : Remove NM
    private List<String> updateStatement(ExecutionRequest executionRequest) {
        List<String> queries = new ArrayList<>();
        queries.add("UPDATE " + getMetadataRepository().getTableNameByLabel("ExecutionRequests") + " SET " +
                "REQUEST_TMS=" + SQLTools.getStringForSQL(executionRequest.getRequestTimestamp()) + "," +
                "REQUEST_NM=" + SQLTools.getStringForSQL(executionRequest.getName()) + "," +
                "REQUEST_DSC=" + SQLTools.getStringForSQL(executionRequest.getDescription()) + "," +
                "NOTIF_EMAIL=" + SQLTools.getStringForSQL(executionRequest.getEmail()) + "," +
                "SCOPE_NM=" + SQLTools.getStringForSQL(executionRequest.getScope()) + "," +
                "CONTEXT_NM=" + SQLTools.getStringForSQL(executionRequest.getContext()) + "," +
                "ST_NM=" + SQLTools.getStringForSQL(executionRequest.getExecutionRequestStatus().value()) +
                " WHERE " +
                "REQUEST_ID =" + SQLTools.getStringForSQL(executionRequest.getMetadataKey().getId()) + ";");
        if (executionRequest instanceof AuthenticatedExecutionRequest) {
            queries.add("UPDATE " + getMetadataRepository().getTableNameByLabel("AuthenticatedExecutionRequests") + " SET " +
                    "USER_ID_NM=" + SQLTools.getStringForSQL(((AuthenticatedExecutionRequest) executionRequest).getUserID()) + "," +
                    "USERNAME=" + SQLTools.getStringForSQL(((AuthenticatedExecutionRequest) executionRequest).getUsername())  +
                    " WHERE " +
                    "REQUEST_ID =" + SQLTools.getStringForSQL(executionRequest.getMetadataKey().getId()) + ";");
            return queries;
        } else if (executionRequest instanceof NonAuthenticatedExecutionRequest) {
            return queries;
        } else {
            LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", executionRequest.toString()));
        }
        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            queries.addAll(ScriptExecutionRequestConfiguration.getInstance().updateStatement(scriptExecutionRequest));
        }
        return queries;
    }
}
