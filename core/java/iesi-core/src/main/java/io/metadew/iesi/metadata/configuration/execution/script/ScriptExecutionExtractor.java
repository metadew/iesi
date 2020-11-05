package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptExecutionExtractor implements ResultSetExtractor<List<ScriptExecution>> {

    @Override
    public List<ScriptExecution> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ScriptExecution> stringScriptExecutionMap = new HashMap<>();
        ScriptExecution scriptExecution;
        List<ScriptExecution> scriptExecutionList = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("SCRPT_REQUEST_ID");
            scriptExecution = stringScriptExecutionMap.get(name);
            if (scriptExecution == null) {
                scriptExecution = mapRow(rs);
                stringScriptExecutionMap.put(name, scriptExecution);
            }
            scriptExecution = mapRow(rs);
            scriptExecutionList.add(scriptExecution);
        }
        return scriptExecutionList;
    }

    private ScriptExecution mapRow(ResultSet rs) throws SQLException {
        ScriptExecutionRequestKey scriptExecutionRequestKey = ScriptExecutionRequestKey.builder()
                .id(rs.getString("SCRPT_REQUEST_ID")).build();
        ScriptExecutionKey scriptExecutionKey = ScriptExecutionKey.builder().id(rs.getString("ID")).build();
        return ScriptExecution.builder()
                .scriptExecutionKey(scriptExecutionKey)
                .scriptExecutionRequestKey(scriptExecutionRequestKey)
                .runId(rs.getString("RUN_ID"))
                .scriptRunStatus(ScriptRunStatus.valueOf(rs.getString("ST_NM")))
                .endTimestamp(rs.getTimestamp("STRT_TMS").toLocalDateTime())
                .startTimestamp(rs.getTimestamp("END_TMS").toLocalDateTime())
                .build();
    }
}
