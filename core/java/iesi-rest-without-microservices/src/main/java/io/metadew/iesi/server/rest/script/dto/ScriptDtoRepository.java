package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.server.rest.script.ScriptFilter;
import io.metadew.iesi.server.rest.script.ScriptFilterOption;
import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.action.ActionParameterDto;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptExecutionDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private String getFetchAllQuery(Pageable pageable, boolean onlyLatestVersions, List<ScriptFilter> scriptFilters, List<String> expansions) {
        return "select script_designs.SCRIPT_ID, script_designs.SCRIPT_NM, script_designs.SCRIPT_DSC, " +
                "versions.SCRIPT_VRS_NB, versions.SCRIPT_VRS_DSC, " +
                "script_labels.ID as LABEL_ID, script_labels.NAME as LABEL_NAME, script_labels.VALUE as LABEL_VALUE, " +
                "actions.ACTION_ID, actions.ACTION_NB, actions.ACTION_DSC, actions.ACTION_NM, actions.ACTION_TYP_NM, actions.COMP_NM, actions.CONDITION_VAL, actions.ITERATION_VAL, actions.EXP_ERR_FL, actions.RETRIES_VAL, actions.STOP_ERR_FL, " +
                "action_parameters.ACTION_PAR_NM, action_parameters.ACTION_PAR_VAL " +
                "from (" + getBaseQuery(pageable, onlyLatestVersions, scriptFilters) + ") script_versions " + //base table
                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script_designs " +
                "on script_designs.SCRIPT_ID = script_versions.SCRIPT_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
                "on script_versions.SCRIPT_ID = versions.SCRIPT_ID and script_versions.SCRIPT_VRS_NB = versions.SCRIPT_VRS_NB " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
                "on script_versions.SCRIPT_ID = script_labels.SCRIPT_ID and script_versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " actions " +
                "on script_versions.SCRIPT_ID = actions.SCRIPT_ID and script_versions.SCRIPT_VRS_NB = actions.SCRIPT_VRS_NB " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameters " +
                "on script_versions.SCRIPT_ID = action_parameters.SCRIPT_ID and script_versions.SCRIPT_VRS_NB = action_parameters.SCRIPT_VRS_NB and actions.ACTION_ID = action_parameters.ACTION_ID;";
    }

    private String getBaseQuery(Pageable pageable, boolean onlyLatestVersions, List<ScriptFilter> scriptFilters) {
        return "select distinct scripts.SCRIPT_ID, scripts.SCRIPT_NM, versions.SCRIPT_VRS_NB " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " scripts " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
                "on scripts.SCRIPT_ID = versions.SCRIPT_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
                "on scripts.SCRIPT_ID = script_labels.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB " +
                getWhereClause(scriptFilters, onlyLatestVersions) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    private String getWhereClause(List<ScriptFilter> scriptFilters, boolean onlyLatestVersions) {
        String filterStatements = scriptFilters.stream().map(scriptFilter -> {
                    if (scriptFilter.getScriptFilterOption().equals(ScriptFilterOption.NAME)) {
                        return " scripts.SCRIPT_NM " + (scriptFilter.isExactMatch() ? "=" : "LIKE") + " '" + (scriptFilter.isExactMatch() ? "" : "%") + scriptFilter.getValue() + (scriptFilter.isExactMatch() ? "" : "%") + "' ";
                    } else if (scriptFilter.getScriptFilterOption().equals(ScriptFilterOption.VERSION)) {
                        return " versions.SCRIPT_VRS_NB = " + Long.parseLong(scriptFilter.getValue()) + " ";
                    } else if (scriptFilter.getScriptFilterOption().equals(ScriptFilterOption.LABEL)) {
                        return "script_labels.NAME = '" + scriptFilter.getValue().split(":")[0] +
                                "' and script_labels.VALUE " + (scriptFilter.isExactMatch() ? "=" : "LIKE") + " '" + (scriptFilter.isExactMatch() ? "" : "%") + scriptFilter.getValue().split(":")[1] + (scriptFilter.isExactMatch() ? "" : "%") + "'";
                    } else {
                        return null;
                    }
                }
        )
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));
        if (!onlyLatestVersions && filterStatements.isEmpty()) {
            return "";
        }
        String onlyLatestVersionStatement = (onlyLatestVersions ? " (versions.SCRIPT_ID, versions.SCRIPT_VRS_NB) in (select scripts.SCRIPT_ID, max(script_versions.SCRIPT_VRS_NB) SCRIPT_VRS_NB " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " scripts " +
                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_versions " +
                "on scripts.SCRIPT_ID = script_versions.SCRIPT_ID group by scripts.SCRIPT_ID) " : "");
        return " WHERE " + filterStatements + (!filterStatements.isEmpty() && onlyLatestVersions ? " and " : "") + onlyLatestVersionStatement;
    }

    private String getLimitAndOffsetClause(Pageable pageable) {
        return pageable.isUnpaged() ? " " : " limit " + pageable.getPageSize() + " offset " + pageable.getOffset() + " ";
    }

    private String getExpansionsWhereClause() {
        return "";
    }

    private String getExpansionsJoinClause() {
        return "";
    }

//    /**
//     * This method return the number of row of the pivot table which is an inner join between scripts and scriptVersions table.
//     * It is design to get the number of ScriptDto that can be create based on the given parameter.
//     * It helps creating the pagination
//     *
//     * @param scriptName          - Name of the script
//     * @param scriptVersion       - Version of the script
//     * @param isLatestVersionOnly - If true, only count last version of a script
//     * @return Number of script that can be created with those current parameter
//     */
//    public long getRowSize(String scriptName, Long scriptVersion, boolean isLatestVersionOnly) {
//        try {
//            String query = "Select COUNT(*) count FROM " + getScriptAndScriptVRSTable(Pageable.unpaged(), scriptName, scriptVersion, isLatestVersionOnly) + ";";
//            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
//            cachedRowSet.next();
//            return cachedRowSet.getLong("count");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public Page<ScriptDto> getAll(Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters) {
        try {
            Map<ScriptKey, ScriptDtoBuilder> scriptDtoBuilders = new LinkedHashMap<>();
            String query = getFetchAllQuery(pageable, isLatestVersionOnly, scriptFilters, expansions);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow2(cachedRowSet, scriptDtoBuilders);
            }
            List<ScriptDto> scriptDtoList = scriptDtoBuilders.values().stream()
                    .map(ScriptDtoBuilder::build)
                    .collect(Collectors.toList());
            return new PageImpl<>(scriptDtoList, pageable, getRowSize(isLatestVersionOnly, scriptFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getRowSize(boolean onlyLatestVersions, List<ScriptFilter> scriptFilters) throws SQLException {
        String query = "select count(*) as row_count from (select distinct scripts.SCRIPT_ID, versions.SCRIPT_VRS_NB " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " scripts " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
                "on scripts.SCRIPT_ID = versions.SCRIPT_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
                "on scripts.SCRIPT_ID = script_labels.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB " +
                getWhereClause(scriptFilters, onlyLatestVersions) +
                ");";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }

    private void mapRow2(CachedRowSet cachedRowSet, Map<ScriptKey, ScriptDtoBuilder> scriptDtoBuilders) throws SQLException {
        ScriptKey scriptKey = new ScriptKey(cachedRowSet.getString("SCRIPT_NM"), cachedRowSet.getLong("SCRIPT_VRS_NB"));
        ScriptDtoBuilder scriptBuilderDto = scriptDtoBuilders.get(scriptKey);
        if (scriptBuilderDto == null) {
            scriptBuilderDto = mapScriptDto2(cachedRowSet);
            scriptDtoBuilders.put(scriptKey, scriptBuilderDto);
        }

        String labelId = cachedRowSet.getString("LABEL_ID");
        if (labelId != null) {
            if (scriptBuilderDto.getLabels().get(labelId) == null) {
                scriptBuilderDto.getLabels().put(labelId, new ScriptLabelDto(cachedRowSet.getString("LABEL_NAME"), cachedRowSet.getString("LABEL_VALUE")));
            } else {
                // skip
            }
        }
        Long actionNumber = cachedRowSet.getLong("ACTION_NB");
        if (actionNumber != null) {
            ActionDtoBuilder actionDtoBuilder = scriptBuilderDto.getActions().get(actionNumber);
            if (actionDtoBuilder == null) {
                actionDtoBuilder = new ActionDtoBuilder(
                        cachedRowSet.getLong("ACTION_NB"),
                        cachedRowSet.getString("ACTION_NM"),
                        cachedRowSet.getString("ACTION_TYP_NM"),
                        cachedRowSet.getString("ACTION_DSC"),
                        cachedRowSet.getString("COMP_NM"),
                        cachedRowSet.getString("CONDITION_VAL"),
                        cachedRowSet.getString("ITERATION_VAL"),
                        cachedRowSet.getString("EXP_ERR_FL").equalsIgnoreCase("y"),
                        cachedRowSet.getString("STOP_ERR_FL").equalsIgnoreCase("y"),
                        cachedRowSet.getInt("RETRIES_VAL"),
                        new HashMap<>());
                scriptBuilderDto.getActions().put(actionNumber, actionDtoBuilder);
            } else {
                // skip
            }

            String actionParameterName = cachedRowSet.getString("ACTION_PAR_NM");
            if (actionParameterName != null) {
                ActionParameterDto actionParameterDto = actionDtoBuilder.getParameters().get(actionParameterName);
                if (actionParameterDto == null) {
                    actionDtoBuilder.getParameters().put(actionParameterName, new ActionParameterDto(
                            cachedRowSet.getString("ACTION_PAR_NM"), cachedRowSet.getString("ACTION_PAR_VAL")
                    ));
                }
            }
        }
    }


    @AllArgsConstructor
    @Getter
    private class ScriptDtoBuilder {
        private String name;
        private String description;
        private ScriptVersionDto version;
        private Map<Long, ActionDtoBuilder> actions;
        private Map<String, ScriptLabelDto> labels;

        public ScriptDto build() {
            return new ScriptDto(name, description, version, new HashSet<>(),
                    actions.values().stream().map(ActionDtoBuilder::build).collect(Collectors.toSet()),
                    new HashSet<>(labels.values()),
                    null,
                    null);
        }
    }

    @AllArgsConstructor
    @Getter
    private class ActionDtoBuilder {
        private long number;
        private String name;
        private String type;
        private String description;
        private String component;
        private String condition;
        private String iteration;
        private boolean errorExpected;
        private boolean errorStop;
        private int retries;
        private Map<String, ActionParameterDto> parameters;

        public ActionDto build() {
            return new ActionDto(number, name, type, description,
                    component, condition, iteration, errorExpected, errorStop, retries,
                    new HashSet<>(parameters.values()));
        }

    }

    private ScriptDtoBuilder mapScriptDto2(CachedRowSet cachedRowSet) throws SQLException {
        return new ScriptDtoBuilder(cachedRowSet.getString("SCRIPT_NM"), cachedRowSet.getString("SCRIPT_DSC"),
                new ScriptVersionDto(cachedRowSet.getLong("SCRIPT_VRS_NB"),
                        cachedRowSet.getString("SCRIPT_VRS_DSC")),
                new HashMap<>(),
                new HashMap<>());
    }

    @Override
    public Page<ScriptDto> getByName(Pageable pageable, String name, List<String> expansions, boolean isLatestVersionOnly) {
        try {
            Map<ScriptKey, ScriptDtoBuilder> scriptDtoBuilders = new LinkedHashMap<>();
            List<ScriptFilter> scriptFilters = Stream.of(new ScriptFilter(ScriptFilterOption.NAME, name, true)).collect(Collectors.toList());
            String query = getFetchAllQuery(pageable, isLatestVersionOnly, scriptFilters, expansions);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow2(cachedRowSet, scriptDtoBuilders);
            }
            return new PageImpl<>(
                    scriptDtoBuilders.values().stream()
                            .map(ScriptDtoBuilder::build)
                            .collect(Collectors.toList()),
                    pageable,
                    getRowSize(isLatestVersionOnly, scriptFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions) {
        try {
            Map<ScriptKey, ScriptDtoBuilder> scriptDtoBuilders = new HashMap<>();
            List<ScriptFilter> scriptFilters = Stream.of(new ScriptFilter(ScriptFilterOption.NAME, name, true),
                    new ScriptFilter(ScriptFilterOption.VERSION, Long.toString(version), true))
                    .collect(Collectors.toList());
            String query = getFetchAllQuery(Pageable.unpaged(), false, scriptFilters, expansions);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow2(cachedRowSet, scriptDtoBuilders);
            }
            if (scriptDtoBuilders.values().size() > 1) {
                log.warn("found multiple script for script " + name + "-" + version);
            }
            return scriptDtoBuilders.values().stream().findFirst().map(ScriptDtoBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    private void mapRow(CachedRowSet cachedRowSet, Map<ScriptKey, ScriptDto> scriptDtos, Map<ActionKey, ActionDto> actionDtos, List<String> expansions) throws SQLException {
//        ScriptKey scriptKey = new ScriptKey(cachedRowSet.getString("SCRIPT_NM"), cachedRowSet.getLong("SCRIPT_VRS_NB"));
//        ScriptDto scriptDto = scriptDtos.get(scriptKey);
//        if (scriptDto == null) {
//            scriptDto = mapScriptDto(cachedRowSet);
//            scriptDtos.put(scriptKey, scriptDto);
//        }
//        int infoType = cachedRowSet.getInt("INFO_TYPE");
//        if (expansions.contains("execution")) {
//            scriptDto.setScriptExecutionInformation(new ScriptExecutionInformation());
//        }
//        if (infoType == 0) {
//            scriptDto.addScriptLabelDto(mapScriptLabelDto(cachedRowSet));
//        } else if (infoType == 1) {
//            // action parameter
//            ActionKey actionKey = new ActionKey(scriptKey, cachedRowSet.getString("ACTION_ID"));
//            ActionDto actionDto = actionDtos.get(actionKey);
//            if (actionDto == null) {
//                actionDto = mapActionDto(cachedRowSet);
//                actionDtos.put(actionKey, actionDto);
//                scriptDto.addActionDto(actionDto);
//            }
//            actionDto.addActionParameterDto(mapActionParameterDto(cachedRowSet));
//        } else if (infoType == 2) {
//            ScriptExecutionInformation scriptExecutionInformation = scriptDto.getScriptExecutionInformation();
//            if (scriptExecutionInformation == null) {
//                scriptExecutionInformation = new ScriptExecutionInformation();
//                scriptDto.setScriptExecutionInformation(scriptExecutionInformation);
//            }
//            scriptExecutionInformation.addScriptExecutionDto(mapScriptExecutionDto(cachedRowSet));
//        } else if (infoType == 3) {
//            ScriptSchedulingInformation scriptSchedulingInformation = scriptDto.getScriptSchedulingInformation();
//            if (scriptSchedulingInformation == null) {
//                scriptSchedulingInformation = new ScriptSchedulingInformation();
//                scriptDto.setScriptSchedulingInformation(scriptSchedulingInformation);
//            }
//        }
//    }

//    private ScriptDto mapScriptDto(CachedRowSet cachedRowSet) throws SQLException {
//        ScriptDto scriptDto = new ScriptDto();
//        scriptDto.setName(cachedRowSet.getString("SCRIPT_NM"));
//        scriptDto.setDescription(cachedRowSet.getString("SCRIPT_DSC"));
//        scriptDto.setVersion(new ScriptVersionDto(
//                cachedRowSet.getLong("SCRIPT_VRS_NB"),
//                cachedRowSet.getString("SCRIPT_VRS_DSC")
//        ));
//        return scriptDto;
//    }
//
//    private ScriptLabelDto mapScriptLabelDto(CachedRowSet cachedRowSet) throws SQLException {
//        String labelName = cachedRowSet.getString("LABEL_NAME");
//        String labelValue = cachedRowSet.getString("LABEL_VALUE");
//        return new ScriptLabelDto(labelName, labelValue);
//    }
//
//    private ActionDto mapActionDto(CachedRowSet cachedRowSet) throws SQLException {
//        ActionDto actionDto = new ActionDto();
//        actionDto.setNumber(cachedRowSet.getLong("ACTION_NB"));
//        actionDto.setName(cachedRowSet.getString("ACTION_NM"));
//        actionDto.setDescription(cachedRowSet.getString("ACTION_DSC"));
//        actionDto.setType(cachedRowSet.getString("ACTION_TYP_NM"));
//        actionDto.setCondition(cachedRowSet.getString("CONDITION_VAL"));
//        actionDto.setErrorExpected(cachedRowSet.getString("EXP_ERR_FL").equalsIgnoreCase("y"));
//        actionDto.setErrorStop(cachedRowSet.getString("STOP_ERR_FL").equalsIgnoreCase("y"));
//        return actionDto;
//    }
//
//    private ActionParameterDto mapActionParameterDto(CachedRowSet cachedRowSet) throws SQLException {
//        return new ActionParameterDto(
//                cachedRowSet.getString("ACTION_PAR_NM"),
//                cachedRowSet.getString("ACTION_PAR_VAL")
//        );
//    }

    private ScriptExecutionDto mapScriptExecutionDto(CachedRowSet cachedRowSet) throws SQLException {
        return new ScriptExecutionDto(cachedRowSet.getString("RUN_ID"),
                cachedRowSet.getString("ENV_NM"),
                ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))
        );
    }

//    /**
//     * This method return the query adapted to the needs.
//     *
//     * @param pageable            - The pageable object containing the required pagination information. If null, it doesn't paginate.
//     * @param scriptName          - Name of the script. If null, it doesn't filter.
//     * @param scriptVersion       - Version of the script. If null, it can returns several version. Doesn't apply if isLatestVersionOnly is true.
//     * @param isLatestVersionOnly - boolean: if true: returns only latest the version of scripts.
//     * @param expansions          - Array containing String naming the required expansions
//     * @return Query adapted to the provided parameters
//     */
//    private String getQuery(Pageable pageable, String scriptName, Long scriptVersion, boolean isLatestVersionOnly, List<String> expansions) {
//        return "Select " +
//                "scriptAndScriptVRS.SCRIPT_ID, scriptAndScriptVRS.SCRIPT_NM, scriptAndScriptVRS.SCRIPT_DSC, " +
//                "scriptAndScriptVRS.SCRIPT_VRS_NB, scriptAndScriptVRS.SCRIPT_VRS_DSC, 0 INFO_TYPE, " +
//                "script_label.NAME LABEL_NAME, script_label.VALUE LABEL_VALUE, " +
//                "null ACTION_ID, null ACTION_NM, null ACTION_NB, null ACTION_DSC, null ACTION_TYP_NM, " +
//                "null CONDITION_VAL, null EXP_ERR_FL, null STOP_ERR_FL, null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
//                "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
//                "FROM " +
//
//                // getWhereStatement included for scriptName, scriptVersion and latestVersion
//                getScriptAndScriptVRSTable(pageable, scriptName, scriptVersion, isLatestVersionOnly) + " scriptAndScriptVRS " +
//
//                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_label " +
//                "on scriptAndScriptVRS.SCRIPT_ID = script_label.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = script_label.SCRIPT_VRS_NB " +
//                "union all " +
//                "Select " + "scriptAndScriptVRS.SCRIPT_ID, scriptAndScriptVRS.SCRIPT_NM, scriptAndScriptVRS.SCRIPT_DSC, " +
//                "scriptAndScriptVRS.SCRIPT_VRS_NB, scriptAndScriptVRS.SCRIPT_VRS_DSC, 1 INFO_TYPE, " +
//                "null LABEL_NAME, null LABEL_VALUE, " +
//                "action.ACTION_ID, action.ACTION_NM, action.ACTION_NB, action.ACTION_DSC, action.ACTION_TYP_NM, " +
//                "action.CONDITION_VAL, action.EXP_ERR_FL, action.STOP_ERR_FL, " +
//                "action_parameter.ACTION_PAR_NM, action_parameter.ACTION_PAR_VAL, " +
//                "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
//                "FROM " +
//
//                // getWhereStatement included for scriptName, scriptVersion and latestVersion
//                getScriptAndScriptVRSTable(pageable, scriptName, scriptVersion, isLatestVersionOnly) + " scriptAndScriptVRS " +
//
//                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " action " +
//                "on scriptAndScriptVRS.SCRIPT_ID = action.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = action.SCRIPT_VRS_NB " +
//                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameter " +
//                "on scriptAndScriptVRS.SCRIPT_ID = action_parameter.SCRIPT_ID and scriptAndScriptVRS.SCRIPT_VRS_NB = action_parameter.SCRIPT_VRS_NB and action.ACTION_ID = action_parameter.ACTION_ID" +
//
//                // getWhereStatement included for scriptName, scriptVersion and latestVersion
//                getExecutionExpansionUnion(pageable, scriptName, scriptVersion, expansions, isLatestVersionOnly) +
//
//                ";";
//    }
//
//    /**
//     * This method helps to paginate the query by limiting directly the pivot table (scripts inner join scriptVersions)
//     * which is the base of the query.
//     *
//     * @param pageable            - The pageable object containing the required pagination information. If null, it doesn't paginate.
//     * @param scriptName          - Name of the script. If null, it doesn't filter.
//     * @param scriptVersion       - Version of the script. If null, it can returns several version. Doesn't apply if isLatestVersionOnly is true.
//     * @param isLatestVersionOnly - boolean: if true: returns only latest the version of scripts.
//     * @return return the query to of the pivot table according to the parameter.
//     */
//    private String getScriptAndScriptVRSTable(@NotNull Pageable pageable, String scriptName, Long scriptVersion, boolean isLatestVersionOnly) {
//        String limitAndOffset = pageable.isUnpaged() ? " " : " limit " + pageable.getPageSize() + " offset " + pageable.getOffset() + " ";
//        return (" (" +
//                "SELECT " +
//                "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, " +
//                "script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC " +
//                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
//                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
//                "on script.SCRIPT_ID = script_version.SCRIPT_ID " +
//                getWhereClause(scriptName, scriptVersion, isLatestVersionOnly).orElse(" ") +
//                getOrderByClause(pageable) +
//                limitAndOffset +
//                ") ");
//    }

    /**
     * This method provide an Order by statement to order the ScriptAndScriptVersionTable
     *
     * @param pageable - pageable object containing or not the order object
     * @return a String containing the ORDER BY statement
     */
    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ";
        List<String> sorting = pageable.getSort().stream().map(order -> {
            // add further sort on the ScriptAndScriptVersionTable here
            if (order.getProperty().equalsIgnoreCase("NAME")) {
                return "scripts.SCRIPT_NM" + " " + order.getDirection();
            } else if (order.getProperty().equalsIgnoreCase("VERSION")) {
                return "versions.SCRIPT_VRS_NB" + " " + order.getDirection();
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

    /**
     * This method provide a where statement depending of the arguments passed.
     *
     * @param scriptName          - if filled, add a filter on the name of the script. If null, doesn't filter on the name.
     * @param scriptVersion       - if filled, add a filter on the version of the script. If null, doesn't filter on the version.
     * @param isLatestVersionOnly - if true, filter to return only the last version of the script. If true, the scriptVersion doesn't apply.
     * @return a String containing the where clause if argument are provided or a space if all args are null.
     */
//    private Optional<String> getWhereClause(String scriptName, Long scriptVersion, boolean isLatestVersionOnly) {
//        List<String> conditions = new ArrayList<>();
//        if (scriptName != null) {
//            conditions.add(" script.SCRIPT_NM=" + SQLTools.GetStringForSQL(scriptName) + " ");
//        }
//        if (isLatestVersionOnly) {
//            conditions.add(
//                    " (script.SCRIPT_ID, script_version.SCRIPT_VRS_NB) in (select script.SCRIPT_ID, max(script_version.SCRIPT_VRS_NB) SCRIPT_VRS_NB " +
//                            "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
//                            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
//                            " script_version on script.SCRIPT_ID = script_version.SCRIPT_ID group by script.SCRIPT_ID) ");
//        } else if (scriptVersion != null) {
//            conditions.add(" script_version.SCRIPT_VRS_NB=" + SQLTools.GetStringForSQL(scriptVersion) + " ");
//        }
//        if (conditions.isEmpty()) return Optional.empty();
//        return Optional.of(" where " + String.join(" and ", conditions) + " ");
//    }
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
