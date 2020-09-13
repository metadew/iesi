package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.filter.ExecutionRequestFilter;
import io.metadew.iesi.server.rest.executionrequest.filter.ExecutionRequestFilterOption;
import io.metadew.iesi.server.rest.executionrequest.filter.IdExecutionRequestFilter;
import io.metadew.iesi.server.rest.executionrequest.filter.IdsExecutionRequestFilter;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
                getOrderByClause(pageable.getSort()) +
                ";";
    }

    private String getFetchAllQuery(Sort sort, List<ExecutionRequestFilter> executionRequestFilters) {
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
                " (" + getBaseQuery(executionRequestFilters) + ") base_execution_requests " +
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
                getOrderByClause(sort) +
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
                getOrderByClause(pageable.getSort()) +
                getLimitAndOffsetClause(pageable);
    }

    private String getBaseQuery(List<ExecutionRequestFilter> executionRequestFilters) {
        return getBaseQuery(Pageable.unpaged(), executionRequestFilters);
    }


    private String getWhereClause(List<ExecutionRequestFilter> executionRequestFilters) {
        String filterStatements = executionRequestFilters.stream().map(executionRequestFilter -> {
                    if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.NAME)) {
                        return " name_script_execution_requests.SCRPT_NAME " + (executionRequestFilter.isExactMatch() ? "=" : "LIKE") + (executionRequestFilter.isExactMatch() ? "" : "%") + ":script_name" + (executionRequestFilter.isExactMatch() ? "" : "%") + " ";
                    } else if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.VERSION)) {
                        return " name_script_execution_requests.SCRPT_VRS = :script_version ";
                    } else if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.ENVIRONMENT)) {
                        return " script_execution_requests.ENVIRONMENT " + (executionRequestFilter.isExactMatch() ? "=" : "LIKE") + " " + (executionRequestFilter.isExactMatch() ? "" : "%") + ":environment" + (executionRequestFilter.isExactMatch() ? "" : "%") + " ";
                    } else if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.ID)) {
                        return " execution_requests.REQUEST_ID " + (executionRequestFilter.isExactMatch() ? "=" : "LIKE") + " " + (executionRequestFilter.isExactMatch() ? "" : "%") + ":request_id" + (executionRequestFilter.isExactMatch() ? "" : "%") + " ";
                    } else if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.IDS)) {
                        return " execution_requests.REQUEST_IDS in (:request_ids) ";
                    } else if (executionRequestFilter.getExecutionRequestFilterOption().equals(ExecutionRequestFilterOption.LABEL)) {
                        return " execution_request_labels.NAME = " + (executionRequestFilter.isExactMatch() ? "=" : "LIKE") + " " + (executionRequestFilter.isExactMatch() ? "" : "%") + ":label_key" + (executionRequestFilter.isExactMatch() ? "" : "%") +
                                " and execution_request_labels.VALUE " + (executionRequestFilter.isExactMatch() ? "=" : "LIKE") + " " + (executionRequestFilter.isExactMatch() ? "" : "%") + ":label_name" + (executionRequestFilter.isExactMatch() ? "" : "%") + " ";
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

    private String getOrderByClause(Sort sort) {
        if (sort.isUnsorted()) return " ";
        List<String> sorting = sort.stream().map(order -> {
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


    private long getRowSize(List<ExecutionRequestFilter> executionRequestFilters) {
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

        return executionJdbcTemplate.queryForObject(query, getSqlParameters(executionRequestFilters), Long.class);
    }

    private final NamedParameterJdbcTemplate executionJdbcTemplate;


    @Autowired
    ExecutionRequestDtoRepository(@Qualifier("executionJdbcTemplate") NamedParameterJdbcTemplate executionJdbcTemplate) {
        this.executionJdbcTemplate = executionJdbcTemplate;
    }

    @Override
    public Page<ExecutionRequestDto> getAll(Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters) {
        String query = getFetchAllQuery(pageable, executionRequestFilters);
        List<ExecutionRequestDto> executionRequestDtos = executionJdbcTemplate.query(query, getSqlParameters(executionRequestFilters), new ExecutionRequestDtoListResultSetExtractor());
        return new PageImpl<>(executionRequestDtos, pageable, getRowSize(executionRequestFilters));
    }

    @Override
    public Optional<ExecutionRequestDto> getById(UUID uuid) {
        List<ExecutionRequestFilter> executionRequestFilters = Stream.of(new IdExecutionRequestFilter(uuid.toString(), true)).collect(Collectors.toList());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(
                        executionJdbcTemplate.query(
                                getFetchAllQuery(Pageable.unpaged(), executionRequestFilters),
                                getSqlParameters(executionRequestFilters),
                                new ExecutionRequestDtoListResultSetExtractor())));
    }

    private MapSqlParameterSource getSqlParameters(List<ExecutionRequestFilter> executionRequestFilters) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        executionRequestFilters.forEach(executionRequestFilter -> executionRequestFilter.addParameter(parameters));
        log.info(parameters.toString());
        return parameters;
    }

    @Override
    public Iterable<ExecutionRequestDto> findAll(Sort sort) {
        String query = getFetchAllQuery(sort, new ArrayList<>());
        return executionJdbcTemplate.query(query, getSqlParameters(new ArrayList<>()), new ExecutionRequestDtoListResultSetExtractor());
    }

    @Override
    public Page<ExecutionRequestDto> findAll(Pageable pageable) {
        return getAll(pageable, new ArrayList<>());
    }

    @Override
    public <S extends ExecutionRequestDto> S save(S entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends ExecutionRequestDto> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ExecutionRequestDto> findById(UUID uuid) {
        return getById(uuid);
    }

    @Override
    public boolean existsById(UUID uuid) {
        return findById(uuid).isPresent();
    }

    @Override
    public Iterable<ExecutionRequestDto> findAll() {
        return getAll(Pageable.unpaged(), new ArrayList<>());
    }

    @Override
    public Iterable<ExecutionRequestDto> findAllById(Iterable<UUID> uuids) {
        return getAll(
                Pageable.unpaged(),
                Stream.of(new IdsExecutionRequestFilter(Stream.of(uuids).map(Object::toString).collect(Collectors.joining(",")), true)).collect(Collectors.toList()));
    }

    @Override
    public long count() {
        return getRowSize(new ArrayList<>());
    }

    @Override
    public void deleteById(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ExecutionRequestDto entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends ExecutionRequestDto> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }
}