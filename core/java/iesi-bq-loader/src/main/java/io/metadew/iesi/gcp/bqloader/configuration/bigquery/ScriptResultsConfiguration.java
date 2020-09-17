package io.metadew.iesi.gcp.bqloader.configuration.bigquery;

import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import io.metadew.iesi.connection.publisher.ScriptResultDto;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ScriptResultsConfiguration {

    private static ScriptResultsConfiguration INSTANCE;

    public synchronized static ScriptResultsConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptResultsConfiguration();
        }
        return INSTANCE;
    }

    private ScriptResultsConfiguration() {
    }

    public String getTableName() {
        return "res_script";
    }

    public Schema getSchema() {
        Schema schema =
                Schema.of(
                        Field.of("RUN_ID", StandardSQLTypeName.STRING),
                        Field.of("PRC_ID", StandardSQLTypeName.INT64),
                        Field.of("PARENT_PRC_ID", StandardSQLTypeName.INT64),
                        Field.of("SCRIPT_ID", StandardSQLTypeName.STRING),
                        Field.of("SCRIPT_NM", StandardSQLTypeName.STRING),
                        Field.of("SCRIPT_VRS_NB", StandardSQLTypeName.INT64),
                        Field.of("ENV_NM", StandardSQLTypeName.STRING),
                        Field.of("ST_NM", StandardSQLTypeName.STRING),
                        Field.of("STRT_TMS", StandardSQLTypeName.TIMESTAMP),
                        Field.of("END_TMS", StandardSQLTypeName.TIMESTAMP));
        return schema;
    }

    public Map<String, Object> getRowContent(ScriptResultDto scriptResultDto) {
        Map<String, Object> rowContent = new HashMap<>();
        rowContent.put("RUN_ID", scriptResultDto.getRunID());
        rowContent.put("PRC_ID",  scriptResultDto.getProcessId());
        rowContent.put("PARENT_PRC_ID",  scriptResultDto.getParentProcessId());
        rowContent.put("SCRIPT_ID",  scriptResultDto.getScriptId());
        rowContent.put("SCRIPT_NM",  scriptResultDto.getScriptName());
        rowContent.put("SCRIPT_VRS_NB",  scriptResultDto.getScriptVersion());
        rowContent.put("ENV_NM",  scriptResultDto.getEnvironment());
        rowContent.put("ST_NM",  scriptResultDto.getStatus().value());
        rowContent.put("STRT_TMS",  scriptResultDto.getStartTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        rowContent.put("END_TMS",  scriptResultDto.getEndTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

        return rowContent;
    }


}
