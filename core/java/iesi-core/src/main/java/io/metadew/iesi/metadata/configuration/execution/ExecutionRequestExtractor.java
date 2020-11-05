package io.metadew.iesi.metadata.configuration.execution;

import io.metadew.iesi.metadata.definition.execution.*;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

public class ExecutionRequestExtractor implements ResultSetExtractor<List<ExecutionRequest>> {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<ExecutionRequest> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ExecutionRequest> executionRequestMap = new HashMap<>();
        ExecutionRequest executionRequest;
        while (rs.next()) {
            String name = rs.getString("EXECUTION_REQUEST_REQUEST_ID");
            executionRequest = executionRequestMap.get(name);
            if (executionRequest == null) {
                executionRequest = mapRow(rs);
                executionRequestMap.put(name, executionRequest);
            }
            addMapping(executionRequest, rs);
        }
        return new ArrayList<>(executionRequestMap.values());
    }

    private ExecutionRequest mapRow(ResultSet rs) throws SQLException {
        ExecutionRequestKey executionRequestKey = ExecutionRequestKey.builder()
                .id(rs.getString("EXECUTION_REQUEST_REQUEST_ID"))
                .build();
        if (rs.getString("AUTH") != null) {
            return AuthenticatedExecutionRequest.builder().executionRequestKey(executionRequestKey)
//                    .requestTimestamp(rs.getTimestamp("EXECUTION_REQUEST_REQUEST_TMS").toLocalDateTime())
                    .name(rs.getString("EXECUTION_REQUEST_REQUEST_NM"))
                    .description(rs.getString("EXECUTION_REQUEST_REQUEST_DSC"))
                    .scope(rs.getString("EXECUTION_REQUEST_SCOPE_NM"))
                    .context(rs.getString("EXECUTION_REQUEST_CONTEXT_NM"))
                    .email(rs.getString("EXECUTION_REQUEST_NOTIF_EMAIL"))
                    .executionRequestStatus(ExecutionRequestStatus.valueOf(rs.getString("EXECUTION_REQUEST_ST_NM")))
                    .scriptExecutionRequests(new ArrayList<>())
                    .executionRequestLabels(new HashSet<>())
                    .space(rs.getString("AUTH_EXECUTION_REQUEST_SPACE_NM"))
                    .user(rs.getString("AUTH_EXECUTION_REQUEST_USER_NM"))
                    .password(rs.getString("AUTH_EXECUTION_REQUEST_USER_PASSWORD"))
                    .build();
        } else if (rs.getString("NON_AUTH") != null) {
            return NonAuthenticatedExecutionRequest.builder().executionRequestKey(executionRequestKey)
//                    .requestTimestamp(rs.getTimestamp("EXECUTION_REQUEST_REQUEST_TMS").toLocalDateTime().toString())
                    .name(rs.getString("EXECUTION_REQUEST_REQUEST_NM"))
                    .description(rs.getString("EXECUTION_REQUEST_REQUEST_DSC"))
                    .scope(rs.getString("EXECUTION_REQUEST_SCOPE_NM"))
                    .context(rs.getString("EXECUTION_REQUEST_CONTEXT_NM"))
                    .email(rs.getString("EXECUTION_REQUEST_NOTIF_EMAIL"))
                    .executionRequestStatus(ExecutionRequestStatus.valueOf(rs.getString("EXECUTION_REQUEST_ST_NM")))
                    .executionRequestLabels(new HashSet<>())
                    .build();
        } else {
            LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", executionRequestKey.toString()));
            return null;
        }
    }

    private void addMapping(ExecutionRequest executionRequest, ResultSet rs) throws SQLException {
        ExecutionRequestLabelKey executionRequestLabelKey = ExecutionRequestLabelKey.builder()
                .id(rs.getString("EXECUTION_REQUEST_REQUEST_ID")).build();
        ExecutionRequestKey executionRequestKey = ExecutionRequestKey.builder()
                .id(rs.getString("EXECUTION_REQUEST_REQUEST_ID")).build();
        if (rs.getString("EXECUTION_REQUEST_REQUEST_ID") != null) {
            ExecutionRequestLabel executionRequestLabel = ExecutionRequestLabel.builder()
                    .executionRequestKey(executionRequestKey)
                    .metadataKey(executionRequestLabelKey)
                    .name(rs.getString("ExecutionRequestLabels_NAME"))
                    .value(rs.getString("ExecutionRequestLabels_VALUE"))
                    .build();
            executionRequest.addExecutionRequestLabel(executionRequestLabel);
        }
//        List<ScriptExecutionRequest> scriptExecutionRequests = ScriptExecutionRequestConfiguration.getInstance().getByExecutionRequest(executionRequestKey);
//        for(ScriptExecutionRequest scriptExecutionRequest: scriptExecutionRequests){
//            assert executionRequest != null;
//            executionRequest.addScriptExecutionRequest(scriptExecutionRequest);
//        }
    }
}
