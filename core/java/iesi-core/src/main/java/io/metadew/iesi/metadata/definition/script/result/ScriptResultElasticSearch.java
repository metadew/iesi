package io.metadew.iesi.metadata.definition.script.result;

import io.metadew.iesi.connection.elasticsearch.ElasticSearchDocument;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.time.LocalDateTime;

public class ScriptResultElasticSearch implements ElasticSearchDocument {

    private final String runId;
    private final Long processId;
    private final Long parentProcessId;
    private final String scriptId;
    private final String scriptName;
    private final Long scriptVersion;
    private final String environment;
    private final String status;
    private final LocalDateTime startTimestamp;
    private final LocalDateTime endTimestamp;

    public ScriptResultElasticSearch(ScriptResult scriptResult) {
        runId = scriptResult.getMetadataKey().getRunId();
        processId = scriptResult.getMetadataKey().getProcessId();
        parentProcessId = scriptResult.getParentProcessId();
        scriptId = scriptResult.getScriptId();
        scriptName = scriptResult.getScriptName();
        scriptVersion = scriptResult.getScriptVersion();
        environment = scriptResult.getEnvironment();
        status = scriptResult.getStatus().value();
        startTimestamp = scriptResult.getStartTimestamp();
        endTimestamp = scriptResult.getEndTimestamp();
    }

    @Override
    public Marker getLoggingMarker() {
        return MarkerManager.getMarker("scriptresults");
    }

}
