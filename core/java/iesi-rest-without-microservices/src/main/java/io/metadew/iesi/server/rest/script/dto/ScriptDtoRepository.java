package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.action.ActionParameterDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScriptDtoRepository implements IScriptDtoRepository {

    public List<ScriptDto> getAll(List<String> expansions) {
        try {
            List<ScriptDto> scriptDtos = new ArrayList<>();

            String query = "Select " +
                    "script_design.SCRIPT_ID, script_design.SCRIPT_NM, script_design.SCRIPT_DSC, script_design.SCRIPT_TYP_NM, " +
                    "script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC, " +
                    "labels.NAME, labels.VALUE," +
                    (expansions.contains("execution") ? getExecutionExpansionSelect() : "") +
                    "actions.ACTION_ID, actions.ACTION_NB, actions.ACTION_NM, actions.ACTION_DSC, actions.ACTION_TYP_NM, actions.CONDITION_VAL, actions.EXP_ERR_FL, actions.STOP_ERR_FL " +
                    "action_parameters.ACTION_PAR_NM, action_parameters.ACTION_PAR_VAL" +
                    "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts") + " script_design " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions") + " script_version " +
                    "on script_design.SCRIPT_ID=script_version.SCRIPT_ID " +
                    (expansions.contains("execution") ? getExecutionExpansionJoins() : "") +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels") + " labels " +
                    "on a.SCRIPT_ID = labels.SCRIPT_ID and b.SCRIPT_VRS_NB = labels.SCRIPT_VRS_NB" +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions") + " actions " +
                    "on script_design.SCRIPT_ID = actions.SCRIPT_ID and script_version.SCRIPT_VRS_NB = actions.SCRIPT_VRS_NB" +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters") + " action_parameters " +
                    "on script_design.SCRIPT_ID = action_parameters.SCRIPT_ID and script_version.SCRIPT_VRS_NB = action_parameters.SCRIPT_VRS_NB and actions.ACTION_ID = action_parameters.ACTION_ID" +
                    "order by script_design.SCRIPT_ID, script_version.SCRIPT_VRS_NB, labels.NAME, actions.ACTION_NB, action_parameters.ACTION_PAR_NM" +
                    (expansions.contains("execution") ? "," + getExecutionExpansionOrderBy() : "") + ";";
            // TODO: how to handle split repositories?
            CachedRowSet cachedRowSet = MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
            ScriptDto scriptDto = null;
            ActionDto actionDto = null;
            ActionParameterDto actionParameterDto = null;
            ScriptLabelDto scriptLabelDto = null;
            while (cachedRowSet.next()) {
                // check if new script
                String scriptName = cachedRowSet.getString("SCRIPT_NM");
                long scriptVersion = cachedRowSet.getLong("SCRIPT_VRS_NB");
                if (scriptDto == null) {
                    scriptDto = mapScriptDto(cachedRowSet);
                } else if (!scriptDto.getName().equals(scriptName) || scriptDto.getVersion().getNumber() != scriptVersion) {
                    scriptDtos.add(scriptDto);
                    scriptDto = mapScriptDto(cachedRowSet);
                    scriptLabelDto = mapScriptLabelDto(cachedRowSet, scriptDto);
                    actionDto = mapActionDto(cachedRowSet);
                    scriptDto.addActionDto(actionDto);
                    actionParameterDto = mapActionParameterDto(cachedRowSet);
                    actionDto.addActionParameterDto(actionParameterDto);
                }
                // check if new label
                String labelName = cachedRowSet.getString("NAME");
                if (scriptLabelDto == null || !scriptLabelDto.getName().equals(labelName)) {
                    scriptLabelDto = mapScriptLabelDto(cachedRowSet, scriptDto);
                }
                // check if new action
                long actionNumber = cachedRowSet.getLong("ACTION_NB");
                if (actionDto == null) {
                    actionDto = mapActionDto(cachedRowSet);
                } else if (actionDto.getNumber() != actionNumber) {
                    scriptDto.addActionDto(actionDto);
                    actionDto = mapActionDto(cachedRowSet);
                    actionParameterDto = mapActionParameterDto(cachedRowSet);
                    actionDto.addActionParameterDto(actionParameterDto);
                }
                // check if new parameter
                String ActionParameterName = cachedRowSet.getString("ACTION_PAR_NM");
                if (actionParameterDto == null) {
                    actionParameterDto = mapActionParameterDto(cachedRowSet);
                } else if (!actionParameterDto.getName().equals(ActionParameterName)) {
                    actionDto.addActionParameterDto(actionParameterDto);
                    actionParameterDto = mapActionParameterDto(cachedRowSet);
                }
            }
            return scriptDtos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ActionParameterDto mapActionParameterDto(CachedRowSet cachedRowSet) {
        return null;
    }

    private ScriptLabelDto mapScriptLabelDto(CachedRowSet cachedRowSet, ScriptDto scriptDto) throws SQLException {
        ScriptLabelDto scriptLabelDto = null;
        String labelName = cachedRowSet.getString("NAME");
        String labelValue = cachedRowSet.getString("VALUE");
        if (labelValue != null) {
            scriptLabelDto = new ScriptLabelDto(labelName, labelValue);
            scriptDto.addScriptLabelDto(scriptLabelDto);
        }
        return scriptLabelDto;
    }

    public ScriptDto mapScriptDto(CachedRowSet cachedRowSet) throws SQLException {
        ScriptDto scriptDto = new ScriptDto();
        scriptDto.setName(cachedRowSet.getString("SCRIPT_NM"));
        scriptDto.setDescription(cachedRowSet.getString("SCRIPT_DSC"));
        scriptDto.setVersion(new ScriptVersionDto(
                cachedRowSet.getLong("SCRIPT_VRS_NB"),
                cachedRowSet.getString("SCRIPT_VRS_DSC")
        ));
        return scriptDto;
    }

    public ActionDto mapActionDto(CachedRowSet cachedRowSet) throws SQLException {
        ActionDto actionDto = new ActionDto();
        actionDto.setNumber(cachedRowSet.getLong("SCRIPT_VRS_NB"));
        actionDto.setName(cachedRowSet.getString("SCRIPT_VRS_NB"));

        return actionDto;
    }

    public List<ScriptDto> getByName(String name, List<String> expansions) {
        List<ScriptDto> scriptDtos = new ArrayList<>();
        return scriptDtos;
    }

    public List<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions) {
        List<ScriptDto> scriptDtos = new ArrayList<>();
        return scriptDtos;
    }

    private String getExecutionExpansionSelect() {
        return "left outer join (select * from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
                " where (SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, STRT_TMS) in (SELECT SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, MAX(STRT_TMS) " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
                "group by SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM)) script_results " +
                "on script_design.SCRIPT_ID = script_results.SCRIPT_ID and script_version.SCRIPT_VRS_NB = script_results.SCRIPT_VRS_NB ";
    }

    private String getExecutionExpansionJoins() {
        return "script_results.RUN_ID, script_results.PRC_ID, script_results.ENV_NM, script_results.ST_NM, script_results.STRT_TMS, script_results.END_TMS,";
    }


    private String getExecutionExpansionOrderBy() {
        return "script_results.ENV_NM";
    }

}
