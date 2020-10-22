package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptParameterExtractor implements ResultSetExtractor<List<ScriptParameter>> {
    @Override
    public List<ScriptParameter> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ScriptParameter> scriptParameterMap = new HashMap<>();
        ScriptParameter scriptParameter;
        List<ScriptParameter> scriptParameters = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("SCRIPT_ID");
            scriptParameter = scriptParameterMap.get(name);
            if (scriptParameter == null) {
                scriptParameter = mapRow(rs);
                scriptParameterMap.put(name, scriptParameter);
            }
            scriptParameter = mapRow(rs);
            scriptParameters.add(scriptParameter);
        }
        return scriptParameters;
    }

    private ScriptParameter mapRow(ResultSet rs) throws SQLException {
        ScriptParameterKey scriptParameterKey = ScriptParameterKey.builder()
                .scriptKey(new ScriptKey(rs.getString("SCRIPT_ID"), rs.getLong("SCRIPT_VRS_NB")))
                .parameterName(rs.getString("SCRIPT_PAR_NM"))
                .build();
        return ScriptParameter.builder()
                .scriptParameterKey(scriptParameterKey)
                .value(rs.getString("SCRIPT_PAR_VAL"))
                .build();
    }
}
