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

    private void mapRow(CachedRowSet cachedRowSet, Map<ScriptResultKey, ScriptExecutionDtoBuildHelper> scriptExecutionDtoBuildHelpers) throws SQLException {

        String runId = cachedRowSet.getString("RUN_ID");
        Long scriptPrcId = cachedRowSet.getLong("PRC_ID");

        ScriptResultKey scriptResultKey = new ScriptResultKey(runId, scriptPrcId);

        // IESI_RES_SCRIPT || IESI_TRC_DES_SCRIPT && IESI_TRC_DES_SCRIPT_VRS
        ScriptExecutionDtoBuildHelper scriptExecutionDtoBuildHelper = scriptExecutionDtoBuildHelpers.get(scriptResultKey);
        if (scriptExecutionDtoBuildHelper == null) {
            scriptExecutionDtoBuildHelper = mapScriptExecutionDtoBuildHelper(cachedRowSet);
            scriptExecutionDtoBuildHelpers.put(scriptResultKey, scriptExecutionDtoBuildHelper);
        }

        // Inputparams of the script
        String inputParameterName = cachedRowSet.getString("SCRIPT_PAR_NM");
        if (inputParameterName != null && scriptExecutionDtoBuildHelper.getInputParameters().get(inputParameterName) == null) {
            scriptExecutionDtoBuildHelper.getInputParameters()
                    .put(inputParameterName, new InputParametersDto(inputParameterName,
                            // Todo: rawValue -> mostly # + name : but is it somewhere in the DB ?
                            "#" + inputParameterName,
                            cachedRowSet.getString("SCRIPT_PAR_VAL"))
                    );
        }

        // DesignLabels
        String designLabelId = cachedRowSet.getString("SCRIPT_LBL_ID");
        if (designLabelId != null && scriptExecutionDtoBuildHelper.getDesignLabels().get(designLabelId) == null) {
            scriptExecutionDtoBuildHelper.getDesignLabels()
                    .put(designLabelId, new ScriptLabelDto(cachedRowSet.getString("SCRIPT_LBL_NM"),
                            cachedRowSet.getString("SCRIPT_LBL_VAL")));
        }

        // Execution Labels
        // Todo: include the execution label in the query
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

        // TODO: Check Primary key to use -> PRC_ID of action and Action_ID
        // Actions - PRK RunID + PrcID + ActionID -> RunID for "this" script will always be the same
        String actionId = cachedRowSet.getString("ACTION_ID");
        if (actionId != null) {
            mapRowScriptAction(scriptExecutionDtoBuildHelper, actionId, cachedRowSet);
        }


    }

    private void mapRowScriptAction(ScriptExecutionDtoBuildHelper scriptExecutionDtoBuildHelper, String actionId, CachedRowSet cachedRowSet) throws SQLException {
        // create ActionKey
        Long actionPrcId = cachedRowSet.getLong("PRC_ID");
        ActionExecutionKey actionExecutionKey = new ActionExecutionKey(actionPrcId, actionId);
        ActionExecutionDtoBuildHelper actionExecutionDtoBuildHelper = scriptExecutionDtoBuildHelper.getActions().get(actionExecutionKey);
        if (actionExecutionDtoBuildHelper == null) {
            actionExecutionDtoBuildHelper = mapActionExecutionDtoBuildHelper(cachedRowSet);
            scriptExecutionDtoBuildHelper.getActions().put(actionExecutionKey, actionExecutionDtoBuildHelper);
        }
        // Todo: check for parameters
        String actionParameterName = cachedRowSet.getString("ACTION_PAR_NM");
        if (actionParameterName != null && actionExecutionDtoBuildHelper.getInputParameters().get(actionParameterName) == null) {
            actionExecutionDtoBuildHelper.getInputParameters()
                    .put(actionParameterName,new InputParametersDto(actionParameterName,
                            // Todo: rawValue -> mostly # + name : but is it somewhere in the DB ?
                    "#" + actionParameterName,
                            cachedRowSet.getString("ACTION_PAR_VAL"))
                    );
        }
    }

    private ScriptExecutionDtoBuildHelper mapScriptExecutionDtoBuildHelper(CachedRowSet cachedRowSet) throws SQLException {
        return ScriptExecutionDtoBuildHelper.builder()
                .runId(cachedRowSet.getString("RUN_ID"))
                .processId(cachedRowSet.getLong("PRC_ID"))
                .scriptId(cachedRowSet.getString("SCRIPT_ID"))
                .scriptName(cachedRowSet.getString("SCRIPT_NM"))
                .scriptVersion(cachedRowSet.getLong("SCRIPT_VRS_NB"))
                .environment(cachedRowSet.getString("ENV_NM"))
                .status(ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")))
                .startTimestamp(SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")))
                .endTimestamp(SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS")))
                .build();
    }

    private ActionExecutionDtoBuildHelper mapActionExecutionDtoBuildHelper(CachedRowSet cachedRowSet) throws SQLException {
        return ActionExecutionDtoBuildHelper.builder()
                .runId(cachedRowSet.getString(""))
                .processId(cachedRowSet.getLong(""))
                .type(cachedRowSet.getString(""))
                .name(cachedRowSet.getString(""))
                .description(cachedRowSet.getString(""))
                .condition(cachedRowSet.getString(""))
                .errorStop(cachedRowSet.getBoolean(""))
                .errorExpected(cachedRowSet.getBoolean(""))
                .status(ScriptRunStatus.valueOf(cachedRowSet.getString("")))
                .startTimestamp(SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("")))
                .endTimestamp(SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("")))
                .build();
    }

    private String getSQLQuery(String runId, Long processId) {
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
