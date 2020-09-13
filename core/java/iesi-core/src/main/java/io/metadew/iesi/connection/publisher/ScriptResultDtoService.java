package io.metadew.iesi.connection.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;

import java.io.IOException;

public class ScriptResultDtoService {

    private static ScriptResultDtoService INSTANCE;

    public synchronized static ScriptResultDtoService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptResultDtoService();
        }
        return INSTANCE;
    }

    private ScriptResultDtoService() {

    }

    public ScriptResultDto convert(ScriptResult scriptResult) {
        return new ScriptResultDto(scriptResult.getMetadataKey().getRunId(),
                scriptResult.getMetadataKey().getProcessId(),
                scriptResult.getParentProcessId(),
                scriptResult.getScriptId(),
                scriptResult.getScriptName(),
                scriptResult.getScriptVersion(),
                scriptResult.getEnvironment(),
                scriptResult.getStatus(),
                scriptResult.getStartTimestamp(),
                scriptResult.getEndTimestamp());
    }

    public String serialize(ScriptResultDto scriptResultDto) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(scriptResultDto);
    }

    public ScriptResultDto deserialize(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.readValue(json, ScriptResultDto.class);
    }

}