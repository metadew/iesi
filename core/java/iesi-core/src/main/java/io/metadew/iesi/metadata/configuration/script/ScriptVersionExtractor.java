package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptVersionExtractor implements ResultSetExtractor<List<ScriptVersion>> {
    @Override
    public List<ScriptVersion> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ScriptVersion> stringScriptVersionMap = new HashMap<>();
        ScriptVersion scriptVersion;
        List<ScriptVersion> scriptVersionList = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("SCRIPT_ID");
            scriptVersion = stringScriptVersionMap.get(name);
            if (scriptVersion == null) {
                scriptVersion = mapRow(rs);
                stringScriptVersionMap.put(name, scriptVersion);
            }
            scriptVersion = mapRow(rs);
            scriptVersionList.add(scriptVersion);
        }
        return scriptVersionList;
    }


    private ScriptVersion mapRow(ResultSet rs) throws SQLException {
        ScriptVersionKey scriptVersionKey = ScriptVersionKey.builder()
                .scriptKey(new ScriptKey(rs.getString("SCRIPT_ID"), rs.getLong("SCRIPT_VRS_NB")))
                .build();
        return ScriptVersion.builder()
                .scriptVersionKey(scriptVersionKey)
                .description(rs.getString("SCRIPT_VRS_DSC"))
                .build();
    }
}
