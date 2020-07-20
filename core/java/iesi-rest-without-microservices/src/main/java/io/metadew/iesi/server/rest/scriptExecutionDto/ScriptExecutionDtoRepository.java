package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.tools.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;

@Log4j2
@Repository
public class ScriptExecutionDtoRepository implements IScriptExecutionDtoRepository {

    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    RepositoryCoordinator repositoryCoordinator;

    @Autowired
    ScriptExecutionDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @Override
    public Optional<ScriptExecutionDto> getByRunIdAndProcessId(String runId, Long processId) {
        try {
            Map<ScriptResultKey, ScriptExecutionDtoBuildHelper> scriptExecutionDtoBuildHelpers = new HashMap<>();
            String SQLQuery = getSQLQuery(runId, processId);
//            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getResultMetadataRepository()
//                    .executeQuery(SQLQuery, "reader");
//            This method is the one that is executed in the end
            CachedRowSet cachedRowSet = repositoryCoordinator.executeQuery(SQLQuery, "reader");

            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptExecutionDtoBuildHelpers);
            }

            if (scriptExecutionDtoBuildHelpers.size() > 1)
                log.warn("found multiple scriptExecution for runId " + runId + " and processId" + processId);
            return scriptExecutionDtoBuildHelpers.values().stream()
                    .map(ScriptExecutionDtoBuildHelper::toScriptExecutionDto).findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * mapRow treats and inserts result of mapping in the provided Map
     *
     * @param cachedRowSet                   - cachedRowSet containing the result of the SQLquery
     * @param scriptExecutionDtoBuildHelpers - Map designed to contain the POJOs made out of the SQLQuery
     * @throws SQLException - Throws SQLException due to the param cachedRowSet
     */
    private void mapRow(CachedRowSet cachedRowSet, Map<ScriptResultKey, ScriptExecutionDtoBuildHelper> scriptExecutionDtoBuildHelpers) throws SQLException {

        // name of columns already verified : ok
        String runId = cachedRowSet.getString("RUN_ID");
        Long scriptPrcId = cachedRowSet.getLong("SCRIPT_PRC_ID");

        ScriptResultKey scriptResultKey = new ScriptResultKey(runId, scriptPrcId);

        // IESI_RES_SCRIPT || IESI_TRC_DES_SCRIPT && IESI_TRC_DES_SCRIPT_VRS
        ScriptExecutionDtoBuildHelper scriptExecutionDtoBuildHelper = scriptExecutionDtoBuildHelpers.get(scriptResultKey);
        if (scriptExecutionDtoBuildHelper == null) {
            scriptExecutionDtoBuildHelper = mapScriptExecutionDtoBuildHelper(cachedRowSet);
            scriptExecutionDtoBuildHelpers.put(scriptResultKey, scriptExecutionDtoBuildHelper);
        }

        // int that gives information about the current row data
        int infoType = cachedRowSet.getInt("INFO_TYPE");

        if (infoType == 0) {
            // Inputparams of the script
            String inputParameterName = cachedRowSet.getString("SCRIPT_PAR_NM");
            if (scriptExecutionDtoBuildHelper.getInputParameters().get(inputParameterName) == null) {
                scriptExecutionDtoBuildHelper.getInputParameters()
                        .put(inputParameterName, new InputParametersDto(inputParameterName,
                                // Todo: rawValue -> value given on execution ? given from infotype 3 (EXE_REQ)?
                                "",
                                cachedRowSet.getString("SCRIPT_PAR_VAL"))
                        );
            }
        } else if (infoType == 1) {
            // DesignLabels
            String designLabelId = cachedRowSet.getString("SCRIPT_LBL_ID");
            if (scriptExecutionDtoBuildHelper.getDesignLabels().get(designLabelId) == null) {
                scriptExecutionDtoBuildHelper.getDesignLabels()
                        .put(designLabelId, new ScriptLabelDto(cachedRowSet.getString("SCRIPT_LBL_NM"),
                                cachedRowSet.getString("SCRIPT_LBL_VAL")));
            }
        }else if(infoType == 2){
            // TODO: to continue
        }


        // Execution Labels
        String designLabelId = cachedRowSet.getString("SCRIPT_LBL_ID");
        String executionLabelId = cachedRowSet.getString("");
        if (executionLabelId != null && scriptExecutionDtoBuildHelper.getExecutionLabels().get(executionLabelId) == null) {
            scriptExecutionDtoBuildHelper.getExecutionLabels()
                    .put(designLabelId, new ExecutionRequestLabelDto(cachedRowSet.getString(""),
                            cachedRowSet.getString("")));
        }

        // Outputs of the script
        String outputName = cachedRowSet.getString("OUT_NM");
        if (outputName != null && scriptExecutionDtoBuildHelper.getOutput().get(outputName) == null) {
            scriptExecutionDtoBuildHelper.getOutput()
                    .put(outputName, new OutputDto(outputName, cachedRowSet.getString("OUT_VAL")));
        }

        // Actions - PRK RunID + PrcID + ActionID -> RunID for "this" script will always be the same
        String actionId = cachedRowSet.getString("ACTION_ID");
        if (actionId != null) {
            mapRowScriptAction(scriptExecutionDtoBuildHelper, actionId, cachedRowSet);
        }


    }

    /**
     * mapRowScriptAction treats the current row and take care of everything regarding Actions
     *
     * @param scriptExecutionDtoBuildHelper - Object that contains several Map, one of them contain the actions
     * @param actionId                      - actionId was obtained from the cachedRowSet, it is passed to avoid getting it of the cachedRowSet again
     * @param cachedRowSet                  - cachedRowSet obtained from the SQLQuery
     * @throws SQLException - Throws SQLException due to the param cachedRowSet
     */
    private void mapRowScriptAction(ScriptExecutionDtoBuildHelper scriptExecutionDtoBuildHelper, String actionId, CachedRowSet cachedRowSet) throws SQLException {
        // create ActionKey
        Long actionPrcId = cachedRowSet.getLong("PRC_ID");
        ActionExecutionKey actionExecutionKey = new ActionExecutionKey(actionPrcId, actionId);
        ActionExecutionDtoBuildHelper actionExecutionDtoBuildHelper = scriptExecutionDtoBuildHelper.getActions().get(actionExecutionKey);
        if (actionExecutionDtoBuildHelper == null) {
            actionExecutionDtoBuildHelper = mapActionExecutionDtoBuildHelper(cachedRowSet);
            scriptExecutionDtoBuildHelper.getActions().put(actionExecutionKey, actionExecutionDtoBuildHelper);
        }

        String actionParameterName = cachedRowSet.getString("ACTION_PAR_NM");
        if (actionParameterName != null && actionExecutionDtoBuildHelper.getInputParameters().get(actionParameterName) == null) {
            actionExecutionDtoBuildHelper.getInputParameters()
                    .put(actionParameterName, new InputParametersDto(actionParameterName,
                            // Todo: rawValue -> name given on execution ?
                            // is it the param in EXE_REQ_PAR
                            "#" + actionParameterName,
                            cachedRowSet.getString("ACTION_PAR_VAL"))
                    );
        }
        String actionOutput = cachedRowSet.getString("ACTION_OUTPUT_NM");
        if (actionOutput != null && actionExecutionDtoBuildHelper.getInputParameters().get(actionOutput) == null) {
            actionExecutionDtoBuildHelper.getOutput()
                    .put(actionOutput, new OutputDto(actionOutput,
                            cachedRowSet.getString("ACTION_OUTPUT_VAL"))
                    );
        }

    }


    // Name of column already verified
    private ScriptExecutionDtoBuildHelper mapScriptExecutionDtoBuildHelper(CachedRowSet cachedRowSet) throws SQLException {
        return ScriptExecutionDtoBuildHelper.builder()
                .runId(cachedRowSet.getString("RUN_ID"))
                .processId(cachedRowSet.getLong("SCRIPT_PRC_ID"))
                .scriptId(cachedRowSet.getString("SCRIPT_ID"))
                .scriptName(cachedRowSet.getString("SCRIPT_NM"))
                .scriptVersion(cachedRowSet.getLong("SCRIPT_VRS_NB"))
                .environment(cachedRowSet.getString("ENV_NM"))
                .status(ScriptRunStatus.valueOf(cachedRowSet.getString("SCRIPT_ST_NM")))
                .startTimestamp(SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("SCRIPT_STRT_TMS")))
                .endTimestamp(SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("SCRIPT_END_TMS")))
                .build();
    }

    private ActionExecutionDtoBuildHelper mapActionExecutionDtoBuildHelper(CachedRowSet cachedRowSet) throws SQLException {
        return ActionExecutionDtoBuildHelper.builder()
                .runId(cachedRowSet.getString("ACTION_RUN_ID"))
                .processId(cachedRowSet.getLong("ACTION_PRC_ID"))
                .type(cachedRowSet.getString("ACTION_TYP_NM"))
                .name(cachedRowSet.getString("ACTION_NM"))
                .description(cachedRowSet.getString("ACTION_DES"))
                .condition(cachedRowSet.getString("ACTION_COND"))
                .errorStop(cachedRowSet.getBoolean(""))
                .errorExpected(cachedRowSet.getBoolean(""))
                .status(ScriptRunStatus.valueOf(cachedRowSet.getString("")))
                .startTimestamp(SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("")))
                .endTimestamp(SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("")))
                .build();
    }

    /**
     * getSQLQuery compute the SQL Statement with or without filter depending of the given parameters
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
                "trc_des_script_par.SCRIPT_PAR_NM SCRIPT_PAR_NM, trc_des_script_par.SCRIPT_PAR_VAL SCRIPT_PAR_VAL, " +
                "null SCRIPT_LBL_ID, " +
                "null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, " +
                "null SCRIPT_EXE_PAR_NM, null SCRIPT_EXE_PAR_VAL, null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, " +
                "null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, " +
                "null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, null ACTION_ST_NM, " +
                "null ACTION_STRT_TMS, null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                "null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResult") + " results " +
                "LEFT OUTER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameterDesignTraces") +
                " trc_des_script_par on results.SCRIPT_ID = trc_des_script_par.SCRIPT_ID " +
                getWhereClause(runId, processId).orElse("") +
                "UNION ALL " +
                "SELECT 1 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, " +
                "results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, " +
                "results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, " +
                "results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, null SCRIPT_PAR_VAL, " +
                "trc_des_script_lbl.SCRIPT_LBL_ID SCRIPT_LBL_ID " +
                "trc_des_script_lbl.NAME SCRIPT_LBL_NM, trc_des_script_lbl.VALUE SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, " +
                "null SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_PAR_NM, null SCRIPT_EXE_PAR_VAL, null SCRIPT_EXE_LBL_NM, " +
                "null SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, " +
                "null ACTION_DSC, null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, " +
                "null ACTION_ST_NM, null ACTION_STRT_TMS, null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                "null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResult") + " results " +
                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabelDesignTraces") + " trc_des_script_lbl " +
                "on results.RUN_ID = trc_des_script_lbl.RUN_ID AND results.PRC_ID = trc_des_script_lbl.PRC_ID " +
                getWhereClause(runId, processId).orElse("") +
                "UNION ALL " +
                "SELECT 2 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, " +
                "results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, " +
                "results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, " +
                "results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, null SCRIPT_PAR_VAL, " +
                "null SCRIPT_LBL_ID, " +
                "null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, script_output.OUT_NM SCRIPT_OUTPUT_NM, " +
                "script_output.OUT_VAL SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_PAR_NM, null SCRIPT_EXE_PAR_VAL, " +
                "null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, " +
                "null ACTION_NM, null ACTION_DSC, null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, " +
                "null ACTION_ST_NM, null ACTION_STRT_TMS, null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                "null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResult") + " results " +
                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResultOutputs") + " script_output " +
                "on results.RUN_ID = script_output.RUN_ID AND results.PRC_ID = script_output.PRC_ID " +
                getWhereClause(runId, processId).orElse("") +
                "UNION ALL " +
                "SELECT 3 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, " +
                "results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, " +
                "results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, " +
                "null SCRIPT_PAR_VAL, null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, " +
                "script_exec_par.NAME SCRIPT_EXE_PAR_NM, script_exec_par.VALUE SCRIPT_EXE_PAR_VAL, null SCRIPT_EXE_LBL_NM, " +
                "null SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, " +
                "null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, null ACTION_ST_NM, null ACTION_STRT_TMS, " +
                "null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResult") + " results " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions") + " script_exec " +
                "on results.RUN_ID = script_exec.RUN_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters") + " script_exec_par " +
                "on script_exec.SCRPT_REQUEST_ID = script_exec_par.SCRIPT_EXEC_REQ_ID " +
                getWhereClause(runId, processId).orElse("") +
                "UNION ALL " +
                "SELECT 4 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, " +
                "results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, " +
                "results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, " +
                "null SCRIPT_PAR_VAL, null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, " +
                "null SCRIPT_EXE_PAR_NM, null SCRIPT_EXE_PAR_VAL, script_exec_lbl.NAME SCRIPT_EXE_LBL_NM, script_exec_lbl.VALUE SCRIPT_EXE_LBL_VAL, " +
                "null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, null ACTION_CONDITION_VAL, " +
                "null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, null ACTION_ST_NM, null ACTION_STRT_TMS, null ACTION_END_TMS, " +
                "null ACTION_PAR_NM, null ACTION_PAR_VAL, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResult") + " results " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions") + " script_exec " +
                "on results.RUN_ID = script_exec.RUN_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests") + " IESER " +
                "on script_exec.SCRPT_REQUEST_ID = IESER.SCRPT_REQUEST_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests") + " IER " +
                "on IESER.ID = IER.REQUEST_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels") + " script_exec_lbl " +
                "on IER.REQUEST_ID = script_exec_lbl.REQUEST_ID " +
                getWhereClause(runId, processId).orElse("") +
                "UNION ALL " +
                "SELECT 5 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, " +
                "results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, " +
                "results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, " +
                "null SCRIPT_PAR_VAL, null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, " +
                "null SCRIPT_EXE_PAR_NM, null SCRIPT_EXE_PAR_VAL, null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, " +
                "action_trc.PRC_ID ACTION_PRC_ID, action_trc.ACTION_ID ACTION_ID, action_trc.ACTION_TYP_NM ACTION_TYP_NM, " +
                "action_trc.ACTION_NM ACTION_NM, action_trc.ACTION_DSC ACTION_DSC, action_trc.CONDITION_VAL ACTION_CONDITION_VAL, " +
                "action_trc.STOP_ERR_FL ACTION_STOP_ERR_FL, action_trc.EXP_ERR_FL ACTION_EXP_ERR_FL, action_res.ST_NM ACTION_ST_NM, " +
                "action_res.STRT_TMS ACTION_STRT_TMS, action_res.END_TMS ACTION_END_TMS, action_trc_par.ACTION_PAR_NM ACTION_PAR_NM, " +
                "action_trc_par.ACTION_PAR_VAL ACTION_PAR_VAL, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResult") + " results " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionDesignTraces") + " action_trc " +
                "on results.RUN_ID = action_trc.RUN_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResults") + " action_res " +
                "on results.RUN_ID = action_res.RUN_ID " +
                "LEFT OUTER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameterDesignTraces") + " action_trc_par " +
                "on action_trc.RUN_ID = action_trc_par.RUN_ID AND action_trc.PRC_ID = action_trc_par.PRC_ID " +
                getWhereClause(runId, processId).orElse("") +
                "UNION ALL " +
                "SELECT 6 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, " +
                "results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, " +
                "results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, " +
                "null SCRIPT_PAR_VAL, null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, " +
                "null SCRIPT_EXE_PAR_NM, null SCRIPT_EXE_PAR_VAL, null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, " +
                "action_trc.PRC_ID ACTION_PRC_ID, action_trc.ACTION_ID ACTION_ID, action_trc.ACTION_TYP_NM ACTION_TYP_NM, " +
                "action_trc.ACTION_NM ACTION_NM, action_trc.ACTION_DSC ACTION_DSC, action_trc.CONDITION_VAL ACTION_CONDITION_VAL, " +
                "action_trc.STOP_ERR_FL ACTION_STOP_ERR_FL, action_trc.EXP_ERR_FL ACTION_EXP_ERR_FL, action_res.ST_NM ACTION_ST_NM, " +
                "action_res.STRT_TMS ACTION_STRT_TMS, action_res.END_TMS ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                "action_res_output.OUT_NM ACTION_OUTPUT_NM, action_res_output.OUT_VAL ACTION_OUTPUT_VAL " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResult") + " results " +
                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionDesignTraces") + " action_trc " +
                "on results.RUN_ID = action_trc.RUN_ID " +
                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResults") + " action_res " +
                "on results.RUN_ID = action_res.RUN_ID " +
                "INNER JOIN " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResultOutputs") + " action_res_output " +
                "on action_res.RUN_ID = action_res_output.RUN_ID AND action_res.PRC_ID = action_res_output.PRC_ID " +
                getWhereClause(runId, processId).orElse("") +
                ";";
    }

    private String getSQLQueryPrevious(String runId, Long processId) {
        return "select * from " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptResults").getName() + " results " +
                "inner join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptDesignTraces").getName() + " script_traces " +
                "on results.RUN_ID = script_traces.RUN_ID and results.PRC_ID = script_traces.PRC_ID " +
                "inner join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptVersionDesignTraces").getName() + " script_version_traces " +
                "on results.RUN_ID = script_version_traces.RUN_ID and results.PRC_ID = script_version_traces.PRC_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptParameterDesignTraces").getName() + " script_params_traces " +
                "on results.RUN_ID = script_params_traces.RUN_ID and " +
                "results.PRC_ID = script_params_traces.PRC_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptLabelDesignTraces").getName() + " script_labels_traces " +
                "on results.RUN_ID = script_labels_traces.RUN_ID and " +
                "results.PRC_ID = script_labels_traces.PRC_ID " +
                "inner join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ActionResults").getName() + " action_results " +
                "on results.RUN_ID = action_results.RUN_ID and " +
                "results.prc_id = action_results.SCRIPT_PRC_ID " +
                "inner join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ActionDesignTraces").getName() + " action_design_traces " +
                "on results.RUN_ID = action_design_traces.RUN_ID and " +
                "action_results.prc_id = action_design_traces.PRC_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ActionParameterDesignTraces").getName() + " action_design_param_traces " +
                "on results.RUN_ID = action_design_param_traces.RUN_ID and " +
                "action_design_traces.PRC_ID = action_design_param_traces.PRC_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ActionResultOutputs").getName() + " action_result_outputs " +
                "on action_result_outputs.RUN_ID = results.RUN_ID and " +
                "action_result_outputs.PRC_ID = action_results.PRC_ID " +
                getWhereClause(runId, processId).orElse("")
                + ";";
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

    // Todo : delete after development
    private String getTable(String label) {
        return MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel(label).getName();
    }
}
