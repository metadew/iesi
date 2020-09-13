package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestImpersonationDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestParameterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ExecutionRequestDtoListResultSetExtractor implements ResultSetExtractor<List<ExecutionRequestDto>> {

    @Override
    public List<ExecutionRequestDto> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ExecutionRequestBuilder> executionRequestBuilderMap = new LinkedHashMap<>();
        while (rs.next()) {
            mapRow(rs, executionRequestBuilderMap);
        }
        return executionRequestBuilderMap.values().stream()
                .map(ExecutionRequestBuilder::build)
                .collect(Collectors.toList());
    }


    private void mapRow(ResultSet resultSet, Map<String, ExecutionRequestBuilder> executionRequestBuilderMap) throws SQLException {
        String executionRequestId = resultSet.getString("exe_req_id");
        ExecutionRequestBuilder executionRequestBuilder = executionRequestBuilderMap.get(executionRequestId);
        if (executionRequestBuilder == null) {
            executionRequestBuilder = mapExecutionRequestBuilderRow(resultSet);
            executionRequestBuilderMap.put(executionRequestId, executionRequestBuilder);
        }
        mapExecutionRequestLabel(resultSet, executionRequestBuilder);
        ScriptExecutionRequestBuilder scriptExecutionRequestBuilder = extractScriptExecutionRequestBuilder(resultSet, executionRequestBuilder);
        if (scriptExecutionRequestBuilder == null) {
            return;
        }
        mapImpersonation(resultSet, scriptExecutionRequestBuilder);
        mapScriptExecutionParameters(resultSet, scriptExecutionRequestBuilder);
        mapScriptExecutionRunId(resultSet, scriptExecutionRequestBuilder);
    }

    private ScriptExecutionRequestBuilder extractScriptExecutionRequestBuilder(ResultSet resultSet, ExecutionRequestBuilder executionRequestBuilder) throws SQLException {
        String scriptExecutionRequestId = resultSet.getString("script_exe_req_id");
        if (scriptExecutionRequestId == null) {
            return null;
        }
        ScriptExecutionRequestBuilder scriptExecutionRequestBuilder = executionRequestBuilder.getScriptExecutionRequests().get(scriptExecutionRequestId);
        if (scriptExecutionRequestBuilder == null) {
            scriptExecutionRequestBuilder = new ScriptExecutionRequestBuilder(
                    resultSet.getString("script_exe_req_id"),
                    resultSet.getString("exe_req_id"),
                    resultSet.getString("script_exe_req_env"),
                    SQLTools.getBooleanFromSql(resultSet.getString("script_exe_req_exit")),
                    new HashMap<>(),
                    new HashMap<>(),
                    ScriptExecutionRequestStatus.valueOf(resultSet.getString("script_exe_req_st")),
                    resultSet.getString("script_exe_req_name_name"),
                    resultSet.getLong("script_exe_req_name_vrs"),
                    null
            );
            executionRequestBuilder.getScriptExecutionRequests().put(scriptExecutionRequestId, scriptExecutionRequestBuilder);
        }
        return scriptExecutionRequestBuilder;
    }

    private void mapScriptExecutionRunId(ResultSet resultSet, ScriptExecutionRequestBuilder scriptExecutionRequestBuilder) throws SQLException {
        // script_exec_id, script_exec_run_id, script_exec_strt_tms, script_exec_end_tms, script_exec_status

        String runId = resultSet.getString("script_exec_run_id");
        if (runId == null) {
            return;
        }
        if (scriptExecutionRequestBuilder.getRunId() == null) {
            scriptExecutionRequestBuilder.setRunId(runId);
        }
    }

    private void mapScriptExecutionParameters(ResultSet resultSet, ScriptExecutionRequestBuilder scriptExecutionRequestBuilder) throws SQLException {
        // script_exe_req_par_id, script_exe_req_par_name, script_exe_req_par_val
        String scriptExecutionRequestParameterId = resultSet.getString("script_exe_req_par_id");
        if (scriptExecutionRequestParameterId == null) {
            return;
        }
        ScriptExecutionRequestParameterDto scriptExecutionRequestParameterDto = scriptExecutionRequestBuilder.getParameters().get(scriptExecutionRequestParameterId);

        if (scriptExecutionRequestParameterDto == null) {
            scriptExecutionRequestParameterDto = new ScriptExecutionRequestParameterDto(
                    resultSet.getString("script_exe_req_par_name"),
                    resultSet.getString("script_exe_req_par_val")
            );
            scriptExecutionRequestBuilder.getParameters().put(scriptExecutionRequestParameterId, scriptExecutionRequestParameterDto);
        }
    }

    private void mapImpersonation(ResultSet resultSet, ScriptExecutionRequestBuilder scriptExecutionRequestBuilder) throws SQLException {
        // script_exe_req_id, script_exe_req_exit, script_exe_req_env,
        // script_exe_req_st, script_exe_req_file, script_exe_req_file_name, script_exe_req_name,
        // script_exe_req_name_name, script_exe_req_name_vrs, script_exe_req_imp_id, script_exe_req_imp_id_id


        String scriptExecutionRequestImpersonationId = resultSet.getString("script_exe_req_imp_id");
        if (scriptExecutionRequestImpersonationId == null) {
            return;
        }
        ScriptExecutionRequestImpersonationDto scriptExecutionRequestImpersonationDto = scriptExecutionRequestBuilder.getImpersonations().get(scriptExecutionRequestImpersonationId);
        if (scriptExecutionRequestImpersonationDto == null) {
            scriptExecutionRequestImpersonationDto = new ScriptExecutionRequestImpersonationDto(resultSet.getString("script_exe_req_imp_id_id"));
            scriptExecutionRequestBuilder.getImpersonations().put(scriptExecutionRequestImpersonationId, scriptExecutionRequestImpersonationDto);
        }
    }

    private void mapExecutionRequestLabel(ResultSet resultSet, ExecutionRequestBuilder executionRequestBuilder) throws SQLException {
        // exe_req_label_id, exe_req_label_name, exe_req_label_value

        String executionRequestLabelId = resultSet.getString("exe_req_label_id");
        if (executionRequestLabelId == null) {
            return;
        }
        ExecutionRequestLabelDto executionRequestLabelDto = executionRequestBuilder.getExecutionRequestLabels().get(executionRequestLabelId);

        if (executionRequestLabelDto == null) {
            executionRequestLabelDto = new ExecutionRequestLabelDto(
                    resultSet.getString("exe_req_label_name"),
                    resultSet.getString("exe_req_label_value")
            );
            executionRequestBuilder.getExecutionRequestLabels().put(executionRequestLabelId, executionRequestLabelDto);
        }
    }

    private ExecutionRequestBuilder mapExecutionRequestBuilderRow(ResultSet resultSet) throws SQLException {
        //             "execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms,
        //             execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc,
        //             execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope,
        //             execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
        return new ExecutionRequestBuilder(
                resultSet.getString("exe_req_id"),
                SQLTools.getLocalDatetimeFromSql(resultSet.getString("exe_req_tms")),
                resultSet.getString("exe_req_name"),
                resultSet.getString("exe_req_desc"),
                resultSet.getString("exe_req_scope"),
                resultSet.getString("exe_req_context"),
                resultSet.getString("exe_req_email"),
                ExecutionRequestStatus.valueOf(resultSet.getString("exec_req_status")),
                new HashMap<>(),
                new HashMap<>()
        );
    }

    @AllArgsConstructor
    @Getter
    private class ExecutionRequestBuilder {

        private String executionRequestId;
        private LocalDateTime requestTimestamp;
        private String name;
        private String description;
        private String scope;
        private String context;
        private String email;
        private ExecutionRequestStatus executionRequestStatus;
        private Map<String, ScriptExecutionRequestBuilder> scriptExecutionRequests;
        public Map<String, ExecutionRequestLabelDto> executionRequestLabels;

        public ExecutionRequestDto build() {
            return new ExecutionRequestDto(executionRequestId, requestTimestamp, name, description, scope, context, email, executionRequestStatus,
                    scriptExecutionRequests.values().stream().map(ScriptExecutionRequestBuilder::build).collect(Collectors.toList()),
                    new HashSet<>(executionRequestLabels.values()));
        }
    }

    @AllArgsConstructor
    @Getter
    private class ScriptExecutionRequestBuilder {
        private String scriptExecutionRequestId;
        private String executionRequestId;
        private String environment;
        private boolean exit;
        private Map<String, ScriptExecutionRequestImpersonationDto> impersonations;
        public Map<String, ScriptExecutionRequestParameterDto> parameters;
        private ScriptExecutionRequestStatus scriptExecutionRequestStatus;
        private String scriptName;
        private Long scriptVersion;
        @Setter
        private String runId;

        public ScriptExecutionRequestDto build() {
            return new ScriptExecutionRequestDto(scriptExecutionRequestId, executionRequestId, environment, exit,
                    new ArrayList<>(impersonations.values()),
                    new ArrayList<>(parameters.values()),
                    scriptExecutionRequestStatus,
                    scriptName, scriptVersion,
                    runId);
        }
    }
}
