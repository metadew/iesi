package io.metadew.iesi.metadata.configuration.action;


import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionParameterExtractor implements ResultSetExtractor<List<ActionParameter>> {
    @Override
    public List<ActionParameter> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ActionParameter> actionParameterMap = new HashMap<>();
        ActionParameter actionParameter;
        List<ActionParameter> actionParameters = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("SCRIPT_ID");
            actionParameter = actionParameterMap.get(name);
            if (actionParameter == null) {
                actionParameter = mapRow(rs);
                actionParameterMap.put(name, actionParameter);
            }
            actionParameter = mapRow(rs);
            actionParameters.add(actionParameter);
        }
        return actionParameters;
    }

    private ActionParameter mapRow(ResultSet rs) throws SQLException {
        ActionParameterKey actionParameterKey = ActionParameterKey.builder()
                .actionKey(ActionKey.builder()
                        .scriptKey(ScriptKey.builder()
                                .scriptId(rs.getString("SCRIPT_ID"))
                                .scriptVersion(rs.getLong("SCRIPT_VRS_NB"))
                                .build())
                        .actionId(rs.getString("ACTION_ID")).build()
                )
                .parameterName(rs.getString("ACTION_PAR_NM"))
                .build();
        return ActionParameter.builder().actionParameterKey(actionParameterKey)
                .value(rs.getString("ACTION_PAR_VAL")).build();
    }
}
