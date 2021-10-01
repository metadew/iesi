package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Log4j2
@ConditionalOnWebApplication
abstract class WorkerAgentExecutionRequestExecutor<T extends ExecutionRequest> extends ExecutionRequestExecutor<T> {

    private final ExecutionRequestConfiguration executionRequestConfiguration;
    private final RoundRobin<ScriptExecutionWorker> scriptExecutionWorkers;

    @Autowired
    @SuppressWarnings("unchecked")
    WorkerAgentExecutionRequestExecutor(ExecutionRequestConfiguration executionRequestConfiguration,
                                        Configuration iesiProperties) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        Collection<Map<String, Object>> scriptExecutionWorkersInfo = (Collection<Map<String, Object>>) iesiProperties.getMandatoryProperty("iesi.workers");
        scriptExecutionWorkers = new RoundRobin<>(
                scriptExecutionWorkersInfo.stream()
                        .map(scriptExecutionWorkerInfo -> new ScriptExecutionWorker(
                                Paths.get((String) scriptExecutionWorkerInfo.get("path")),
                                        (Integer) scriptExecutionWorkerInfo.get("timeout"))
                        )
                        .collect(Collectors.toSet()),
                ScriptExecutionWorker.class);
    }

    @Override
    public void execute(T executionRequest) {
        checkUserAccess(executionRequest);
        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.ACCEPTED);
        executionRequestConfiguration.update(executionRequest);

        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {

            //TODO: start the script as a async separate process and follow up
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
        if (isWindows) {
            builder.command(
                    "bin/iesi-execute.cmd",
                    "-scriptExecutionRequestKey",
                    scriptExecutionRequest.getMetadataKey().getId()
            );
        } else {
            builder.command(
                    "./bin/iesi-execute.sh",
                    "-scriptExecutionRequestKey",
                    scriptExecutionRequest.getMetadataKey().getId()
            );
        }
        builder.directory(scriptExecutionWorker.getPath().toFile());
        Process process = builder.start();
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), log::info);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        // TODO: what to do when failed
        boolean exited = process.waitFor(scriptExecutionWorker.getTimeoutInMinutes(), TimeUnit.MINUTES);
        if (!exited) {
            process.destroyForcibly();
        }
    }

    abstract void checkUserAccess(T executionRequest);

    private static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
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
