package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestImpersonationDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestParameterDto;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Log4j2
public class ExecutionRequestDtoRepository extends PaginatedRepository implements IExecutionRequestDtoRepository {

    private String getFetchAllQuery(Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters) {
        return "select execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass, " +
                "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                "execution_request_labels.ID as exe_req_label_id, execution_request_labels.NAME as exe_req_label_name, execution_request_labels.VALUE as exe_req_label_value, " +
                "script_execution_requests.SCRPT_REQUEST_ID as script_exe_req_id, script_execution_requests.EXIT as script_exe_req_exit, script_execution_requests.ENVIRONMENT as script_exe_req_env, script_execution_requests.ST_NM script_exe_req_st, " +
                "file_script_execution_requests.ID as script_exe_req_file_id, file_script_execution_requests.SCRPT_FILENAME as script_exe_req_file_name, " +
                "name_script_execution_requests.ID as script_exe_req_name_id, name_script_execution_requests.SCRPT_NAME as script_exe_req_name_name, name_script_execution_requests.SCRPT_VRS as script_exe_req_name_vrs, " +
                "script_execution_request_imps.ID as script_exe_req_imp_id, script_execution_request_imps.IMP_ID as script_exe_req_imp_id_id, " +
                "script_execution_request_pars.ID as script_exe_req_par_id, script_execution_request_pars.NAME as script_exe_req_par_name, script_execution_request_pars.VALUE as script_exe_req_par_val, " +
                "script_executions.ID as script_exec_id, script_executions.RUN_ID as script_exec_run_id, script_executions.STRT_TMS as script_exec_strt_tms, script_executions.END_TMS as script_exec_end_tms, script_executions.ST_NM as script_exec_status " +
                "from " +
                // base table
                " (" + getBaseQuery(pageable, executionRequestFilters) + ") base_execution_requests " +
                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                "on base_execution_requests.REQUEST_ID = execution_requests.REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                "on base_execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                "on base_execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " execution_request_labels " +
                "on base_execution_requests.REQUEST_ID = execution_request_labels.REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                "on base_execution_requests.REQUEST_ID = script_execution_requests.ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() + " script_execution_request_imps " +
                "on script_execution_requests.SCRPT_REQUEST_ID = script_execution_request_imps.SCRIPT_EXEC_REQ_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " script_execution_request_pars " +
                "on script_execution_requests.SCRPT_REQUEST_ID = script_execution_request_pars.SCRIPT_EXEC_REQ_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() + " script_executions " +
                "on script_execution_requests.SCRPT_REQUEST_ID = script_executions.SCRPT_REQUEST_ID " +
                getOrderByClause(pageable) +
                ";";
    }

    private String getBaseQuery(Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters) {
        return "SELECT distinct execution_requests.REQUEST_ID, execution_requests.REQUEST_TMS, name_script_execution_requests.SCRPT_NAME, name_script_execution_requests.SCRPT_VRS " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " execution_request_labels " +
                "on execution_requests.REQUEST_ID = execution_request_labels.REQUEST_ID " +
                getWhereClause(executionRequestFilters) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }


    private String getWhereClause(List<ExecutionRequestFilter> executionRequestFilters) {
        String filterStatements = executionRequestFilters.stream().map(executionRequestFilter -> {
                    if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.NAME)) {
                        return " name_script_execution_requests.SCRPT_NAME " + (executionRequestFilter.isExactMatch() ? "=" : "LIKE") + " '" + (executionRequestFilter.isExactMatch() ? "" : "%") + executionRequestFilter.getValue() + (executionRequestFilter.isExactMatch() ? "" : "%") + "' ";
                    } else if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.VERSION)) {
                        return " name_script_execution_requests.SCRPT_VRS = " + Long.parseLong(executionRequestFilter.getValue()) + " ";
                    } else if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.ENVIRONMENT)) {
                        return " script_execution_requests.ENVIRONMENT " + (executionRequestFilter.isExactMatch() ? "=" : "LIKE") + " '" + (executionRequestFilter.isExactMatch() ? "" : "%") + executionRequestFilter.getValue() + (executionRequestFilter.isExactMatch() ? "" : "%") + "' ";
                    } else if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.ID)) {
                        return " execution_requests.REQUEST_ID " + (executionRequestFilter.isExactMatch() ? "=" : "LIKE") + " '" + (executionRequestFilter.isExactMatch() ? "" : "%") + executionRequestFilter.getValue() + (executionRequestFilter.isExactMatch() ? "" : "%") + "' ";
                    } else if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.LABEL)) {
                        return " execution_request_labels.NAME = '" + executionRequestFilter.getValue().split(":")[0] +
                                "' and execution_request_labels.VALUE " + (executionRequestFilter.isExactMatch() ? "=" : "LIKE") + " '" + (executionRequestFilter.isExactMatch() ? "" : "%") + executionRequestFilter.getValue().split(":")[1] + (executionRequestFilter.isExactMatch() ? "" : "%") + "' ";
                    } else {
                        return null;
                    }
                }
        )
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));
        if (filterStatements.isEmpty()) {
            return "";
        }
        return " WHERE " + filterStatements;
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ";
        List<String> sorting = pageable.getSort().stream().map(order -> {
            // add further sort on the ScriptAndScriptVersionTable here
            if (order.getProperty().equalsIgnoreCase("SCRIPT")) {
                return "name_script_execution_requests.SCRPT_NAME " + order.getDirection();
            } else if (order.getProperty().equalsIgnoreCase("REQUEST_TIMESTAMP")) {
                return "execution_requests.REQUEST_TMS " + order.getDirection();
            } else if (order.getProperty().equalsIgnoreCase("VERSION")) {
                return "name_script_execution_requests.SCRPT_VRS " + order.getDirection();
            } else {
                return null;
            }
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sorting.isEmpty()) {
            return "";
        }
        return " ORDER BY " + String.join(", ", sorting) + " ";
    }


    private long getRowSize(List<ExecutionRequestFilter> executionRequestFilters) throws SQLException {
        String query = "select count(*) as row_count from (" +
                "SELECT distinct execution_requests.REQUEST_ID " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " execution_request_labels " +
                "on execution_requests.REQUEST_ID = execution_request_labels.REQUEST_ID " +
                getWhereClause(executionRequestFilters) +
                ");";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;


    @Autowired
    ExecutionRequestDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @Override
    public Page<ExecutionRequestDto> getAll(Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters) {
        try {
            Map<String, ExecutionRequestBuilder> executionRequestBuilderMap = new LinkedHashMap<>();
            String query = getFetchAllQuery(pageable, executionRequestFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, executionRequestBuilderMap);
            }
            List<ExecutionRequestDto> executionRequestDtoList = executionRequestBuilderMap.values().stream()
                    .map(ExecutionRequestBuilder::build)
                    .collect(Collectors.toList());
            return new PageImpl<>(executionRequestDtoList, pageable, getRowSize(executionRequestFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExecutionRequestDto> getById(UUID uuid) {
        try {
            Map<String, ExecutionRequestBuilder> executionRequestBuilderMap = new HashMap<>();
            List<ExecutionRequestFilter> executionRequestFilters = Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.ID, uuid.toString(), true)).collect(Collectors.toList());
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository()
                    .executeQuery(getFetchAllQuery(Pageable.unpaged(), executionRequestFilters), "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, executionRequestBuilderMap);
            }
            return executionRequestBuilderMap.values().stream()
                    .findFirst()
                    .map(ExecutionRequestBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<String, ExecutionRequestBuilder> executionRequestBuilderMap) throws SQLException {
        String executionRequestId = cachedRowSet.getString("exe_req_id");
        ExecutionRequestBuilder executionRequestBuilder = executionRequestBuilderMap.get(executionRequestId);
        if (executionRequestBuilder == null) {
            executionRequestBuilder = mapExecutionRequestBuilderRow(cachedRowSet);
            executionRequestBuilderMap.put(executionRequestId, executionRequestBuilder);
        }
        mapExecutionRequestLabel(cachedRowSet, executionRequestBuilder);
        ScriptExecutionRequestBuilder scriptExecutionRequestBuilder = extractScriptExecutionRequestBuilder(cachedRowSet, executionRequestBuilder);
        if (scriptExecutionRequestBuilder == null) {
            return;
        }
        mapImpersonation(cachedRowSet, scriptExecutionRequestBuilder);
        mapScriptExecutionParameters(cachedRowSet, scriptExecutionRequestBuilder);
        mapScriptExecutionRunId(cachedRowSet, scriptExecutionRequestBuilder);
    }

    private ScriptExecutionRequestBuilder extractScriptExecutionRequestBuilder(CachedRowSet cachedRowSet, ExecutionRequestBuilder executionRequestBuilder) throws SQLException {
        String scriptExecutionRequestId = cachedRowSet.getString("script_exe_req_id");
        if (scriptExecutionRequestId == null) {
            return null;
        }
        ScriptExecutionRequestBuilder scriptExecutionRequestBuilder = executionRequestBuilder.getScriptExecutionRequests().get(scriptExecutionRequestId);
        if (scriptExecutionRequestBuilder == null) {
            scriptExecutionRequestBuilder = new ScriptExecutionRequestBuilder(
                    cachedRowSet.getString("script_exe_req_id"),
                    cachedRowSet.getString("exe_req_id"),
                    cachedRowSet.getString("script_exe_req_env"),
                    SQLTools.getBooleanFromSql(cachedRowSet.getString("script_exe_req_exit")),
                    new HashMap<>(),
                    new HashMap<>(),
                    ScriptExecutionRequestStatus.valueOf(cachedRowSet.getString("script_exe_req_st")),
                    cachedRowSet.getString("script_exe_req_name_name"),
                    cachedRowSet.getLong("script_exe_req_name_vrs"),
                    null,
                    null
            );
            executionRequestBuilder.getScriptExecutionRequests().put(scriptExecutionRequestId, scriptExecutionRequestBuilder);
        }
        return scriptExecutionRequestBuilder;
    }

    private void mapScriptExecutionRunId(CachedRowSet cachedRowSet, ScriptExecutionRequestBuilder scriptExecutionRequestBuilder) throws SQLException {
        // script_exec_id, script_exec_run_id, script_exec_strt_tms, script_exec_end_tms, script_exec_status

        String runId = cachedRowSet.getString("script_exec_run_id");
        if (runId == null) {
            return;
        }
        if (scriptExecutionRequestBuilder.getRunId() == null) {
            scriptExecutionRequestBuilder.setRunId(runId);
            scriptExecutionRequestBuilder.setScriptRunStatus(ScriptRunStatus.valueOf(cachedRowSet.getString("script_exec_status")));
        }
    }

    private void mapScriptExecutionParameters(CachedRowSet cachedRowSet, ScriptExecutionRequestBuilder scriptExecutionRequestBuilder) throws SQLException {
        // script_exe_req_par_id, script_exe_req_par_name, script_exe_req_par_val
        String scriptExecutionRequestParameterId = cachedRowSet.getString("script_exe_req_par_id");
        if (scriptExecutionRequestParameterId == null) {
            return;
        }
        ScriptExecutionRequestParameterDto scriptExecutionRequestParameterDto = scriptExecutionRequestBuilder.getParameters().get(scriptExecutionRequestParameterId);

        if (scriptExecutionRequestParameterDto == null) {
            scriptExecutionRequestParameterDto = new ScriptExecutionRequestParameterDto(
                    cachedRowSet.getString("script_exe_req_par_name"),
                    cachedRowSet.getString("script_exe_req_par_val")
            );
            scriptExecutionRequestBuilder.getParameters().put(scriptExecutionRequestParameterId, scriptExecutionRequestParameterDto);
        }
    }

    private void mapImpersonation(CachedRowSet cachedRowSet, ScriptExecutionRequestBuilder scriptExecutionRequestBuilder) throws SQLException {
        // script_exe_req_id, script_exe_req_exit, script_exe_req_env,
        // script_exe_req_st, script_exe_req_file, script_exe_req_file_name, script_exe_req_name,
        // script_exe_req_name_name, script_exe_req_name_vrs, script_exe_req_imp_id, script_exe_req_imp_id_id


        String scriptExecutionRequestImpersonationId = cachedRowSet.getString("script_exe_req_imp_id");
        if (scriptExecutionRequestImpersonationId == null) {
            return;
        }
        ScriptExecutionRequestImpersonationDto scriptExecutionRequestImpersonationDto = scriptExecutionRequestBuilder.getImpersonations().get(scriptExecutionRequestImpersonationId);
        if (scriptExecutionRequestImpersonationDto == null) {
            scriptExecutionRequestImpersonationDto = new ScriptExecutionRequestImpersonationDto(cachedRowSet.getString("script_exe_req_imp_id_id"));
            scriptExecutionRequestBuilder.getImpersonations().put(scriptExecutionRequestImpersonationId, scriptExecutionRequestImpersonationDto);
        }
    }

    private void mapExecutionRequestLabel(CachedRowSet cachedRowSet, ExecutionRequestBuilder executionRequestBuilder) throws SQLException {
        // exe_req_label_id, exe_req_label_name, exe_req_label_value

        String executionRequestLabelId = cachedRowSet.getString("exe_req_label_id");
        if (executionRequestLabelId == null) {
            return;
        }
        ExecutionRequestLabelDto executionRequestLabelDto = executionRequestBuilder.getExecutionRequestLabels().get(executionRequestLabelId);

        if (executionRequestLabelDto == null) {
            executionRequestLabelDto = new ExecutionRequestLabelDto(
                    cachedRowSet.getString("exe_req_label_name"),
                    cachedRowSet.getString("exe_req_label_value")
            );
            executionRequestBuilder.getExecutionRequestLabels().put(executionRequestLabelId, executionRequestLabelDto);
        }
    }

    private ExecutionRequestBuilder mapExecutionRequestBuilderRow(CachedRowSet cachedRowSet) throws SQLException {
        //             "execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms,
        //             execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc,
        //             execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope,
        //             execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
        return new ExecutionRequestBuilder(
                cachedRowSet.getString("exe_req_id"),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("exe_req_tms")),
                cachedRowSet.getString("exe_req_name"),
                cachedRowSet.getString("exe_req_desc"),
                cachedRowSet.getString("exe_req_scope"),
                cachedRowSet.getString("exe_req_context"),
                cachedRowSet.getString("exe_req_email"),
                ExecutionRequestStatus.valueOf(cachedRowSet.getString("exec_req_status")),
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
                    scriptExecutionRequests.values().stream()
                            .map(ScriptExecutionRequestBuilder::build)
                            .collect(Collectors.toSet()),
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
        @Setter
        private ScriptRunStatus scriptRunStatus;

        public ScriptExecutionRequestDto build() {
            return new ScriptExecutionRequestDto(
                    scriptExecutionRequestId,
                    executionRequestId,
                    environment,
                    exit,
                    new HashSet<>(impersonations.values()),
                    new HashSet<>(parameters.values()),
                    scriptExecutionRequestStatus,
                    scriptName, scriptVersion,
                    runId,
                    scriptRunStatus
            );
        }

    }
}