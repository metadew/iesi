package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class ScriptVersionConfiguration extends Configuration<ScriptVersion, ScriptVersionKey> {

    private static ScriptVersionConfiguration INSTANCE;

    private static final String FETCH_ALL_QUERY = "select " +
            "script_designs.SCRIPT_ID, script_designs.SECURITY_GROUP_NAME, script_designs.SECURITY_GROUP_ID, script_designs.SCRIPT_NM, script_designs.SCRIPT_DSC, script_designs.deleted_at as script_deleted_at, " +
            "versions.SCRIPT_VRS_NB, versions.SCRIPT_VRS_DSC, versions.CREATED_BY, versions.CREATED_AT, versions.LAST_MODIFIED_BY, versions.LAST_MODIFIED_AT, versions.DELETED_AT, " +
            "script_labels.ID as LABEL_ID, script_labels.NAME as LABEL_NAME, script_labels.VALUE as LABEL_VALUE, " +
            "actions.ACTION_ID, actions.ACTION_NB, actions.ACTION_DSC, actions.ACTION_NM, actions.ACTION_TYP_NM, actions.COMP_NM, actions.CONDITION_VAL, actions.ITERATION_VAL, actions.EXP_ERR_FL, actions.RETRIES_VAL, actions.STOP_ERR_FL, " +
            "action_parameters.ACTION_PAR_NM, action_parameters.ACTION_PAR_VAL, " +
            "script_parameters.SCRIPT_PAR_NM,script_parameters.SCRIPT_PAR_VAL " +
            "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script_designs " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
            "on script_designs.SCRIPT_ID = versions.SCRIPT_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
            "on script_designs.SCRIPT_ID = script_labels.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB and versions.DELETED_AT = script_labels.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() + " script_parameters " +
            "on script_designs.SCRIPT_ID = script_parameters.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_parameters.SCRIPT_VRS_NB and versions.DELETED_AT = script_parameters.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " actions " +
            "on script_designs.SCRIPT_ID = actions.SCRIPT_ID and versions.SCRIPT_VRS_NB = actions.SCRIPT_VRS_NB and actions.DELETED_AT = versions.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameters " +
            "on actions.SCRIPT_ID = action_parameters.SCRIPT_ID and actions.SCRIPT_VRS_NB = action_parameters.SCRIPT_VRS_NB and actions.ACTION_ID = action_parameters.ACTION_ID and actions.DELETED_AT = action_parameters.DELETED_AT;";

    private static final String FETCH_ALL_ACTIVE_QUERY = "select " +
            "script_designs.SCRIPT_ID, script_designs.SECURITY_GROUP_NAME, script_designs.SECURITY_GROUP_ID, script_designs.SCRIPT_NM, script_designs.SCRIPT_DSC, script_designs.deleted_at as script_deleted_at, " +
            "versions.SCRIPT_VRS_NB, versions.SCRIPT_VRS_DSC, versions.CREATED_BY, versions.CREATED_AT, versions.LAST_MODIFIED_BY, versions.LAST_MODIFIED_AT, versions.DELETED_AT, " +
            "script_labels.ID as LABEL_ID, script_labels.NAME as LABEL_NAME, script_labels.VALUE as LABEL_VALUE, " +
            "actions.ACTION_ID, actions.ACTION_NB, actions.ACTION_DSC, actions.ACTION_NM, actions.ACTION_TYP_NM, actions.COMP_NM, actions.CONDITION_VAL, actions.ITERATION_VAL, actions.EXP_ERR_FL, actions.RETRIES_VAL, actions.STOP_ERR_FL, " +
            "action_parameters.ACTION_PAR_NM, action_parameters.ACTION_PAR_VAL, " +
            "script_parameters.SCRIPT_PAR_NM,script_parameters.SCRIPT_PAR_VAL " +
            "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script_designs " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
            "on script_designs.SCRIPT_ID = versions.SCRIPT_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
            "on script_designs.SCRIPT_ID = script_labels.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB and versions.DELETED_AT = script_labels.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() + " script_parameters " +
            "on script_designs.SCRIPT_ID = script_parameters.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_parameters.SCRIPT_VRS_NB and versions.DELETED_AT = script_parameters.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " actions " +
            "on script_designs.SCRIPT_ID = actions.SCRIPT_ID and versions.SCRIPT_VRS_NB = actions.SCRIPT_VRS_NB and actions.DELETED_AT = versions.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameters " +
            "on actions.SCRIPT_ID = action_parameters.SCRIPT_ID and actions.SCRIPT_VRS_NB = action_parameters.SCRIPT_VRS_NB and actions.ACTION_ID = action_parameters.ACTION_ID and actions.DELETED_AT = action_parameters.DELETED_AT" +
            " where versions.DELETED_AT = 'NA';";


    private static final String FETCH_BY_KEY_QUERY = "select " +
            "script_designs.SCRIPT_ID, script_designs.SECURITY_GROUP_NAME, script_designs.SECURITY_GROUP_ID, script_designs.SCRIPT_NM, script_designs.SCRIPT_DSC, script_designs.deleted_at as script_deleted_at, " +
            "versions.SCRIPT_VRS_NB, versions.SCRIPT_VRS_DSC, versions.CREATED_BY, versions.CREATED_AT, versions.LAST_MODIFIED_BY, versions.LAST_MODIFIED_AT, versions.DELETED_AT, " +
            "script_labels.ID as LABEL_ID, script_labels.NAME as LABEL_NAME, script_labels.VALUE as LABEL_VALUE, " +
            "actions.ACTION_ID, actions.ACTION_NB, actions.ACTION_DSC, actions.ACTION_NM, actions.ACTION_TYP_NM, actions.COMP_NM, actions.CONDITION_VAL, actions.ITERATION_VAL, actions.EXP_ERR_FL, actions.RETRIES_VAL, actions.STOP_ERR_FL, " +
            "action_parameters.ACTION_PAR_NM, action_parameters.ACTION_PAR_VAL, " +
            "script_parameters.SCRIPT_PAR_NM,script_parameters.SCRIPT_PAR_VAL " +
            "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script_designs " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
            "on script_designs.SCRIPT_ID = versions.SCRIPT_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
            "on script_designs.SCRIPT_ID = script_labels.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB and versions.DELETED_AT = script_labels.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() + " script_parameters " +
            "on script_designs.SCRIPT_ID = script_parameters.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_parameters.SCRIPT_VRS_NB and versions.DELETED_AT = script_parameters.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " actions " +
            "on script_designs.SCRIPT_ID = actions.SCRIPT_ID and versions.SCRIPT_VRS_NB = actions.SCRIPT_VRS_NB and actions.DELETED_AT = versions.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameters " +
            "on actions.SCRIPT_ID = action_parameters.SCRIPT_ID and actions.SCRIPT_VRS_NB = action_parameters.SCRIPT_VRS_NB and actions.ACTION_ID = action_parameters.ACTION_ID and actions.DELETED_AT = action_parameters.DELETED_AT" +
            " where script_designs.SCRIPT_ID = %s and versions.SCRIPT_VRS_NB = %s and versions.DELETED_AT = %s;";

    private static final String FETCH_BY_NAME_AND_VERSION_AND_ACTIVE_QUERY = "select " +
            "script_designs.SCRIPT_ID, script_designs.SECURITY_GROUP_NAME, script_designs.SECURITY_GROUP_ID, script_designs.SCRIPT_NM, script_designs.SCRIPT_DSC, script_designs.deleted_at as script_deleted_at, " +
            "versions.SCRIPT_VRS_NB, versions.SCRIPT_VRS_DSC, versions.CREATED_BY, versions.CREATED_AT, versions.LAST_MODIFIED_BY, versions.LAST_MODIFIED_AT, versions.DELETED_AT, " +
            "script_labels.ID as LABEL_ID, script_labels.NAME as LABEL_NAME, script_labels.VALUE as LABEL_VALUE, " +
            "actions.ACTION_ID, actions.ACTION_NB, actions.ACTION_DSC, actions.ACTION_NM, actions.ACTION_TYP_NM, actions.COMP_NM, actions.CONDITION_VAL, actions.ITERATION_VAL, actions.EXP_ERR_FL, actions.RETRIES_VAL, actions.STOP_ERR_FL, " +
            "action_parameters.ACTION_PAR_NM, action_parameters.ACTION_PAR_VAL, " +
            "script_parameters.SCRIPT_PAR_NM,script_parameters.SCRIPT_PAR_VAL " +
            "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script_designs " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
            "on script_designs.SCRIPT_ID = versions.SCRIPT_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
            "on script_designs.SCRIPT_ID = script_labels.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB and versions.DELETED_AT = script_labels.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() + " script_parameters " +
            "on script_designs.SCRIPT_ID = script_parameters.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_parameters.SCRIPT_VRS_NB and versions.DELETED_AT = script_parameters.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " actions " +
            "on script_designs.SCRIPT_ID = actions.SCRIPT_ID and versions.SCRIPT_VRS_NB = actions.SCRIPT_VRS_NB and actions.DELETED_AT = versions.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameters " +
            "on actions.SCRIPT_ID = action_parameters.SCRIPT_ID and actions.SCRIPT_VRS_NB = action_parameters.SCRIPT_VRS_NB and actions.ACTION_ID = action_parameters.ACTION_ID and actions.DELETED_AT = action_parameters.DELETED_AT" +
            " where script_designs.SCRIPT_NM = %s and versions.SCRIPT_VRS_NB = %s and versions.DELETED_AT = 'NA';";

    private static final String FETCH_BY_SCRIPT_KEY_QUERY = "select " +
            "script_designs.SCRIPT_ID, script_designs.SECURITY_GROUP_NAME, script_designs.SECURITY_GROUP_ID, script_designs.SCRIPT_NM, script_designs.SCRIPT_DSC, script_designs.deleted_at as script_deleted_at, " +
            "versions.SCRIPT_VRS_NB, versions.SCRIPT_VRS_DSC, versions.CREATED_BY, versions.CREATED_AT, versions.LAST_MODIFIED_BY, versions.LAST_MODIFIED_AT, versions.DELETED_AT, " +
            "script_labels.ID as LABEL_ID, script_labels.NAME as LABEL_NAME, script_labels.VALUE as LABEL_VALUE, " +
            "actions.ACTION_ID, actions.ACTION_NB, actions.ACTION_DSC, actions.ACTION_NM, actions.ACTION_TYP_NM, actions.COMP_NM, actions.CONDITION_VAL, actions.ITERATION_VAL, actions.EXP_ERR_FL, actions.RETRIES_VAL, actions.STOP_ERR_FL, " +
            "action_parameters.ACTION_PAR_NM, action_parameters.ACTION_PAR_VAL, " +
            "script_parameters.SCRIPT_PAR_NM,script_parameters.SCRIPT_PAR_VAL " +
            "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script_designs " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " versions " +
            "on script_designs.SCRIPT_ID = versions.SCRIPT_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() + " script_labels " +
            "on script_designs.SCRIPT_ID = script_labels.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_labels.SCRIPT_VRS_NB and versions.DELETED_AT = script_labels.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() + " script_parameters " +
            "on script_designs.SCRIPT_ID = script_parameters.SCRIPT_ID and versions.SCRIPT_VRS_NB = script_parameters.SCRIPT_VRS_NB and versions.DELETED_AT = script_parameters.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() + " actions " +
            "on script_designs.SCRIPT_ID = actions.SCRIPT_ID and versions.SCRIPT_VRS_NB = actions.SCRIPT_VRS_NB and actions.DELETED_AT = versions.DELETED_AT " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() + " action_parameters " +
            "on actions.SCRIPT_ID = action_parameters.SCRIPT_ID and actions.SCRIPT_VRS_NB = action_parameters.SCRIPT_VRS_NB and actions.ACTION_ID = action_parameters.ACTION_ID and actions.DELETED_AT = action_parameters.DELETED_AT" +
            " where script_designs.SCRIPT_ID = %s;";

    private static final String COUNT_ACTIVE_QUERY = "SELECT COUNT(DISTINCT SCRIPT_VRS_NB) AS total_versions FROM " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " WHERE SCRIPT_ID = %s AND DELETED_AT = 'NA' ;";

    private static final String COUNT_QUERY = "SELECT COUNT(DISTINCT SCRIPT_VRS_NB) AS total_versions FROM " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " WHERE SCRIPT_ID = %s ;";

    private static final String SCRIPT_SOFT_DELETE_BY_ID_QUERY = "UPDATE " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " SET " +
            " DELETED_AT = %s " +
            " WHERE SCRIPT_ID = %s;";

    private static final String SCRIPT_DELETE_BY_ID_QUERY = "DELETE FROM " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s ;";


    public static synchronized ScriptVersionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptVersionConfiguration();
        }
        return INSTANCE;
    }

    private ScriptVersionConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    @Override
    public Optional<ScriptVersion> get(ScriptVersionKey scriptVersionKey) {
        Map<ScriptVersionKey, ScriptVersionBuilder> scriptVersionBuilders = new LinkedHashMap<>();

        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                String.format(FETCH_BY_KEY_QUERY,
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()),
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptVersion()),
                        SQLTools.getStringForSQL(scriptVersionKey.getDeletedAt())),
                "reader");
        try {
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptVersionBuilders);
            }
            return scriptVersionBuilders.values().stream()
                    .map(ScriptVersionBuilder::build)
                    .findFirst();
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            throw new RuntimeException(String.format("Can not find %s", scriptVersionKey));
        }
    }

    public Optional<ScriptVersion> getByNameAndVersionAndActive(String name, long version) {
        Map<ScriptVersionKey, ScriptVersionBuilder> scriptVersionBuilders = new LinkedHashMap<>();

        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                String.format(FETCH_BY_NAME_AND_VERSION_AND_ACTIVE_QUERY,
                        SQLTools.getStringForSQL(name),
                        SQLTools.getStringForSQL(version)),
                "reader");
        try {
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptVersionBuilders);
            }
            return scriptVersionBuilders.values().stream()
                    .map(ScriptVersionBuilder::build)
                    .findFirst();
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            throw new RuntimeException(String.format("Can not find %s-%s", name, version));
        }
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<ScriptVersionKey, ScriptVersionBuilder> scriptVersionBuilders) throws SQLException {
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey(new ScriptKey(cachedRowSet.getString("SCRIPT_ID")), cachedRowSet.getLong("SCRIPT_VRS_NB"), cachedRowSet.getString("DELETED_AT"));
        ScriptVersionBuilder scriptVersionBuilder = scriptVersionBuilders.get(scriptVersionKey);
        if (scriptVersionBuilder == null) {
            scriptVersionBuilder = mapScriptVersion(cachedRowSet);
            scriptVersionBuilders.put(scriptVersionKey, scriptVersionBuilder);
        }
        mapParameter(cachedRowSet, scriptVersionBuilder);
        mapLabel(cachedRowSet, scriptVersionBuilder);
        mapAction(cachedRowSet, scriptVersionBuilder);
    }

    private void mapParameter(CachedRowSet cachedRowSet, ScriptVersionBuilder scriptVersionBuilder) throws SQLException {
        String scriptParameterName = cachedRowSet.getString("SCRIPT_PAR_NM");
        if (scriptParameterName != null) {
            scriptVersionBuilder.getParameters().add(new ScriptParameter(
                    new ScriptParameterKey(new ScriptVersionKey(new ScriptKey(cachedRowSet.getString("SCRIPT_ID")), cachedRowSet.getLong("SCRIPT_VRS_NB"), cachedRowSet.getString("DELETED_AT")),
                    cachedRowSet.getString("SCRIPT_PAR_NM")),
                    SQLTools.getStringFromSQLClob(cachedRowSet, "SCRIPT_PAR_VAL")));
        }
    }


    private void mapAction(CachedRowSet cachedRowSet, ScriptVersionBuilder scriptVersionBuilder) throws SQLException {
        // actions.ACTION_ID, actions.ACTION_NB, actions.ACTION_DSC, actions.ACTION_NM, actions.ACTION_TYP_NM, actions.COMP_NM, actions.CONDITION_VAL, actions.ITERATION_VAL, actions.EXP_ERR_FL, actions.RETRIES_VAL, actions.STOP_ERR_FL, " +
        //        "action_parameters.ACTION_PAR_NM, action_parameters.ACTION_PAR_VAL " +
        String actionNumber = cachedRowSet.getString("ACTION_ID");
        if (actionNumber != null) {
            ActionKey actionKey = new ActionKey(
                    new ScriptVersionKey(new ScriptKey(cachedRowSet.getString("SCRIPT_ID")), cachedRowSet.getLong("SCRIPT_VRS_NB"), cachedRowSet.getString("DELETED_AT")),
                    cachedRowSet.getString("ACTION_ID")
            );
            ActionBuilder actionBuilder = scriptVersionBuilder.getActions().get(actionKey);
            if (actionBuilder == null) {
                actionBuilder = new ActionBuilder(
                        actionKey,
                        cachedRowSet.getLong("ACTION_NB"),
                        cachedRowSet.getString("ACTION_NM"),
                        cachedRowSet.getString("ACTION_TYP_NM"),
                        cachedRowSet.getString("ACTION_DSC"),
                        cachedRowSet.getString("COMP_NM"),
                        SQLTools.getStringFromSQLClob(cachedRowSet, "CONDITION_VAL"),
                        cachedRowSet.getString("ITERATION_VAL"),
                        cachedRowSet.getString("EXP_ERR_FL"),
                        cachedRowSet.getString("STOP_ERR_FL"),
                        cachedRowSet.getString("RETRIES_VAL"),
                        new HashMap<>());
                scriptVersionBuilder.getActions().put(actionKey, actionBuilder);
            }
            mapActionParameter(cachedRowSet, actionBuilder);
        }
    }

    private void mapActionParameter(CachedRowSet cachedRowSet, ActionBuilder actionDtoBuilder) throws SQLException {
        String actionParameterName = cachedRowSet.getString("ACTION_PAR_NM");
        if (actionParameterName != null) {
            ActionParameterKey actionParameterKey = new ActionParameterKey(
                    new ActionKey(
                            new ScriptVersionKey(new ScriptKey(cachedRowSet.getString("SCRIPT_ID")), cachedRowSet.getLong("SCRIPT_VRS_NB"), cachedRowSet.getString("DELETED_AT")),
                            cachedRowSet.getString("ACTION_ID")),
                    actionParameterName
            );
            ActionParameter actionParameterDto = actionDtoBuilder.getParameters().get(actionParameterKey);
            if (actionParameterDto == null) {
                actionDtoBuilder.getParameters().put(actionParameterKey, new ActionParameter(
                        actionParameterKey,
                        SQLTools.getStringFromSQLClob(cachedRowSet, "ACTION_PAR_VAL")
                ));
            }
        }
    }

    private void mapLabel(CachedRowSet cachedRowSet, ScriptVersionBuilder scriptVersionBuilder) throws SQLException {
        String labelId = cachedRowSet.getString("LABEL_ID");
        if (labelId != null && scriptVersionBuilder.getLabels().get(new ScriptLabelKey(labelId)) == null) {
            scriptVersionBuilder.getLabels().put(new ScriptLabelKey(labelId), new ScriptLabel(
                    new ScriptLabelKey(labelId),
                    new ScriptVersionKey(new ScriptKey(cachedRowSet.getString("SCRIPT_ID")), cachedRowSet.getLong("SCRIPT_VRS_NB"), cachedRowSet.getString("DELETED_AT")),
                    cachedRowSet.getString("LABEL_NAME"),
                    cachedRowSet.getString("LABEL_VALUE")));
        }
    }

    private ScriptVersionBuilder mapScriptVersion(CachedRowSet cachedRowSet) throws SQLException {
        //  "versions.SCRIPT_VRS_NB, versions.SCRIPT_VRS_DSC, versions.CREATED_BY, versions.CREATED_AT, versions.LAST_MODIFIED_BY, versions.LAST_MODIFIED_AT, versions.DELETED_AT
        //            "script_designs.SCRIPT_ID, script_designs.SECURITY_GROUP_NAME, script_designs.SECURITY_GROUP_ID, script_designs.SCRIPT_NM, script_designs.SCRIPT_DSC, script_design.deleted_at as script_deleted_at " +
        return new ScriptVersionBuilder(
                new ScriptVersionKey(new ScriptKey(cachedRowSet.getString("SCRIPT_ID")), cachedRowSet.getLong("SCRIPT_VRS_NB"), cachedRowSet.getString("DELETED_AT")),
                new ScriptBuilder(
                        new ScriptKey(cachedRowSet.getString("SCRIPT_ID")),
                        new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("SECURITY_GROUP_ID"))),
                        cachedRowSet.getString("SCRIPT_NM"),
                        cachedRowSet.getString("SECURITY_GROUP_NAME"),
                        cachedRowSet.getString("SCRIPT_DSC"),
                        cachedRowSet.getString("DELETED_AT")
                ),
                new HashSet<>(),
                new HashMap<>(),
                new HashMap<>(),
                cachedRowSet.getString("SCRIPT_VRS_DSC"),
                cachedRowSet.getString("CREATED_BY"),
                cachedRowSet.getString("CREATED_AT"),
                cachedRowSet.getString("LAST_MODIFIED_BY"),
                cachedRowSet.getString("LAST_MODIFIED_AT"));
    }

    @Override
    public List<ScriptVersion> getAll() {
        Map<ScriptVersionKey, ScriptVersionBuilder> scriptVersionBuilders = new LinkedHashMap<>();

        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                FETCH_ALL_QUERY,
                "reader");
        try {

            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptVersionBuilders);
            }
            return scriptVersionBuilders.values().stream()
                    .map(ScriptVersionBuilder::build)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("exception=" + e.getMessage());
            log.info("exception.stacktrace=" + stackTrace.toString());
            throw new RuntimeException("Cannot retrieve all scripts due to " + stackTrace.toString());
        }
    }

    public List<ScriptVersion> getAllActive() {
        Map<ScriptVersionKey, ScriptVersionBuilder> scriptVersionBuilders = new LinkedHashMap<>();

        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                FETCH_ALL_ACTIVE_QUERY,
                "reader");
        try {

            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptVersionBuilders);
            }
            return scriptVersionBuilders.values().stream()
                    .map(ScriptVersionBuilder::build)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("exception=" + e.getMessage());
            log.info("exception.stacktrace=" + stackTrace.toString());
            throw new RuntimeException("Cannot retrieve all scripts due to " + stackTrace.toString());
        }
    }

    @Override
    public void delete(ScriptVersionKey scriptVersionKey) {
        log.trace(MessageFormat.format("deleting ScriptVersion {0}", scriptVersionKey.toString()));
        ActionConfiguration.getInstance().deleteByScript(scriptVersionKey);
        ScriptParameterConfiguration.getInstance().deleteByScript(scriptVersionKey);
        ScriptLabelConfiguration.getInstance().deleteByScriptVersion(scriptVersionKey);
        String deleteStatement = getDeleteStatement(scriptVersionKey);
        getMetadataRepository().executeUpdate(deleteStatement);

        CachedRowSet crs = getMetadataRepository().executeQuery(
                String.format(COUNT_QUERY,
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId())),
                "reader");
        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                getMetadataRepository().executeUpdate(String.format(
                        SCRIPT_DELETE_BY_ID_QUERY,
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId())));
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info(String.format("exception=%s", e));
            log.debug(String.format("exception.stacktrace=%s", stackTrace));
        }
    }

    public void softDelete(ScriptVersionKey scriptVersionKey, String timeStamp) {
        log.trace(MessageFormat.format("deleting ScriptVersion {0}", scriptVersionKey.toString()));
        ActionConfiguration.getInstance().softDeleteByScriptVersion(scriptVersionKey, timeStamp);
        ScriptParameterConfiguration.getInstance().softDeleteByScriptVersion(scriptVersionKey, timeStamp);
        ScriptLabelConfiguration.getInstance().softDeleteByScriptVersion(scriptVersionKey, timeStamp);

        String deleteStatement = markInactiveScriptVersion(scriptVersionKey, timeStamp);
        getMetadataRepository().executeUpdate(deleteStatement);

        CachedRowSet crs = getMetadataRepository().executeQuery(
                String.format(COUNT_ACTIVE_QUERY,
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId())),
                "reader");
        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                getMetadataRepository().executeUpdate(String.format(
                        SCRIPT_SOFT_DELETE_BY_ID_QUERY,
                        SQLTools.getStringForSQL(timeStamp),
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId())));
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info(String.format("exception=%s", e));
            log.debug(String.format("exception.stacktrace=%s", stackTrace));
        }
    }

    public void softDeleteByScriptKey(ScriptKey scriptKey, String timeStamp) {
        getByScriptKey(scriptKey).forEach(scriptVersion -> softDelete(scriptVersion.getMetadataKey(), timeStamp));
    }

    public Set<ScriptVersion> getByScriptKey(ScriptKey scriptKey) {
        Map<ScriptVersionKey, ScriptVersionBuilder> scriptVersionBuilders = new LinkedHashMap<>();
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                String.format(FETCH_BY_SCRIPT_KEY_QUERY,
                        SQLTools.getStringForSQL(scriptKey.getScriptId())),
                "reader");
        try {
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, scriptVersionBuilders);
            }
            return scriptVersionBuilders.values().stream()
                    .map(ScriptVersionBuilder::build)
                    .collect(Collectors.toSet());
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("exception=" + e.getMessage());
            log.info("exception.stacktrace=" + stackTrace.toString());
            throw new RuntimeException("Cannot retrieve all scripts due to " + stackTrace.toString());
        }
    }

    public void deleteByScriptKey(ScriptKey scriptKey) {
        getByScriptKey(scriptKey)
                .forEach(scriptVersion -> delete(scriptVersion.getMetadataKey()));
    }

//    public List<ScriptVersion> getActiveByScriptId(String scriptId) {
//        List<ScriptVersion> scriptVersions = new ArrayList<>();
//        String queryVersionScript = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
//                + " WHERE DELETED_AT = 'NA' AND SCRIPT_ID = " + SQLTools.getStringForSQL(scriptId);
//        CachedRowSet crsVersionScript = getMetadataRepository().executeQuery(queryVersionScript, "reader");
//        try {
//            while (crsVersionScript.next()) {
//                scriptVersions.add(new ScriptVersion(
//                        crsVersionScript.getString("SCRIPT_ID"),
//                        crsVersionScript.getLong("SCRIPT_VRS_NB"),
//                        crsVersionScript.getString("SCRIPT_VRS_DSC"),
//                        crsVersionScript.getString("CREATED_BY"),
//                        crsVersionScript.getString("CREATED_AT"),
//                        crsVersionScript.getString("LAST_MODIFIED_BY"),
//                        crsVersionScript.getString("LAST_MODIFIED_AT"),
//                        crsVersionScript.getString("DELETED_AT")));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return scriptVersions;
//    }
//
//    public List<ScriptVersion> getDeletedByScriptId(String scriptId) {
//        List<ScriptVersion> scriptVersions = new ArrayList<>();
//        String queryVersionScript = "select * from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
//                + " WHERE DELETED_AT != 'NA' AND SCRIPT_ID = " + SQLTools.getStringForSQL(scriptId);
//        CachedRowSet crsVersionScript = getMetadataRepository().executeQuery(queryVersionScript, "reader");
//        try {
//            while (crsVersionScript.next()) {
//                scriptVersions.add(new ScriptVersion(
//                        crsVersionScript.getString("SCRIPT_ID"),
//                        crsVersionScript.getLong("SCRIPT_VRS_NB"),
//                        crsVersionScript.getString("SCRIPT_VRS_DSC"),
//                        crsVersionScript.getString("CREATED_BY"),
//                        crsVersionScript.getString("CREATED_AT"),
//                        crsVersionScript.getString("LAST_MODIFIED_BY"),
//                        crsVersionScript.getString("LAST_MODIFIED_AT"),
//                        crsVersionScript.getString("DELETED_AT")));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return scriptVersions;
//    }

    public Optional<ScriptVersion> getLatestVersionByScriptIdAndActive(String scriptId) {
        log.trace(MessageFormat.format("Fetching latest version for script {0}.", scriptId));
        String queryScriptVersion = "select max(SCRIPT_VRS_NB) as \"MAX_VRS_NB\" from "
                + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " where script_id = " + SQLTools.getStringForSQL(scriptId) +
                " and DELETED_AT = 'NA' " + " ;";
        CachedRowSet crsScriptVersion = getMetadataRepository().executeQuery(queryScriptVersion, "reader");
        try {
            if (crsScriptVersion.size() == 0) {
                crsScriptVersion.close();
                return Optional.empty();
            } else {
                crsScriptVersion.next();
                long latestScriptVersion = crsScriptVersion.getLong("MAX_VRS_NB");
                crsScriptVersion.close();
                return get(new ScriptVersionKey(new ScriptKey(scriptId), latestScriptVersion, "NA"));
            }
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.info("exception.stacktrace=" + stackTrace);

            return Optional.empty();
        }
    }

    @Override
    public void insert(ScriptVersion scriptVersion) {
        // TODO: check if script exists
        log.trace(MessageFormat.format("Inserting ScriptVersion {0}-{1}.", scriptVersion.getScriptId(), scriptVersion.getNumber()));
        if (exists(scriptVersion)) {
            throw new MetadataAlreadyExistsException(scriptVersion);
        }

        if (!ScriptConfiguration.getInstance().exists(scriptVersion.getScript())) {
            ScriptConfiguration.getInstance().insert(scriptVersion.getScript());
        }

        // add Parameters
        for (ScriptParameter scriptParameter : scriptVersion.getParameters()) {
            ScriptParameterConfiguration.getInstance().insert(scriptParameter);
        }

        // add Parameters
        for (ScriptLabel scriptLabel : scriptVersion.getLabels()) {
            ScriptLabelConfiguration.getInstance().insert(scriptLabel);
        }

        // add actions
        for (Action action : scriptVersion.getActions()) {
            ActionConfiguration.getInstance().insert(action);
        }
        getMetadataRepository().executeUpdate(getInsertStatement(scriptVersion));
    }

    @Override
    public void update(ScriptVersion scriptVersion) {
        log.trace(MessageFormat.format("Updating ScriptVersion {0}-{1}.", scriptVersion.getScriptId(), scriptVersion.getNumber()));
        if (!exists(scriptVersion)) {
            throw new MetadataDoesNotExistException(scriptVersion);
        }

        for (ScriptParameter scriptParameter : scriptVersion.getParameters()) {
            ScriptParameterConfiguration.getInstance().update(scriptParameter);
        }

        for (ScriptLabel scriptLabel : scriptVersion.getLabels()) {
            ScriptLabelConfiguration.getInstance().update(scriptLabel);
        }

        for (Action action : scriptVersion.getActions()) {
            ActionConfiguration.getInstance().update(action);
        }
        getMetadataRepository().executeUpdate(updateStatement(scriptVersion));
    }

    public void restoreDeletedScriptVersion(ScriptVersionKey scriptVersionKey) {
        log.trace(MessageFormat.format("Restoring deleted ScriptVersion {0}", scriptVersionKey.getScriptKey().getScriptId()));
        if (!existsDeleted(scriptVersionKey)) {
            throw new MetadataDoesNotExistException(scriptVersionKey.getScriptKey());
        }
        getMetadataRepository().executeUpdate(markActiveScriptVersion(scriptVersionKey));
        log.trace(MessageFormat.format("Successfully Restored deleted ScriptVersion {0}", scriptVersionKey.getScriptKey()));
    }

    private String markInactiveScriptVersion(ScriptVersionKey scriptVersionKey, String timeStamp) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptVersions") + " SET " +
                " DELETED_AT = " + SQLTools.getStringForSQL(timeStamp) +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptVersion()) +
                " AND DELETED_AT = " + SQLTools.getStringForSQL(scriptVersionKey.getDeletedAt()) + ";";
    }

    private String getDeleteStatement(ScriptVersionKey scriptVersionKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptVersion()) +
                " AND DELETED_AT = " + SQLTools.getStringForSQL(scriptVersionKey.getDeletedAt()) + ";";
    }

    private String markActiveScriptVersion(ScriptVersionKey scriptVersionKey) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptVersions") + " SET " +
                " DELETED_AT = 'NA' " +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptVersion()) +
                " AND DELETED_AT = " + SQLTools.getStringForSQL(scriptVersionKey.getDeletedAt()) + ";";
    }

    private String updateStatement(ScriptVersion scriptVersion) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " SET LAST_MODIFIED_BY = " + SQLTools.getStringForSQL(scriptVersion.getLastModifiedBy()) + ", " +
                " LAST_MODIFIED_AT = " + SQLTools.getStringForSQL(scriptVersion.getLastModifiedAt()) + ", " +
                " SCRIPT_VRS_DSC = " + SQLTools.getStringForSQL(scriptVersion.getDescription()) +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getScriptVersion()) +
                " AND DELETED_AT = " + SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getDeletedAt()) + ";";
    }

    public boolean exists(ScriptVersionKey scriptVersionKey) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptVersion()) +
                " and DELETED_AT = " + SQLTools.getStringForSQL(scriptVersionKey.getDeletedAt()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public boolean existsDeleted(ScriptVersionKey scriptVersionKey) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionKey.getScriptVersion()) +
                " and DELETED_AT = " + SQLTools.getStringForSQL(scriptVersionKey.getDeletedAt()) + " ;";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public String getInsertStatement(ScriptVersion scriptVersion) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC, CREATED_BY, CREATED_AT, DELETED_AT) VALUES (" +
                SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getScriptKey().getScriptId()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getScriptVersion()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getDescription()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getCreatedBy()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getCreatedAt()) + ", " +
                SQLTools.getStringForSQL(scriptVersion.getMetadataKey().getDeletedAt()) + ");";
    }

    @AllArgsConstructor
    @Getter
    private class ScriptBuilder {
        private final ScriptKey scriptKey;
        private final SecurityGroupKey securityGroupKey;
        private final String name;
        private final String securityGroupName;
        private final String description;
        private final String deletedAt;

        public Script build() {
            return new Script(
                    scriptKey,
                    securityGroupKey,
                    securityGroupName,
                    name,
                    description,
                    deletedAt);
        }
    }

    @AllArgsConstructor
    @Getter
    private class ScriptVersionBuilder {
        private final ScriptVersionKey scriptVersionKey;
        private final ScriptBuilder scriptBuilder;
        private final Set<ScriptParameter> parameters;
        private final Map<ActionKey, ActionBuilder> actions;
        private final Map<ScriptLabelKey, ScriptLabel> labels;
        private final String description;
        private final String createdBy;
        private final String createdAt;
        private final String lastModifiedBy;
        private final String lastModifiedAt;

        public ScriptVersion build() {
            return new ScriptVersion(
                    scriptVersionKey,
                    scriptBuilder.build(),
                    description,
                    parameters,
                    actions.values().stream().map(ActionBuilder::build).collect(Collectors.toSet()),
                    new HashSet<>(labels.values()),
                    createdBy,
                    createdAt,
                    lastModifiedBy,
                    lastModifiedAt);
        }
    }


    @AllArgsConstructor
    @Getter
    private class ActionBuilder {
        private final ActionKey actionKey;
        private final long number;
        private final String name;
        private final String type;
        private final String description;
        private final String component;
        private final String condition;
        private final String iteration;
        private final String errorExpected;
        private final String errorStop;
        private final String retries;
        private final Map<ActionParameterKey, ActionParameter> parameters;

        public Action build() {
            return new Action(actionKey, number, type, name, description,
                    component, condition, iteration, errorExpected, errorStop, retries,
                    new ArrayList<>(parameters.values()));
        }

    }

}