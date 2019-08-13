package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.exception.action.ActionParameterAlreadyExistsException;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

public class ActionParameterConfiguration {

    private final static Logger LOGGER = LogManager.getLogger();


    public ActionParameterConfiguration() {
    }

    public String getInsertStatement(String scriptId, long scriptVersionNumber, String actionId, ActionParameter actionParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
                .getTableNameByLabel("ActionParameters") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + "," +
                scriptVersionNumber + "," +
                SQLTools.GetStringForSQL(actionId) + "," +
                SQLTools.GetStringForSQL(actionParameter.getName()) + "," +
                SQLTools.GetStringForSQL(actionParameter.getValue()) + ");";
    }

    public Optional<ActionParameter> get(String scriptId, long scriptVersionNumber, String actionId, String actionParameterName) throws SQLException {
        ActionParameter actionParameter = new ActionParameter();
        String queryActionParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ActionParameters")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber)
                + " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " and ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterName) + ";";
        CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository()
                .executeQuery(queryActionParameter, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.info(MessageFormat.format("Found multiple implementations for ActionParameter {0}-{1}-{2}-{4}. Returning first implementation", scriptId, scriptVersionNumber, actionId, actionParameter.getName()));
        }
        cachedRowSet.next();
        return Optional.of(new ActionParameter(actionParameterName, cachedRowSet.getString("ACTION_PAR_VAL")));
    }


    public void insert(String scriptId, long scriptVersionNumber, String actionId, ActionParameter actionParameter) throws ActionParameterAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameter {0}-{1}-{2}-{3}.", scriptId, scriptVersionNumber, actionId, actionParameter.getName()));
        if (exists(scriptId, scriptVersionNumber, actionId, actionParameter)) {
            throw new ActionParameterAlreadyExistsException(MessageFormat.format(
                    "ActionParameter {0}-{1}-{2}-{3} already exists", scriptId, scriptVersionNumber, actionId, actionParameter.getName()));
        }
        String insertQuery = "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
                .getTableNameByLabel("ActionParameters") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + "," +
                scriptVersionNumber + "," +
                SQLTools.GetStringForSQL(actionId) + "," +
                SQLTools.GetStringForSQL(actionParameter.getName()) + "," +
                SQLTools.GetStringForSQL(actionParameter.getValue()) + ");";
        MetadataControl.getInstance().getDesignMetadataRepository().executeUpdate(insertQuery);
    }

    private boolean exists(String scriptId, long scriptVersionNumber, String actionId, ActionParameter actionParameter) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ActionParameters")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber)
                + " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " and ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameter.getName()) + ";";
        CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

}