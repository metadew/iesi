package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptLabelExtractor implements ResultSetExtractor<List<ScriptLabel>> {
    @Override
    public List<ScriptLabel> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ScriptLabel> scriptLabelMap = new HashMap<>();
        ScriptLabel scriptLabel;
        List<ScriptLabel> scriptLabels = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("SCRIPT_ID");
            scriptLabel = scriptLabelMap.get(name);
            if (scriptLabel == null) {
                scriptLabel = mapRow(rs);
                scriptLabelMap.put(name, scriptLabel);
            }
            scriptLabel = mapRow(rs);
            scriptLabels.add(scriptLabel);
        }
        return scriptLabels;
    }

    private ScriptLabel mapRow(ResultSet rs) throws SQLException {
        ScriptLabelKey scriptLabelKey = ScriptLabelKey.builder()
                .id(rs.getString("ID"))
                .build();
        ScriptKey scriptKey = ScriptKey.builder()
                .scriptId(rs.getString("SCRIPT_ID"))
                .scriptVersion(rs.getLong("SCRIPT_VRS_NB"))
                .build();
        return ScriptLabel.builder()
                .scriptLabelKey(scriptLabelKey)
                .scriptKey(scriptKey)
                .name(rs.getString("NAME"))
                .value(rs.getString("VALUE"))
                .build();
    }
}

