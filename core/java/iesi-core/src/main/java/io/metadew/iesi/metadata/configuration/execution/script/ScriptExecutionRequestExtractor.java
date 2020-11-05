package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.*;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptExecutionRequestExtractor implements ResultSetExtractor<List<ScriptExecutionRequest>> {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<ScriptExecutionRequest> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ScriptExecutionRequest> stringScriptExecutionRequestMap = new HashMap<>();
        ScriptExecutionRequest scriptExecutionRequest;
        while (rs.next()) {
            String name = rs.getString("ScriptExecutionRequests_SCRPT_REQUEST_ID");
            scriptExecutionRequest = stringScriptExecutionRequestMap.get(name);
            if (scriptExecutionRequest == null) {
                scriptExecutionRequest = mapRow(rs);
                stringScriptExecutionRequestMap.put(name, scriptExecutionRequest);
            }
            addMapping(scriptExecutionRequest, rs);
        }
        return new ArrayList<>(stringScriptExecutionRequestMap.values());
    }

    private ScriptExecutionRequest mapRow(ResultSet rs) throws SQLException {
        ScriptExecutionRequestKey scriptExecutionRequestKey = ScriptExecutionRequestKey.builder()
                .id(rs.getString("ScriptExecutionRequests_SCRPT_REQUEST_ID"))
                .build();
        ExecutionRequestKey executionRequestKey = ExecutionRequestKey.builder()
                .id(rs.getString("ScriptExecutionRequests_ID"))
                .build();
        if (rs.getString("FILE_REQ") != null) {
            return ScriptFileExecutionRequest.builder()
                    .scriptExecutionRequestKey(scriptExecutionRequestKey)
                    .executionRequestKey(executionRequestKey)
                    .fileName(rs.getString("ScriptFileExecutionRequests_SCRPT_FILENAME"))
                    .environment(rs.getString("ScriptExecutionRequests_ENVIRONMENT"))
                    .exit(rs.getBoolean("ScriptExecutionRequests_EXIT"))
                    .impersonations(new ArrayList<>())
                    .parameters(new ArrayList<>())
                    .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.valueOf(rs.getString("ScriptExecutionRequests_ST_NM")))
                    .build();
        } else if (rs.getString("NAME_REQ") != null) {
            return ScriptNameExecutionRequest.builder()
                    .scriptExecutionRequestKey(scriptExecutionRequestKey)
                    .executionRequestKey(executionRequestKey)
                    .environment(rs.getString("ScriptExecutionRequests_ENVIRONMENT"))
                    .exit(rs.getBoolean("ScriptExecutionRequests_EXIT"))
                    .impersonations(new ArrayList<>())
                    .parameters(new ArrayList<>())
                    .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.valueOf(rs.getString("ScriptExecutionRequests_ST_NM")))
                    .scriptName(rs.getString("ScriptNameExecutionRequests_SCRPT_NAME"))
                    .scriptVersion(rs.getLong("ScriptNameExecutionRequests_SCRPT_VRS"))
                    .build();
        } else {
            LOGGER.warn(MessageFormat.format("ExecutionRequest {0} does not have a certain class", executionRequestKey.toString()));
            return null;
        }
    }

    private void addMapping(ScriptExecutionRequest scriptExecutionRequest, ResultSet rs) throws SQLException {
        ScriptExecutionRequestImpersonationKey scriptExecutionRequestImpersonationKey = ScriptExecutionRequestImpersonationKey.builder()
                .id(rs.getString("ScriptExecutionRequestImpersonations_ID")).build();
        ScriptExecutionRequestKey scriptExecutionRequestKey = ScriptExecutionRequestKey.builder()
                .id(rs.getString("ScriptExecutionRequestImpersonations_SCRIPT_EXEC_REQ_ID")).build();
        ImpersonationKey impersonationKey = ImpersonationKey.builder()
                .name(rs.getString("ScriptExecutionRequestImpersonations_IMP_ID")).build();
        if (rs.getString("ScriptExecutionRequestImpersonations_ID") != null) {
            ScriptExecutionRequestImpersonation scriptExecutionRequestImpersonation = ScriptExecutionRequestImpersonation.builder()
                    .scriptExecutionRequestImpersonationKey(scriptExecutionRequestImpersonationKey)
                    .scriptExecutionRequestKey(scriptExecutionRequestKey)
                    .impersonationKey(impersonationKey)
                    .build();
            scriptExecutionRequest.addScriptExecutionRequestImpersonation(scriptExecutionRequestImpersonation);
        }
        ScriptExecutionRequestParameterKey scriptExecutionRequestParameterKey = ScriptExecutionRequestParameterKey.builder()
                .id(rs.getString("ScriptExecutionRequestParameters_SCRIPT_EXEC_REQ_ID")).build();
        if (rs.getString("ScriptExecutionRequestParameters_ID") != null) {
            ScriptExecutionRequestParameter scriptExecutionRequestParameter = ScriptExecutionRequestParameter.builder()
                    .scriptExecutionRequestParameterKey(scriptExecutionRequestParameterKey)
                    .scriptExecutionRequestKey(scriptExecutionRequestKey)
                    .name(rs.getString("ScriptExecutionRequestParameters_NAME"))
                    .value(rs.getString("ScriptExecutionRequestParameters_VALUE"))
                    .build();
            scriptExecutionRequest.addScriptExecutionRequestParameter(scriptExecutionRequestParameter);
        }
    }
}
