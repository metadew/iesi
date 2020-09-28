package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptExecutionDtoListResultSetExtractor implements ResultSetExtractor<List<ScriptExecutionDto>> {

    private void mapRow(ResultSet resultSet, Map<ScriptResultKey, ScriptExecutionDtoBuildHelper> scriptExecutionDtoBuildHelpers) throws SQLException {

        String runId = resultSet.getString("RUN_ID");
        Long scriptPrcId = resultSet.getLong("SCRIPT_PRC_ID");

        ScriptResultKey scriptResultKey = new ScriptResultKey(runId, scriptPrcId);

        ScriptExecutionDtoBuildHelper scriptExecutionDtoBuildHelper = scriptExecutionDtoBuildHelpers.get(scriptResultKey);
        if (scriptExecutionDtoBuildHelper == null) {
            scriptExecutionDtoBuildHelper = mapScriptExecutionDtoBuildHelper(resultSet);
            scriptExecutionDtoBuildHelpers.put(scriptResultKey, scriptExecutionDtoBuildHelper);
        }

        // infoType is an int that gives information about the current row data
        int infoType = resultSet.getInt("INFO_TYPE");

        if (infoType == 0) {
            // ExecutionInputparams of the script
            String scriptExecInputParameterName = resultSet.getString("SCRIPT_EXEC_REQ_PAR_NAME");
            // infotype 0 could not contain parameter name as it also initialize the script
            if (scriptExecInputParameterName != null &&
                    scriptExecutionDtoBuildHelper.getInputParameters().get(scriptExecInputParameterName) == null) {
                scriptExecutionDtoBuildHelper.getInputParameters()
                        .put(scriptExecInputParameterName, new ExecutionInputParameterDto(scriptExecInputParameterName,
                                resultSet.getString("SCRIPT_EXEC_REQ_PAR_VALUE")));
            }
        } else if (infoType == 1) {
            // Infotype 1: rows are present only if containing DesignLabels
            String designLabelId = resultSet.getString("SCRIPT_LBL_ID");
            if (scriptExecutionDtoBuildHelper.getDesignLabels().get(designLabelId) == null) {
                scriptExecutionDtoBuildHelper.getDesignLabels()
                        .put(designLabelId, new ScriptLabelDto(resultSet.getString("SCRIPT_LBL_NM"),
                                resultSet.getString("SCRIPT_LBL_VAL")));
            }
        } else if (infoType == 2) {
            // Infotype 2: rows are present only if containing Outputs of the script
            String outputName = resultSet.getString("SCRIPT_OUTPUT_NM");
            if (scriptExecutionDtoBuildHelper.getOutput().get(outputName) == null) {
                scriptExecutionDtoBuildHelper.getOutput()
                        .put(outputName, new OutputDto(outputName, resultSet.getString("SCRIPT_OUTPUT_VAL")));
            }
        } else if (infoType == 3) {
            // Infotype 3: rows are present only if containing Execution Labels
            String executionLabelName = resultSet.getString("SCRIPT_EXE_LBL_NM");
            if (scriptExecutionDtoBuildHelper.getExecutionLabels().get(executionLabelName) == null) {
                scriptExecutionDtoBuildHelper.getExecutionLabels()
                        .put(executionLabelName, new ExecutionRequestLabelDto(executionLabelName,
                                resultSet.getString("SCRIPT_EXE_LBL_VAL")));
            }
        } else {
            // else infotype 4 and 5 -> Action
            // Actions - PRK RunID + PrcID + ActionID : RunID of action is the same than the RunID of the script
            String actionId = resultSet.getString("ACTION_ID");
            Long actionPrcId = resultSet.getLong("ACTION_PRC_ID");
            ActionExecutionKey actionExecutionKey = new ActionExecutionKey(actionId, actionPrcId);

            ActionExecutionDtoBuildHelper actionExecutionDtoBuildHelper = scriptExecutionDtoBuildHelper.getActions().get(actionExecutionKey);
            if (actionExecutionDtoBuildHelper == null) {
                actionExecutionDtoBuildHelper = mapActionExecutionDtoBuildHelper(resultSet);
                scriptExecutionDtoBuildHelper.getActions().put(actionExecutionKey, actionExecutionDtoBuildHelper);
            }

            if (infoType == 4) {
                // Infotype 4: always present if the script contains action and could contain action parameter
                // script + script action + action parameters
                String actionParameterName = resultSet.getString("ACTION_PAR_NM");
                if (actionParameterName != null && actionExecutionDtoBuildHelper.getInputParameters().get(actionParameterName) == null) {
                    actionExecutionDtoBuildHelper.getInputParameters()
                            .put(actionParameterName, new ActionInputParametersDto(actionParameterName,
                                    resultSet.getString("ACTION_PAR_VAL_RAW"),
                                    resultSet.getString("ACTION_PAR_VAL_RESOLVED")));
                }
            } else if (infoType == 5) {
                // Infotype 5: could not be present if the action doesn't contain any action
                // script + script action + action output
                String actionOutput = resultSet.getString("ACTION_OUTPUT_NM");
                if (actionExecutionDtoBuildHelper.getOutput().get(actionOutput) == null) {
                    actionExecutionDtoBuildHelper.getOutput()
                            .put(actionOutput, new OutputDto(actionOutput,
                                    resultSet.getString("ACTION_OUTPUT_VAL")));
                }
            }
        }
    }

    /**
     * This methods create and return an ScriptExecutionDtoBuildHelper:
     * inputParameters, designLabels, executionLabels, actions and output are created empty
     *
     * @param resultSet - item containing the fields required to create the object
     * @return ScriptExecutionDtoBuildHelper - Object similar to ScriptExecutionDto but containing Hashmap instead of List
     * @throws SQLException - Throws SQLException due to the param resultSet
     */
    private ScriptExecutionDtoBuildHelper mapScriptExecutionDtoBuildHelper(ResultSet resultSet) throws SQLException {
        return new ScriptExecutionDtoBuildHelper(resultSet.getString("RUN_ID"),
                resultSet.getLong("SCRIPT_PRC_ID"),
                resultSet.getLong("SCRIPT_PARENT_PRC_ID"),
                resultSet.getString("SCRIPT_ID"),
                resultSet.getString("SCRIPT_NM"),
                resultSet.getLong("SCRIPT_VRS_NB"),
                resultSet.getString("ENV_NM"),
                ScriptRunStatus.valueOf(resultSet.getString("SCRIPT_ST_NM")),
                SQLTools.getLocalDatetimeFromSql(resultSet.getString("SCRIPT_STRT_TMS")),
                SQLTools.getLocalDatetimeFromSql(resultSet.getString("SCRIPT_END_TMS")),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>());
    }

    /**
     * This methods create and return an ActionExecutionDtoBuildHelper: only inputParameters and output aren't completed
     *
     * @param resultSet - item containing the fields required to create the object
     * @return mapActionExecutionDtoBuildHelper: object similar to ActionExecutionDto but containing map instead of list
     * @throws SQLException - Throws SQLException due to the param resultSet
     */
    private ActionExecutionDtoBuildHelper mapActionExecutionDtoBuildHelper(ResultSet resultSet) throws SQLException {
        return new ActionExecutionDtoBuildHelper(resultSet.getString("RUN_ID"),
                resultSet.getLong("ACTION_PRC_ID"),
                resultSet.getString("ACTION_TYP_NM"),
                resultSet.getString("ACTION_NM"),
                resultSet.getString("ACTION_DSC"),
                resultSet.getString("ACTION_CONDITION_VAL"),
                resultSet.getString("ACTION_STOP_ERR_FL").equalsIgnoreCase("y") ||
                        resultSet.getString("ACTION_STOP_ERR_FL").equalsIgnoreCase("yes"),
                resultSet.getString("ACTION_EXP_ERR_FL").equalsIgnoreCase("y") ||
                        resultSet.getString("ACTION_EXP_ERR_FL").equalsIgnoreCase("yes"),
                ScriptRunStatus.valueOf(resultSet.getString("ACTION_ST_NM")),
                SQLTools.getLocalDatetimeFromSql(resultSet.getString("ACTION_STRT_TMS")),
                SQLTools.getLocalDatetimeFromSql(resultSet.getString("ACTION_END_TMS")),
                new HashMap<>(),
                new HashMap<>());
    }

    @Override
    public List<ScriptExecutionDto> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<ScriptResultKey, ScriptExecutionDtoBuildHelper> scriptExecutionDtoBuildHelperMap = new LinkedHashMap<>();
        while (rs.next()) {
            mapRow(rs, scriptExecutionDtoBuildHelperMap);
        }
        return scriptExecutionDtoBuildHelperMap.values().stream()
                .map(ScriptExecutionDtoBuildHelper::toScriptExecutionDto)
                .collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    private class ScriptExecutionDtoBuildHelper {

        private final String runId;

        private final Long processId;
        private final Long parentProcessId;

        private final String scriptId;
        private final String scriptName;
        private final Long scriptVersion;
        private final String environment;
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
                    environment,
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
