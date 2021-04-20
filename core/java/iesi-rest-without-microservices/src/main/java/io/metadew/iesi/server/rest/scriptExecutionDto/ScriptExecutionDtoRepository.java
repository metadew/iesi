package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.service.user.IESIPrivilege;
import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Repository
public class ScriptExecutionDtoRepository implements IScriptExecutionDtoRepository {

    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    ScriptExecutionDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @Override
    public List<ScriptExecutionDto> getAll(Authentication authentication) {
        return getExecutionDtoList(
                authentication,
                null,
                null);
    }

    @Override
    public List<ScriptExecutionDto> getByRunId(Authentication authentication, String runId) {
        return getExecutionDtoList(
                authentication,
                runId,
                null);
    }

    @Override
    public Optional<ScriptExecutionDto> getByRunIdAndProcessId(Authentication authentication, String runId, Long processId) {
        List<ScriptExecutionDto> scriptExecutionDtoStream = getExecutionDtoList(
                authentication,
                runId,
                processId);
        if (scriptExecutionDtoStream.size() > 1)
            log.warn("found multiple scriptExecution for runId " + runId + " and processId" + processId);
        return scriptExecutionDtoStream.stream().findFirst();
    }

    /**
     * This method take care of running the appropriate query depending of the provided parameter
     * and call subMethod to create the ScriptExecutionDto as POJOs
     *
     * @param runId     - runId of the ScriptExecution, can be null
     * @param processId - processId of the ScriptExecution, can be null
     * @return Stream of ScriptExecutionDto that can contain zero, one or several ScriptExecutionDto
     */
    private List<ScriptExecutionDto> getExecutionDtoList(Authentication authentication, String runId, Long processId) {
        try {
            Map<ScriptResultKey, ScriptExecutionDtoBuildHelper> scriptExecutionDtoBuildHelpers = new HashMap<>();
            String sqlQuery = getSQLQuery(authentication, runId, processId);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getResultMetadataRepository()
                    .executeQuery(sqlQuery, "reader");

            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptExecutionDtoBuildHelpers);
            }

            return scriptExecutionDtoBuildHelpers.values().stream()
                    .map(ScriptExecutionDtoBuildHelper::toScriptExecutionDto)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * mapRow treats the result of the query and populate the provided Map
     *
     * @param cachedRowSet                   - cachedRowSet containing the result of the SQLquery
     * @param scriptExecutionDtoBuildHelpers - Map designed to contain the POJOs made out of the SQLQuery
     * @throws SQLException - Throws SQLException due to the param cachedRowSet
     */
    private void mapRow(CachedRowSet cachedRowSet, Map<ScriptResultKey, ScriptExecutionDtoBuildHelper> scriptExecutionDtoBuildHelpers) throws SQLException {

        String runId = cachedRowSet.getString("RUN_ID");
        Long scriptPrcId = cachedRowSet.getLong("SCRIPT_PRC_ID");

        ScriptResultKey scriptResultKey = new ScriptResultKey(runId, scriptPrcId);

        ScriptExecutionDtoBuildHelper scriptExecutionDtoBuildHelper = scriptExecutionDtoBuildHelpers.get(scriptResultKey);
        if (scriptExecutionDtoBuildHelper == null) {
            scriptExecutionDtoBuildHelper = mapScriptExecutionDtoBuildHelper(cachedRowSet);
            scriptExecutionDtoBuildHelpers.put(scriptResultKey, scriptExecutionDtoBuildHelper);
        }

        // infoType is an int that gives information about the current row data
        // ExecutionInputparams of the script
        String scriptExecInputParameterName = cachedRowSet.getString("SCRIPT_EXEC_REQ_PAR_NAME");
        // infotype 0 could not contain parameter name as it also initialize the script
        if (scriptExecInputParameterName != null &&
                scriptExecutionDtoBuildHelper.getInputParameters().get(scriptExecInputParameterName) == null) {
            scriptExecutionDtoBuildHelper.getInputParameters()
                    .put(scriptExecInputParameterName, new ExecutionInputParameterDto(scriptExecInputParameterName,
                            cachedRowSet.getString("SCRIPT_EXEC_REQ_PAR_VALUE")));
        }
        // Infotype 1: rows are present only if containing DesignLabels
        String designLabelId = cachedRowSet.getString("SCRIPT_LBL_ID");
        if (designLabelId != null && scriptExecutionDtoBuildHelper.getDesignLabels().get(designLabelId) == null) {
            scriptExecutionDtoBuildHelper.getDesignLabels()
                    .put(designLabelId, new ScriptLabelDto(cachedRowSet.getString("SCRIPT_LBL_NM"),
                            cachedRowSet.getString("SCRIPT_LBL_VAL")));
        }
        // Infotype 2: rows are present only if containing Outputs of the script
        String outputName = cachedRowSet.getString("SCRIPT_OUTPUT_NM");
        if (outputName != null && scriptExecutionDtoBuildHelper.getOutput().get(outputName) == null) {
            scriptExecutionDtoBuildHelper.getOutput()
                    .put(outputName, new OutputDto(outputName, cachedRowSet.getString("SCRIPT_OUTPUT_VAL")));
        }
        // Infotype 3: rows are present only if containing Execution Labels
        String executionLabelName = cachedRowSet.getString("SCRIPT_EXE_LBL_NM");
        if (executionLabelName != null && scriptExecutionDtoBuildHelper.getExecutionLabels().get(executionLabelName) == null) {
            scriptExecutionDtoBuildHelper.getExecutionLabels()
                    .put(executionLabelName, new ExecutionRequestLabelDto(executionLabelName,
                            cachedRowSet.getString("SCRIPT_EXE_LBL_VAL")));
        }
        // else infotype 4 and 5 -> Action
        // Actions - PRK RunID + PrcID + ActionID : RunID of action is the same than the RunID of the script
        String actionId = cachedRowSet.getString("ACTION_ID");
        Long actionPrcId = cachedRowSet.getLong("ACTION_PRC_ID");
        if (actionId != null) {
            ActionExecutionKey actionExecutionKey = new ActionExecutionKey(actionId, actionPrcId);
            ActionExecutionDtoBuildHelper actionExecutionDtoBuildHelper = scriptExecutionDtoBuildHelper.getActions().get(actionExecutionKey);

            if (actionExecutionDtoBuildHelper == null) {
                actionExecutionDtoBuildHelper = mapActionExecutionDtoBuildHelper(cachedRowSet);
                scriptExecutionDtoBuildHelper.getActions().put(actionExecutionKey, actionExecutionDtoBuildHelper);
            }

            // Infotype 4: always present if the script contains action and could contain action parameter
            // script + script action + action parameters
            String actionParameterName = cachedRowSet.getString("ACTION_PAR_NM");
            if (actionParameterName != null && actionExecutionDtoBuildHelper.getInputParameters().get(actionParameterName) == null) {
                actionExecutionDtoBuildHelper.getInputParameters()
                        .put(actionParameterName, new ActionInputParametersDto(actionParameterName,
                                cachedRowSet.getString("ACTION_PAR_VAL_RAW"),
                                cachedRowSet.getString("ACTION_PAR_VAL_RESOLVED")));
            }
            // Infotype 5: could not be present if the action doesn't contain any action
            // script + script action + action output
            String actionOutput = cachedRowSet.getString("ACTION_OUTPUT_NM");
            if (actionOutput != null && actionExecutionDtoBuildHelper.getOutput().get(actionOutput) == null) {
                actionExecutionDtoBuildHelper.getOutput()
                        .put(actionOutput, new OutputDto(actionOutput,
                                cachedRowSet.getString("ACTION_OUTPUT_VAL")));
            }
        }

    }

    /**
     * This methods create and return an ScriptExecutionDtoBuildHelper:
     * inputParameters, designLabels, executionLabels, actions and output are created empty
     *
     * @param cachedRowSet - item containing the fields required to create the object
     * @return ScriptExecutionDtoBuildHelper - Object similar to ScriptExecutionDto but containing Hashmap instead of List
     * @throws SQLException - Throws SQLException due to the param cachedRowSet
     */
    private ScriptExecutionDtoBuildHelper mapScriptExecutionDtoBuildHelper(CachedRowSet cachedRowSet) throws SQLException {
        return new ScriptExecutionDtoBuildHelper(cachedRowSet.getString("RUN_ID"),
                cachedRowSet.getLong("SCRIPT_PRC_ID"),
                cachedRowSet.getLong("SCRIPT_PARENT_PRC_ID"),
                cachedRowSet.getString("SCRIPT_ID"),
                cachedRowSet.getString("SCRIPT_NM"),
                cachedRowSet.getLong("SCRIPT_VRS_NB"),
                cachedRowSet.getString("SCRIPT_SECURITY_GROUP_NAME"),
                cachedRowSet.getString("ENV_NM"),
                cachedRowSet.getString("USER_ID"),
                cachedRowSet.getString("USERNAME"),
                ScriptRunStatus.valueOf(cachedRowSet.getString("SCRIPT_ST_NM")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("SCRIPT_STRT_TMS")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("SCRIPT_END_TMS")),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>());
    }

    /**
     * This methods create and return an ActionExecutionDtoBuildHelper: only inputParameters and output aren't completed
     *
     * @param cachedRowSet - item containing the fields required to create the object
     * @return mapActionExecutionDtoBuildHelper: object similar to ActionExecutionDto but containing map instead of list
     * @throws SQLException - Throws SQLException due to the param cachedRowSet
     */
    private ActionExecutionDtoBuildHelper mapActionExecutionDtoBuildHelper(CachedRowSet cachedRowSet) throws SQLException {
        return new ActionExecutionDtoBuildHelper(cachedRowSet.getString("RUN_ID"),
                cachedRowSet.getLong("ACTION_PRC_ID"),
                cachedRowSet.getString("ACTION_TYP_NM"),
                cachedRowSet.getString("ACTION_NM"),
                cachedRowSet.getString("ACTION_DSC"),
                cachedRowSet.getString("ACTION_CONDITION_VAL"),
                cachedRowSet.getString("ACTION_STOP_ERR_FL").equalsIgnoreCase("y") ||
                        cachedRowSet.getString("ACTION_STOP_ERR_FL").equalsIgnoreCase("yes"),
                cachedRowSet.getString("ACTION_EXP_ERR_FL").equalsIgnoreCase("y") ||
                        cachedRowSet.getString("ACTION_EXP_ERR_FL").equalsIgnoreCase("yes"),
                ScriptRunStatus.valueOf(cachedRowSet.getString("ACTION_ST_NM")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("ACTION_STRT_TMS")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("ACTION_END_TMS")),
                new HashMap<>(),
                new HashMap<>());
    }


    /**
     * This method computes the SQL Statement with or without filter depending of the given parameters.
     * The method uses Union all query to make an efficient query on the DataBase
     *
     * @param runId     - runId of the Script
     * @param processId - processId of the Script
     * @return Return a String containing the SQL statement
     */
    private String getSQLQuery(Authentication authentication, String runId, Long processId) {
        return "SELECT " +
                "results.RUN_ID RUN_ID, " +
                "results.PRC_ID SCRIPT_PRC_ID, " +
                "results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, " +
                "results.SCRIPT_ID SCRIPT_ID, " +
                "results.SCRIPT_NM SCRIPT_NM, " +
                "results.SCRIPT_VRS_NB SCRIPT_VRS_NB, " +
                "results.SECURITY_GROUP_NAME SCRIPT_SECURITY_GROUP_NAME, " +
                "results.ENV_NM ENV_NM, " +
                "results.ST_NM SCRIPT_ST_NM, " +
                "results.STRT_TMS SCRIPT_STRT_TMS, " +
                "results.END_TMS SCRIPT_END_TMS, " +
                "script_exec_req_par.NAME SCRIPT_EXEC_REQ_PAR_NAME, " +
                "script_exec_req_par.VALUE SCRIPT_EXEC_REQ_PAR_VALUE, " +
                "trc_des_script_lbl.SCRIPT_LBL_ID SCRIPT_LBL_ID, " +
                "trc_des_script_lbl.NAME SCRIPT_LBL_NM, " +
                "trc_des_script_lbl.VALUE SCRIPT_LBL_VAL, " +
                "script_output.OUT_NM SCRIPT_OUTPUT_NM, " +
                "script_output.OUT_VAL SCRIPT_OUTPUT_VAL, " +
                "script_exec_lbl.NAME SCRIPT_EXE_LBL_NM, " +
                "script_exec_lbl.VALUE SCRIPT_EXE_LBL_VAL, " +
                "action_trc.PRC_ID ACTION_PRC_ID, " +
                "action_trc.ACTION_ID ACTION_ID, " +
                "action_trc.ACTION_TYP_NM ACTION_TYP_NM, " +
                "action_trc.ACTION_NM ACTION_NM, " +
                "action_trc.ACTION_DSC ACTION_DSC, " +
                "action_trc.CONDITION_VAL ACTION_CONDITION_VAL, " +
                "action_trc.STOP_ERR_FL ACTION_STOP_ERR_FL, " +
                "action_trc.EXP_ERR_FL ACTION_EXP_ERR_FL, " +
                "action_res.ST_NM ACTION_ST_NM, " +
                "action_res.STRT_TMS ACTION_STRT_TMS, " +
                "action_res.END_TMS ACTION_END_TMS, " +
                "action_des_trc_par.ACTION_PAR_NM ACTION_PAR_NM, " +
                "action_des_trc_par.ACTION_PAR_VAL ACTION_PAR_VAL_RAW, " +
                "action_trc_par.ACTION_PAR_VAL ACTION_PAR_VAL_RESOLVED, " +
                "action_res_output.OUT_NM ACTION_OUTPUT_NM, " +
                "action_res_output.OUT_VAL ACTION_OUTPUT_VAL, " +
                "auth_execution_requests.USER_ID exe_req_auth_user_id, " +
                "auth_execution_requests.USERNAME exe_req_auth_username " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " results " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() + " script_exec " +
                "on results.RUN_ID = script_exec.RUN_ID AND results.PRC_ID = -1 " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " script_exec_req_par " +
                "on script_exec_req_par.SCRIPT_EXEC_REQ_ID = script_exec.SCRPT_REQUEST_ID " +
                "LEFT OUTER  JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabelDesignTraces").getName() + " trc_des_script_lbl " +
                "on results.RUN_ID = trc_des_script_lbl.RUN_ID AND results.PRC_ID = trc_des_script_lbl.PRC_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResultOutputs").getName() + " script_output " +
                "on results.RUN_ID = script_output.RUN_ID AND results.PRC_ID = script_output.PRC_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequests").getName() + " IESER " +
                "on script_exec.SCRPT_REQUEST_ID = IESER.SCRPT_REQUEST_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName() + " IER " +
                "on IESER.ID = IER.REQUEST_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " auth_execution_requests " +
                "on base_execution_requests.REQUEST_ID = auth_execution_requests.REQUEST_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " script_exec_lbl " +
                "on IER.REQUEST_ID = script_exec_lbl.REQUEST_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResults").getName() + " action_res " +
                "on results.RUN_ID = action_res.RUN_ID AND results.PRC_ID = action_res.SCRIPT_PRC_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionDesignTraces").getName() + " action_trc " +
                "on results.RUN_ID = action_trc.RUN_ID AND action_trc.PRC_ID = action_res.PRC_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameterDesignTraces").getName() + " action_des_trc_par " +
                "on action_trc.RUN_ID = action_des_trc_par.RUN_ID AND action_trc.PRC_ID = action_des_trc_par.PRC_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameterTraces").getName() + " action_trc_par " +
                "on action_trc.RUN_ID = action_trc_par.RUN_ID AND action_trc.PRC_ID = action_trc_par.PRC_ID " +
                "AND action_trc_par.ACTION_PAR_NM = action_des_trc_par.ACTION_PAR_NM " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResultOutputs").getName() + " action_res_output " +
                "on action_res.RUN_ID = action_res_output.RUN_ID AND action_res.PRC_ID = action_res_output.PRC_ID " +
                getWhereClause(authentication, runId, processId).orElse("") + ";";

    }

    /**
     * getWhereClause return a String containing or not the Where SQL statement depending if the parameters are null or not
     *
     * @param runId     - If null, doesn't provide a where clause regarding runID
     * @param processId - If null, doesn't provide a where clause regarding processID
     * @return - Return an Optional of String containing the where clause or nothing if null parameters were passed
     */
    private Optional<String> getWhereClause(Authentication authentication, String runId, Long processId) {
        List<String> conditions = new ArrayList<>();
        if (runId != null) conditions.add(" results.RUN_ID = " + SQLTools.getStringForSQL(runId));
        if (processId != null) conditions.add(" results.prc_id = " + SQLTools.getStringForSQL(processId));
        if (conditions.isEmpty()) return Optional.empty();        // Only filter on authentication if explicitly mentioned
        if (authentication != null) {
            Set<String> securityGroups = authentication.getAuthorities().stream()
                    .filter(authority -> authority instanceof IESIGrantedAuthority)
                    .map(authority -> (IESIGrantedAuthority) authority)
                    .filter(grantedAuthority -> grantedAuthority.getPrivilegeName().equals(IESIPrivilege.EXECUTION_REQUESTS_READ.getPrivilege()))
                    .map(IESIGrantedAuthority::getSecurityGroupName)
                    .map(SQLTools::getStringForSQL).collect(Collectors.toSet());
            conditions.add(" results.SECURITY_GROUP_NAME IN (" + String.join(", ", securityGroups) + ") ");
        }
        return Optional.of(" where " + String.join(" and ", conditions) + " ");
    }

    /**
     * This class is an helper to build ScriptExecutionDto.
     * ScriptExecutionDto has to use list but should not contain duplicate.
     * The same applies for ActionExecutionDto, a referenced object.
     * Thus this class helps by using map and by providing a simple method to convert itself into an ActionExecutionDto
     */
    @Getter
    @AllArgsConstructor
    private class ScriptExecutionDtoBuildHelper {

        private final String runId;

        private final Long processId;
        private final Long parentProcessId;

        private final String scriptId;
        private final String scriptName;
        private final Long scriptVersion;
        private final String securityGroupName;
        private final String environment;
        private final String userId;
        private final String username;
        private final ScriptRunStatus status;
        private final LocalDateTime startTimestamp;
        private final LocalDateTime endTimestamp;
        private final Map<String, ExecutionInputParameterDto> inputParameters;
        private final Map<String, ScriptLabelDto> designLabels;
        private final Map<String, ExecutionRequestLabelDto> executionLabels;
        private final Map<ActionExecutionKey, ActionExecutionDtoBuildHelper> actions;
        private final Map<String, OutputDto> output;

        public ScriptExecutionDto toScriptExecutionDto() {
            return new ScriptExecutionDto(runId,
                    processId,
                    parentProcessId,
                    scriptId,
                    scriptName,
                    scriptVersion,
                    securityGroupName,
                    environment,
                    userId,
                    username,
                    status,
                    startTimestamp,
                    endTimestamp,
                    new ArrayList<>(inputParameters.values()),
                    new ArrayList<>(designLabels.values()),
                    new ArrayList<>(executionLabels.values()),
                    actions.values().stream()
                            .map(ActionExecutionDtoBuildHelper::toActionExecutionDto)
                            .collect(Collectors.toList()),
                    new ArrayList<>(output.values()));
        }

    }

    /**
     * This class is an helper to build ActionExecutionDto.
     * ActionExecutionDto has to use list but should not contain duplicate.
     * Thus this class helps by using map and by providing a simple method to convert itself into an ActionExecutionDto
     */
    @Getter
    @AllArgsConstructor
    private class ActionExecutionDtoBuildHelper {

        private final String runId;
        private final Long processId;
        private final String type;
        private final String name;
        private final String description;
        private final String condition;
        private final boolean errorStop;
        private final boolean errorExpected;
        private final ScriptRunStatus status;
        private final LocalDateTime startTimestamp;
        private final LocalDateTime endTimestamp;
        private final Map<String, ActionInputParametersDto> inputParameters;
        private final Map<String, OutputDto> output;

        public ActionExecutionDto toActionExecutionDto() {
            return new ActionExecutionDto(runId,
                    processId,
                    type,
                    name,
                    description,
                    condition,
                    errorStop,
                    errorExpected,
                    status,
                    startTimestamp,
                    endTimestamp,
                    new ArrayList<>(inputParameters.values()),
                    new ArrayList<>(output.values()));
        }
    }

}
