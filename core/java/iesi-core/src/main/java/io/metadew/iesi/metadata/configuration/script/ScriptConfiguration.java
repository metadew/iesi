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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

@Log4j2
public class ScriptConfiguration extends Configuration<Script, ScriptKey> {

    private static final String FETCH_ALL_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " ;";

    private static final String FETCH_BY_NAME_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_NM = %s;";

    private static final String FETCH_BY_ID_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s;";

    private static final String EXISTS_BY_NAME_QUERY = "SELECT " +
            "SCRIPT_ID " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_NM = %s;";

    private static final String EXISTS_BY_ID_QUERY = "SELECT " +
            "SCRIPT_ID " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s;";

    private static final String INSERT_QUERY = "INSERT INTO " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " (SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC) VALUES " +
            "(%s, %s, %s, %s, %s);";

    private static final String UPDATE_QUERY = "UPDATE " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " SET " +
            "SCRIPT_DSC = %s " +
            "WHERE SCRIPT_ID = %s;";

    private static final String COUNT_QUERY = "SELECT COUNT(DISTINCT SCRIPT_VRS_NB) AS total_versions FROM " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " WHERE SCRIPT_ID = %s AND SCRIPT_VRS_NB != %s;";

    private static final String DELETE_BY_ID_QUERY = "DELETE FROM " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s;";

    private static final String UPDATE_BY_ID_QUERY_FOR_SOFT_DELETE = "UPDATE " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " SET " +
            " IS_ACTIVE = true " +
            " WHERE SCRIPT_ID = %s;";

    private static ScriptConfiguration instance;

    public static synchronized ScriptConfiguration getInstance() {
        if (instance == null) {
            instance = new ScriptConfiguration();
        }
        return instance;
    }

    private ScriptConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    public void init(MetadataRepository metadataRepository) {
    }

    public Optional<SecurityGroup> getSecurityGroup(String name) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(FETCH_BY_NAME_QUERY, SQLTools.getStringForSQL(name)),
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
                String.format(FETCH_BY_ID_QUERY, SQLTools.getStringForSQL(scriptKey.getScriptId())),
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
                String.format(FETCH_BY_ID_QUERY, SQLTools.getStringForSQL(scriptKey.getScriptId())),
                "reader");
        try {
            if (crsScript.size() == 0) {
                return Optional.empty();
            } else if (crsScript.size() > 1) {
                log.warn(MessageFormat.format("Found multiple implementations for script {0}-{1}. Returning first implementation", scriptKey.getScriptId(), scriptKey.getScriptVersion()));
            }
            crsScript.next();

            // Get the version
            Optional<ScriptVersion> scriptVersion = ScriptVersionConfiguration.getInstance().get(new ScriptVersionKey(new ScriptKey(scriptKey.getScriptId(), scriptKey.getScriptVersion())));
            if (!scriptVersion.isPresent()) {
                return Optional.empty();
            }

            // Get the actions
            List<Action> actions = ActionConfiguration.getInstance().getByScript(scriptKey);

            // Get parameters
            List<ScriptParameter> scriptParameters = ScriptParameterConfiguration.getInstance().getByScript(scriptKey);

            // Get labels
            List<ScriptLabel> scriptLabels = ScriptLabelConfiguration.getInstance().getByScript(scriptKey);

            Script script = new Script(
                    scriptKey,
                    new SecurityGroupKey(UUID.fromString(crsScript.getString("SECURITY_GROUP_ID"))),
                    crsScript.getString("SECURITY_GROUP_NAME"),
                    crsScript.getString("SCRIPT_NM"),
                    crsScript.getString("SCRIPT_DSC"),
                    scriptVersion.get(),
                    scriptParameters,
                    actions,
                    scriptLabels,
                    crsScript.getString("DELETED_AT"));
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
                String.format(EXISTS_BY_ID_QUERY, SQLTools.getStringForSQL(scriptId)),
                "reader");
        return crsScript.size() >= 1;
    }

    public boolean existsByName(String scriptName) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(EXISTS_BY_NAME_QUERY, SQLTools.getStringForSQL(scriptName)),
                "reader");
        return crsScript.size() >= 0;
    }

    public boolean exists(ScriptKey scriptKey) {
        try {
            CachedRowSet crsScript = getMetadataRepository().executeQuery(
                    String.format(EXISTS_BY_ID_QUERY, SQLTools.getStringForSQL(scriptKey.getScriptId())),
                    "reader");
            if (crsScript.size() == 0) {
                return false;
            }
            crsScript.next();
            return ScriptVersionConfiguration.getInstance().exists(new ScriptVersionKey(scriptKey));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String scriptName) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(EXISTS_BY_NAME_QUERY, SQLTools.getStringForSQL(scriptName)),
                "reader");
        return crsScript.size() > 0;
    }

    @Override
    public List<Script> getAll() {
        List<Script> scripts = new ArrayList<>();
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                FETCH_ALL_QUERY,
                "reader");

        try {
            while (crsScript.next()) {
                String scriptId = crsScript.getString("SCRIPT_ID");
                List<ScriptVersion> scriptVersions = ScriptVersionConfiguration.getInstance().getByScriptId(scriptId);
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
        ScriptVersionConfiguration.getInstance().delete(scriptVersionKey);
        ActionConfiguration.getInstance().deleteByScript(scriptKey);
        ScriptParameterConfiguration.getInstance().deleteByScript(scriptKey);
        ScriptLabelConfiguration.getInstance().deleteByScript(scriptKey);
        getDeleteStatement(scriptKey)
                .ifPresent(getMetadataRepository()::executeUpdate);
    }

    public void markScriptInactive (ScriptKey scriptKey) {
        log.trace(MessageFormat.format("Marking Delete script {0}", scriptKey.toString()));
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey(
                new ScriptKey(
                        scriptKey.getScriptId(),
                        scriptKey.getScriptVersion()
                ));
        ScriptVersionConfiguration.getInstance().update(scriptVersionKey);
        getUpdateStatement(scriptKey)
                .ifPresent(getMetadataRepository()::executeUpdate);
    }

    public List<Script> getByName(String scriptName) {
        try {
            log.trace(MessageFormat.format("Fetching scripts by name ''{0}''", scriptName));
            List<Script> scripts = new ArrayList<>();
            CachedRowSet crsScript = getMetadataRepository().executeQuery(
                    String.format(FETCH_BY_NAME_QUERY, SQLTools.getStringForSQL(scriptName)),
                    "reader");
            if (crsScript.size() == 0) {
                return scripts;
            } else if (crsScript.size() > 1) {
                log.warn(MessageFormat.format("Found multiple implementations for Script {0}. Returning first implementation", scriptName));
            }
            crsScript.next();
            List<ScriptVersion> scriptVersions = ScriptVersionConfiguration.getInstance().getByScriptId(crsScript.getString("SCRIPT_ID"));

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
            ScriptParameterConfiguration.getInstance().insert(scriptParameter);
        }

        // add Parameters
        for (ScriptLabel scriptLabel : script.getLabels()) {
            ScriptLabelConfiguration.getInstance().insert(scriptLabel);
        }

        // add version
        ScriptVersionConfiguration.getInstance().insert(script.getVersion());
        // add actions
        for (Action action : script.getActions()) {
            ActionConfiguration.getInstance().insert(action);
        }

        getMetadataRepository().executeUpdate(getInsertStatement(script));
    }

    private String getInsertStatement(Script script) {
        if (!existsById(script.getMetadataKey().getScriptId())) {
            return String.format(INSERT_QUERY,
                    SQLTools.getStringForSQL(script.getMetadataKey().getScriptId()),
                    SQLTools.getStringForSQL(script.getSecurityGroupKey().getUuid()),
                    SQLTools.getStringForSQL(script.getSecurityGroupName()),
                    SQLTools.getStringForSQL(script.getName()),
                    SQLTools.getStringForSQL(script.getDescription()));
        } else {
            return String.format(UPDATE_QUERY,
                    SQLTools.getStringForSQL(script.getDescription()),
                    SQLTools.getStringForSQL(script.getMetadataKey().getScriptId()));
        }
    }

    private Optional<String> getDeleteStatement(ScriptKey scriptVersionKey) {
        CachedRowSet crs = getMetadataRepository().executeQuery(
                String.format(COUNT_QUERY,
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptId()),
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptVersion())),
                "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                return Optional.of(String.format(
                        DELETE_BY_ID_QUERY,
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

    private Optional<String> getUpdateStatement(ScriptKey scriptVersionKey) {
        CachedRowSet crs = getMetadataRepository().executeQuery(
                String.format(COUNT_QUERY,
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptId()),
                        SQLTools.getStringForSQL(scriptVersionKey.getScriptVersion())),
                "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                return Optional.of(String.format(
                        UPDATE_BY_ID_QUERY_FOR_SOFT_DELETE,
                        SQLTools.getStringForSQL(false),
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


    public Optional<Script> getLatestVersion(String scriptName) {
        Optional<ScriptVersion> latestVersion = ScriptVersionConfiguration.getInstance().getLatestVersionNumber(IdentifierTools.getScriptIdentifier(scriptName));
        if (latestVersion.isPresent()) {
            return get(new ScriptKey(latestVersion.get().getScriptId(), latestVersion.get().getNumber()));
        } else {
            return Optional.empty();
        }
    }

}