package io.metadew.iesi.connection.publisher;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.metadew.iesi.common.configuration.ScriptRunStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ScriptResultDto {

    // ScriptResultKey
    private String runID;
    private Long processId;

    private Long parentProcessId;
    private String scriptId;
    private String scriptName;
    private Long scriptVersion;
    private String environment;
    private String status; // Enum
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime startTimestamp;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime endTimestamp;

}
