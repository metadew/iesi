package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
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

public class ActionParameterConfiguration extends Configuration<ActionParameter, ActionParameterKey> {

    private final static Logger LOGGER = LogManager.getLogger();
    private static ActionParameterConfiguration INSTANCE;

    public synchronized static ActionParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionParameterConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ActionParameterConfiguration() {
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

    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) " +
            " VALUES (:id, :version, :actionId, :parameter, :value)";
    private final static String deleteStatement = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName()
            + " WHERE SCRIPT_ID = :id AND SCRIPT_VRS_NB = :versionNumber AND ACTION_ID = :actionId AND ACTION_PAR_NM = :parameter;";
    private final static String deleteByAction = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName()
            + " WHERE SCRIPT_ID = :id AND SCRIPT_VRS_NB = :versionNumber AND ACTION_ID = :actionId ;";
    private final static String deleteByScript = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName()
            + " WHERE SCRIPT_ID = :id AND SCRIPT_VRS_NB = :versionNumber ;";

    @Override
    public Optional<ActionParameter> get(ActionParameterKey actionParameterKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ActionParameter> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ActionParameterKey actionParameterKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionParameter {0}.", actionParameterKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionParameterKey.getActionKey().getScriptKey().getScriptId())
                .addValue("versionNumber", actionParameterKey.getActionKey().getScriptKey().getScriptVersion())
                .addValue("actionId", actionParameterKey.getActionKey().getActionId())
                .addValue("parameter", actionParameterKey.getParameterName());
        namedParameterJdbcTemplate.update(
                deleteStatement,
                sqlParameterSource);
    }

    @Override
    public void insert(ActionParameter actionParameter) {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameter {0}.", actionParameter.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionParameter.getMetadataKey().getActionKey().getScriptKey().getScriptId())
                .addValue("version", actionParameter.getMetadataKey().getActionKey().getScriptKey().getScriptVersion())
                .addValue("actionId", actionParameter.getMetadataKey().getActionKey().getActionId())
                .addValue("parameter", actionParameter.getMetadataKey().getParameterName())
                .addValue("value", actionParameter.getValue());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public void deleteByAction(ActionKey actionKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionKey.getScriptKey().getScriptId())
                .addValue("versionNumber", actionKey.getScriptKey().getScriptVersion())
                .addValue("actionId", actionKey.getActionId());
        namedParameterJdbcTemplate.update(
                deleteByAction,
                sqlParameterSource);
    }

    public void deleteByScript(ScriptKey scriptKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptKey.getScriptId())
                .addValue("versionNumber", scriptKey.getScriptVersion());
        namedParameterJdbcTemplate.update(
                deleteByScript,
                sqlParameterSource);
    }
}