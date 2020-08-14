package io.metadew.iesi.server.rest.executionrequest;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Log4j2
public class ExecutionRequestDtoRepository implements IExecutionRequestDtoRepository {

    private static final String fetchAllQuery =
            // fetch all execution requests and possible script execution requests
            "select  execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "4 INFO_TYPE, " +
                    "null exe_req_label_id, null exe_req_label_name, null exe_req_label_value, " +
                    "script_execution_requests.SCRPT_REQUEST_ID as script_exe_req_id, script_execution_requests.EXIT as script_exe_req_exit, script_execution_requests.ENVIRONMENT as script_exe_req_env, script_execution_requests.ST_NM script_exe_req_st, " +
                    "file_script_execution_requests.ID as script_exe_req_file, file_script_execution_requests.SCRPT_FILENAME as script_exe_req_file_name, " +
                    "name_script_execution_requests.ID as script_exe_req_name, name_script_execution_requests.SCRPT_NAME as script_exe_req_name_name, name_script_execution_requests.SCRPT_VRS as script_exe_req_name_vrs, " +
                    "null script_exe_req_imp_id, null script_exe_req_imp_id_id, " +
                    "null script_exe_req_par_id, null script_exe_req_par_name, null script_exe_req_par_value, " +
                    "null script_exec_id, null script_exec_run_id, null script_exec_strt_tms, null script_exec_end_tms, null script_exec_end_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                    "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                    "union all " +
                    // fetch all script execution request impersonations
                    "select execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "0 INFO_TYPE, " +
                    "execution_request_labels.ID as exe_req_label_id, execution_request_labels.NAME as exe_req_label_name, execution_request_labels.VALUE as exe_req_label_value, " +
                    "null script_exe_req_id, null script_exe_req_exit,null script_exe_req_env, null script_exe_req_st, " +
                    "null script_exe_req_file, null script_exe_req_file_name, " +
                    "null script_exe_req_name, null script_exe_req_name_name, null script_exe_req_name_vrs, " +
                    "null script_exe_req_imp_id, null script_exe_req_imp_id_id, " +
                    "null script_exe_req_par_id, null script_exe_req_par_name, null script_exe_req_par_value, " +
                    "null script_exec_id, null script_exec_run_id, null script_exec_strt_tms, null script_exec_end_tms, null script_exec_end_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " execution_request_labels " +
                    "on execution_requests.REQUEST_ID = execution_request_labels.REQUEST_ID " +
                    "union all " +
                    "select  " +
                    "execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "1 INFO_TYPE, " +
                    "null exe_req_label_id, null exe_req_label_name, null exe_req_label_value, " +
                    "script_execution_requests.SCRPT_REQUEST_ID as script_exe_req_id, script_execution_requests.EXIT as script_exe_req_exit, script_execution_requests.ENVIRONMENT as script_exe_req_env, script_execution_requests.ST_NM script_exe_req_st, " +
                    "file_script_execution_requests.ID as script_exe_req_file, file_script_execution_requests.SCRPT_FILENAME as script_exe_req_file_name, " +
                    "name_script_execution_requests.ID as script_exe_req_name, name_script_execution_requests.SCRPT_NAME as script_exe_req_name_name, name_script_execution_requests.SCRPT_VRS as script_exe_req_name_vrs, " +
                    "script_execution_request_imps.ID as script_exe_req_imp_id, script_execution_request_imps.IMP_ID as script_exe_req_imp_id_id, " +
                    "null script_exe_req_par_id, null script_exe_req_par_name, null script_exe_req_par_value, " +
                    "null script_exec_id, null script_exec_run_id, null script_exec_strt_tms, null script_exec_end_tms, null script_exec_end_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                    "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() + " script_execution_request_imps " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = script_execution_request_imps.SCRIPT_EXEC_REQ_ID " +
                    "union all " +
                    // fetch all script execution request parameters
                    "select  execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "2 INFO_TYPE, " +
                    "null exe_req_label_id, null exe_req_label_name, null exe_req_label_value, " +
                    "script_execution_requests.SCRPT_REQUEST_ID as script_exe_req_id, script_execution_requests.EXIT as script_exe_req_exit, script_execution_requests.ENVIRONMENT as script_exe_req_env, script_execution_requests.ST_NM script_exe_req_st, " +
                    "file_script_execution_requests.ID as script_exe_req_file, file_script_execution_requests.SCRPT_FILENAME as script_exe_req_file_name, " +
                    "name_script_execution_requests.ID as script_exe_req_name, name_script_execution_requests.SCRPT_NAME as script_exe_req_name_name, name_script_execution_requests.SCRPT_VRS as script_exe_req_name_vrs, " +
                    "null script_exe_req_imp_id, null script_exe_req_imp_id_id, " +
                    "script_execution_request_pars.ID as script_exe_req_par_id, script_execution_request_pars.NAME as script_exe_req_par_name, script_execution_request_pars.VALUE as script_exe_req_par_val, " +
                    "null script_exec_id, null script_exec_run_id, null script_exec_strt_tms, null script_exec_end_tms, null script_exec_end_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                    "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " script_execution_request_pars " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = script_execution_request_pars.SCRIPT_EXEC_REQ_ID " +
                    "union all " +
                    // fetch all script execution request script executions
                    "select execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "3 INFO_TYPE, " +
                    "null exe_req_label_id, null exe_req_label_name, null exe_req_label_value, " +
                    "script_execution_requests.SCRPT_REQUEST_ID as script_exe_req_id, script_execution_requests.EXIT as script_exe_req_exit, script_execution_requests.ENVIRONMENT as script_exe_req_env, script_execution_requests.ST_NM as script_exe_req_st, " +
                    "file_script_execution_requests.ID as script_exe_req_file, file_script_execution_requests.SCRPT_FILENAME as script_exe_req_file_name, " +
                    "name_script_execution_requests.ID as script_exe_req_name, name_script_execution_requests.SCRPT_NAME as script_exe_req_name_name, name_script_execution_requests.SCRPT_VRS as script_exe_req_name_vrs, " +
                    "null script_exe_req_imp_id, null script_exe_req_imp_id_id, " +
                    "null script_exe_req_par_id, null script_exe_req_par_name, null script_exe_req_par_value, " +
                    "script_executions.ID as script_exec_id, script_executions.RUN_ID as script_exec_run_id, script_executions.STRT_TMS as script_exec_strt_tms, script_executions.END_TMS as script_exec_end_tms, script_executions.ST_NM as script_exec_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                    "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() + " script_executions " +
                    "on script_executions.SCRPT_REQUEST_ID = script_execution_requests.SCRPT_REQUEST_ID; ";

    private static final String fetchSingleQuery =
            "select  execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "4 INFO_TYPE, " +
                    "null exe_req_label_id, null exe_req_label_name, null exe_req_label_value, " +
                    "script_execution_requests.SCRPT_REQUEST_ID as script_exe_req_id, script_execution_requests.EXIT as script_exe_req_exit, script_execution_requests.ENVIRONMENT as script_exe_req_env, script_execution_requests.ST_NM script_exe_req_st, " +
                    "file_script_execution_requests.ID as script_exe_req_file, file_script_execution_requests.SCRPT_FILENAME as script_exe_req_file_name, " +
                    "name_script_execution_requests.ID as script_exe_req_name, name_script_execution_requests.SCRPT_NAME as script_exe_req_name_name, name_script_execution_requests.SCRPT_VRS as script_exe_req_name_vrs, " +
                    "null script_exe_req_imp_id, null script_exe_req_imp_id_id, " +
                    "null script_exe_req_par_id, null script_exe_req_par_name, null script_exe_req_par_value, " +
                    "null script_exec_id, null script_exec_run_id, null script_exec_strt_tms, null script_exec_end_tms, null script_exec_end_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                    "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                    "where execution_requests.REQUEST_ID = {0}" +
                    "union all " +
                    // fetch all script execution request impersonations
                    "select execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "0 INFO_TYPE, " +
                    "execution_request_labels.ID as exe_req_label_id, execution_request_labels.NAME as exe_req_label_name, execution_request_labels.VALUE as exe_req_label_value, " +
                    "null script_exe_req_id, null script_exe_req_exit,null script_exe_req_env, null script_exe_req_st, " +
                    "null script_exe_req_file, null script_exe_req_file_name, " +
                    "null script_exe_req_name, null script_exe_req_name_name, null script_exe_req_name_vrs, " +
                    "null script_exe_req_imp_id, null script_exe_req_imp_id_id, " +
                    "null script_exe_req_par_id, null script_exe_req_par_name, null script_exe_req_par_value, " +
                    "null script_exec_id, null script_exec_run_id, null script_exec_strt_tms, null script_exec_end_tms, null script_exec_end_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " execution_request_labels " +
                    "on execution_requests.REQUEST_ID = execution_request_labels.REQUEST_ID " +
                    "where execution_requests.REQUEST_ID = {0} " +
                    "union all " +
                    "select  " +
                    "execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "1 INFO_TYPE, " +
                    "null exe_req_label_id, null exe_req_label_name, null exe_req_label_value, " +
                    "script_execution_requests.SCRPT_REQUEST_ID as script_exe_req_id, script_execution_requests.EXIT as script_exe_req_exit, script_execution_requests.ENVIRONMENT as script_exe_req_env, script_execution_requests.ST_NM script_exe_req_st, " +
                    "file_script_execution_requests.ID as script_exe_req_file, file_script_execution_requests.SCRPT_FILENAME as script_exe_req_file_name, " +
                    "name_script_execution_requests.ID as script_exe_req_name, name_script_execution_requests.SCRPT_NAME as script_exe_req_name_name, name_script_execution_requests.SCRPT_VRS as script_exe_req_name_vrs, " +
                    "script_execution_request_imps.ID as script_exe_req_imp_id, script_execution_request_imps.IMP_ID as script_exe_req_imp_id_id, " +
                    "null script_exe_req_par_id, null script_exe_req_par_name, null script_exe_req_par_value, " +
                    "null script_exec_id, null script_exec_run_id, null script_exec_strt_tms, null script_exec_end_tms, null script_exec_end_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                    "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() + " script_execution_request_imps " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = script_execution_request_imps.SCRIPT_EXEC_REQ_ID " +
                    "where execution_requests.REQUEST_ID = {0} " +
                    "union all " +
                    // fetch all script execution request parameters
                    "select  execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "2 INFO_TYPE, " +
                    "null exe_req_label_id, null exe_req_label_name, null exe_req_label_value, " +
                    "script_execution_requests.SCRPT_REQUEST_ID as script_exe_req_id, script_execution_requests.EXIT as script_exe_req_exit, script_execution_requests.ENVIRONMENT as script_exe_req_env, script_execution_requests.ST_NM script_exe_req_st, " +
                    "file_script_execution_requests.ID as script_exe_req_file, file_script_execution_requests.SCRPT_FILENAME as script_exe_req_file_name, " +
                    "name_script_execution_requests.ID as script_exe_req_name, name_script_execution_requests.SCRPT_NAME as script_exe_req_name_name, name_script_execution_requests.SCRPT_VRS as script_exe_req_name_vrs, " +
                    "null script_exe_req_imp_id, null script_exe_req_imp_id_id, " +
                    "script_execution_request_pars.ID as script_exe_req_par_id, script_execution_request_pars.NAME as script_exe_req_par_name, script_execution_request_pars.VALUE as script_exe_req_par_val, " +
                    "null script_exec_id, null script_exec_run_id, null script_exec_strt_tms, null script_exec_end_tms, null script_exec_end_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                    "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " script_execution_request_pars " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = script_execution_request_pars.SCRIPT_EXEC_REQ_ID " +
                    "where execution_requests.REQUEST_ID = {0} " +
                    "union all " +
                    // fetch all script execution request script executions
                    "select execution_requests.REQUEST_ID as exe_req_id, execution_requests.REQUEST_TMS as exe_req_tms, execution_requests.REQUEST_NM as exe_req_name, execution_requests.REQUEST_DSC as exe_req_desc, execution_requests.NOTIF_EMAIL as exe_req_email, execution_requests.SCOPE_NM as exe_req_scope, execution_requests.CONTEXT_NM as exe_req_context, execution_requests.ST_NM as exec_req_status, " +
                    "auth_execution_requests.REQUEST_ID as exe_req_auth, auth_execution_requests.SPACE_NM as exe_req_auth_space, auth_execution_requests.USER_NM as exe_req_auth_user, auth_execution_requests.USER_PASSWORD as exe_req_auth_pass,  " +
                    "non_auth_execution_requests.REQUEST_ID as exe_req_non_auth, " +
                    "3 INFO_TYPE, " +
                    "null exe_req_label_id, null exe_req_label_name, null exe_req_label_value, " +
                    "script_execution_requests.SCRPT_REQUEST_ID as script_exe_req_id, script_execution_requests.EXIT as script_exe_req_exit, script_execution_requests.ENVIRONMENT as script_exe_req_env, script_execution_requests.ST_NM as script_exe_req_st, " +
                    "file_script_execution_requests.ID as script_exe_req_file, file_script_execution_requests.SCRPT_FILENAME as script_exe_req_file_name, " +
                    "name_script_execution_requests.ID as script_exe_req_name, name_script_execution_requests.SCRPT_NAME as script_exe_req_name_name, name_script_execution_requests.SCRPT_VRS as script_exe_req_name_vrs, " +
                    "null script_exe_req_imp_id, null script_exe_req_imp_id_id, " +
                    "null script_exe_req_par_id, null script_exe_req_par_name, null script_exe_req_par_value, " +
                    "script_executions.ID as script_exec_id, script_executions.RUN_ID as script_exec_run_id, script_executions.STRT_TMS as script_exec_strt_tms, script_executions.END_TMS as script_exec_end_tms, script_executions.ST_NM as script_exec_status " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " execution_requests " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("NonAuthenticatedExecutionRequests").getName() + " non_auth_execution_requests " +
                    "on execution_requests.REQUEST_ID = non_auth_execution_requests.REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " script_execution_requests " +
                    "on execution_requests.REQUEST_ID = script_execution_requests.ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptFileExecutionRequests").getName() + " file_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = file_script_execution_requests.SCRPT_REQUEST_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptNameExecutionRequests").getName() + " name_script_execution_requests " +
                    "on script_execution_requests.SCRPT_REQUEST_ID = name_script_execution_requests.SCRPT_REQUEST_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() + " script_executions " +
                    "on script_executions.SCRPT_REQUEST_ID = script_execution_requests.SCRPT_REQUEST_ID " +
                    "where execution_requests.REQUEST_ID = {0};";

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    ExecutionRequestDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @Override
    public List<ExecutionRequestDto> getAll() {
        try {
            Map<String, ExecutionRequestBuilder> executionRequestBuilderMap = new HashMap<>();
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(fetchAllQuery, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, executionRequestBuilderMap);
            }
            return executionRequestBuilderMap.values().stream().map(ExecutionRequestBuilder::build).collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExecutionRequestDto> getById(UUID uuid) {
        try {
            Map<String, ExecutionRequestBuilder> executionRequestBuilderMap = new HashMap<>();
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository()
                    .executeQuery(MessageFormat.format(fetchSingleQuery, SQLTools.GetStringForSQL(uuid.toString())), "reader");
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

        int infoType = cachedRowSet.getInt("INFO_TYPE");

        if (infoType == 0) {
            // labels
            mapExecutionRequestLabel(cachedRowSet, executionRequestBuilder);
        } else if (infoType == 1) {
            // script execution request impersonations
            mapImpersonation(cachedRowSet, executionRequestBuilder);
        } else if (infoType == 2) {
            // script execution request parameters
            mapScriptExecutionParameters(cachedRowSet, executionRequestBuilder);
        } else if (infoType == 3) {
            // script execution request run id
            mapScriptExecutionRunId(cachedRowSet, executionRequestBuilder);
        } else if (infoType == 4) {
            // link script execution request
            extractScriptExecutionRequestBuilder(cachedRowSet, executionRequestBuilder);
        } else {
            log.warn("unknown info type found for execution request : " + infoType);
        }
    }

    private ScriptExecutionRequestBuilder extractScriptExecutionRequestBuilder(CachedRowSet cachedRowSet, ExecutionRequestBuilder executionRequestBuilder) throws SQLException {
        String scriptExecutionRequestId = cachedRowSet.getString("script_exe_req_id");
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
                    null
            );
            executionRequestBuilder.getScriptExecutionRequests().put(scriptExecutionRequestId, scriptExecutionRequestBuilder);
        }
        return scriptExecutionRequestBuilder;
    }

    private void mapScriptExecutionRunId(CachedRowSet cachedRowSet, ExecutionRequestBuilder executionRequestBuilder) throws SQLException {
        ScriptExecutionRequestBuilder scriptExecutionRequestBuilder = extractScriptExecutionRequestBuilder(cachedRowSet, executionRequestBuilder);
        // script_exec_id, script_exec_run_id, script_exec_strt_tms, script_exec_end_tms, script_exec_status

        if (scriptExecutionRequestBuilder.getRunId() == null) {
            String runId = cachedRowSet.getString("script_exec_run_id");
            scriptExecutionRequestBuilder.setRunId(runId);
        } else {
            log.warn("duplicate runId found for execution request " + executionRequestBuilder.getExecutionRequestId());
        }
    }

    private void mapScriptExecutionParameters(CachedRowSet cachedRowSet, ExecutionRequestBuilder executionRequestBuilder) throws SQLException {
        ScriptExecutionRequestBuilder scriptExecutionRequestBuilder = extractScriptExecutionRequestBuilder(cachedRowSet, executionRequestBuilder);
        // script_exe_req_par_id, script_exe_req_par_name, script_exe_req_par_value
        String scriptExecutionRequestParameterId = cachedRowSet.getString("script_exe_req_par_id");
        ScriptExecutionRequestParameterDto scriptExecutionRequestParameterDto = scriptExecutionRequestBuilder.getParameters().get(scriptExecutionRequestParameterId);

        if (scriptExecutionRequestParameterDto == null) {
            scriptExecutionRequestParameterDto = new ScriptExecutionRequestParameterDto(
                    cachedRowSet.getString("script_exe_req_par_name"),
                    cachedRowSet.getString("script_exe_req_par_value")
            );
            scriptExecutionRequestBuilder.getParameters().put(scriptExecutionRequestParameterId, scriptExecutionRequestParameterDto);
        } else {
            log.warn("duplicate parameter " + scriptExecutionRequestParameterId + " found for execution request " + executionRequestBuilder.getExecutionRequestId());
        }
    }

    private void mapImpersonation(CachedRowSet cachedRowSet, ExecutionRequestBuilder executionRequestBuilder) throws SQLException {
        // script_exe_req_id, script_exe_req_exit, script_exe_req_env,
        // script_exe_req_st, script_exe_req_file, script_exe_req_file_name, script_exe_req_name,
        // script_exe_req_name_name, script_exe_req_name_vrs, script_exe_req_imp_id, script_exe_req_imp_id_id

        ScriptExecutionRequestBuilder scriptExecutionRequestBuilder = extractScriptExecutionRequestBuilder(cachedRowSet, executionRequestBuilder);

        String scriptExecutionRequestImpersonationId = cachedRowSet.getString("script_exe_req_imp_id");
        ScriptExecutionRequestImpersonationDto scriptExecutionRequestImpersonationDto = scriptExecutionRequestBuilder.getImpersonations().get(scriptExecutionRequestImpersonationId);

        if (scriptExecutionRequestImpersonationDto == null) {
            scriptExecutionRequestImpersonationDto = new ScriptExecutionRequestImpersonationDto(cachedRowSet.getString("script_exe_req_imp_id_id"));
            scriptExecutionRequestBuilder.getImpersonations().put(scriptExecutionRequestImpersonationId, scriptExecutionRequestImpersonationDto);
        } else {
            log.warn("duplicate impersonation " + scriptExecutionRequestImpersonationId + " found for execution request " + executionRequestBuilder.getExecutionRequestId());
        }
    }

    private void mapExecutionRequestLabel(CachedRowSet cachedRowSet, ExecutionRequestBuilder executionRequestBuilder) throws SQLException {
        // exe_req_label_id, exe_req_label_name, exe_req_label_value

        String executionRequestLabelId = cachedRowSet.getString("exe_req_label_id");
        ExecutionRequestLabelDto executionRequestLabelDto = executionRequestBuilder.getExecutionRequestLabels().get(executionRequestLabelId);

        if (executionRequestLabelDto == null) {
            executionRequestLabelDto = new ExecutionRequestLabelDto(
                    cachedRowSet.getString("exe_req_label_name"),
                    cachedRowSet.getString("exe_req_label_value")
            );
            executionRequestBuilder.getExecutionRequestLabels().put(executionRequestLabelId, executionRequestLabelDto);
        } else {
            log.warn("duplicate label " + executionRequestLabelId + " found for execution request " + executionRequestBuilder.getExecutionRequestId());
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
        private Map<String, ExecutionRequestLabelDto> executionRequestLabels;

        public ExecutionRequestDto build() {
            return new ExecutionRequestDto(executionRequestId, requestTimestamp, name, description, scope, context, email, executionRequestStatus,
                    scriptExecutionRequests.values().stream().map(ScriptExecutionRequestBuilder::build).collect(Collectors.toList()),
                    new ArrayList<>(executionRequestLabels.values()));
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
        private Map<String, ScriptExecutionRequestParameterDto> parameters;
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
