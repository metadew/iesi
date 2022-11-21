package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

@Log4j2
@Component
public class ScriptConfiguration extends Configuration<Script, ScriptKey> {

    private final MetadataTablesConfiguration metadataTablesConfiguration;
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final ActionConfiguration actionConfiguration;
    private final ScriptVersionConfiguration scriptVersionConfiguration;
    private final ScriptParameterConfiguration scriptParameterConfiguration;
    private final ScriptLabelConfiguration scriptLabelConfiguration;

    private String fetchAllQuery() {
        return "SELECT " +
                "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Scripts").getName() + " ;";
    }

    private String fetchByNameQuery() {
        return "SELECT " +
                "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Scripts").getName() +
                " WHERE SCRIPT_NM = %s;";
    }

    private String fetchByIdQuery() {
        return "SELECT " +
                "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Scripts").getName() +
                " WHERE SCRIPT_ID = %s;";
    }

    private String existsByNameQuery() {
        return "SELECT " +
                "SCRIPT_ID " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Scripts").getName() +
                " WHERE SCRIPT_NM = %s;";
    }

    private String existsByIdQuery() {
        return "SELECT " +
                "SCRIPT_ID " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Scripts").getName() +
                " WHERE SCRIPT_ID = %s;";
    }

    private String insertQuery() {
        return  "INSERT INTO " +
                metadataTablesConfiguration.getMetadataTableNameByLabel("Scripts").getName() +
                " (SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC) VALUES " +
                "(%s, %s, %s, %s, %s);";
    }

    private String updateQuery() {
        return "UPDATE " +
                metadataTablesConfiguration.getMetadataTableNameByLabel("Scripts").getName() + " SET " +
                "SCRIPT_DSC = %s " +
                "WHERE SCRIPT_ID = %s;";
    }

    private String countQuery() {
        return "SELECT COUNT(DISTINCT SCRIPT_VRS_NB) AS total_versions FROM " +
                metadataTablesConfiguration.getMetadataTableNameByLabel("ScriptVersions").getName() +
                " WHERE SCRIPT_ID = %s AND SCRIPT_VRS_NB != %s;";
    }

    private String deleteByIdQuery() {
        return "DELETE FROM " +
                metadataTablesConfiguration.getMetadataTableNameByLabel("Scripts").getName() +
                " WHERE SCRIPT_ID = %s;";
    }

    public ScriptConfiguration(MetadataTablesConfiguration metadataTablesConfiguration,
                               MetadataRepositoryConfiguration metadataRepositoryConfiguration,
                               ActionConfiguration actionConfiguration,
                               ScriptVersionConfiguration scriptVersionConfiguration,
                               ScriptParameterConfiguration scriptParameterConfiguration,
                               ScriptLabelConfiguration scriptLabelConfiguration) {
        this.metadataTablesConfiguration = metadataTablesConfiguration;
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.actionConfiguration = actionConfiguration;
        this.scriptVersionConfiguration = scriptVersionConfiguration;
        this.scriptParameterConfiguration = scriptParameterConfiguration;
        this.scriptLabelConfiguration = scriptLabelConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getDesignMetadataRepository());
    }

    public void init(MetadataRepository metadataRepository) {
    }

    public Optional<SecurityGroup> getSecurityGroup(String name) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(fetchByNameQuery(), SQLTools.getStringForSQL(name)),
                "reader");
        try {
            if (crsScript.size() == 0) {
                return Optional.empty();
            } else if (crsScript.size() > 1) {
                log.warn(String.format("Found multiple implementations for script %s. Returning first implementation", name));
            }
            crsScript.next();
            return Optional.of(new SecurityGroup(
                    new SecurityGroupKey(UUID.fromString(crsScript.getString("SECURITY_GROUP_ID"))),
                    crsScript.getString("SECURITY_GROUP_NAME"),
                    new HashSet<>(),
                    new HashSet<>()
            ));
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            log.info(String.format("exception=%s", e));
            log.debug(String.format("exception.stacktrace=%s", stackTrace));

            return Optional.empty();
        }
    }

    public Optional<SecurityGroup> getSecurityGroup(ScriptKey scriptKey) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(fetchByIdQuery(), SQLTools.getStringForSQL(scriptKey.getScriptId())),
                "reader");
        try {
            if (crsScript.size() == 0) {
                return Optional.empty();
            } else if (crsScript.size() > 1) {
                log.warn(String.format("Found multiple implementations for script %s. Returning first implementation", scriptKey));
            }
            crsScript.next();
            return Optional.of(new SecurityGroup(
                    new SecurityGroupKey(UUID.fromString(crsScript.getString("SECURITY_GROUP_ID"))),
                    crsScript.getString("SECURITY_GROUP_NAME"),
                    new HashSet<>(),
                    new HashSet<>()
            ));
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            log.info(String.format("exception=%s", e));
            log.debug(String.format("exception.stacktrace=%s", stackTrace));

            return Optional.empty();
        }
    }

    @Override
    public Optional<Script> get(ScriptKey scriptKey) {
        // had to change this to only get the script, because the script doesn't have version as id
        // return get(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber());
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(fetchByIdQuery(), SQLTools.getStringForSQL(scriptKey.getScriptId())),
                "reader");
        try {
            if (crsScript.size() == 0) {
                return Optional.empty();
            } else if (crsScript.size() > 1) {
                log.warn(MessageFormat.format("Found multiple implementations for script {0}-{1}. Returning first implementation", scriptKey.getScriptId(), scriptKey.getScriptVersion()));
            }
            crsScript.next();

            // Get the version
            Optional<ScriptVersion> scriptVersion = scriptVersionConfiguration.get(
                    new ScriptVersionKey(new ScriptKey(scriptKey.getScriptId(), scriptKey.getScriptVersion())));
            if (!scriptVersion.isPresent()) {
                return Optional.empty();
            }

            // Get the actions
            List<Action> actions = actionConfiguration.getByScript(scriptKey);

            // Get parameters
            List<ScriptParameter> scriptParameters = scriptParameterConfiguration.getByScript(scriptKey);

            // Get labels
            List<ScriptLabel> scriptLabels = scriptLabelConfiguration.getByScript(scriptKey);

            Script script = new Script(
                    scriptKey,
                    new SecurityGroupKey(UUID.fromString(crsScript.getString("SECURITY_GROUP_ID"))),
                    crsScript.getString("SECURITY_GROUP_NAME"),
                    crsScript.getString("SCRIPT_NM"),
                    crsScript.getString("SCRIPT_DSC"),
                    scriptVersion.get(),
                    scriptParameters,
                    actions,
                    scriptLabels);
            crsScript.close();
            return Optional.of(script);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            log.info(String.format("exception=%s", e));
            log.debug(String.format("exception.stacktrace=%s", stackTrace));

            return Optional.empty();
        }
    }

    public boolean existsById(String scriptId) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(existsByIdQuery(), SQLTools.getStringForSQL(scriptId)),
                "reader");
        return crsScript.size() >= 1;
    }

    public boolean existsByName(String scriptName) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(existsByNameQuery(), SQLTools.getStringForSQL(scriptName)),
                "reader");
        return crsScript.size() >= 0;
    }

    public boolean exists(ScriptKey scriptKey) {
        try {
            CachedRowSet crsScript = getMetadataRepository().executeQuery(
                    String.format(existsByIdQuery(), SQLTools.getStringForSQL(scriptKey.getScriptId())),
                    "reader");
            if (crsScript.size() == 0) {
                return false;
            }
            crsScript.next();
            return scriptVersionConfiguration.exists(new ScriptVersionKey(scriptKey));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String scriptName) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(existsByNameQuery(), SQLTools.getStringForSQL(scriptName)),
                "reader");
        return crsScript.size() > 0;
    }

    @Override
    public List<Script> getAll() {
        List<Script> scripts = new ArrayList<>();
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                fetchAllQuery(),
                "reader");

        try {
            while (crsScript.next()) {
                String scriptId = crsScript.getString("SCRIPT_ID");
                List<ScriptVersion> scriptVersions = scriptVersionConfiguration.getByScriptId(scriptId);
                for (ScriptVersion scriptVersion : scriptVersions) {
                    get(new ScriptKey(scriptId, scriptVersion.getNumber())).ifPresent(scripts::add);
                }
            }

        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            log.info(String.format("exception=%s", e));
            log.debug(String.format("exception.stacktrace=%s", stackTrace));
        }
        return scripts;
    }

    @Override
    public void delete(ScriptKey scriptKey) {
        log.trace(MessageFormat.format("Deleting script {0}", scriptKey.toString()));
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey(
                new ScriptKey(
                        scriptKey.getScriptId(),
                        scriptKey.getScriptVersion()
                ));
        scriptVersionConfiguration.delete(scriptVersionKey);
        actionConfiguration.deleteByScript(scriptKey);
        scriptParameterConfiguration.deleteByScript(scriptKey);
        scriptLabelConfiguration.deleteByScript(scriptKey);
        getDeleteStatement(scriptKey)
                .ifPresent(getMetadataRepository()::executeUpdate);
    }

    public List<Script> getByName(String scriptName) {
        try {
            log.trace(MessageFormat.format("Fetching scripts by name ''{0}''", scriptName));
            List<Script> scripts = new ArrayList<>();
            CachedRowSet crsScript = getMetadataRepository().executeQuery(
                    String.format(fetchByNameQuery(), SQLTools.getStringForSQL(scriptName)),
                    "reader");
            if (crsScript.size() == 0) {
                return scripts;
            } else if (crsScript.size() > 1) {
                log.warn(MessageFormat.format("Found multiple implementations for Script {0}. Returning first implementation", scriptName));
            }
            crsScript.next();
            List<ScriptVersion> scriptVersions = scriptVersionConfiguration.getByScriptId(crsScript.getString("SCRIPT_ID"));

            for (ScriptVersion scriptVersion : scriptVersions) {
                get(new ScriptKey(crsScript.getString("SCRIPT_ID"), scriptVersion.getNumber())).ifPresent(scripts::add);
            }
            crsScript.close();
            return scripts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByName(String scriptName) {
        for (Script script : getByName(scriptName)) {
            delete(script.getMetadataKey());
        }
    }

    public void insert(Script script) {
        log.trace(MessageFormat.format("Inserting script {0}-{1}.", script.getName(), script.getVersion().getNumber()));
        if (exists(script)) {
            throw new MetadataAlreadyExistsException(script);
        }

        // add Parameters
        for (ScriptParameter scriptParameter : script.getParameters()) {
            scriptParameterConfiguration.insert(scriptParameter);
        }

        // add Parameters
        for (ScriptLabel scriptLabel : script.getLabels()) {
            scriptLabelConfiguration.insert(scriptLabel);
        }

        // add version
        scriptVersionConfiguration.insert(script.getVersion());
        // add actions
        for (Action action : script.getActions()) {
            actionConfiguration.insert(action);
        }

        getMetadataRepository().executeUpdate(getInsertStatement(script));
    }

    private String getInsertStatement(Script script) {
        if (!existsById(script.getMetadataKey().getScriptId())) {
            return String.format(insertQuery(),
                    SQLTools.getStringForSQL(script.getMetadataKey().getScriptId()),
                    SQLTools.getStringForSQL(script.getSecurityGroupKey().getUuid()),
                    SQLTools.getStringForSQL(script.getSecurityGroupName()),
                    SQLTools.getStringForSQL(script.getName()),
                    SQLTools.getStringForSQL(script.getDescription()));
        } else {
            return String.format(updateQuery(),
                    SQLTools.getStringForSQL(script.getDescription()),
                    SQLTools.getStringForSQL(script.getMetadataKey().getScriptId()));
        }
    }

    private Optional<String> getDeleteStatement(ScriptKey scriptVersionKey) {
        CachedRowSet crs = getMetadataRepository().executeQuery(
                String.format(countQuery(),
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptId()),
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptVersion())),
                "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                return Optional.of(String.format(
                        deleteByIdQuery(),
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptId())));

            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            log.info(String.format("exception=%s", e));
            log.debug(String.format("exception.stacktrace=%s", stackTrace));
            return Optional.empty();
        }
    }

    @Override
    public void update(Script script) {
        scriptVersionConfiguration.update(script.getVersion());
        ScriptKey scriptKey = script.getMetadataKey();

        scriptParameterConfiguration.deleteByScript(scriptKey);
        for (ScriptParameter scriptParameter : script.getParameters()) {
            scriptParameterConfiguration.insert(scriptParameter);
        }

        actionConfiguration.deleteByScript(scriptKey);
        for (Action action : script.getActions()) {
            actionConfiguration.insert(action);
        }

        scriptLabelConfiguration.deleteByScript(scriptKey);
        for (ScriptLabel scriptLabel : script.getLabels()) {
            scriptLabelConfiguration.insert(scriptLabel);
        }
        getMetadataRepository().executeUpdate(getInsertStatement(script));
    }

    public Optional<Script> getLatestVersion(String scriptName) {
        Optional<ScriptVersion> latestVersion = scriptVersionConfiguration.getLatestVersionNumber(IdentifierTools.getScriptIdentifier(scriptName));
        if (latestVersion.isPresent()) {
            return get(new ScriptKey(latestVersion.get().getScriptId(), latestVersion.get().getNumber()));
        } else {
            return Optional.empty();
        }
    }

}