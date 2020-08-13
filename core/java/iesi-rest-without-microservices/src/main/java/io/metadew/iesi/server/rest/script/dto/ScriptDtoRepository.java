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

    /**
     * This method return the number of row of the pivot table which is an inner join between scripts and scriptVersions table.
     * It is design to get the number of ScriptDto that can be create based on the given parameter.
     * It helps creating the pagination
     *
     * @param scriptName          - Name of the script
     * @param scriptVersion       - Version of the script
     * @param isLatestVersionOnly - If true, only count last version of a script
     * @return Number of script that can be created with those current parameter
     */
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
            String query = getQuery(pageable, null, null, isLatestVersionOnly, expansions);
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
    public Page<ScriptDto> getByName(Pageable pageable, String name, List<String> expansions, boolean isLatestVersionOnly) {
        try {
            Map<ScriptKey, ScriptDto> scriptDtos = new HashMap<>();
            Map<ActionKey, ActionDto> actionDtos = new HashMap<>();
            String query = getQuery(pageable, name, null, isLatestVersionOnly, expansions);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptDtos, actionDtos, expansions);
            }
            return new PageImpl<>(new ArrayList<>(scriptDtos.values()), pageable, getRowSize(name, null, isLatestVersionOnly));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions) {
        try {
            Map<ScriptKey, ScriptDto> scriptDtos = new HashMap<>();
            Map<ActionKey, ActionDto> actionDtos = new HashMap<>();
            String query = getQuery(null, name, version, false, expansions);
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

    /**
     * This method return the query adapted to the needs.
     *
     * @param pageable            - The pageable object containing the required pagination information. If null, it doesn't paginate.
     * @param scriptName          - Name of the script. If null, it doesn't filter.
     * @param scriptVersion       - Version of the script. If null, it can returns several version. Doesn't apply if isLatestVersionOnly is true.
     * @param isLatestVersionOnly - boolean: if true: returns only latest the version of scripts.
     * @param expansions          - Array containing String naming the required expansions
     * @return Query adapted to the provided parameters
     */
    private String getQuery(Pageable pageable, String scriptName, Long scriptVersion, boolean isLatestVersionOnly, List<String> expansions) {
        return "Select " +
                "scriptAndScriptVRS.SCRIPT_ID, scriptAndScriptVRS.SCRIPT_NM, scriptAndScriptVRS.SCRIPT_DSC, " +
                "scriptAndScriptVRS.SCRIPT_VRS_NB, scriptAndScriptVRS.SCRIPT_VRS_DSC, 0 INFO_TYPE, " +
                "script_label.NAME LABEL_NAME, script_label.VALUE LABEL_VALUE, " +
                "null ACTION_ID, null ACTION_NM, null ACTION_NB, null ACTION_DSC, null ACTION_TYP_NM, " +
                "null CONDITION_VAL, null EXP_ERR_FL, null STOP_ERR_FL, null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                "FROM " +

                // getWhereStatement included for scriptName, scriptVersion and latestVersion
                getScriptAndScriptVRSTable(pageable, scriptName, scriptVersion, isLatestVersionOnly) + " scriptAndScriptVRS " +

                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_label " +
                "on scriptAndScriptVRS.SCRIPT_ID = script_label.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = script_label.SCRIPT_VRS_NB " +
                "union all " +
                "Select " + "scriptAndScriptVRS.SCRIPT_ID, scriptAndScriptVRS.SCRIPT_NM, scriptAndScriptVRS.SCRIPT_DSC, " +
                "scriptAndScriptVRS.SCRIPT_VRS_NB, scriptAndScriptVRS.SCRIPT_VRS_DSC, 1 INFO_TYPE, " +
                "null LABEL_NAME, null LABEL_VALUE, " +
                "action.ACTION_ID, action.ACTION_NM, action.ACTION_NB, action.ACTION_DSC, action.ACTION_TYP_NM, " +
                "action.CONDITION_VAL, action.EXP_ERR_FL, action.STOP_ERR_FL, " +
                "action_parameter.ACTION_PAR_NM, action_parameter.ACTION_PAR_VAL, " +
                "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                "FROM " +

                // getWhereStatement included for scriptName, scriptVersion and latestVersion
                getScriptAndScriptVRSTable(pageable, scriptName, scriptVersion, isLatestVersionOnly) + " scriptAndScriptVRS " +

                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " action " +
                "on scriptAndScriptVRS.SCRIPT_ID = action.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = action.SCRIPT_VRS_NB " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameter " +
                "on scriptAndScriptVRS.SCRIPT_ID = action_parameter.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = action_parameter.SCRIPT_VRS_NB and action.ACTION_ID = action_parameter.ACTION_ID" +

                // getWhereStatement included for scriptName, scriptVersion and latestVersion
                getExecutionExpansionUnion(pageable, scriptName, scriptVersion, expansions, isLatestVersionOnly) +

                ";";
    }

    /**
     * This method helps to paginate the query by limiting directly the pivot table (scripts inner join scriptVersions)
     * which is the base of the query.
     *
     * @param pageable            - The pageable object containing the required pagination information. If null, it doesn't paginate.
     * @param scriptName          - Name of the script. If null, it doesn't filter.
     * @param scriptVersion       - Version of the script. If null, it can returns several version. Doesn't apply if isLatestVersionOnly is true.
     * @param isLatestVersionOnly - boolean: if true: returns only latest the version of scripts.
     * @return return the query to of the pivot table according to the parameter.
     */
    private String getScriptAndScriptVRSTable(Pageable pageable, String scriptName, Long scriptVersion, boolean isLatestVersionOnly) {
        String limitAndOffset = pageable == null || pageable.isUnpaged() ? " " : " limit " + pageable.getPageSize() + " offset " + pageable.getOffset() + " ";
        return (" (" +
                "SELECT " +
                "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, " +
                "script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                "on script.SCRIPT_ID = script_version.SCRIPT_ID " +
                getWhereClause(scriptName, scriptVersion, isLatestVersionOnly).orElse(" ") +
                getOrderByStatementForScriptAndScriptVersionTable(pageable) +
                limitAndOffset +
                ") ");
    }

    /**
     * This method provide an Order by statement to order the
     * @param pageable - pageable object containing or not the order object
     * @return a String containing the ORDER BY statement
     */
    private String getOrderByStatementForScriptAndScriptVersionTable(Pageable pageable) {
        if (pageable == null || !pageable.getSort().isSorted())
            return " ";
        List<String> sorting = new ArrayList<>();
        pageable.getSort().stream().forEach(order -> {

            if(order.getProperty().equalsIgnoreCase("NAME"))
                sorting.add("script.SCRIPT_NM" + " " + order.getDirection());

        });
        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    /**
     * This method provide a where statement depending of the arguments passed.
     *
     * @param scriptName          - if filled, add a filter on the name of the script. If null, doesn't filter on the name.
     * @param scriptVersion       - if filled, add a filter on the version of the script. If null, doesn't filter on the version.
     * @param isLatestVersionOnly - if true, filter to return only the last version of the script. If true, the scriptVersion doesn't apply.
     * @return a String containing the where clause if argument are provided or a space if all args are null.
     */
    private Optional<String> getWhereClause(String scriptName, Long scriptVersion, boolean isLatestVersionOnly) {
        List<String> conditions = new ArrayList<>();
        if (scriptName != null) {
            conditions.add(" script.SCRIPT_NM=" + SQLTools.GetStringForSQL(scriptName) + " ");
        }
        if (isLatestVersionOnly) {
            conditions.add(
                    " (script.SCRIPT_ID, script_version.SCRIPT_VRS_NB) in (select script.SCRIPT_ID, max(script_version.SCRIPT_VRS_NB) SCRIPT_VRS_NB " +
                            "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
                            " script_version on script.SCRIPT_ID = script_version.SCRIPT_ID group by script.SCRIPT_ID) ");
        } else if (scriptVersion != null) {
            conditions.add(" script_version.SCRIPT_VRS_NB=" + SQLTools.GetStringForSQL(scriptVersion) + " ");
        }
        if (conditions.isEmpty()) return Optional.empty();
        return Optional.of(" where " + String.join(" and ", conditions) + " ");
    }

    private String getExecutionExpansionUnion(Pageable pageable, String scriptName, Long scriptVersion, List<String> expansions, Boolean isLatestVersionOnly) {
        if (expansions != null && expansions.contains("execution")) {
            return " union all select " +
                    "scriptAndScriptVRS.SCRIPT_ID, scriptAndScriptVRS.SCRIPT_NM, scriptAndScriptVRS.SCRIPT_DSC, " +
                    "scriptAndScriptVRS.SCRIPT_VRS_NB, scriptAndScriptVRS.SCRIPT_VRS_DSC, " +
                    "2 INFO_TYPE, " +
                    "null LABEL_NAME, null LABEL_VALUE, null ACTION_ID, null ACTION_NM, null ACTION_NB, null ACTION_DSC, " +
                    "null ACTION_TYP_NM, null CONDITION_VAL, null EXP_ERR_FL, null STOP_ERR_FL, null ACTION_PAR_NM, " +
                    "null ACTION_PAR_VAL, script_result.RUN_ID RUN_ID, script_result.PRC_ID PRC_ID, " +
                    "script_result.ENV_NM ENV_NM, script_result.ST_NM ST_NM, script_result.STRT_TMS STRT_TMS, " +
                    "script_result.END_TMS END_TMS " +
                    "FROM " +

                    // getWhereStatement included for scriptName, scriptVersion and latestVersion
                    getScriptAndScriptVRSTable(pageable, scriptName, scriptVersion, isLatestVersionOnly) + " scriptAndScriptVRS " +

                    "inner join (select * from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() +
                    " where (SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, STRT_TMS) " +
                    "in (SELECT SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, MAX(STRT_TMS) " +
                    "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " " +
                    "group by SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM)) script_result " +
                    "on scriptAndScriptVRS.SCRIPT_ID = script_result.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = script_result.SCRIPT_VRS_NB ";
        } else {
            return " ";
        }
    }
}
