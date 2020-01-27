package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkRuntime;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class ExecutionRequestListener implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private Queue<ExecutionRequest> executionRequestsQueue;
    private final ExecutorService executor;
    private boolean keepRunning = true;

    public ExecutionRequestListener() {
        int threadSize = FrameworkSettingConfiguration.getInstance().getSettingPath("server.threads")
                .map(settingPath -> Integer.parseInt(FrameworkControl.getInstance().getProperty(settingPath)))
                .orElse(4);
        LOGGER.info(MessageFormat.format("starting listener with thread pool size {0}", threadSize));

        executor = new ThreadPoolExecutor(threadSize, threadSize,
                0L, TimeUnit.MILLISECONDS,
                new EmptyQueue());
        executionRequestsQueue = new LinkedBlockingQueue<>();
        new Thread(ExecutionRequestMonitor.getInstance()).start();
    }

    public void run() {
        ThreadContext.put("location", FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("logs"));
        ThreadContext.put("context.name", FrameworkExecution.getInstance().getFrameworkExecutionContext().getContext().getName());
        ThreadContext.put("context.scope", FrameworkExecution.getInstance().getFrameworkExecutionContext().getContext().getScope());
        ThreadContext.put("fwk.runid", FrameworkRuntime.getInstance().getFrameworkRunId());
        ThreadContext.put("fwk.code", FrameworkConfiguration.getInstance().getFrameworkCode());
        try {
            while (keepRunning) {
                pollNewRequests();
                scheduleRequests();
                Thread.sleep(1000);
            }
        } catch (MetadataDoesNotExistException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void scheduleRequests() {
        boolean workersAvailable = true;
        while (workersAvailable && executionRequestsQueue.peek() != null) {
            ExecutionRequest executionRequest = executionRequestsQueue.peek();
            try {
                executor.submit(new ExecutionRequestTask(executionRequest));
                executionRequestsQueue.remove();
            } catch (RejectedExecutionException e) {
                workersAvailable = false;
            }
        }
        LOGGER.debug(MessageFormat.format("executionrequestlistener={0} execution requests in queue", executionRequestsQueue.size()));
    }

    private void pollNewRequests() throws MetadataDoesNotExistException {
        LOGGER.trace("executionrequestlistener=fetching new requests");
        List<ExecutionRequest> executionRequests = ExecutionRequestConfiguration.getInstance().getAllNew();
        LOGGER.trace(MessageFormat.format("executionrequestlistener=found {0} new execution requests", executionRequests.size()));
        for (ExecutionRequest executionRequest : executionRequests) {
            LOGGER.info(MessageFormat.format("executionrequestlistener=submitting request {0} for execution", executionRequest.getMetadataKey().getId()));
            executionRequest.updateExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
            ExecutionRequestConfiguration.getInstance().update(executionRequest);
            executionRequestsQueue.add(executionRequest);
        }
    }

    public void shutdown() throws InterruptedException {
        keepRunning = false;
        LOGGER.info("executionrequestlistener=shutting down execution request listener...");
        if (!executor.awaitTermination(5, TimeUnit.SECONDS))  {
            LOGGER.info("executionrequestlistener=forcing execution request listener shutdown...");
            executor.shutdownNow();
        }
        ExecutionRequestMonitor.getInstance().shutdown();
        LOGGER.info("executionrequestlistener=execution request listener shutdown");
        Thread mainThread = Thread.currentThread();
        mainThread.join(2000);
    }

    private static class EmptyQueue extends ArrayBlockingQueue<Runnable> {
        private static final long serialVersionUID = 1L;

        EmptyQueue() {
            super(1);
        }

        public int remainingCapacity() {
            return 0;
        }

        public boolean add(Runnable runnable) {
            throw new IllegalStateException("Queue is full");
        }

        public void put(Runnable runnable) throws InterruptedException {
            throw new InterruptedException("Unable to insert into queue");
        }

        public boolean offer(Runnable runnable, long timeout, TimeUnit timeUnit) throws InterruptedException {
            Thread.sleep(timeUnit.toMillis(timeout));
            return false;
        }

        public boolean addAll(Collection<? extends Runnable> collection) {
            if (collection.size() > 0) {
                throw new IllegalArgumentException("Too many items in collection");
            } else {
                return false;
            }
        }
    }

}
