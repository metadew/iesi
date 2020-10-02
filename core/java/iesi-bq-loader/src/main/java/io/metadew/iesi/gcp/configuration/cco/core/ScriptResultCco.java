package io.metadew.iesi.gcp.configuration.cco.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptResultCco {
    private String runID;
    private Long processId;
    private Long parentProcessId;
    private String scriptId;
    private String scriptName;
    private Long scriptVersion;
    private String environment;
    private String status;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss.SSS"
    )
    private Timestamp startTimestamp;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss.SSS"
    )
    private Timestamp endTimestamp;

}
