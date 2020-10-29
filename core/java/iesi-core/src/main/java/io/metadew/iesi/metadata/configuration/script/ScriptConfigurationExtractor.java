package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptConfigurationExtractor implements ResultSetExtractor<List<Script>> {

    @Override
    public List<Script> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Script> scriptMap = new HashMap<>();
        Script script;
        while (rs.next()) {
            String name = rs.getString("Scripts_SCRIPT_ID");
            script = scriptMap.get(name);
            if (script == null) {
                script = mapRow(rs);
                scriptMap.put(name, script);
            }
            addMapping(script, rs);
        }
        return new ArrayList<>(scriptMap.values());
    }

    private Script mapRow(ResultSet rs) throws SQLException {
        ScriptKey scriptKey = ScriptKey.builder()
                .scriptId(rs.getString("Scripts_SCRIPT_ID"))
                .scriptVersion(rs.getLong("ScriptParameters_SCRIPT_VRS_NB"))
                .build();
        return Script.builder()
                .scriptKey(scriptKey)
                .name(rs.getString("Scripts_SCRIPT_NM"))
                .description(rs.getString("Scripts_SCRIPT_DSC"))
                .version(ScriptVersion.builder()
                        .scriptVersionKey(ScriptVersionKey.builder()
                                .scriptKey(scriptKey)
                                .build())
                        .description(rs.getString("ScriptVersions_SCRIPT_VRS_NB"))
                        .build())
                .labels(new ArrayList<>())
                .parameters(new ArrayList<>())
                .build();
    }

    private void addMapping(Script script, ResultSet rs) throws SQLException {
        ScriptParameterKey scriptParameterKey = ScriptParameterKey.builder()
                .scriptKey(ScriptKey.builder().scriptId(rs.getString("ScriptParameters_SCRIPT_ID")).scriptVersion(rs.getLong("ScriptParameters_SCRIPT_VRS_NB")).build())
                .parameterName(rs.getString("ScriptParameters_SCRIPT_PAR_NM")).build();
        ScriptParameter scriptParameter = null;
        if(rs.getString("ScriptParameters_SCRIPT_ID") != null){
         scriptParameter = ScriptParameter.builder().scriptParameterKey(scriptParameterKey)
                .value(rs.getString("ScriptParameters_SCRIPT_PAR_VAL")).build();
        }
        script.addParameters(scriptParameter);

        ScriptLabelKey scriptLabelKey = ScriptLabelKey.builder()
                .id(rs.getString("ScriptLabels_ID"))
                .build();
        ScriptKey scriptKey = ScriptKey.builder()
                .scriptId(rs.getString("ScriptLabels_SCRIPT_ID"))
                .scriptVersion(rs.getLong("ScriptLabels_SCRIPT_VRS_NB"))
                .build();
       ScriptLabel scriptLabel = null;
        if(rs.getString("ScriptLabels_SCRIPT_ID") != null){
            scriptLabel   = ScriptLabel.builder()
                .scriptLabelKey(scriptLabelKey)
                .scriptKey(scriptKey)
                .name(rs.getString("ScriptLabels_NAME"))
                .value(rs.getString("ScriptLabels_VALUE"))
                .build();
        }
        script.addLabels(scriptLabel);
    }
}