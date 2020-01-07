package io.metadew.iesi.metadata.configuration.action.performance;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.performance.exception.ActionPerformanceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.action.performance.exception.ActionPerformanceDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.performance.ActionPerformance;
import io.metadew.iesi.metadata.definition.action.performance.key.ActionPerformanceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionPerformanceConfiguration extends Configuration<ActionPerformance, ActionPerformanceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionPerformanceConfiguration INSTANCE;

    public synchronized static ActionPerformanceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionPerformanceConfiguration();
        }
        return INSTANCE;
    }

    private ActionPerformanceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }


    @Override
    public Optional<ActionPerformance> get(ActionPerformanceKey key) {
        try {
            String queryAction = "select RUN_ID, PRC_ID, ACTION_ID, CONTEXT_NM, SCOPE_NM, STRT_TMS, END_TMS, DURATION_VAL from "
                    + getMetadataRepository().getTableNameByLabel("ActionResultPerformances") + " where " +
                    "RUN_ID = " + SQLTools.GetStringForSQL(key.getRunId()) + " AND " +
                    "PRC_ID = " + key.getProcedureId() + " AND " +
                    "SCOPE_NM = " + SQLTools.GetStringForSQL(key.getScope()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryAction, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionParameterTrace {0}. Returning first implementation", key.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionPerformance(new ActionPerformanceKey(cachedRowSet.getString("RUN_ID"),
                    cachedRowSet.getLong("PRC_ID"),
                    cachedRowSet.getString("SCOPE_NM")),
                    cachedRowSet.getString("CONTEXT_NM"),
                    cachedRowSet.getString("ACTION_ID"),
                    cachedRowSet.getTimestamp("STRT_TMS").toLocalDateTime(),
                    cachedRowSet.getTimestamp("END_TMS").toLocalDateTime(),
                    cachedRowSet.getDouble("DURATION_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionPerformance> getAll() {
        try {
            List<ActionPerformance> actionPerformances = new ArrayList<>();
            String queryAction = "select RUN_ID, PRC_ID, ACTION_ID, CONTEXT_NM, SCOPE_NM, STRT_TMS, END_TMS, DURATION_VAL from "
                    + getMetadataRepository().getTableNameByLabel("ActionResultPerformances") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryAction, "reader");
            while (cachedRowSet.next()) {
                actionPerformances.add(new ActionPerformance(new ActionPerformanceKey(cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("SCOPE_NM")),
                        cachedRowSet.getString("CONTEXT_NM"),
                        cachedRowSet.getString("ACTION_ID"),
                        cachedRowSet.getTimestamp("STRT_TMS").toLocalDateTime(),
                        cachedRowSet.getTimestamp("END_TMS").toLocalDateTime(),
                        cachedRowSet.getDouble("DURATION_VAL")));
            }
            return actionPerformances;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionPerformanceKey key) throws ActionPerformanceDoesNotExistException {
        String queryAction = "delete from "
                + getMetadataRepository().getTableNameByLabel("ActionResultPerformances") + " where " +
                "RUN_ID = " + SQLTools.GetStringForSQL(key.getRunId()) + " AND " +
                "PRC_ID = " + SQLTools.GetStringForSQL(key.getProcedureId()) + " AND " +
                "SCOPE_NM = " + SQLTools.GetStringForSQL(key.getScope()) + ";";
        getMetadataRepository().executeUpdate(queryAction);
    }

    @Override
    public void insert(ActionPerformance actionPerformance) throws ActionPerformanceAlreadyExistsException {
        String queryAction = "insert into "
                + getMetadataRepository().getTableNameByLabel("ActionResultPerformances") +
                " (RUN_ID, PRC_ID, ACTION_ID, SCOPE_NM, CONTEXT_NM, STRT_TMS, END_TMS, DURATION_VAL) values (" +
                SQLTools.GetStringForSQL(actionPerformance.getMetadataKey().getRunId()) + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getMetadataKey().getProcedureId()) + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getActionId()) + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getMetadataKey().getScope()) + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getContext()) + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getStartTimestamp()) + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getEndTimestamp()) + ", " +
                SQLTools.GetStringForSQL(actionPerformance.getDuration()) + ");";
        getMetadataRepository().executeUpdate(queryAction);
    }

    @Override
    public void update(ActionPerformance actionPerformance) {
        String queryAction = "UPDATE " + getMetadataRepository().getTableNameByLabel("ActionResultPerformances") +
                " SET " +
                "CONTEXT_NM = " + SQLTools.GetStringForSQL(actionPerformance.getContext()) + ", " +
                "STRT_TMS = " + SQLTools.GetStringForSQL(actionPerformance.getStartTimestamp()) + ", " +
                "END_TMS = " + SQLTools.GetStringForSQL(actionPerformance.getEndTimestamp()) + ", " +
                "DURATION_VAL = " + SQLTools.GetStringForSQL(actionPerformance.getDuration()) +
                " WHERE RUN_ID = " + SQLTools.GetStringForSQL(actionPerformance.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.GetStringForSQL(actionPerformance.getMetadataKey().getProcedureId()) +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionPerformance.getActionId()) +
                " AND SCOPE_NM =" + SQLTools.GetStringForSQL(actionPerformance.getMetadataKey().getScope()) + ";";


        getMetadataRepository().executeUpdate(queryAction);
    }
}
