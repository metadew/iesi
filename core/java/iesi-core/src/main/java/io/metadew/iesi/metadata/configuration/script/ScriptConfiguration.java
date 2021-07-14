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

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
public class ScriptConfiguration extends Configuration<Script, ScriptKey> {

    private static final String FETCH_ALL_ACTIVE_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC, DELETED_AT " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE DELETED_AT = 'NA' ;";

    private static final String FETCH_ALL_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC, DELETED_AT " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + ";";


    private static final String FETCH_ALL_DELETED_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC, DELETED_AT " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE DELETED_AT != 'NA' ;";

    private static final String FETCH_BY_NAME_AND_ACTIVE_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC, DELETED_AT " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_NM = %s AND DELETED_AT = 'NA' ;";

    private static final String FETCH_BY_ID_AND_ACTIVE_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC, DELETED_AT " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s AND DELETED_AT = 'NA' ;";

    private static final String FETCH_BY_ID_AND_DELETED_AT_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC, DELETED_AT " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s AND DELETED_AT = %s' ;";

    private static final String FETCH_BY_ID_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC, DELETED_AT " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s ;";

    private static final String FETCH_DELETED_BY_ID_QUERY = "SELECT " +
            "SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC, DELETED_AT " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s AND DELETED_AT != 'NA' ;";

    private static final String EXISTS_BY_NAME_AND_ACTIVE_QUERY = "SELECT " +
            "SCRIPT_ID " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_NM = %s AND DELETED_AT = 'NA' ;";

    private static final String EXISTS_BY_ID_AND_ACTIVE_QUERY = "SELECT " +
            "SCRIPT_ID " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s AND DELETED_AT = 'NA' ;";

    private static final String EXISTS_BY_ID = "SELECT " +
            "SCRIPT_ID " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s ;";

    private static final String EXISTS_DELETED_BY_ID_AND_TIME_QUERY = "SELECT " +
            "SCRIPT_ID " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s AND DELETED_AT = %s ;";

    private static final String EXISTS_DELETED_BY_ID_QUERY = "SELECT " +
            "SCRIPT_ID " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s AND DELETED_AT != 'NA' ;";


    private static final String INSERT_QUERY = "INSERT INTO " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " (SCRIPT_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NAME, SCRIPT_NM, SCRIPT_DSC, DELETED_AT) VALUES " +
            "(%s, %s, %s, %s, %s, %s);";

    private static final String UPDATE_QUERY = "UPDATE " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " SET " +
            "SCRIPT_DSC = %s " +
            "WHERE SCRIPT_ID = %s;";

    private static final String SOFT_DELETE_BY_ID_QUERY = "UPDATE " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " SET " +
            " DELETED_AT = %s " +
            " WHERE SCRIPT_ID = %s;";

    private static final String DELETE_BY_ID_QUERY = "DELETE FROM " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() +
            " WHERE SCRIPT_ID = %s ;";

    private static final String RESTORE_SOFT_DELETED_BY_ID_AND_TIME_QUERY = "UPDATE " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " SET " +
            " DELETED_AT = 'NA' " +
            " WHERE SCRIPT_ID = %s AND DELETED_AT = %s;";

    private static final String RESTORE_SOFT_DELETED_BY_ID_QUERY = "UPDATE " +
            MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " SET " +
            " DELETED_AT = 'NA' " +
            " WHERE SCRIPT_ID = %s AND DELETED_AT != 'NA';";


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

    /**
     * retrieve security group of script with provided {@code name}
     *
     * @param name: name of the script
     * @return security group of script
     */
    public Optional<SecurityGroup> getSecurityGroup(String name) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(FETCH_BY_NAME_AND_ACTIVE_QUERY, SQLTools.getStringForSQL(name)),
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

    /**
     * retrieve security group of script with provided {@code scriptKey}
     *
     * @param scriptKey: scriptKey of the script
     * @return security group of script
     */
    public Optional<SecurityGroup> getSecurityGroup(ScriptKey scriptKey) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(FETCH_BY_ID_AND_ACTIVE_QUERY, SQLTools.getStringForSQL(scriptKey.getScriptId())),
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
                String.format(FETCH_BY_ID_QUERY,
                        SQLTools.getStringForSQL(scriptKey.getScriptId())),
                "reader");
        try {
            if (crsScript.size() == 0) {
                return Optional.empty();
            } else if (crsScript.size() > 1) {
                log.warn(MessageFormat.format("Found multiple implementations for script {0}. Returning first implementation", scriptKey.getScriptId()));
            }
            crsScript.next();


            Script script = new Script(
                    scriptKey,
                    new SecurityGroupKey(UUID.fromString(crsScript.getString("SECURITY_GROUP_ID"))),
                    crsScript.getString("SECURITY_GROUP_NAME"),
                    crsScript.getString("SCRIPT_NM"),
                    crsScript.getString("SCRIPT_DSC"),
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
                String.format(EXISTS_BY_ID_AND_ACTIVE_QUERY, SQLTools.getStringForSQL(scriptId)),
                "reader");
        return crsScript.size() >= 1;
    }

    public boolean existsByName(String scriptName) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(EXISTS_BY_NAME_AND_ACTIVE_QUERY, SQLTools.getStringForSQL(scriptName)),
                "reader");
        return crsScript.size() >= 0;
    }

    public boolean exists(ScriptKey scriptKey) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(EXISTS_BY_ID, SQLTools.getStringForSQL(scriptKey.getScriptId())),
                "reader");
        return crsScript.size() > 0;

    }

    public boolean existsDeleted(ScriptKey scriptKey) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(EXISTS_DELETED_BY_ID_QUERY, SQLTools.getStringForSQL(scriptKey.getScriptId())),
                "reader");
        return crsScript.size() > 0;
    }

    public boolean exists(String scriptName) {
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                String.format(EXISTS_BY_NAME_AND_ACTIVE_QUERY, SQLTools.getStringForSQL(scriptName)),
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
                scripts.add(new Script(
                        new ScriptKey("SCRIPT_ID"),
                        new SecurityGroupKey(UUID.fromString(crsScript.getString("SECURITY_GROUP_ID"))),
                        crsScript.getString("SECURITY_GROUP_NAME"),
                        crsScript.getString("SCRIPT_NM"),
                        crsScript.getString("SCRIPT_DSC"),
                        crsScript.getString("DELETED_AT"))
                );
            }

        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            log.info(String.format("exception=%s", e));
            log.debug(String.format("exception.stacktrace=%s", stackTrace));
        }
        return scripts;
    }

    public List<Script> getAllActive() {
        List<Script> scripts = new ArrayList<>();
        CachedRowSet crsScript = getMetadataRepository().executeQuery(
                FETCH_ALL_ACTIVE_QUERY,
                "reader");

        try {
            while (crsScript.next()) {
                scripts.add(new Script(
                        new ScriptKey("SCRIPT_ID"),
                        new SecurityGroupKey(UUID.fromString(crsScript.getString("SECURITY_GROUP_ID"))),
                        crsScript.getString("SECURITY_GROUP_NAME"),
                        crsScript.getString("SCRIPT_NM"),
                        crsScript.getString("SCRIPT_DSC"),
                        crsScript.getString("DELETED_AT"))
                );
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
        log.trace(MessageFormat.format("Marking Delete script {0}", scriptKey.toString()));
        ScriptVersionConfiguration.getInstance().deleteByScriptKey(scriptKey);
        getMetadataRepository().executeUpdate(String.format(
                DELETE_BY_ID_QUERY,
                SQLTools.getStringForSQL(scriptKey.getScriptId())));
    }

    public void softDelete(ScriptKey scriptKey) {
        log.trace(MessageFormat.format("Marking Soft Delete script {0}", scriptKey.toString()));
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String deletedAt = localDateTime.format(formatter);
        ScriptVersionConfiguration.getInstance().softDeleteByScriptKey(scriptKey, deletedAt);
        getMetadataRepository().executeUpdate(String.format(
                SOFT_DELETE_BY_ID_QUERY,
                SQLTools.getStringForSQL(deletedAt),
                SQLTools.getStringForSQL(scriptKey.getScriptId())));
    }

    public Optional<Script> getActiveByName(String scriptName) {
        try {
            log.trace(MessageFormat.format("Fetching scripts by name ''{0}''", scriptName));
            List<Script> scripts = new ArrayList<>();
            CachedRowSet crsScript = getMetadataRepository().executeQuery(
                    String.format(FETCH_BY_NAME_AND_ACTIVE_QUERY, SQLTools.getStringForSQL(scriptName)),
                    "reader");
            if (crsScript.size() == 0) {
                return Optional.empty();
            }
            crsScript.next();
            return Optional.of(new Script(
                    new ScriptKey(crsScript.getString("SCRIPT_ID")),
                    new SecurityGroupKey(UUID.fromString(crsScript.getString("SECURITY_GROUP_ID"))),
                    crsScript.getString("SECURITY_GROUP_NAME"),
                    crsScript.getString("SCRIPT_NM"),
                    crsScript.getString("SCRIPT_DSC"),
                    crsScript.getString("DELETED_AT"))
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByName(String scriptName) {
        getActiveByName(scriptName)
                .ifPresent(script -> delete(script.getMetadataKey()));
    }

    public void insert(Script script) {
        log.trace(MessageFormat.format("Inserting script {0}-{1}.", script.getName()));
        if (exists(script)) {
            throw new MetadataAlreadyExistsException(script);
        }

        getMetadataRepository().executeUpdate(getInsertStatement(script));

    }

    @Override
    public void update(Script script) {
        getMetadataRepository().executeUpdate(getUpdateQuery(script));
    }

    private String getInsertStatement(Script script) {
//        if (existsDeleted(script.getMetadataKey())) {
//            return String.format(RESTORE_SOFT_DELETED_BY_ID_QUERY,
//                    SQLTools.getStringForSQL(script.getMetadataKey().getScriptId()));
//        } else if (!existsById(script.getMetadataKey().getScriptId())) {
            return String.format(INSERT_QUERY,
                    SQLTools.getStringForSQL(script.getMetadataKey().getScriptId()),
                    SQLTools.getStringForSQL(script.getSecurityGroupKey().getUuid()),
                    SQLTools.getStringForSQL(script.getSecurityGroupName()),
                    SQLTools.getStringForSQL(script.getName()),
                    SQLTools.getStringForSQL(script.getDescription()),
                    SQLTools.getStringForSQL(script.getDeletedAt()));

    }

    private String getUpdateQuery(Script script) {
        return String.format(UPDATE_QUERY,
                SQLTools.getStringForSQL(script.getDescription()),
                SQLTools.getStringForSQL(script.getMetadataKey().getScriptId()));
    }

//    private Optional<String> getDeleteStatement(ScriptKey scriptKey) {
//        CachedRowSet crs = getMetadataRepository().executeQuery(
//                String.format(COUNT_QUERY,
//                        SQLTools.getStringForSQL(scriptKey.getScriptId()),
//                        SQLTools.getStringForSQL(scriptKey.getScriptVersion())),
//                "reader");
//
//        try {
//            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
//                return Optional.of(String.format(
//                        DELETE_BY_ID_QUERY,
//                        SQLTools.getStringForSQL(scriptKey.getScriptId())));
//
//            } else {
//                return Optional.empty();
//            }
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            log.info(String.format("exception=%s", e));
//            log.debug(String.format("exception.stacktrace=%s", stackTrace));
//            return Optional.empty();
//        }
//    }

//    private Optional<String> getSoftDeleteStatement(ScriptKey scriptKey, String timeStamp) {
//        CachedRowSet crs = getMetadataRepository().executeQuery(
//                String.format(COUNT_ACTIVE_QUERY,
//                        SQLTools.getStringForSQL(scriptKey.getScriptId()),
//                        SQLTools.getStringForSQL(scriptKey.getScriptVersion())),
//                "reader");
//
//        try {
//            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
//                return Optional.of(String.format(
//                        SOFT_DELETE_BY_ID_QUERY,
//                        SQLTools.getStringForSQL(timeStamp),
//                        SQLTools.getStringForSQL(scriptKey.getScriptId()),
//                        SQLTools.getStringForSQL(scriptKey.getDeletedAt())));
//            } else {
//                return Optional.empty();
//            }
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//
//            log.info(String.format("exception=%s", e));
//            log.debug(String.format("exception.stacktrace=%s", stackTrace));
//            return Optional.empty();
//        }
//    }

}