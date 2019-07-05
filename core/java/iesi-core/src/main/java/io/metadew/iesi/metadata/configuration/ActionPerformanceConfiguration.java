package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.exception.ActionPerformanceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ActionPerformanceDoesNotExistException;
import io.metadew.iesi.metadata.definition.ActionPerformance;
import io.metadew.iesi.metadata.definition.key.ActionPerformanceKey;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionPerformanceConfiguration extends Configuration<ActionPerformance, ActionPerformanceKey> {


    public ActionPerformanceConfiguration(MetadataControl metadataControl) {
        super(metadataControl);
    }

    @Override
    public Optional<ActionPerformance> get(ActionPerformanceKey key) throws SQLException {
        String queryAction = "select RUN_ID, PRC_ID, ACTION_ID, CONTEXT_NM, SCOPE_NM, STRT_TMS, END_TMS, DURATION_VAL from "
                + getMetadataControl().getResultMetadataRepository().getTableNameByLabel("ActionResultPerformances")+ " where " +
                "RUN_ID = " + SQLTools.GetStringForSQL(key.getRunId()) + " AND " +
                "PRC_ID = " + key.getProcedureId() + " AND " +
                "ACTION_ID = " + SQLTools.GetStringForSQL(key.getActionId()) + " AND " +
                "SCOPE_NM = " + SQLTools.GetStringForSQL(key.getScope()) + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getResultMetadataRepository().executeQuery(queryAction, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            // TODO: log
        }
        cachedRowSet.next();
        return Optional.of(new ActionPerformance(new ActionPerformanceKey(cachedRowSet.getString("RUN_ID"),
                cachedRowSet.getLong("PRC_ID"),
                cachedRowSet.getString("ACTION_ID"),
                cachedRowSet.getString("SCOPE_NM")),
                cachedRowSet.getString("CONTEXT_NM"),
                cachedRowSet.getTimestamp("STRT_TMS").toLocalDateTime(),
                cachedRowSet.getTimestamp("END_TMS").toLocalDateTime(),
                cachedRowSet.getDouble("DURATION_VAL")));
    }

    @Override
    public List<ActionPerformance> getAll() throws SQLException {
        List<ActionPerformance> actionPerformances = new ArrayList<>();
        String queryAction = "select RUN_ID, PRC_ID, ACTION_ID, CONTEXT_NM, SCOPE_NM, STRT_TMS, END_TMS, DURATION_VAL from "
                + getMetadataControl().getResultMetadataRepository().getTableNameByLabel("ActionResultPerformances")+ ";";
        CachedRowSet cachedRowSet = getMetadataControl().getResultMetadataRepository().executeQuery(queryAction, "reader");
        while (cachedRowSet.next()) {
            actionPerformances.add(new ActionPerformance(new ActionPerformanceKey(cachedRowSet.getString("RUN_ID"),
                    cachedRowSet.getLong("PRC_ID"),
                    cachedRowSet.getString("ACTION_ID"),
                    cachedRowSet.getString("SCOPE_NM")),
                    cachedRowSet.getString("CONTEXT_NM"),
                    cachedRowSet.getTimestamp("STRT_TMS").toLocalDateTime(),
                    cachedRowSet.getTimestamp("END_TMS").toLocalDateTime(),
                    cachedRowSet.getDouble("DURATION_VAL")));
        }
        return actionPerformances;
    }

    @Override
    public void delete(ActionPerformanceKey key) throws SQLException, ActionPerformanceDoesNotExistException {
        if (!exists(key)) {
            throw new ActionPerformanceDoesNotExistException(MessageFormat.format("Action Performance {0}-{1}-{2}-{3} does not exist",
                    key.getRunId(), key.getProcedureId(), key.getActionId(), key.getScope()));
        }
        String queryAction = "delete from "
                + getMetadataControl().getResultMetadataRepository().getTableNameByLabel("ActionResultPerformances")+ " where " +
                "RUN_ID = " + SQLTools.GetStringForSQL(key.getRunId()) + " AND " +
                "PRC_ID = " + key.getProcedureId() + " AND " +
                "ACTION_ID = " + SQLTools.GetStringForSQL(key.getActionId()) + " AND " +
                "SCOPE_NM = " + SQLTools.GetStringForSQL(key.getScope()) + ";";
        getMetadataControl().getResultMetadataRepository().executeUpdate(queryAction);
    }

    @Override
    public void insert(ActionPerformance actionPerformance) throws SQLException, ActionPerformanceAlreadyExistsException {
        if (exists(actionPerformance.getMetadataKey())) {
            throw new ActionPerformanceAlreadyExistsException(MessageFormat.format("Action Performance {0}-{1}-{2}-{3} already exists",
                    actionPerformance.getMetadataKey().getRunId(), actionPerformance.getMetadataKey().getProcedureId(),
                    actionPerformance.getMetadataKey().getActionId(), actionPerformance.getMetadataKey().getScope()));
        }
        String queryAction = "insert into "
                + getMetadataControl().getResultMetadataRepository().getTableNameByLabel("ActionResultPerformances")+
                " (RUN_ID, PRC_ID, ACTION_ID, CONTEXT_NM, SCOPE_NM, STRT_TMS, END_TMS, DURATION_VAL) values (" +
                SQLTools.GetStringForSQL(actionPerformance.getMetadataKey().getRunId()) + ", " +
                actionPerformance.getMetadataKey().getProcedureId() + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getMetadataKey().getActionId()) + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getMetadataKey().getScope()) + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getContext()) + ", " +
                SQLTools.GetStringForSQL(Timestamp.valueOf(actionPerformance.getStartTimestamp())) + ", " +
                SQLTools.GetStringForSQL(Timestamp.valueOf(actionPerformance.getEndTimestamp())) + ", " +
                actionPerformance.getDuration() + ");";
        getMetadataControl().getResultMetadataRepository().executeUpdate(queryAction);
    }
}
