package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
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
<<<<<<< HEAD
=======
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
>>>>>>> master
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Repository
<<<<<<< HEAD
public class ScriptDtoRepository implements IScriptDtoRepository {
=======
public class ScriptDtoRepository extends PaginatedRepository implements IScriptDtoRepository {
>>>>>>> master

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final FilterService filterService;

    @Autowired
    public ScriptDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration, FilterService filterService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.filterService = filterService;
    }

<<<<<<< HEAD
    @Override
    public List<ScriptDto> getAll(List<String> expansions, boolean isLatestVersionOnly) {
        try {
            Map<ScriptKey, ScriptDto> scriptDtos = new HashMap<>();
            Map<ActionKey, ActionDto> actionDtos = new HashMap<>();
            String query = "Select " +
                    "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, script.SCRIPT_TYP_NM, " +
                    "script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC, 0 INFO_TYPE, " +
                    "script_label.NAME LABEL_NAME, script_label.VALUE LABEL_VALUE, " +
                    "null ACTION_ID, null ACTION_NM, null ACTION_NB, null ACTION_DSC, null ACTION_TYP_NM, " +
                    "null CONDITION_VAL, null EXP_ERR_FL, null STOP_ERR_FL, null ACTION_PAR_NM, null ACTION_PAR_VAL, " +
                    "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                    "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                    "on script.SCRIPT_ID=script_version.SCRIPT_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_label " +
                    "on script.SCRIPT_ID = script_label.SCRIPT_ID and script_version.SCRIPT_VRS_NB = script_label.SCRIPT_VRS_NB " +
                    getWhereClause(null, null, isLatestVersionOnly).orElse("") +
                    "union all " +
                    "Select " + "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, script.SCRIPT_TYP_NM, " +
                    "script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC, 1 INFO_TYPE, " +
                    "null LABEL_NAME, null LABEL_VALUE, " +
                    "action.ACTION_ID, action.ACTION_NM, action.ACTION_NB, action.ACTION_DSC, action.ACTION_TYP_NM, " +
                    "action.CONDITION_VAL, action.EXP_ERR_FL, action.STOP_ERR_FL, " +
                    "action_parameter.ACTION_PAR_NM, action_parameter.ACTION_PAR_VAL, " +
                    "null RUN_ID, null PRC_ID, null ENV_NM, null ST_NM, null STRT_TMS, null END_TMS " +
                    "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                    "on script.SCRIPT_ID=script_version.SCRIPT_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " action " +
                    "on script.SCRIPT_ID = action.SCRIPT_ID and script_version.SCRIPT_VRS_NB = action.SCRIPT_VRS_NB " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameter " +
                    "on script.SCRIPT_ID = action_parameter.SCRIPT_ID and script_version.SCRIPT_VRS_NB = action_parameter.SCRIPT_VRS_NB and action.ACTION_ID = action_parameter.ACTION_ID" +
                    getWhereClause(null, null, isLatestVersionOnly).orElse("") +
                    (expansions != null && expansions.contains("execution") ? getExecutionExpansionUnion(null, null, isLatestVersionOnly) : "") +
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
=======
    private String getFetchAllQuery(Authentication authentication, Pageable pageable, boolean onlyLatestVersions, List<ScriptFilter> scriptFilters, List<String> expansions) {
        return "select script_designs.SCRIPT_ID, script_designs.SECURITY_GROUP_NAME, script_designs.SCRIPT_NM, script_designs.SCRIPT_DSC, " +
                "versions.SCRIPT_VRS_NB, versions.SCRIPT_VRS_DSC, " +
                "script_labels.ID as LABEL_ID, script_labels.NAME as LABEL_NAME, script_labels.VALUE as LABEL_VALUE, " +
                "actions.ACTION_ID, actions.ACTION_NB, actions.ACTION_DSC, actions.ACTION_NM, actions.ACTION_TYP_NM, actions.COMP_NM, actions.CONDITION_VAL, actions.ITERATION_VAL, actions.EXP_ERR_FL, actions.RETRIES_VAL, actions.STOP_ERR_FL, " +
                "action_parameters.ACTION_PAR_NM, action_parameters.ACTION_PAR_VAL " +
                "from (" + getBaseQuery(authentication, pageable, onlyLatestVersions, scriptFilters) + ") base_scripts " + //base table
                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script_designs " +
                "on base_scripts.SCRIPT_ID = script_designs.SCRIPT_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
                "on base_scripts.SCRIPT_ID = versions.SCRIPT_ID and base_scripts.SCRIPT_VRS_NB = versions.SCRIPT_VRS_NB " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
                "on base_scripts.SCRIPT_ID = script_labels.SCRIPT_ID and base_scripts.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " actions " +
                "on base_scripts.SCRIPT_ID = actions.SCRIPT_ID and base_scripts.SCRIPT_VRS_NB = actions.SCRIPT_VRS_NB " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameters " +
                "on base_scripts.SCRIPT_ID = action_parameters.SCRIPT_ID and base_scripts.SCRIPT_VRS_NB = action_parameters.SCRIPT_VRS_NB and actions.ACTION_ID = action_parameters.ACTION_ID" +
                getOrderByClause(pageable) +
                ";";
    }

    /**
     * This method will return a subquery only returning unique script ids according to the provided arguments
     *
     * @param authentication
     * @param pageable           :           Pageable object containing pagination and sorting information
     * @param onlyLatestVersions : boolean stating whether all or only latest version of a script should be retreived
     * @param scriptFilters      :      List of ScriptFilters describing which results should be filtered from the query resultset
     * @return query
     */
    private String getBaseQuery(Authentication authentication, Pageable pageable, boolean onlyLatestVersions, List<ScriptFilter> scriptFilters) {
        return "select distinct script_designs.SCRIPT_ID, script_designs.SECURITY_GROUP_NAME, script_designs.SCRIPT_NM, versions.SCRIPT_VRS_NB " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script_designs " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
                "on script_designs.SCRIPT_ID = versions.SCRIPT_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
                "on script_designs.SCRIPT_ID = script_labels.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB " +
                getWhereClause(authentication, scriptFilters, onlyLatestVersions) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    private String getWhereClause(Authentication authentication, List<ScriptFilter> scriptFilters, boolean onlyLatestVersions) {
        String filterStatements = scriptFilters.stream()
                .map(scriptFilter -> {
                    if (scriptFilter.getFilterOption().equals(ScriptFilterOption.NAME)) {
                        return filterService.getStringCondition("script_designs.SCRIPT_NM", scriptFilter);
                    } else if (scriptFilter.getFilterOption().equals(ScriptFilterOption.VERSION)) {
                        return filterService.getLongCondition("versions.SCRIPT_VRS_NB", scriptFilter);
                    } else if (scriptFilter.getFilterOption().equals(ScriptFilterOption.LABEL)) {
                        return filterService.getKeyValueCondition("script_labels.NAME", "script_labels.VALUE", scriptFilter);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));

        if (authentication != null) {
            Set<String> securityGroups = authentication.getAuthorities().stream()
                    .filter(authority -> authority instanceof IESIGrantedAuthority)
                    .map(authority -> (IESIGrantedAuthority) authority)
                    .map(IESIGrantedAuthority::getSecurityGroupName)
                    .map(SQLTools::getStringForSQL).collect(Collectors.toSet());
            filterStatements = filterStatements +
                    (filterStatements.isEmpty() ? "" : " and ") +
                    " script_designs.SECURITY_GROUP_NAME IN (" + String.join(", ", securityGroups) + ") ";
        }
        if (onlyLatestVersions) {
            filterStatements = (filterStatements.isEmpty() ? "" : filterStatements + " and ") +
                    " (versions.SCRIPT_ID, versions.SCRIPT_VRS_NB) in (select scripts.SCRIPT_ID, max(script_versions.SCRIPT_VRS_NB) SCRIPT_VRS_NB " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " scripts " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_versions " +
                    "on scripts.SCRIPT_ID = script_versions.SCRIPT_ID group by scripts.SCRIPT_ID) ";
        }
        return filterStatements.isEmpty() ? "" : " WHERE " + filterStatements;
    }

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
                return "script_designs.SCRIPT_NM" + " " + order.getDirection();
            } else if (order.getProperty().equalsIgnoreCase("VERSION")) {
                return "versions.SCRIPT_VRS_NB" + " " + order.getDirection();
            } else {
                return null;
>>>>>>> master
            }
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sorting.isEmpty()) {
            return "";
        }
        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    private String getExpansionsWhereClause() {
        return "";
    }

    private String getExpansionsJoinClause() {
        return "";
    }

    @Override
<<<<<<< HEAD
    public Optional<ScriptDto> getByNameAndVersion(String name, long version, List<String> expansions) {
=======
    public Page<ScriptDto> getAll(Authentication authentication, Pageable pageable, List<String> expansions, boolean isLatestVersionOnly, List<ScriptFilter> scriptFilters) {
>>>>>>> master
        try {
            Map<ScriptKey, ScriptDtoBuilder> scriptDtoBuilders = new LinkedHashMap<>();
            String query = getFetchAllQuery(authentication, pageable, isLatestVersionOnly, scriptFilters, expansions);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
<<<<<<< HEAD
                mapRow(cachedRowSet, scriptDtos, actionDtos, expansions);
=======
                mapRow(cachedRowSet, scriptDtoBuilders);
>>>>>>> master
            }
            List<ScriptDto> scriptDtoList = scriptDtoBuilders.values().stream()
                    .map(ScriptDtoBuilder::build)
                    .collect(Collectors.toList());
            return new PageImpl<>(scriptDtoList, pageable, getRowSize(authentication, isLatestVersionOnly, scriptFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getRowSize(Authentication authentication, boolean onlyLatestVersions, List<ScriptFilter> scriptFilters) throws SQLException {
        String query = "select count(*) as row_count from (select distinct script_designs.SCRIPT_ID, versions.SCRIPT_VRS_NB " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script_designs " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
                "on script_designs.SCRIPT_ID = versions.SCRIPT_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
                "on script_designs.SCRIPT_ID = script_labels.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB " +
                getWhereClause(authentication, scriptFilters, onlyLatestVersions) +
                ");";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<ScriptKey, ScriptDtoBuilder> scriptDtoBuilders) throws SQLException {
        ScriptKey scriptKey = new ScriptKey(cachedRowSet.getString("SCRIPT_NM"), cachedRowSet.getLong("SCRIPT_VRS_NB"));
        ScriptDtoBuilder scriptBuilderDto = scriptDtoBuilders.get(scriptKey);
        if (scriptBuilderDto == null) {
            scriptBuilderDto = mapScriptDto2(cachedRowSet);
            scriptDtoBuilders.put(scriptKey, scriptBuilderDto);
        }
        mapScriptLabel(cachedRowSet, scriptBuilderDto);
        mapAction(cachedRowSet, scriptBuilderDto);

    }

    private void mapAction(CachedRowSet cachedRowSet, ScriptDtoBuilder scriptBuilderDto) throws SQLException {
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
            }
            mapActionParameter(cachedRowSet, actionDtoBuilder);
        }
    }

<<<<<<< HEAD
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
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                "on script.SCRIPT_ID=script_version.SCRIPT_ID " +
                "inner join (select * from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() +
                " where (SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, STRT_TMS) " +
                "in (SELECT SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, MAX(STRT_TMS) " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + " " +
                "group by SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM)) script_result " +
                "on script.SCRIPT_ID = script_result.SCRIPT_ID and script_version.SCRIPT_VRS_NB = script_result.SCRIPT_VRS_NB" +
                getWhereClause(scriptName, scriptVersion, isLatestVersion).orElse("") + "";
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
=======
    private void mapActionParameter(CachedRowSet cachedRowSet, ActionDtoBuilder actionDtoBuilder) throws SQLException {
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

    private void mapScriptLabel(CachedRowSet cachedRowSet, ScriptDtoBuilder scriptBuilderDto) throws SQLException {
        String labelId = cachedRowSet.getString("LABEL_ID");
        if (labelId != null && scriptBuilderDto.getLabels().get(labelId) == null) {
            scriptBuilderDto.getLabels().put(labelId, new ScriptLabelDto(cachedRowSet.getString("LABEL_NAME"), cachedRowSet.getString("LABEL_VALUE")));
        }
    }


    @AllArgsConstructor
    @Getter
    private class ScriptDtoBuilder {
        private final String name;
        private final String securityGroupName;
        private final String description;
        private final ScriptVersionDto version;
        private final Map<Long, ActionDtoBuilder> actions;
        private final Map<String, ScriptLabelDto> labels;

        public ScriptDto build() {
            return new ScriptDto(name,
                    securityGroupName,
                    description,
                    version,
                    new HashSet<>(),
                    actions.values().stream().map(ActionDtoBuilder::build).collect(Collectors.toSet()),
                    new HashSet<>(labels.values()),
                    null,
                    null);
>>>>>>> master
        }
    }

<<<<<<< HEAD
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
=======
    @AllArgsConstructor
    @Getter
    private class ActionDtoBuilder {
        private final long number;
        private final String name;
        private final String type;
        private final String description;
        private final String component;
        private final String condition;
        private final String iteration;
        private final boolean errorExpected;
        private final boolean errorStop;
        private final int retries;
        private final Map<String, ActionParameterDto> parameters;

        public ActionDto build() {
            return new ActionDto(number, name, type, description,
                    component, condition, iteration, errorExpected, errorStop, retries,
                    new HashSet<>(parameters.values()));
        }

    }

    private ScriptDtoBuilder mapScriptDto2(CachedRowSet cachedRowSet) throws SQLException {
        return new ScriptDtoBuilder(cachedRowSet.getString("SCRIPT_NM"),
                cachedRowSet.getString("SECURITY_GROUP_NAME"),
                cachedRowSet.getString("SCRIPT_DSC"),
                new ScriptVersionDto(cachedRowSet.getLong("SCRIPT_VRS_NB"),
                        cachedRowSet.getString("SCRIPT_VRS_DSC")),
                new HashMap<>(),
                new HashMap<>());
    }

    @Override
    public Page<ScriptDto> getByName(Authentication authentication, Pageable pageable, String name, List<String> expansions, boolean isLatestVersionOnly) {
        try {
            Map<ScriptKey, ScriptDtoBuilder> scriptDtoBuilders = new LinkedHashMap<>();
            List<ScriptFilter> scriptFilters = Stream.of(new ScriptFilter(ScriptFilterOption.NAME, name, true)).collect(Collectors.toList());
            String query = getFetchAllQuery(authentication, pageable, isLatestVersionOnly, scriptFilters, expansions);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptDtoBuilders);
>>>>>>> master
            }
            return new PageImpl<>(
                    scriptDtoBuilders.values().stream()
                            .map(ScriptDtoBuilder::build)
                            .collect(Collectors.toList()),
                    pageable,
                    getRowSize(authentication, isLatestVersionOnly, scriptFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ScriptDto> getByNameAndVersion(Authentication authentication, String name, long version, List<String> expansions) {
        try {
            Map<ScriptKey, ScriptDtoBuilder> scriptDtoBuilders = new HashMap<>();
            List<ScriptFilter> scriptFilters = Stream.of(new ScriptFilter(ScriptFilterOption.NAME, name, true),
                    new ScriptFilter(ScriptFilterOption.VERSION, Long.toString(version), true))
                    .collect(Collectors.toList());
            String query = getFetchAllQuery(authentication, Pageable.unpaged(), false, scriptFilters, expansions);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptDtoBuilders);
            }
            if (scriptDtoBuilders.values().size() > 1) {
                log.warn("found multiple script for script " + name + "-" + version);
            }
            return scriptDtoBuilders.values().stream().findFirst().map(ScriptDtoBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ScriptExecutionDto mapScriptExecutionDto(CachedRowSet cachedRowSet) throws SQLException {
        return new ScriptExecutionDto(cachedRowSet.getString("RUN_ID"),
                cachedRowSet.getString("ENV_NM"),
                ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))
        );
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