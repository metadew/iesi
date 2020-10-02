package io.metadew.iesi.gcp.configuration.cco.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptExecutionCco {
    private String runId;
    private Long processId;
    private Long parentProcessId;
    private String scriptId;
    private String scriptName;
    private Long scriptVersion;
    private String environment;
    private Timestamp startTimestamp;
    private String status;
    private Timestamp endTimestamp;
    private List<ExecutionInputParameterCco> inputParameters = new ArrayList<>();
    private List<ScriptLabelCco> designLabels = new ArrayList<>();
    private List<ExecutionRequestLabelCco> executionLabels = new ArrayList<>();
    private List<ActionExecutionCco> actions = new ArrayList<>();
    private List<OutputCco> output = new ArrayList<>();

}
