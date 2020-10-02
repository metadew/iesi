package io.metadew.iesi.gcp.configuration.bigquery;

import com.google.api.services.bigquery.model.TableRow;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import io.metadew.iesi.gcp.configuration.cco.rest.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptExecutionConfiguration extends TableConfiguration {

    public ScriptExecutionConfiguration() {
        super();
        this.setTableName("script_executions");
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
                        Field.of("END_TMS", StandardSQLTypeName.TIMESTAMP),
                        // Input parameters
                        Field.newBuilder(
                                "SCRIPT_PAR",
                                StandardSQLTypeName.STRUCT,
                                Field.of("SCRIPT_PAR_NM", StandardSQLTypeName.STRING),
                                Field.of("SCRIPT_PAR_VAL", StandardSQLTypeName.STRING)
                        )
                                .setMode(Field.Mode.REPEATED)
                                .build(),
                        // Script labels
                        Field.newBuilder(
                                "SCRIPT_LBL",
                                StandardSQLTypeName.STRUCT,
                                Field.of("SCRIPT_LBL_NM", StandardSQLTypeName.STRING),
                                Field.of("SCRIPT_LBL_VAL", StandardSQLTypeName.STRING)
                        )
                                .setMode(Field.Mode.REPEATED)
                                .build(),
                        // Execution Labels
                        Field.newBuilder(
                                "EXE_LBL",
                                StandardSQLTypeName.STRUCT,
                                Field.of("EXE_LBL_NM", StandardSQLTypeName.STRING),
                                Field.of("EXE_LBL_VAL", StandardSQLTypeName.STRING)
                        )
                                .setMode(Field.Mode.REPEATED)
                                .build(),
                        // Actions
                        Field.newBuilder(
                                "ACTION",
                                StandardSQLTypeName.STRUCT,
                                Field.of("PRC_ID", StandardSQLTypeName.INT64),
                                Field.of("ACTION_TYP_NM", StandardSQLTypeName.STRING),
                                Field.of("ACTION_NM", StandardSQLTypeName.STRING),
                                Field.of("ACTION_DESC", StandardSQLTypeName.STRING),
                                Field.of("CONDITION_VAL", StandardSQLTypeName.STRING),
                                Field.of("STOP_ERR_FL", StandardSQLTypeName.STRING),
                                Field.of("EXP_ERR_FL", StandardSQLTypeName.STRING),
                                Field.of("ST_NM", StandardSQLTypeName.STRING),
                                Field.of("STRT_TMS", StandardSQLTypeName.TIMESTAMP),
                                Field.of("END_TMS", StandardSQLTypeName.TIMESTAMP),
                                // Output
                                Field.newBuilder(
                                        "ACTION_PAR",
                                        StandardSQLTypeName.STRUCT,
                                        Field.of("ACTION_PAR_NM", StandardSQLTypeName.STRING),
                                        Field.of("ACTION_PAR_VAL", StandardSQLTypeName.STRING),
                                        Field.of("ACTION_PAR_RES", StandardSQLTypeName.STRING)
                                )
                                        .setMode(Field.Mode.REPEATED)
                                        .build(),
                                // Output
                                Field.newBuilder(
                                        "ACTION_OUT",
                                        StandardSQLTypeName.STRUCT,
                                        Field.of("OUT_NM", StandardSQLTypeName.STRING),
                                        Field.of("OUT_VAL", StandardSQLTypeName.STRING)
                                )
                                        .setMode(Field.Mode.REPEATED)
                                        .build()
                        )
                                .setMode(Field.Mode.REPEATED)
                                .build(),
                        // Output
                        Field.newBuilder(
                                "SCRIPT_OUT",
                                StandardSQLTypeName.STRUCT,
                                Field.of("OUT_NM", StandardSQLTypeName.STRING),
                                Field.of("OUT_VAL", StandardSQLTypeName.STRING)
                        )
                                .setMode(Field.Mode.REPEATED)
                                .build()
                );
        return schema;
    }

    @Override
    public Map<String, Object> getRowContent(ScriptExecutionCco scriptExecutionCco) {
        Map<String, Object> rowContent = new HashMap<>();
        rowContent.put("RUN_ID", scriptExecutionCco.getRunId());
        rowContent.put("PRC_ID",  scriptExecutionCco.getProcessId());
        rowContent.put("PARENT_PRC_ID",  scriptExecutionCco.getParentProcessId());
        rowContent.put("SCRIPT_ID",  scriptExecutionCco.getScriptId());
        rowContent.put("SCRIPT_NM",  scriptExecutionCco.getScriptName());
        rowContent.put("SCRIPT_VRS_NB",  scriptExecutionCco.getScriptVersion());
        rowContent.put("ENV_NM",  scriptExecutionCco.getEnvironment());
        rowContent.put("ST_NM",  scriptExecutionCco.getStatus());
        rowContent.put("STRT_TMS",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(scriptExecutionCco.getStartTimestamp()));
        rowContent.put("END_TMS",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(scriptExecutionCco.getEndTimestamp()));

        //Script parameters
        List<Map<String, Object>> scriptParameterContentList = new ArrayList<Map<String, Object>>();
        for (ExecutionInputParameterCco executionInputParameterCco : scriptExecutionCco.getInputParameters()) {
            Map<String, Object> scriptParameterContent = new HashMap<>();
            scriptParameterContent.put("SCRIPT_PAR_NM", executionInputParameterCco.getName());
            scriptParameterContent.put("SCRIPT_PAR_VAL", executionInputParameterCco.getValue());
            scriptParameterContentList.add(scriptParameterContent);
        }
        rowContent.put ("SCRIPT_PAR", scriptParameterContentList);

        //Script labels
        List<Map<String, Object>> scriptLabelContentList = new ArrayList<Map<String, Object>>();
        for (ScriptLabelCco scriptLabelCco : scriptExecutionCco.getDesignLabels()) {
            Map<String, Object> scriptLabelContent = new HashMap<>();
            scriptLabelContent.put("SCRIPT_LBL_NM", scriptLabelCco.getName());
            scriptLabelContent.put("SCRIPT_LBL_VAL", scriptLabelCco.getValue());
            scriptLabelContentList.add(scriptLabelContent);
        }
        rowContent.put ("SCRIPT_LBL", scriptLabelContentList);

        //Execution labels
        List<Map<String, Object>> executionLabelContentList = new ArrayList<Map<String, Object>>();
        for (ExecutionRequestLabelCco executionRequestLabelCco : scriptExecutionCco.getExecutionLabels()) {
            Map<String, Object> executionLabelContent = new HashMap<>();
            executionLabelContent.put("EXE_LBL_NM", executionRequestLabelCco.getName());
            executionLabelContent.put("EXE_LBL_VAL", executionRequestLabelCco.getValue());
            executionLabelContentList.add(executionLabelContent);
        }
        rowContent.put ("EXE_LBL", executionLabelContentList);

        List<Map<String, Object>> actionContentList = new ArrayList<Map<String, Object>>();
        for (ActionExecutionCco actionExecutionCco : scriptExecutionCco.getActions()) {
            Map<String, Object> actionContent = new HashMap<>();
            actionContent.put("PRC_ID", actionExecutionCco.getProcessId());
            actionContent.put("ACTION_TYP_NM", actionExecutionCco.getType());
            actionContent.put("ACTION_NM", actionExecutionCco.getName());
            actionContent.put("ACTION_DESC", actionExecutionCco.getDescription());
            actionContent.put("CONDITION_VAL", actionExecutionCco.getCondition());
            actionContent.put("STOP_ERR_FL", ((actionExecutionCco.isErrorStop()) ? "1" : "0"));
            actionContent.put("EXP_ERR_FL", ((actionExecutionCco.isErrorExpected()) ? "1" : "0"));
            actionContent.put("ST_NM", actionExecutionCco.getStatus());
            actionContent.put("STRT_TMS", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(actionExecutionCco.getStartTimestamp()));
            actionContent.put("END_TMS", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(actionExecutionCco.getEndTimestamp()));

            //Action parameters
            List<Map<String, Object>> actionParameterContentList = new ArrayList<Map<String, Object>>();
            for (ActionInputParameterCco actionInputParameterCco : actionExecutionCco.getInputParameters()) {
                Map<String, Object> actionParameterContent = new HashMap<>();
                actionParameterContent.put("ACTION_PAR_NM", actionInputParameterCco.getName());
                actionParameterContent.put("ACTION_PAR_VAL", actionInputParameterCco.getRawValue());
                actionParameterContent.put("ACTION_PAR_RES", actionInputParameterCco.getResolvedValue());
                actionParameterContentList.add(actionParameterContent);
            }
            actionContent.put ("ACTION_PAR", actionParameterContentList);

            //Action output
            List<Map<String, Object>> actionOutputContentList = new ArrayList<Map<String, Object>>();
            for (OutputCco outputCco : actionExecutionCco.getOutput()) {
                Map<String, Object> actionOutputContent = new HashMap<>();
                actionOutputContent.put("OUT_NM", outputCco.getName());
                actionOutputContent.put("OUT_VAL", outputCco.getValue());
                actionOutputContentList.add(actionOutputContent);
            }
            actionContent.put ("ACTION_OUT", actionOutputContentList);

            actionContentList.add(actionContent);
        }
        rowContent.put ("ACTION", actionContentList);

        //Script output
        List<Map<String, Object>> scriptOutputContentList = new ArrayList<Map<String, Object>>();
        for (OutputCco outputCco : scriptExecutionCco.getOutput()) {
            Map<String, Object> scriptOutputContent = new HashMap<>();
            scriptOutputContent.put("OUT_NM", outputCco.getName());
            scriptOutputContent.put("OUT_VAL", outputCco.getValue());
            scriptOutputContentList.add(scriptOutputContent);
        }
        rowContent.put ("SCRIPT_OUT", scriptOutputContentList);

        return rowContent;
    }


}
