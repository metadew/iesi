package io.metadew.iesi.metadata.configuration.action.performance;

import io.metadew.iesi.metadata.definition.action.performance.ActionPerformance;
import io.metadew.iesi.metadata.definition.action.performance.key.ActionPerformanceKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionPerformanceExtractorConfiguration implements ResultSetExtractor<List<ActionPerformance>> {
    @Override
    public List<ActionPerformance> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ActionPerformance> actionMap = new HashMap<>();
        ActionPerformance actionPerformance;
        List<ActionPerformance> actionPerformances = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("RUN_ID");
            actionPerformance = actionMap.get(name);
            if (actionPerformance == null) {
                actionPerformance = mapRow(rs);
                actionMap.put(name, actionPerformance);
            }
            actionPerformance = mapRow(rs);
            actionPerformances.add(actionPerformance);
        }
        return new ArrayList<>(actionMap.values());
    }

    private ActionPerformance mapRow(ResultSet rs) throws SQLException {
        ActionPerformanceKey actionPerformanceKey = ActionPerformanceKey.builder()
                .runId(rs.getString("RUN_ID")).procedureId(rs.getLong("PRC_ID")).scope(rs.getString("SCOPE_NM")).build();
        return ActionPerformance.builder()
                .actionPerformanceKey(actionPerformanceKey)
                .context(rs.getString("CONTEXT_NM"))
                .actionId(rs.getString("ACTION_ID"))
                .startTimestamp(rs.getTimestamp("STRT_TMS").toLocalDateTime())
                .stopTimestamp(rs.getTimestamp("END_TMS").toLocalDateTime())
                .duration(rs.getDouble("DURATION_VAL"))
                .build();
    }
}
