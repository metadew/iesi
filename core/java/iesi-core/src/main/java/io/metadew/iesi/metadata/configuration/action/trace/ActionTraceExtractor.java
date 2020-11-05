package io.metadew.iesi.metadata.configuration.action.trace;

import io.metadew.iesi.metadata.definition.action.trace.ActionTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionTraceKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionTraceExtractor implements ResultSetExtractor<List<ActionTrace>> {
    @Override
    public List<ActionTrace> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ActionTrace> actionTraceMap = new HashMap<>();
        ActionTrace actionTrace;
        List<ActionTrace> actionTraces = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("ActionTraces_RUN_ID");
            actionTrace = actionTraceMap.get(name);
            if (actionTrace == null) {
                actionTrace = mapRow(rs);
                actionTraceMap.put(name, actionTrace);
            }
            actionTrace = mapRow(rs);
            actionTraces.add(actionTrace);
        }
        return actionTraces;
    }

    private ActionTrace mapRow(ResultSet rs) throws SQLException {
        ActionTraceKey actionTraceKey = ActionTraceKey.builder().runId(rs.getString("ActionTraces_RUN_ID"))
                .processId(rs.getLong("ActionTraces_PRC_ID")).actionId(rs.getString("ActionTraces_ACTION_ID"))
                .build();
        return ActionTrace.builder().actionTraceKey(actionTraceKey)
                .number(rs.getLong("ActionTraces_ACTION_NB"))
                .type(rs.getString("ActionTraces_ACTION_TYP_NM"))
                .name(rs.getString("ActionTraces_ACTION_NM"))
                .description(rs.getString("ActionTraces_ACTION_DSC"))
                .component(rs.getString("ActionTraces_COMP_NM"))
                .iteration(rs.getString("ActionTraces_ITERATION_VAL"))
                .errorExpected(rs.getString("ActionTraces_EXP_ERR_FL"))
                .errorStop(rs.getString("ActionTraces_STOP_ERR_FL"))
                .retries(rs.getInt("ActionTraces_RETRIES_VAL"))
                .build();
    }
}