package io.metadew.iesi.gcp.configuration.bigquery;

import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import io.metadew.iesi.gcp.configuration.cco.core.ScriptResultCco;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ScriptResultConfiguration extends TableConfiguration {

    public ScriptResultConfiguration() {
        super();
        this.setTableName("res_script");
    }

    @Override
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

    @Override
    public Map<String, Object> getRowContent(ScriptResultCco scriptResultCco) {
        Map<String, Object> rowContent = new HashMap<>();
        rowContent.put("RUN_ID", scriptResultCco.getRunID());
        rowContent.put("PRC_ID",  scriptResultCco.getProcessId());
        rowContent.put("PARENT_PRC_ID",  scriptResultCco.getParentProcessId());
        rowContent.put("SCRIPT_ID",  scriptResultCco.getScriptId());
        rowContent.put("SCRIPT_NM",  scriptResultCco.getScriptName());
        rowContent.put("SCRIPT_VRS_NB",  scriptResultCco.getScriptVersion());
        rowContent.put("ENV_NM",  scriptResultCco.getEnvironment());
        rowContent.put("ST_NM",  scriptResultCco.getStatus());
        rowContent.put("STRT_TMS",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(scriptResultCco.getStartTimestamp()));
        rowContent.put("END_TMS",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(scriptResultCco.getEndTimestamp()));

        return rowContent;
    }


}
