package io.metadew.iesi.gcp.configuration.cco.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionExecutionCco {

    private String runId;
    private Long processId;
    private String type;
    private String name;
    private String description;
    private String condition;
    private boolean errorStop;
    private boolean errorExpected;
    private String status;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private List<ActionInputParameterCco> inputParameters;
    private List<OutputCco> output;

}