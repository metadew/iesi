package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.guard.GuardConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AuthenticatedExecutionRequestExecutor implements ExecutionRequestExecutor<AuthenticatedExecutionRequest> {

    private final boolean authenticationEnabled;
    private final ExecutionRequestConfiguration executionRequestConfiguration;
    private final RoundRobin<ScriptExecutionWorker> scriptExecutionWorkers;

    @Autowired
    @SuppressWarnings("unchecked")
    public AuthenticatedExecutionRequestExecutor(ExecutionRequestConfiguration executionRequestConfiguration,
                                                 GuardConfiguration guardConfiguration,
                                                 Configuration iesiProperties) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        this.authenticationEnabled = guardConfiguration.getGuardSetting("authenticate")
                .map(s -> s.equalsIgnoreCase("y"))
                .orElseThrow(() -> new RuntimeException("no value set for guard.authenticate"));
        Collection<Map<String, String>> scriptExecutionWorkersInfo = (Collection<Map<String, String>>) iesiProperties.getMandatoryProperty("iesi.workers");
        scriptExecutionWorkers = new RoundRobin<>(
                scriptExecutionWorkersInfo.stream()
                        .map(scriptExecutionWorkerInfo -> new ScriptExecutionWorker(Paths.get(scriptExecutionWorkerInfo.get("path"))))
                        .collect(Collectors.toSet()),
                ScriptExecutionWorker.class);
    }

    @Override
    public Class<AuthenticatedExecutionRequest> appliesTo() {
        return AuthenticatedExecutionRequest.class;
    }

    @Override
    public void execute(AuthenticatedExecutionRequest executionRequest) {
        if (authenticationEnabled) {
            checkUserAccess(executionRequest);
        } else {
            log.info("authentication.disabled:access automatically granted");
        }
        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.ACCEPTED);
        executionRequestConfiguration.update(executionRequest);

        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {

            //TODO: start the script as a async separate process and follow up
            // for every agent, check which ones to use
            log.info("Executing " + scriptExecutionRequest.toString());
            try {
                ScriptExecutionWorker scriptExecutionWorker = selectScriptExecutionWorker(scriptExecutionRequest);
                executeScriptExecutionRequest(scriptExecutionWorker, scriptExecutionRequest);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ScriptExecutionWorker selectScriptExecutionWorker(ScriptExecutionRequest scriptExecutionRequest) {
        // in future the scriptExecutionRequest can be used to make a more educated pick in worker
        return scriptExecutionWorkers.get();
    }

    private void executeScriptExecutionRequest(ScriptExecutionWorker scriptExecutionWorker, ScriptExecutionRequest scriptExecutionRequest) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        // TODO: build iesi-execute command with script execution request key
        if (isWindows) {
            builder.command("cmd.exe",
                    "/c",
                    "bin/iesi-execute.cmd",
                    "-scriptExecutionRequestKey",
                    scriptExecutionRequest.getMetadataKey().getId());
        } else {
            builder.command(
                    "sh",
                    "-c",
                    "bin/iesi-execute.sh",
                    "-scriptExecutionRequestKey",
                    scriptExecutionRequest.getMetadataKey().getId());
        }
        // TODO: set iesi home
        builder.directory(scriptExecutionWorker.getPath().toFile());
        Process process = builder.start();
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        Thread.sleep(60*1000L);
        assert exitCode == 0;
        log.info("Executed " + scriptExecutionRequest.toString());
    }

    private void checkUserAccess(AuthenticatedExecutionRequest executionRequest) {

    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    private static class RoundRobin<E> {
        private final AtomicInteger next = new AtomicInteger(0);
        private final E[] elements;

        @SuppressWarnings("unchecked")
        public RoundRobin(Collection<E> queue, Class<E> clazz) {
            this.elements = queue.toArray((E[]) Array.newInstance(clazz, queue.size()));
        }

        public E get() {
            return elements[next.getAndIncrement() % elements.length];
        }
    }
}
