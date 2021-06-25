package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
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

    private ActionParameterConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    @Override
    public Optional<ActionParameter> get(ActionParameterKey actionParameterKey) {
        try {
            String queryActionParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                    + getMetadataRepository().getTableNameByLabel("ActionParameters") +
                    " where SCRIPT_ID = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getScriptKey().getScriptId()) +
                    " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getScriptKey().getScriptVersion()) +
                    " AND ACTION_ID = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getActionId()) +
                    " and ACTION_PAR_NM = " + SQLTools.getStringForSQL(actionParameterKey.getParameterName()) +
                    " and DELETED_AT = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getScriptKey().getDeletedAt()) ;
            CachedRowSet cachedRowSet = getMetadataRepository()
                    .executeQuery(queryActionParameter, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for ActionParameter {0}. Returning first implementation", actionParameterKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionParameter(actionParameterKey,
                    SQLTools.getStringFromSQLClob(cachedRowSet, "ACTION_PAR_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionParameter> getAll() {
        List<ActionParameter> actionParameters = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ActionParameters") ;
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ActionParameterKey actionParameterKey = new ActionParameterKey(
                        crs.getString("SCRIPT_ID"),
                        crs.getLong("SCRIPT_VRS_NB"),
                        crs.getString("ACTION_ID"),
                        crs.getString("ACTION_PAR_NM"),
                        crs.getString("DELETED_AT"));
                actionParameters.add(new ActionParameter(actionParameterKey,
                        SQLTools.getStringFromSQLClob(crs, "ACTION_PAR_VAL")));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return actionParameters;
    }

    @Override
    public void delete(ActionParameterKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionParameter {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public String deleteStatement(ActionParameterKey actionParameterKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getScriptKey().getScriptVersion()) +
                " AND ACTION_ID = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getActionId()) +
                " AND ACTION_PAR_NM = " + SQLTools.getStringForSQL(actionParameterKey.getParameterName()) + ";";
    }

    @Override
    public void insert(ActionParameter actionParameter) {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameter {0}.", actionParameter.toString()));
        if (exists(actionParameter)) {
            throw new MetadataAlreadyExistsException(actionParameter);
        }
        String insertQuery = "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ActionParameters") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL, DELETED_AT ) VALUES (" +
                SQLTools.getStringForSQL(actionParameter.getMetadataKey().getActionKey().getScriptKey().getScriptId()) + "," +
                SQLTools.getStringForSQL(actionParameter.getMetadataKey().getActionKey().getScriptKey().getScriptVersion()) + "," +
                SQLTools.getStringForSQL(actionParameter.getMetadataKey().getActionKey().getActionId()) + "," +
                SQLTools.getStringForSQL(actionParameter.getMetadataKey().getParameterName()) + "," +
                SQLTools.getStringForSQLClob(actionParameter.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + "," +
                SQLTools.getStringForSQL(actionParameter.getMetadataKey().getActionKey().getScriptKey().getDeletedAt()) + " );";
        getMetadataRepository().executeUpdate(insertQuery);
    }

    public boolean exists(ActionParameterKey actionParameterKey) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                + getMetadataRepository().getTableNameByLabel("ActionParameters")
                + " where SCRIPT_ID = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getScriptKey().getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getScriptKey().getScriptVersion())
                + " AND ACTION_ID = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getActionId()) +
                " and ACTION_PAR_NM = " + SQLTools.getStringForSQL(actionParameterKey.getParameterName()) +
                " and DELETED_AT = " + SQLTools.getStringForSQL(actionParameterKey.getActionKey().getScriptKey().getDeletedAt());
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public List<ActionParameter> getByAction(ActionKey actionKey) {
        List<ActionParameter> actionParameters = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ActionParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptVersion()) +
                " AND ACTION_ID = " + SQLTools.getStringForSQL(actionKey.getActionId()) +
                " AND DELETED_AT = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getDeletedAt());
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                actionParameters.add(new ActionParameter(new ActionParameterKey(actionKey,
                        crs.getString("ACTION_PAR_NM")),
                        SQLTools.getStringFromSQLClob(crs, "ACTION_PAR_VAL")));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return actionParameters;
    }

    public void deleteByAction(ActionKey actionKey) {
        String query = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptVersion()) +
                " AND ACTION_ID = " + SQLTools.getStringForSQL(actionKey.getActionId()) + ";";
        getMetadataRepository().executeUpdate(query);
    }

    public void deleteByScript(ScriptKey scriptKey) {
        LOGGER.trace(MessageFormat.format("Deleting action parameters for script {0}", scriptKey.toString()));
        String query = "UPDATE " + getMetadataRepository().getTableNameByLabel("ActionParameters") +
                " SET DELETED_AT = " + SQLTools.getStringForSQL(scriptKey.getDeletedAt()) +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(scriptKey.getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptKey.getScriptVersion()) +
                " AND DELETED_AT = 'NA' ;";
        getMetadataRepository().executeUpdate(query);
    }

}