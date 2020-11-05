package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final static String deleteStatement = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " where SCRIPT_ID = :id AND SCRIPT_VRS_NB = :version AND ACTION_ID = :actionId  ;";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) " +
            " VALUES (:id, :version, :actionId, :actionNb, :type, :actionName, :actionDescription, :componentName, :iterationVal, :conditionVal, :retriesVal, :ErrorExpected, :stopError)";
    private final static String deleteByScript = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " where SCRIPT_ID = :id AND SCRIPT_VRS_NB = :scriptId  ;";

    @Override
    public Optional<Action> get(ActionKey actionKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Action> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ActionKey actionKey) {
        LOGGER.trace(MessageFormat.format("Deleting Action {0}.", actionKey.toString()));
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