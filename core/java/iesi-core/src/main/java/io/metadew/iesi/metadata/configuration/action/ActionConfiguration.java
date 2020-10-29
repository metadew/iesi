package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ActionConfiguration extends Configuration<Action, ActionKey> {

    private final static Logger LOGGER = LogManager.getLogger();
    private static ActionConfiguration INSTANCE;

    public synchronized static ActionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ActionConfiguration() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(MetadataRepositoryConfiguration.getInstance()
                .getDesignMetadataRepository()
                .getRepositoryCoordinator()
                .getDatabases().values().stream()
                .findFirst()
                .map(Database::getConnectionPool)
                .orElseThrow(RuntimeException::new));
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }


    private final static String queryAction = "select Actions.SCRIPT_ID as Actions_SCRIPT_ID, Actions.SCRIPT_VRS_NB as Actions_SCRIPT_VRS_NB, Actions.ACTION_ID as Actions_ACTION_ID , Actions.ACTION_NB as Actions_ACTION_NB , Actions.ACTION_TYP_NM as Actions_ACTION_TYP_NM , Actions.ACTION_NM as Actions_ACTION_NM  , Actions.ACTION_DSC as Actions_ACTION_DSC, Actions.COMP_NM as Actions_COMP_NM , Actions.ITERATION_VAL as Actions_ITERATION_VAL, Actions.CONDITION_VAL as Actions_CONDITION_VAL, Actions.EXP_ERR_FL as Actions_EXP_ERR_FL, Actions.STOP_ERR_FL as Actions_STOP_ERR_FL, Actions.RETRIES_VAL as Actions_RETRIES_VAL, " +
            " ActionParameters.SCRIPT_ID as ActionParameters_SCRIPT_ID, ActionParameters.SCRIPT_VRS_NB as ActionParameters_SCRIPT_VRS_NB, ActionParameters.ACTION_ID as ActionParameters_ACTION_ID , ActionParameters.ACTION_PAR_NM as ActionParameters_ACTION_PAR_NM , ActionParameters.ACTION_PAR_VAL  as ActionParameters_ACTION_PAR_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " Actions LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " ActionParameters ON Actions.SCRIPT_ID=ActionParameters.SCRIPT_ID " +
            " where Actions.ACTION_ID = :id ";
    private final static String getAll = "select Actions.SCRIPT_ID as Actions_SCRIPT_ID, Actions.SCRIPT_VRS_NB as Actions_SCRIPT_VRS_NB, Actions.ACTION_ID as Actions_ACTION_ID , Actions.ACTION_NB as Actions_ACTION_NB , Actions.ACTION_TYP_NM as Actions_ACTION_TYP_NM , Actions.ACTION_NM as Actions_ACTION_NM  , Actions.ACTION_DSC as Actions_ACTION_DSC, Actions.COMP_NM as Actions_COMP_NM , Actions.ITERATION_VAL as Actions_ITERATION_VAL, Actions.CONDITION_VAL as Actions_CONDITION_VAL, Actions.EXP_ERR_FL as Actions_EXP_ERR_FL, Actions.STOP_ERR_FL as Actions_STOP_ERR_FL, Actions.RETRIES_VAL as Actions_RETRIES_VAL, " +
            " ActionParameters.SCRIPT_ID as ActionParameters_SCRIPT_ID, ActionParameters.SCRIPT_VRS_NB as ActionParameters_SCRIPT_VRS_NB, ActionParameters.ACTION_ID as ActionParameters_ACTION_ID , ActionParameters.ACTION_PAR_NM as ActionParameters_ACTION_PAR_NM , ActionParameters.ACTION_PAR_VAL  as ActionParameters_ACTION_PAR_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " Actions LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " ActionParameters  ON Actions.SCRIPT_ID=ActionParameters.SCRIPT_ID ;";
    private final static String deleteStatement = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " where SCRIPT_ID = :id AND SCRIPT_VRS_NB = :version AND ACTION_ID = :actionId  ;";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) " +
            " VALUES (:id, :version, :actionId, :actionNb, :type, :actionName, :actionDescription, :componentName, :iterationVal, :conditionVal, :retriesVal, :ErrorExpected, :stopError)";
    private final static String exists = "select Actions.SCRIPT_ID as Actions_SCRIPT_ID, Actions.SCRIPT_VRS_NB as Actions_SCRIPT_VRS_NB, Actions.ACTION_ID as Actions_ACTION_ID , Actions.ACTION_NB as Actions_ACTION_NB , Actions.ACTION_TYP_NM as Actions_ACTION_TYP_NM , Actions.ACTION_NM as Actions_ACTION_NM  , Actions.ACTION_DSC as Actions_ACTION_DSC, Actions.COMP_NM as Actions_COMP_NM , Actions.ITERATION_VAL as Actions_ITERATION_VAL, Actions.CONDITION_VAL as Actions_CONDITION_VAL, Actions.EXP_ERR_FL as Actions_EXP_ERR_FL, Actions.STOP_ERR_FL as Actions_STOP_ERR_FL, Actions.RETRIES_VAL as Actions_RETRIES_VAL, " +
            " ActionParameters.SCRIPT_ID as ActionParameters_SCRIPT_ID, ActionParameters.SCRIPT_VRS_NB as ActionParameters_SCRIPT_VRS_NB, ActionParameters.ACTION_ID as ActionParameters_ACTION_ID , ActionParameters.ACTION_PAR_NM as ActionParameters_ACTION_PAR_NM , ActionParameters.ACTION_PAR_VAL  as ActionParameters_ACTION_PAR_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " Actions LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " ActionParameters ON Actions.SCRIPT_ID=ActionParameters.SCRIPT_ID " +
            " where  Actions.ACTION_ID = :id AND  Actions.SCRIPT_ID = :scriptId AND  Actions.SCRIPT_VRS_NB = :version  ;";
    private final static String getByScript = "select Actions.SCRIPT_ID as Actions_SCRIPT_ID, Actions.SCRIPT_VRS_NB as Actions_SCRIPT_VRS_NB, Actions.ACTION_ID as Actions_ACTION_ID , Actions.ACTION_NB as Actions_ACTION_NB , Actions.ACTION_TYP_NM as Actions_ACTION_TYP_NM , Actions.ACTION_NM as Actions_ACTION_NM  , Actions.ACTION_DSC as Actions_ACTION_DSC, Actions.COMP_NM as Actions_COMP_NM , Actions.ITERATION_VAL as Actions_ITERATION_VAL, Actions.CONDITION_VAL as Actions_CONDITION_VAL, Actions.EXP_ERR_FL as Actions_EXP_ERR_FL, Actions.STOP_ERR_FL as Actions_STOP_ERR_FL, Actions.RETRIES_VAL as Actions_RETRIES_VAL, " +
            " ActionParameters.SCRIPT_ID as ActionParameters_SCRIPT_ID, ActionParameters.SCRIPT_VRS_NB as ActionParameters_SCRIPT_VRS_NB, ActionParameters.ACTION_ID as ActionParameters_ACTION_ID , ActionParameters.ACTION_PAR_NM as ActionParameters_ACTION_PAR_NM , ActionParameters.ACTION_PAR_VAL  as ActionParameters_ACTION_PAR_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " Actions LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " ActionParameters ON Actions.SCRIPT_ID=ActionParameters.SCRIPT_ID " +
            " WHERE Actions.SCRIPT_ID = :id AND Actions.SCRIPT_VRS_NB = :versionNumber order by Actions.ACTION_NB ASC ;";
    private final static String deleteByScript = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " where SCRIPT_ID = :id AND SCRIPT_VRS_NB = :scriptId  ;";

    @Override
    public Optional<Action> get(ActionKey actionKey) {
        LOGGER.trace(MessageFormat.format("Fetching action {0}.", actionKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionKey.getActionId())
                .addValue("scriptId", actionKey.getScriptKey().getScriptId())
                .addValue("version", actionKey.getScriptKey().getScriptVersion());
        Optional<Action> actions = Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryAction,
                        sqlParameterSource,
                        new ActionConfigurationExtractor())));
        return Optional.of(
                new Action(
                        actionKey,
                        actions.get().getNumber(),
                        actions.get().getType(),
                        actions.get().getName(),
                        actions.get().getDescription(),
                        actions.get().getComponent(),
                        actions.get().getCondition(),
                        actions.get().getIteration(),
                        actions.get().ErrorExpectedGet(),
                        actions.get().ErrorStopGet(),
                        Integer.toString(actions.get().getRetries()),
                        actions.get().getParameters()
                )
        );
    }

    @Override
    public List<Action> getAll() {
        return namedParameterJdbcTemplate.query(
                getAll,
                new ActionConfigurationExtractor());
    }

    @Override
    public void delete(ActionKey actionKey) {
        LOGGER.trace(MessageFormat.format("Deleting Action {0}.", actionKey.toString()));
        if (!exists(actionKey)) {
            throw new MetadataDoesNotExistException(actionKey);
        }
        ActionParameterConfiguration.getInstance().deleteByAction(actionKey);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionKey.getScriptKey().getScriptId())
                .addValue("version", actionKey.getScriptKey().getScriptVersion())
                .addValue("actionId", actionKey.getActionId());
        namedParameterJdbcTemplate.update(
                deleteStatement,
                sqlParameterSource);
    }

    @Override
    public void insert(Action action) {
        LOGGER.trace(MessageFormat.format("Inserting action {0}.", action.toString()));
        if (exists(action.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(action);
        }
        for (ActionParameter actionParameter : action.getParameters()) {
            ActionParameterConfiguration.getInstance().insert(actionParameter);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", action.getMetadataKey().getScriptKey().getScriptId())
                .addValue("version", action.getMetadataKey().getScriptKey().getScriptVersion())
                .addValue("actionId", action.getMetadataKey().getActionId())
                .addValue("actionNb", action.getNumber())
                .addValue("type", action.getType())
                .addValue("actionName", action.getName())
                .addValue("actionDescription", action.getDescription())
                .addValue("componentName", action.getComponent())
                .addValue("iterationVal", action.getIteration())
                .addValue("conditionVal", action.getCondition())
                .addValue("retriesVal", action.getRetries())
                .addValue("ErrorExpected", action.getErrorExpected())
                .addValue("stopError", action.getErrorStop());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public boolean exists(ActionKey actionKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionKey.getActionId())
                .addValue("scriptId", actionKey.getScriptKey().getScriptId())
                .addValue("version", actionKey.getScriptKey().getScriptVersion());
        List<Action> actions = namedParameterJdbcTemplate.query(exists, sqlParameterSource, new ActionConfigurationExtractor());
        return actions.size() >= 1;
    }

    public List<Action> getByScript(ScriptKey scriptKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptKey.getScriptId())
                .addValue("versionNumber", scriptKey.getScriptVersion());
        List<Action> actions = namedParameterJdbcTemplate.query(
                getByScript,
                sqlParameterSource,
                new ActionConfigurationExtractor());
        System.out.println(actions);
        return actions;
    }

    public void deleteByScript(ScriptKey scriptKey) {
        ActionParameterConfiguration.getInstance().deleteByScript(scriptKey);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptKey.getScriptId())
                .addValue("scriptId", scriptKey.getScriptVersion());
        namedParameterJdbcTemplate.update(
                deleteByScript,
                sqlParameterSource);
    }

}