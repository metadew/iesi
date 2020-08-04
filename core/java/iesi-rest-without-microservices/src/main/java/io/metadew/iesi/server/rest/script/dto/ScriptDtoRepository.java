package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.action.ActionParameterDto;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptExecutionDto;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptExecutionInformation;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptSchedulingInformation;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@Log4j2
@Repository
public class ScriptDtoRepository implements IScriptDtoRepository {

    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    public ScriptDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @Autowired
    public void setMetadataRepositoryConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    public long getRowSize(String scriptName, Long scriptVersion, boolean isLatestVersionOnly) {
        try {
            String query = "Select COUNT(*) count FROM " + getScriptAndScriptVRSTable(null, scriptName, scriptVersion, isLatestVersionOnly) + ";";
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            cachedRowSet.next();
            return cachedRowSet.getLong("count");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<ScriptDto> getAll(Pageable pageable, List<String> expansions, boolean isLatestVersionOnly) {
        try {
            Map<ScriptKey, ScriptDto> scriptDtos = new HashMap<>();
            Map<ActionKey, ActionDto> actionDtos = new HashMap<>();
            String query = getQuery(pageable,null,null,isLatestVersionOnly,expansions);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptDtos, actionDtos, expansions);
            }
            return new PageImpl<>(new ArrayList<>(scriptDtos.values()), pageable, getRowSize(null, null, isLatestVersionOnly));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptDto> getByName(String name, List<String> expansions) {
        try {
            Map<ScriptKey, ScriptDto> scriptDtos = new HashMap<>();
            Map<ActionKey, ActionDto> actionDtos = new HashMap<>();
            String query = "Select " +
                    "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, script.SCRIPT_TYP_NM, script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC, 0 INFO_TYPE, " +
                    "script_label.NAME LABEL_NAME, script_label.VALUE LABEL_VALUE, " +
                    "null ACTION_ID, null ACTION_NM, null ACTION_NB, null ACTION_DSC, null ACTION_TYP_NM, null CONDITION_VAL, null EXP_ERR_FL, null STOP_ERR_FL, " +
                    "null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                    "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                    "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                    "on script.SCRIPT_ID=script_version.SCRIPT_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_label " +
                    "on script.SCRIPT_ID = script_label.SCRIPT_ID and script_version.SCRIPT_VRS_NB = script_label.SCRIPT_VRS_NB " +
                    getWhereClause(name, null).orElse("") +
                    "union all " +
                    "Select " + "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, script.SCRIPT_TYP_NM, script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC, 1 INFO_TYPE, " +
                    "null LABEL_NAME, null LABEL_VALUE, " +
                    "action.ACTION_ID, action.ACTION_NM, action.ACTION_NB, action.ACTION_DSC, action.ACTION_TYP_NM, action.CONDITION_VAL, action.EXP_ERR_FL, action.STOP_ERR_FL, " +
                    "action_parameter.ACTION_PAR_NM, action_parameter.ACTION_PAR_VAL, " +
                    "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                    "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                    "on script.SCRIPT_ID=script_version.SCRIPT_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " action " +
                    "on script.SCRIPT_ID = action.SCRIPT_ID and script_version.SCRIPT_VRS_NB = action.SCRIPT_VRS_NB " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameter " +
                    "on script.SCRIPT_ID = action_parameter.SCRIPT_ID and script_version.SCRIPT_VRS_NB = action_parameter.SCRIPT_VRS_NB and action.ACTION_ID = action_parameter.ACTION_ID" +
                    getWhereClause(name, null).orElse("") +
                    (expansions != null && expansions.contains("execution") ? getExecutionExpansionUnion(name, null) : "") +
                    ";";
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptDtos, actionDtos, expansions);
            }
            return new ArrayList<>(scriptDtos.values());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions) {
        try {
            Map<ScriptKey, ScriptDto> scriptDtos = new HashMap<>();
            Map<ActionKey, ActionDto> actionDtos = new HashMap<>();
            String query = "Select " +
                    "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, script.SCRIPT_TYP_NM, script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC, 0 INFO_TYPE, " +
                    "script_label.NAME LABEL_NAME, script_label.VALUE LABEL_VALUE, null ACTION_ID, null ACTION_NM, null ACTION_NB, null ACTION_DSC, null ACTION_TYP_NM, null CONDITION_VAL, null EXP_ERR_FL, null STOP_ERR_FL, " +
                    "null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                    "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                    "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                    "on script.SCRIPT_ID=script_version.SCRIPT_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_label " +
                    "on script.SCRIPT_ID = script_label.SCRIPT_ID and script_version.SCRIPT_VRS_NB = script_label.SCRIPT_VRS_NB " +
                    getWhereClause(name, version).orElse("") +
                    "union all " +
                    "Select " + "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, script.SCRIPT_TYP_NM, script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC, 1 INFO_TYPE, " +
                    "null LABEL_NAME, null LABEL_VALUE, action.ACTION_ID, action.ACTION_NM, action.ACTION_NB, action.ACTION_DSC, action.ACTION_TYP_NM, action.CONDITION_VAL, action.EXP_ERR_FL, action.STOP_ERR_FL, " +
                    "action_parameter.ACTION_PAR_NM, action_parameter.ACTION_PAR_VAL, " +
                    "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                    "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                    "on script.SCRIPT_ID=script_version.SCRIPT_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " action " +
                    "on script.SCRIPT_ID = action.SCRIPT_ID and script_version.SCRIPT_VRS_NB = action.SCRIPT_VRS_NB " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameter " +
                    "on script.SCRIPT_ID = action_parameter.SCRIPT_ID and script_version.SCRIPT_VRS_NB = action_parameter.SCRIPT_VRS_NB and action.ACTION_ID = action_parameter.ACTION_ID" +
                    getWhereClause(name, version).orElse("") +
                    (expansions != null && expansions.contains("execution") ? getExecutionExpansionUnion(name, version) : "") +
                    ";";
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptDtos, actionDtos, expansions);
            }
            if (scriptDtos.size() > 1) {
                log.warn("found multiple script for script " + name + "-" + version);
            }
            return scriptDtos.values().stream().findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private String getScriptAndScriptVRSTable(Pageable pageable, String scriptName, Long scriptVersion, boolean isLatestVersion) {
        String limitAndOffset = pageable == null ? " " : " limit " + pageable.getPageSize() + " offset " + pageable.getOffset() + " ";

        return (" (" +
                "SELECT * FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                "on script.SCRIPT_ID = script_version.SCRIPT_ID " +
                getWhereClause(scriptName, scriptVersion, isLatestVersion).orElse(" ") +
                limitAndOffset +
                ") ");
    }


    private String getExecutionExpansionUnion(String scriptName, Long scriptVersion) {
        return getExecutionExpansionUnion(scriptName, scriptVersion, false);
    }

    private String getExecutionExpansionUnion(String scriptName, Long scriptVersion, Boolean isLatestVersion) {
        return " union all select " +
                "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, script.SCRIPT_TYP_NM, script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC, 2 INFO_TYPE, " +
                "null LABEL_NAME, null LABEL_VALUE, null ACTION_ID, null ACTION_NM, null ACTION_NB, null ACTION_DSC, null ACTION_TYP_NM, " +
                "null CONDITION_VAL, null EXP_ERR_FL, null STOP_ERR_FL, " +
                "null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                "script_result.RUN_ID RUN_ID, script_result.PRC_ID PRC_ID, script_result.ENV_NM ENV_NM, script_result.ST_NM ST_NM, script_result.STRT_TMS STRT_TMS, script_result.END_TMS END_TMS " +
                "FROM " +

                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                "on script.SCRIPT_ID=script_version.SCRIPT_ID " +

                "inner join (select * from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() +
                " where (SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, STRT_TMS) " +
                "in (SELECT SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, MAX(STRT_TMS) " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " " +
                "group by SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM)) script_result " +
                "on script.SCRIPT_ID = script_result.SCRIPT_ID and script_version.SCRIPT_VRS_NB = script_result.SCRIPT_VRS_NB" +
                getWhereClause(scriptName, scriptVersion, isLatestVersion).orElse(" ") + "";
    }

    private Optional<String> getWhereClause(String scriptName, Long scriptVersion) {
        return getWhereClause(scriptName, scriptVersion, false);
    }

    private Optional<String> getWhereClause(String scriptName, Long scriptVersion, boolean isLatestVersion) {
        List<String> conditions = new ArrayList<>();
        if (scriptName != null) {
            conditions.add(" script.SCRIPT_NM=" + SQLTools.GetStringForSQL(scriptName));
        }
        if (isLatestVersion) {
            conditions.add(
                    " (script.SCRIPT_ID, script_version.SCRIPT_VRS_NB) in (select script.SCRIPT_ID, max(script_version.SCRIPT_VRS_NB) SCRIPT_VRS_NB " +
                            "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
                            " script_version on script.SCRIPT_ID = script_version.SCRIPT_ID group by script.SCRIPT_ID)");
        } else if (scriptVersion != null) {
            conditions.add(" script_version.SCRIPT_VRS_NB=" + SQLTools.GetStringForSQL(scriptVersion));
        }
        if (conditions.isEmpty()) return Optional.empty();
        return Optional.of(" where " + String.join(" and ", conditions) + " ");
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<ScriptKey, ScriptDto> scriptDtos, Map<ActionKey, ActionDto> actionDtos, List<String> expansions) throws SQLException {
        ScriptKey scriptKey = new ScriptKey(cachedRowSet.getString("SCRIPT_NM"), cachedRowSet.getLong("SCRIPT_VRS_NB"));
        ScriptDto scriptDto = scriptDtos.get(scriptKey);
        if (scriptDto == null) {
            scriptDto = mapScriptDto(cachedRowSet);
            scriptDtos.put(scriptKey, scriptDto);
        }
        int infoType = cachedRowSet.getInt("INFO_TYPE");
        if (expansions.contains("execution")) {
            scriptDto.setScriptExecutionInformation(new ScriptExecutionInformation());
        }
        if (infoType == 0) {
            scriptDto.addScriptLabelDto(mapScriptLabelDto(cachedRowSet));
        } else if (infoType == 1) {
            // action parameter
            ActionKey actionKey = new ActionKey(scriptKey, cachedRowSet.getString("ACTION_ID"));
            ActionDto actionDto = actionDtos.get(actionKey);
            if (actionDto == null) {
                actionDto = mapActionDto(cachedRowSet);
                actionDtos.put(actionKey, actionDto);
                scriptDto.addActionDto(actionDto);
            }
            actionDto.addActionParameterDto(mapActionParameterDto(cachedRowSet));
        } else if (infoType == 2) {
            ScriptExecutionInformation scriptExecutionInformation = scriptDto.getScriptExecutionInformation();
            if (scriptExecutionInformation == null) {
                scriptExecutionInformation = new ScriptExecutionInformation();
                scriptDto.setScriptExecutionInformation(scriptExecutionInformation);
            }
            scriptExecutionInformation.addScriptExecutionDto(mapScriptExecutionDto(cachedRowSet));
        } else if (infoType == 3) {
            ScriptSchedulingInformation scriptSchedulingInformation = scriptDto.getScriptSchedulingInformation();
            if (scriptSchedulingInformation == null) {
                scriptSchedulingInformation = new ScriptSchedulingInformation();
                scriptDto.setScriptSchedulingInformation(scriptSchedulingInformation);
            }
        }
    }

    private ScriptDto mapScriptDto(CachedRowSet cachedRowSet) throws SQLException {
        ScriptDto scriptDto = new ScriptDto();
        scriptDto.setName(cachedRowSet.getString("SCRIPT_NM"));
        scriptDto.setDescription(cachedRowSet.getString("SCRIPT_DSC"));
        scriptDto.setVersion(new ScriptVersionDto(
                cachedRowSet.getLong("SCRIPT_VRS_NB"),
                cachedRowSet.getString("SCRIPT_VRS_DSC")
        ));
        return scriptDto;
    }

    private ScriptLabelDto mapScriptLabelDto(CachedRowSet cachedRowSet) throws SQLException {
        String labelName = cachedRowSet.getString("LABEL_NAME");
        String labelValue = cachedRowSet.getString("LABEL_VALUE");
        return new ScriptLabelDto(labelName, labelValue);
    }

    private ActionDto mapActionDto(CachedRowSet cachedRowSet) throws SQLException {
        ActionDto actionDto = new ActionDto();
        actionDto.setNumber(cachedRowSet.getLong("ACTION_NB"));
        actionDto.setName(cachedRowSet.getString("ACTION_NM"));
        actionDto.setDescription(cachedRowSet.getString("ACTION_DSC"));
        actionDto.setType(cachedRowSet.getString("ACTION_TYP_NM"));
        actionDto.setCondition(cachedRowSet.getString("CONDITION_VAL"));
        actionDto.setErrorExpected(cachedRowSet.getString("EXP_ERR_FL").equalsIgnoreCase("y"));
        actionDto.setErrorStop(cachedRowSet.getString("STOP_ERR_FL").equalsIgnoreCase("y"));
        return actionDto;
    }

    private ActionParameterDto mapActionParameterDto(CachedRowSet cachedRowSet) throws SQLException {
        return new ActionParameterDto(
                cachedRowSet.getString("ACTION_PAR_NM"),
                cachedRowSet.getString("ACTION_PAR_VAL")
        );
    }

    private ScriptExecutionDto mapScriptExecutionDto(CachedRowSet cachedRowSet) throws SQLException {
        return new ScriptExecutionDto(cachedRowSet.getString("RUN_ID"),
                cachedRowSet.getString("ENV_NM"),
                ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))
        );
    }

    private String getQuery(Pageable pageable, String scriptName, Long scriptVersion, boolean isLatestVersionOnly, List<String> expansions){
        return "Select " +
                "scriptAndScriptVRS.SCRIPT_ID, scriptAndScriptVRS.SCRIPT_NM, scriptAndScriptVRS.SCRIPT_DSC, scriptAndScriptVRS.SCRIPT_TYP_NM, " +
                "scriptAndScriptVRS.SCRIPT_VRS_NB, scriptAndScriptVRS.SCRIPT_VRS_DSC, 0 INFO_TYPE, " +
                "script_label.NAME LABEL_NAME, script_label.VALUE LABEL_VALUE, " +
                "null ACTION_ID, null ACTION_NM, null ACTION_NB, null ACTION_DSC, null ACTION_TYP_NM, " +
                "null CONDITION_VAL, null EXP_ERR_FL, null STOP_ERR_FL, null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                "FROM " +
                // getWhereStatement included
                getScriptAndScriptVRSTable(pageable, scriptName, scriptVersion, isLatestVersionOnly) + " scriptAndScriptVRS " +

                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_label " +
                "on scriptAndScriptVRS.SCRIPT_ID = script_label.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = script_label.SCRIPT_VRS_NB " +
                "union all " +
                "Select " + "scriptAndScriptVRS.SCRIPT_ID, scriptAndScriptVRS.SCRIPT_NM, scriptAndScriptVRS.SCRIPT_DSC, scriptAndScriptVRS.SCRIPT_TYP_NM, " +
                "scriptAndScriptVRS.SCRIPT_VRS_NB, scriptAndScriptVRS.SCRIPT_VRS_DSC, 1 INFO_TYPE, " +
                "null LABEL_NAME, null LABEL_VALUE, " +
                "action.ACTION_ID, action.ACTION_NM, action.ACTION_NB, action.ACTION_DSC, action.ACTION_TYP_NM, " +
                "action.CONDITION_VAL, action.EXP_ERR_FL, action.STOP_ERR_FL, " +
                "action_parameter.ACTION_PAR_NM, action_parameter.ACTION_PAR_VAL, " +
                "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                "FROM " +
                // getWhereStatement included
                getScriptAndScriptVRSTable(pageable, scriptName, scriptVersion, isLatestVersionOnly) + " scriptAndScriptVRS " +

                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " action " +
                "on scriptAndScriptVRS.SCRIPT_ID = action.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = action.SCRIPT_VRS_NB " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameter " +
                "on scriptAndScriptVRS.SCRIPT_ID = action_parameter.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = action_parameter.SCRIPT_VRS_NB and action.ACTION_ID = action_parameter.ACTION_ID" +
                (expansions != null && expansions.contains("execution") ? getExecutionExpansionUnion(scriptName, scriptVersion, isLatestVersionOnly) : "") +
                ";";
    }
}
