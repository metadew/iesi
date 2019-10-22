package io.metadew.iesi.metadata.configuration.action.result;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.result.exception.ActionResultAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.action.result.exception.ActionResultDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.result.ActionResult;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionResultConfiguration extends Configuration<ActionResult, ActionResultKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionResultConfiguration INSTANCE;

    public synchronized static ActionResultConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionResultConfiguration();
        }
        return INSTANCE;
    }

    private ActionResultConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ActionResult> get(ActionResultKey actionResultKey) {
        try {
            String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
                    + getMetadataRepository().getTableNameByLabel("ActionResults")
                    + " where RUN_ID = " + SQLTools.GetStringForSQL(actionResultKey.getRunId()) + " and PRC_ID = " + SQLTools.GetStringForSQL(actionResultKey.getProcessId())
                    + " and ACTION_ID = " + SQLTools.GetStringForSQL(actionResultKey.getActionId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionResult {0}. Returning first implementation", actionResultKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionResult(actionResultKey,
                    cachedRowSet.getLong("SCRIPT_PRC_ID"),
                    cachedRowSet.getString("ACTION_NM"),
                    cachedRowSet.getString("ENV_NM"),
                    cachedRowSet.getString("ST_NM"),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionResult> getAll() {
        try {
            List<ActionResult> scriptResults = new ArrayList<>();
            String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
                    + getMetadataRepository().getTableNameByLabel("ActionResults") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptResults.add(new ActionResult(new ActionResultKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("ACTION_ID")),
                        cachedRowSet.getLong("SCRIPT_PRC_ID"),
                        cachedRowSet.getString("ACTION_NM"),
                        cachedRowSet.getString("ENV_NM"),
                        cachedRowSet.getString("ST_NM"),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
            }
            return scriptResults;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionResultKey actionResultKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ActionResult {0}.", actionResultKey.toString()));
        if (!exists(actionResultKey)) {
            throw new ActionResultDoesNotExistException(MessageFormat.format(
                    "ActionResult {0} does not exists", actionResultKey.toString()));
        }
        String deleteStatement = deleteStatement(actionResultKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionResultKey actionResultKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionResults") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionResultKey.getRunId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionResultKey.getActionId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(actionResultKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ActionResult actionResult) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ActionResult {0}.", actionResult.toString()));
        if (exists(actionResult.getMetadataKey())) {
            throw new ActionResultAlreadyExistsException(MessageFormat.format(
                    "ActionResult {0} already exists", actionResult.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(actionResult);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ActionResult actionResult) {
        return "INSERT INTO "
                + getMetadataRepository().getTableNameByLabel("ActionResults")
                + " (RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS) VALUES ("
                + SQLTools.GetStringForSQL(actionResult.getMetadataKey().getRunId()) + ","
                + SQLTools.GetStringForSQL(actionResult.getMetadataKey().getProcessId()) + ","
                + SQLTools.GetStringForSQL(actionResult.getScriptProcessId()) + ","
                + SQLTools.GetStringForSQL(actionResult.getMetadataKey().getActionId()) + ","
                + SQLTools.GetStringForSQL(actionResult.getActionName()) + ","
                + SQLTools.GetStringForSQL(actionResult.getEnvironment()) + ","
                + SQLTools.GetStringForSQL(actionResult.getStatus()) + ","
                + SQLTools.GetStringForSQL(actionResult.getStartTimestamp()) + ","
                + SQLTools.GetStringForSQL(actionResult.getEndTimestamp()) + ");";
    }

//    public List<ActionResult> getActions(String runId) {
//        List<ActionResult> actionResults = new ArrayList<>();
//        String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
//                + getMetadataRepository()
//                .getTableNameByLabel("ActionResults")
//                + " where RUN_ID = '" + runId + "' order by PRC_ID asc, STRT_TMS asc";
//        CachedRowSet crsActionResults = getMetadataRepository()
//                .executeQuery(query, "reader");
//        try {
//            while (crsActionResults.next()) {
//                ActionResult actionResult = new ActionResult();
//                Long processId = crsActionResults.getLong("PRC_ID");
//                actionResult.setProcessId(processId);
//                actionResult.setScriptProcessId(crsActionResults.getLong("SCRIPT_PRC_ID"));
//                actionResult.setActionId(crsActionResults.getString("ACTION_ID"));
//                actionResult.setActionName(crsActionResults.getString("ACTION_NM"));
//                actionResult.setEnvironment(crsActionResults.getString("ENV_NM"));
//                actionResult.setStatus(crsActionResults.getString("ST_NM"));
//                actionResult.setStartTimestamp(crsActionResults.getString("STRT_TMS"));
//                actionResult.setEndTimestamp(crsActionResults.getString("END_TMS"));
//                actionResult.setOutputs(actionResultOutputConfiguration.getActionResultOutputs(runId, processId));
//                actionResults.add(actionResult);
//            }
//            crsActionResults.close();
//        } catch (Exception e) {
//            StringWriter StackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(StackTrace));
//        }
//
//        if (actionResults.size() == 0) {
//            throw new RuntimeException("actionresult.error.empty");
//        }
//
//        return actionResults;
//    }
//
//    public List<ActionResult> getActions(String runId, Long scriptProcessId) {
//        List<ActionResult> actionResults = new ArrayList<>();
//        String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
//                + getMetadataRepository()
//                .getTableNameByLabel("ActionResults")
//                + " where RUN_ID = '" + runId + "' and SCRIPT_PRC_ID = " + scriptProcessId + " order by PRC_ID asc, STRT_TMS asc";
//        CachedRowSet crsActionResults = getMetadataRepository()
//                .executeQuery(query, "reader");
//        try {
//            while (crsActionResults.next()) {
//                ActionResult actionResult = new ActionResult();
//                actionResult.setProcessId(crsActionResults.getLong("PRC_ID"));
//                actionResult.setScriptProcessId(crsActionResults.getLong("SCRIPT_PRC_ID"));
//                actionResult.setActionId(crsActionResults.getString("ACTION_ID"));
//                actionResult.setActionName(crsActionResults.getString("ACTION_NM"));
//                actionResult.setEnvironment(crsActionResults.getString("ENV_NM"));
//                actionResult.setStatus(crsActionResults.getString("ST_NM"));
//                actionResult.setStartTimestamp(crsActionResults.getString("STRT_TMS"));
//                actionResult.setEndTimestamp(crsActionResults.getString("END_TMS"));
//                actionResults.add(actionResult);
//            }
//            crsActionResults.close();
//        } catch (Exception e) {
//            StringWriter StackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(StackTrace));
//        }
//
//        if (actionResults.size() == 0) {
//            throw new RuntimeException("actionresult.error.empty");
//        }
//
//        return actionResults;
//    }

}