package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.metadata.definition.action.Action;
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

public class ActionConfigurationExtractor implements ResultSetExtractor<List<Action>> {
    @Override
    public List<Action> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Action> actionMap = new HashMap<>();
        Action action;
        while (rs.next()) {
            String name = rs.getString("Actions_SCRIPT_ID");
            action = actionMap.get(name);
            if (action == null) {
                action = mapRow(rs);
                actionMap.put(name, action);
            }
            addMapping(action, rs);
        }
        return new ArrayList<>(actionMap.values());
    }

    private Action mapRow(ResultSet rs) throws SQLException {
        ActionKey actionKey = ActionKey.builder()
                .scriptKey(ScriptKey.builder()
                        .scriptId(rs.getString("Actions_SCRIPT_ID"))
                        .scriptVersion(rs.getLong("Actions_SCRIPT_VRS_NB"))
                        .build())
                .actionId(rs.getString("Actions_ACTION_ID")).build();
        return Action.builder()
                .actionKey(actionKey)
                .number(rs.getLong("Actions_ACTION_NB"))
                .type(rs.getString("Actions_ACTION_TYP_NM"))
                .name(rs.getString("Actions_ACTION_NM"))
                .description(rs.getString("Actions_ACTION_DSC"))
                .component(rs.getString("Actions_COMP_NM"))
                .condition(rs.getString("Actions_CONDITION_VAL"))
                .iteration(rs.getString("Actions_ITERATION_VAL"))
                .errorExpected(rs.getString("Actions_EXP_ERR_FL"))
                .errorStop(rs.getString("Actions_STOP_ERR_FL"))
                .retries(rs.getString("Actions_RETRIES_VAL"))
                .parameters(new ArrayList<>())
                .build();
    }

    private void addMapping(Action action, ResultSet rs) throws SQLException {
        ActionParameterKey actionParameterKey = ActionParameterKey.builder()
                .actionKey(ActionKey.builder()
                        .scriptKey(ScriptKey.builder()
                                .scriptId(rs.getString("ActionParameters_SCRIPT_ID"))
                                .scriptVersion(rs.getLong("ActionParameters_SCRIPT_VRS_NB"))
                                .build())
                        .actionId(rs.getString("ActionParameters_ACTION_ID")).build()
                )
                .parameterName(rs.getString("ActionParameters_ACTION_PAR_NM"))
                .build();
        ActionParameter actionParameter = null;
        if (rs.getString("ActionParameters_SCRIPT_ID") != null) {
            actionParameter = ActionParameter.builder().actionParameterKey(actionParameterKey)
                    .value(rs.getString("ActionParameters_ACTION_PAR_VAL")).build();
       
        }
        action.addParameters(actionParameter);
    }
}
