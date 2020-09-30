package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Repository
public class ScriptExecutionDtoRepository implements IScriptExecutionDtoRepository {

    private final NamedParameterJdbcTemplate executionJdbcTemplate;

    @Autowired
    ScriptExecutionDtoRepository(@Qualifier("executionJdbcTemplate") NamedParameterJdbcTemplate executionJdbcTemplate) {
        this.executionJdbcTemplate = executionJdbcTemplate;
    }

    @Override
    public List<ScriptExecutionDto> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ScriptExecutionDto> getByRunId(String runId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ScriptExecutionDto> getByRunIdAndProcessId(String runId, Long processId) {
        return Optional.ofNullable(
                DataAccessUtils.singleResult(
                        executionJdbcTemplate.query(
                                getSQLQuery(runId, processId),
                                new ScriptExecutionDtoListResultSetExtractor())));
    }

    /**
     * This method computes the SQL Statement with or without filter depending of the given parameters.
     * The method uses Union all query to make an efficient query on the DataBase
     *
     * @param runId     - runId of the Script
     * @param processId - processId of the Script
     * @return Return a String containing the SQL statement
     */
    private String getSQLQuery(String runId, Long processId) {
        return "SELECT 0 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, " +
                "results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, " +
                "results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, " +
                "results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, " +
                "script_exec_req_par.NAME SCRIPT_EXEC_REQ_PAR_NAME, script_exec_req_par.VALUE SCRIPT_EXEC_REQ_PAR_VALUE, " +
                "null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, " +
                "null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_LBL_NM, " +
                "null SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, " +
                "null ACTION_DSC, null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, " +
                "null ACTION_ST_NM, null ACTION_STRT_TMS, null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL_RAW, " +
                "null ACTION_PAR_VAL_RESOLVED, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +

                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " results " +

                "LEFT OUTER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() + " script_exec " +
                "on results.RUN_ID = script_exec.RUN_ID AND results.PRC_ID = -1 " +
                "LEFT OUTER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " script_exec_req_par " +
                "on script_exec_req_par.SCRIPT_EXEC_REQ_ID = script_exec.SCRPT_REQUEST_ID " +

                getWhereClause(runId, processId).orElse("") +
                "UNION ALL " +
                "SELECT 1 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, " +
                "results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, " +
                "results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, " +
                "results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, " +
                "null SCRIPT_EXEC_REQ_PAR_NAME, null SCRIPT_EXEC_REQ_PAR_VALUE, " +
                "trc_des_script_lbl.SCRIPT_LBL_ID SCRIPT_LBL_ID, " +
                "trc_des_script_lbl.NAME SCRIPT_LBL_NM, trc_des_script_lbl.VALUE SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, " +
                "null SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, " +
                "null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, null ACTION_CONDITION_VAL, " +
                "null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, null ACTION_ST_NM, null ACTION_STRT_TMS, " +
                "null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL_RAW, null ACTION_PAR_VAL_RESOLVED, " +
                "null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +

                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " results " +

                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabelDesignTraces").getName() + " trc_des_script_lbl " +
                "on results.RUN_ID = trc_des_script_lbl.RUN_ID AND results.PRC_ID = trc_des_script_lbl.PRC_ID " +
                getWhereClause(runId, processId).orElse("") +

                "UNION ALL " +
                "SELECT 2 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, " +
                "results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, " +
                "results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, " +
                "results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, " +
                "null SCRIPT_EXEC_REQ_PAR_NAME, null SCRIPT_EXEC_REQ_PAR_VALUE, " +
                "null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, " +
                "null SCRIPT_LBL_VAL, script_output.OUT_NM SCRIPT_OUTPUT_NM, script_output.OUT_VAL SCRIPT_OUTPUT_VAL, " +
                "null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, null ACTION_ID, " +
                "null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, " +
                "null ACTION_EXP_ERR_FL, null ACTION_ST_NM, null ACTION_STRT_TMS, null ACTION_END_TMS, null ACTION_PAR_NM, " +
                "null ACTION_PAR_VAL_RAW, null ACTION_PAR_VAL_RESOLVED, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +

                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " results " +

                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResultOutputs").getName() + " script_output " +
                "on results.RUN_ID = script_output.RUN_ID AND results.PRC_ID = script_output.PRC_ID " +
                getWhereClause(runId, processId).orElse("") +

                "UNION ALL " +
                "SELECT 3 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, " +
                "results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, " +
                "results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, " +
                "results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, " +
                "null SCRIPT_EXEC_REQ_PAR_NAME, null SCRIPT_EXEC_REQ_PAR_VALUE, " +
                "null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, " +
                "null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, " +
                "script_exec_lbl.NAME SCRIPT_EXE_LBL_NM, script_exec_lbl.VALUE SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, " +
                "null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, null ACTION_CONDITION_VAL, " +
                "null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, null ACTION_ST_NM, null ACTION_STRT_TMS, " +
                "null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL_RAW, null ACTION_PAR_VAL_RESOLVED, " +
                "null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +

                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " results " +

                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() + " script_exec " +
                "on results.RUN_ID = script_exec.RUN_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " IESER " +
                "on script_exec.SCRPT_REQUEST_ID = IESER.SCRPT_REQUEST_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " IER " +
                "on IESER.ID = IER.REQUEST_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " script_exec_lbl " +
                "on IER.REQUEST_ID = script_exec_lbl.REQUEST_ID " +
                getWhereClause(runId, processId).orElse("") +

                "UNION ALL " +
                "SELECT 4 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, " +
                "results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, " +
                "results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, " +
                "results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, " +
                "null SCRIPT_EXEC_REQ_PAR_NAME, null SCRIPT_EXEC_REQ_PAR_VALUE, " +
                "null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, " +
                "null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_LBL_NM, " +
                "null SCRIPT_EXE_LBL_VAL, action_trc.PRC_ID ACTION_PRC_ID, action_trc.ACTION_ID ACTION_ID, " +
                "action_trc.ACTION_TYP_NM ACTION_TYP_NM, action_trc.ACTION_NM ACTION_NM, " +
                "action_trc.ACTION_DSC ACTION_DSC, action_trc.CONDITION_VAL ACTION_CONDITION_VAL, " +
                "action_trc.STOP_ERR_FL ACTION_STOP_ERR_FL, action_trc.EXP_ERR_FL ACTION_EXP_ERR_FL, " +
                "action_res.ST_NM ACTION_ST_NM, action_res.STRT_TMS ACTION_STRT_TMS, action_res.END_TMS ACTION_END_TMS, " +
                "action_des_trc_par.ACTION_PAR_NM ACTION_PAR_NM, action_des_trc_par.ACTION_PAR_VAL ACTION_PAR_VAL_RAW, " +
                "action_trc_par.ACTION_PAR_VAL ACTION_PAR_VAL_RESOLVED, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +

                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " results " +

                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionDesignTraces").getName() + " action_trc " +
                "on results.RUN_ID = action_trc.RUN_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResults").getName() + " action_res " +
                "on results.RUN_ID = action_res.RUN_ID AND action_trc.PRC_ID = action_res.PRC_ID AND results.PRC_ID = action_res.SCRIPT_PRC_ID " +
                "LEFT OUTER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameterDesignTraces").getName() + " action_des_trc_par " +
                "on action_trc.RUN_ID = action_des_trc_par.RUN_ID AND action_trc.PRC_ID = action_des_trc_par.PRC_ID " +
                "LEFT OUTER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameterTraces").getName() + " action_trc_par " +
                "on action_trc.RUN_ID = action_trc_par.RUN_ID AND action_trc.PRC_ID = action_trc_par.PRC_ID " +
                "AND action_trc_par.ACTION_PAR_NM = action_des_trc_par.ACTION_PAR_NM " +
                getWhereClause(runId, processId).orElse("") +

                "UNION ALL " +
                "SELECT 5 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, " +
                "results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, " +
                "results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, " +
                "results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, " +
                "null SCRIPT_EXEC_REQ_PAR_NAME, null SCRIPT_EXEC_REQ_PAR_VALUE, " +
                "null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, " +
                "null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_LBL_NM, " +
                "null SCRIPT_EXE_LBL_VAL, action_trc.PRC_ID ACTION_PRC_ID, action_trc.ACTION_ID ACTION_ID, " +
                "action_trc.ACTION_TYP_NM ACTION_TYP_NM, action_trc.ACTION_NM ACTION_NM, action_trc.ACTION_DSC ACTION_DSC, " +
                "action_trc.CONDITION_VAL ACTION_CONDITION_VAL, action_trc.STOP_ERR_FL ACTION_STOP_ERR_FL, " +
                "action_trc.EXP_ERR_FL ACTION_EXP_ERR_FL, action_res.ST_NM ACTION_ST_NM, " +
                "action_res.STRT_TMS ACTION_STRT_TMS, action_res.END_TMS ACTION_END_TMS, null ACTION_PAR_NM, " +
                "null ACTION_PAR_VAL_RAW, null ACTION_PAR_VAL_RESOLVED, action_res_output.OUT_NM ACTION_OUTPUT_NM, " +
                "action_res_output.OUT_VAL ACTION_OUTPUT_VAL " +

                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " results " +

                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionDesignTraces").getName() + " action_trc " +
                "on results.RUN_ID = action_trc.RUN_ID " +
                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResults").getName() + " action_res " +
                "on results.RUN_ID = action_res.RUN_ID AND action_trc.PRC_ID = action_res.PRC_ID AND results.PRC_ID = action_res.SCRIPT_PRC_ID " +
                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResultOutputs").getName() + " action_res_output " +
                "on action_res.RUN_ID = action_res_output.RUN_ID AND action_res.PRC_ID = action_res_output.PRC_ID " +
                getWhereClause(runId, processId).orElse("") +
                ";";
    }

    /**
     * getWhereClause return a String containing or not the Where SQL statement depending if the parameters are null or not
     *
     * @param runId     - If null, doesn't provide a where clause regarding runID
     * @param processId - If null, doesn't provide a where clause regarding processID
     * @return - Return an Optional of String containing the where clause or nothing if null parameters were passed
     */
    private Optional<String> getWhereClause(String runId, Long processId) {
        List<String> conditions = new ArrayList<>();
        if (runId != null) conditions.add(" results.RUN_ID = " + SQLTools.GetStringForSQL(runId));
        if (processId != null) conditions.add(" results.prc_id = " + SQLTools.GetStringForSQL(processId));
        if (conditions.isEmpty()) return Optional.empty();
        return Optional.of(" where " + String.join(" and ", conditions) + " ");
    }
}
