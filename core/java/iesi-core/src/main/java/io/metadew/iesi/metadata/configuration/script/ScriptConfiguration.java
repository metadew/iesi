package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScriptConfiguration extends Configuration<Script, ScriptKey> {

    private static final Logger LOGGER = LogManager.getLogger();

    private static ScriptConfiguration INSTANCE;

    public synchronized static ScriptConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ScriptConfiguration() {
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

    private final static String queryScript = "select Scripts.SCRIPT_ID as Scripts_SCRIPT_ID, Scripts.SCRIPT_NM as Scripts_SCRIPT_NM, Scripts.SCRIPT_DSC as Scripts_SCRIPT_DSC, " +
            " ScriptLabels.ID as ScriptLabels_ID, ScriptLabels.SCRIPT_ID as ScriptLabels_SCRIPT_ID, ScriptLabels.SCRIPT_VRS_NB as ScriptLabels_SCRIPT_VRS_NB, ScriptLabels.NAME as ScriptLabels_NAME, ScriptLabels.VALUE as ScriptLabels_VALUE, " +
            " ScriptParameters.SCRIPT_ID as ScriptParameters_SCRIPT_ID, ScriptParameters.SCRIPT_VRS_NB as ScriptParameters_SCRIPT_VRS_NB , ScriptParameters.SCRIPT_PAR_NM as ScriptParameters_SCRIPT_PAR_NM, ScriptParameters.SCRIPT_PAR_VAL as ScriptParameters_SCRIPT_PAR_VAL," +
            " ScriptVersions.SCRIPT_ID as ScriptVersions_SCRIPT_ID, ScriptVersions.SCRIPT_VRS_NB as ScriptVersions_SCRIPT_VRS_NB , ScriptVersions.SCRIPT_VRS_DSC as ScriptVersions_SCRIPT_VRS_DSC, " +
            " Actions.SCRIPT_ID as Actions_SCRIPT_ID, Actions.SCRIPT_VRS_NB as Actions_SCRIPT_VRS_NB, Actions.ACTION_ID as Actions_ACTION_ID , Actions.ACTION_NB as Actions_ACTION_NB , Actions.ACTION_TYP_NM as Actions_ACTION_TYP_NM , Actions.ACTION_NM as Actions_ACTION_NM  , Actions.ACTION_DSC as Actions_ACTION_DSC, Actions.COMP_NM as Actions_COMP_NM , Actions.ITERATION_VAL as Actions_ITERATION_VAL, Actions.CONDITION_VAL as Actions_CONDITION_VAL, Actions.EXP_ERR_FL as Actions_EXP_ERR_FL, Actions.STOP_ERR_FL as Actions_STOP_ERR_FL, Actions.RETRIES_VAL as Actions_RETRIES_VAL, " +
            " ActionParameters.SCRIPT_ID as ActionParameters_SCRIPT_ID, ActionParameters.SCRIPT_VRS_NB as ActionParameters_SCRIPT_VRS_NB, ActionParameters.ACTION_ID as ActionParameters_ACTION_ID , ActionParameters.ACTION_PAR_NM as ActionParameters_ACTION_PAR_NM , ActionParameters.ACTION_PAR_VAL  as ActionParameters_ACTION_PAR_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " Scripts LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() +
            " ScriptLabels on Scripts.SCRIPT_ID=ScriptLabels.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " ScriptParameters on Scripts.SCRIPT_ID=ScriptParameters.SCRIPT_ID LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " ScriptVersions on Scripts.SCRIPT_ID=ScriptVersions.SCRIPT_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " Actions on Scripts.SCRIPT_ID=Actions.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " ActionParameters ON Scripts.SCRIPT_ID=ActionParameters.SCRIPT_ID where Scripts.SCRIPT_ID = :id order by Scripts.SCRIPT_ID ASC ";
    private final static String getAll = "select Scripts.SCRIPT_ID as Scripts_SCRIPT_ID, Scripts.SCRIPT_NM as Scripts_SCRIPT_NM, Scripts.SCRIPT_DSC as Scripts_SCRIPT_DSC, " +
            " ScriptLabels.ID as ScriptLabels_ID, ScriptLabels.SCRIPT_ID as ScriptLabels_SCRIPT_ID, ScriptLabels.SCRIPT_VRS_NB as ScriptLabels_SCRIPT_VRS_NB, ScriptLabels.NAME as ScriptLabels_NAME, ScriptLabels.VALUE as ScriptLabels_VALUE, " +
            " ScriptParameters.SCRIPT_ID as ScriptParameters_SCRIPT_ID, ScriptParameters.SCRIPT_VRS_NB as ScriptParameters_SCRIPT_VRS_NB , ScriptParameters.SCRIPT_PAR_NM as ScriptParameters_SCRIPT_PAR_NM, ScriptParameters.SCRIPT_PAR_VAL as ScriptParameters_SCRIPT_PAR_VAL," +
            " ScriptVersions.SCRIPT_ID as ScriptVersions_SCRIPT_ID, ScriptVersions.SCRIPT_VRS_NB as ScriptVersions_SCRIPT_VRS_NB , ScriptVersions.SCRIPT_VRS_DSC as ScriptVersions_SCRIPT_VRS_DSC, " +
            " Actions.SCRIPT_ID as Actions_SCRIPT_ID, Actions.SCRIPT_VRS_NB as Actions_SCRIPT_VRS_NB, Actions.ACTION_ID as Actions_ACTION_ID , Actions.ACTION_NB as Actions_ACTION_NB , Actions.ACTION_TYP_NM as Actions_ACTION_TYP_NM , Actions.ACTION_NM as Actions_ACTION_NM  , Actions.ACTION_DSC as Actions_ACTION_DSC, Actions.COMP_NM as Actions_COMP_NM , Actions.ITERATION_VAL as Actions_ITERATION_VAL, Actions.CONDITION_VAL as Actions_CONDITION_VAL, Actions.EXP_ERR_FL as Actions_EXP_ERR_FL, Actions.STOP_ERR_FL as Actions_STOP_ERR_FL, Actions.RETRIES_VAL as Actions_RETRIES_VAL, " +
            " ActionParameters.SCRIPT_ID as ActionParameters_SCRIPT_ID, ActionParameters.SCRIPT_VRS_NB as ActionParameters_SCRIPT_VRS_NB, ActionParameters.ACTION_ID as ActionParameters_ACTION_ID , ActionParameters.ACTION_PAR_NM as ActionParameters_ACTION_PAR_NM , ActionParameters.ACTION_PAR_VAL  as ActionParameters_ACTION_PAR_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " Scripts LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() +
            " ScriptLabels on Scripts.SCRIPT_ID=ScriptLabels.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " ScriptParameters on Scripts.SCRIPT_ID=ScriptParameters.SCRIPT_ID LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " ScriptVersions on Scripts.SCRIPT_ID=ScriptVersions.SCRIPT_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " Actions on Scripts.SCRIPT_ID=Actions.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " ActionParameters ON Scripts.SCRIPT_ID=ActionParameters.SCRIPT_ID  order by Scripts.SCRIPT_NM ASC ";
    private final static String exists = "select Scripts.SCRIPT_ID as Scripts_SCRIPT_ID, Scripts.SCRIPT_NM as Scripts_SCRIPT_NM, Scripts.SCRIPT_DSC as Scripts_SCRIPT_DSC, " +
            " ScriptLabels.ID as ScriptLabels_ID, ScriptLabels.SCRIPT_ID as ScriptLabels_SCRIPT_ID, ScriptLabels.SCRIPT_VRS_NB as ScriptLabels_SCRIPT_VRS_NB, ScriptLabels.NAME as ScriptLabels_NAME, ScriptLabels.VALUE as ScriptLabels_VALUE, " +
            " ScriptParameters.SCRIPT_ID as ScriptParameters_SCRIPT_ID, ScriptParameters.SCRIPT_VRS_NB as ScriptParameters_SCRIPT_VRS_NB , ScriptParameters.SCRIPT_PAR_NM as ScriptParameters_SCRIPT_PAR_NM, ScriptParameters.SCRIPT_PAR_VAL as ScriptParameters_SCRIPT_PAR_VAL," +
            " ScriptVersions.SCRIPT_ID as ScriptVersions_SCRIPT_ID, ScriptVersions.SCRIPT_VRS_NB as ScriptVersions_SCRIPT_VRS_NB , ScriptVersions.SCRIPT_VRS_DSC as ScriptVersions_SCRIPT_VRS_DSC, " +
            " Actions.SCRIPT_ID as Actions_SCRIPT_ID, Actions.SCRIPT_VRS_NB as Actions_SCRIPT_VRS_NB, Actions.ACTION_ID as Actions_ACTION_ID , Actions.ACTION_NB as Actions_ACTION_NB , Actions.ACTION_TYP_NM as Actions_ACTION_TYP_NM , Actions.ACTION_NM as Actions_ACTION_NM  , Actions.ACTION_DSC as Actions_ACTION_DSC, Actions.COMP_NM as Actions_COMP_NM , Actions.ITERATION_VAL as Actions_ITERATION_VAL, Actions.CONDITION_VAL as Actions_CONDITION_VAL, Actions.EXP_ERR_FL as Actions_EXP_ERR_FL, Actions.STOP_ERR_FL as Actions_STOP_ERR_FL, Actions.RETRIES_VAL as Actions_RETRIES_VAL, " +
            " ActionParameters.SCRIPT_ID as ActionParameters_SCRIPT_ID, ActionParameters.SCRIPT_VRS_NB as ActionParameters_SCRIPT_VRS_NB, ActionParameters.ACTION_ID as ActionParameters_ACTION_ID , ActionParameters.ACTION_PAR_NM as ActionParameters_ACTION_PAR_NM , ActionParameters.ACTION_PAR_VAL  as ActionParameters_ACTION_PAR_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " Scripts LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() +
            " ScriptLabels on Scripts.SCRIPT_ID=ScriptLabels.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " ScriptParameters on Scripts.SCRIPT_ID=ScriptParameters.SCRIPT_ID LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " ScriptVersions on Scripts.SCRIPT_ID=ScriptVersions.SCRIPT_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " Actions on Scripts.SCRIPT_ID=Actions.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " ActionParameters ON Scripts.SCRIPT_ID=ActionParameters.SCRIPT_ID where Scripts.SCRIPT_ID = :id " +
            " ;";
    private final static String existsScriptName = "select Scripts.SCRIPT_ID as Scripts_SCRIPT_ID, Scripts.SCRIPT_NM as Scripts_SCRIPT_NM, Scripts.SCRIPT_DSC as Scripts_SCRIPT_DSC, " +
            " ScriptLabels.ID as ScriptLabels_ID, ScriptLabels.SCRIPT_ID as ScriptLabels_SCRIPT_ID, ScriptLabels.SCRIPT_VRS_NB as ScriptLabels_SCRIPT_VRS_NB, ScriptLabels.NAME as ScriptLabels_NAME, ScriptLabels.VALUE as ScriptLabels_VALUE, " +
            " ScriptParameters.SCRIPT_ID as ScriptParameters_SCRIPT_ID, ScriptParameters.SCRIPT_VRS_NB as ScriptParameters_SCRIPT_VRS_NB , ScriptParameters.SCRIPT_PAR_NM as ScriptParameters_SCRIPT_PAR_NM, ScriptParameters.SCRIPT_PAR_VAL as ScriptParameters_SCRIPT_PAR_VAL," +
            " ScriptVersions.SCRIPT_ID as ScriptVersions_SCRIPT_ID, ScriptVersions.SCRIPT_VRS_NB as ScriptVersions_SCRIPT_VRS_NB , ScriptVersions.SCRIPT_VRS_DSC as ScriptVersions_SCRIPT_VRS_DSC, " +
            " Actions.SCRIPT_ID as Actions_SCRIPT_ID, Actions.SCRIPT_VRS_NB as Actions_SCRIPT_VRS_NB, Actions.ACTION_ID as Actions_ACTION_ID , Actions.ACTION_NB as Actions_ACTION_NB , Actions.ACTION_TYP_NM as Actions_ACTION_TYP_NM , Actions.ACTION_NM as Actions_ACTION_NM  , Actions.ACTION_DSC as Actions_ACTION_DSC, Actions.COMP_NM as Actions_COMP_NM , Actions.ITERATION_VAL as Actions_ITERATION_VAL, Actions.CONDITION_VAL as Actions_CONDITION_VAL, Actions.EXP_ERR_FL as Actions_EXP_ERR_FL, Actions.STOP_ERR_FL as Actions_STOP_ERR_FL, Actions.RETRIES_VAL as Actions_RETRIES_VAL, " +
            " ActionParameters.SCRIPT_ID as ActionParameters_SCRIPT_ID, ActionParameters.SCRIPT_VRS_NB as ActionParameters_SCRIPT_VRS_NB, ActionParameters.ACTION_ID as ActionParameters_ACTION_ID , ActionParameters.ACTION_PAR_NM as ActionParameters_ACTION_PAR_NM , ActionParameters.ACTION_PAR_VAL  as ActionParameters_ACTION_PAR_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " Scripts LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() +
            " ScriptLabels on Scripts.SCRIPT_ID=ScriptLabels.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " ScriptParameters on Scripts.SCRIPT_ID=ScriptParameters.SCRIPT_ID LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " ScriptVersions on Scripts.SCRIPT_ID=ScriptVersions.SCRIPT_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " Actions on Scripts.SCRIPT_ID=Actions.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " ActionParameters ON Scripts.SCRIPT_ID=ActionParameters.SCRIPT_ID  WHERE Scripts.SCRIPT_NM  = :name  ;";
    private final static String getByName = "select Scripts.SCRIPT_ID as Scripts_SCRIPT_ID, Scripts.SCRIPT_NM as Scripts_SCRIPT_NM, Scripts.SCRIPT_DSC as Scripts_SCRIPT_DSC, " +
            " ScriptLabels.ID as ScriptLabels_ID, ScriptLabels.SCRIPT_ID as ScriptLabels_SCRIPT_ID, ScriptLabels.SCRIPT_VRS_NB as ScriptLabels_SCRIPT_VRS_NB, ScriptLabels.NAME as ScriptLabels_NAME, ScriptLabels.VALUE as ScriptLabels_VALUE, " +
            " ScriptParameters.SCRIPT_ID as ScriptParameters_SCRIPT_ID, ScriptParameters.SCRIPT_VRS_NB as ScriptParameters_SCRIPT_VRS_NB , ScriptParameters.SCRIPT_PAR_NM as ScriptParameters_SCRIPT_PAR_NM, ScriptParameters.SCRIPT_PAR_VAL as ScriptParameters_SCRIPT_PAR_VAL," +
            " ScriptVersions.SCRIPT_ID as ScriptVersions_SCRIPT_ID, ScriptVersions.SCRIPT_VRS_NB as ScriptVersions_SCRIPT_VRS_NB , ScriptVersions.SCRIPT_VRS_DSC as ScriptVersions_SCRIPT_VRS_DSC, " +
            " Actions.SCRIPT_ID as Actions_SCRIPT_ID, Actions.SCRIPT_VRS_NB as Actions_SCRIPT_VRS_NB, Actions.ACTION_ID as Actions_ACTION_ID , Actions.ACTION_NB as Actions_ACTION_NB , Actions.ACTION_TYP_NM as Actions_ACTION_TYP_NM , Actions.ACTION_NM as Actions_ACTION_NM  , Actions.ACTION_DSC as Actions_ACTION_DSC, Actions.COMP_NM as Actions_COMP_NM , Actions.ITERATION_VAL as Actions_ITERATION_VAL, Actions.CONDITION_VAL as Actions_CONDITION_VAL, Actions.EXP_ERR_FL as Actions_EXP_ERR_FL, Actions.STOP_ERR_FL as Actions_STOP_ERR_FL, Actions.RETRIES_VAL as Actions_RETRIES_VAL, " +
            " ActionParameters.SCRIPT_ID as ActionParameters_SCRIPT_ID, ActionParameters.SCRIPT_VRS_NB as ActionParameters_SCRIPT_VRS_NB, ActionParameters.ACTION_ID as ActionParameters_ACTION_ID , ActionParameters.ACTION_PAR_NM as ActionParameters_ACTION_PAR_NM , ActionParameters.ACTION_PAR_VAL  as ActionParameters_ACTION_PAR_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " Scripts LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() +
            " ScriptLabels on Scripts.SCRIPT_ID=ScriptLabels.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " ScriptParameters on Scripts.SCRIPT_ID=ScriptParameters.SCRIPT_ID LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " ScriptVersions on Scripts.SCRIPT_ID=ScriptVersions.SCRIPT_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Actions").getName() +
            " Actions on Scripts.SCRIPT_ID=Actions.SCRIPT_ID  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionParameters").getName() +
            " ActionParameters ON Scripts.SCRIPT_ID=ActionParameters.SCRIPT_ID WHERE Scripts.SCRIPT_NM  = :name  ;";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " (SCRIPT_ID, SCRIPT_NM, SCRIPT_DSC) VALUES (:id, :name, :description)";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " SET SCRIPT_DSC = :description  WHERE SCRIPT_ID = :id ; ";
    private final static String countQuery = "SELECT COUNT(DISTINCT SCRIPT_VRS_NB) AS total_versions FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName()
            + " WHERE SCRIPT_ID = :id AND SCRIPT_VRS_NB != :version ;";
    private final static String delete = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName()
            + " WHERE SCRIPT_ID = :id ;";

    @Override
    public Optional<Script> get(ScriptKey scriptKey) {
        LOGGER.trace(MessageFormat.format("Fetching script {0}-{1}.", scriptKey.getScriptId(), scriptKey.getScriptVersion()));
        if (!exists(scriptKey)) {
            throw new MetadataDoesNotExistException(scriptKey);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptKey.getScriptId());
        Optional<Script> scripts = Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryScript,
                        sqlParameterSource,
                        new ScriptConfigurationExtractor())));
        return Optional.of(Script.builder().scriptKey(scriptKey).name(scripts.get().getName()).description(scripts.get().getDescription()).version(scripts.get().getVersion())
                .actions(scripts.get().getActions().stream().distinct().collect(Collectors.toList())).parameters(scripts.get().getParameters().stream().distinct().collect(Collectors.toList())).labels(scripts.get().getLabels().stream().distinct().collect(Collectors.toList())).build());
    }

    public boolean exists(ScriptKey scriptKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptKey.getScriptId());
        List<Script> scripts = namedParameterJdbcTemplate.query(
                exists,
                sqlParameterSource,
                new ScriptConfigurationExtractor());
        return scripts.size() > 0;
    }

    public boolean exists(String scriptName) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", scriptName);
        List<Script> scripts = namedParameterJdbcTemplate.query(
                existsScriptName,
                sqlParameterSource,
                new ScriptConfigurationExtractor());
        return scripts.size() > 0;
    }

    @Override
    public List<Script> getAll() {
        return namedParameterJdbcTemplate.query(getAll, new ScriptConfigurationExtractor());
    }

    @Override
    public void delete(ScriptKey scriptKey) {
        LOGGER.trace(MessageFormat.format("Deleting script {0}-{1}.", scriptKey.toString()));
        if (!exists(scriptKey)) {
            throw new MetadataDoesNotExistException(scriptKey);
        }
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey(new ScriptKey(scriptKey.getScriptId(), scriptKey.getScriptVersion()));
        ScriptVersionConfiguration.getInstance().delete(scriptVersionKey);
        ActionConfiguration.getInstance().deleteByScript(scriptKey);
        ScriptParameterConfiguration.getInstance().deleteByScript(scriptKey);
        ScriptLabelConfiguration.getInstance().deleteByScript(scriptKey);
        getDeleteStatement(scriptKey);
    }

    public List<Script> getByName(String scriptName) {
        List<Script> script = new ArrayList<>();
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", scriptName);
        List<Script> scripts = namedParameterJdbcTemplate.query(
                getByName,
                sqlParameterSource,
                new ScriptConfigurationExtractor());
        for (Script scriptVersion : scripts) {
            get(new ScriptKey(scriptVersion.getVersion().getScriptId(), scriptVersion.getVersion().getNumber())).ifPresent(script::add);
        }
        return script;
    }


    public void deleteByName(String scriptName) {
        for (Script script : getByName(scriptName)) {
            delete(script.getMetadataKey());
        }
    }

    public void insert(Script script) {
        LOGGER.trace(MessageFormat.format("Inserting script {0}-{1}.", script.getName(), script.getVersion().getNumber()));
        if (exists(script)) {
            throw new MetadataAlreadyExistsException(script);
        }

        for (ScriptParameter scriptParameter : script.getParameters()) {
            ScriptParameterConfiguration.getInstance().insert(scriptParameter);
        }

        for (ScriptLabel scriptLabel : script.getLabels()) {
            ScriptLabelConfiguration.getInstance().insert(scriptLabel);
        }

        ScriptVersionConfiguration.getInstance().insert(script.getVersion());

        for (Action action : script.getActions()) {
            ActionConfiguration.getInstance().insert(action);
        }

        if (!exists(script.getMetadataKey().getScriptId())) {
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                    .addValue("id", script.getMetadataKey().getScriptId())
                    .addValue("name", script.getName())
                    .addValue("description", script.getDescription());
            namedParameterJdbcTemplate.update(
                    insert,
                    sqlParameterSource);
        } else {
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                    .addValue("description", script.getDescription())
                    .addValue("id", script.getMetadataKey().getScriptId());
            namedParameterJdbcTemplate.update(
                    update,
                    sqlParameterSource);
        }
    }

    private void getDeleteStatement(ScriptKey scriptVersionKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptVersionKey.getScriptId())
                .addValue("version", scriptVersionKey.getScriptVersion());
        long total = namedParameterJdbcTemplate.query(
                countQuery,
                sqlParameterSource,
                new ScriptConfigurationExtractorTotal());
        if (total == 0) {
            SqlParameterSource sqlParameterSource1 = new MapSqlParameterSource()
                    .addValue("id", scriptVersionKey.getScriptId());
            namedParameterJdbcTemplate.update(
                    delete,
                    sqlParameterSource1);
        } else {
            Optional.empty();
        }
    }

    public Optional<Script> getLatestVersion(String scriptName) {
        Optional<Script> latestVersion = ScriptVersionConfiguration.getInstance().getLatestVersionNumber(IdentifierTools.getScriptIdentifier(scriptName));
        if (latestVersion.isPresent()) {
            return get(new ScriptKey(latestVersion.get().getMetadataKey().getScriptId(), latestVersion.get().getVersion().getNumber()));
        } else {
            return Optional.empty();
        }
    }
}