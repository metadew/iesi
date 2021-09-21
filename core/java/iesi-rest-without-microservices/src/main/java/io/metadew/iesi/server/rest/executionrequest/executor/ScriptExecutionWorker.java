package io.metadew.iesi.server.rest.executionrequest.executor;

import lombok.Data;

import java.nio.file.Path;

@Data
public class ScriptExecutionWorker {

    private final Path path;
    private final Integer timeoutInMinutes;

}
