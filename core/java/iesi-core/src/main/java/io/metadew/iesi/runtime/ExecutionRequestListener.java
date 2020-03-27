package io.metadew.iesi.runtime;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

@Log4j2
public class ExecutionRequestListener implements Runnable {

    private Queue<ExecutionRequest> executionRequestsQueue;
    private final ExecutorService executor;
    private boolean keepRunning = true;

    public ExecutionRequestListener() {
        int threadSize = Configuration.getInstance()
                .getProperty("server.threads.size")
                .map(settingPath -> (Integer) settingPath)
                .orElse(4);
        log.info(MessageFormat.format("starting listener with thread pool size {0}", threadSize));

        executor = new ThreadPoolExecutor(threadSize, threadSize,
                0L, TimeUnit.MILLISECONDS,
                new EmptyQueue());
        executionRequestsQueue = new LinkedBlockingQueue<>();
        new Thread(ExecutionRequestMonitor.getInstance()).start();
    }

    public void run() {
        ThreadContext.put("location", FrameworkConfiguration.getInstance().getMandatoryFrameworkFolder("logs").getAbsolutePath());
        try {
            while (keepRunning) {
                pollNewRequests();
                scheduleRequests();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void scheduleRequests() {
        log.debug(MessageFormat.format("executionrequestlistener={0} execution requests in queue", executionRequestsQueue.size()));
        boolean workersAvailable = true;
        while (workersAvailable && executionRequestsQueue.peek() != null) {
            ExecutionRequest executionRequest = executionRequestsQueue.peek();
            try {
                log.debug("executionrequestlistener=trying to assign "+executionRequest.getMetadataKey().toString()+" to execute execution requests");
                executor.submit(new ExecutionRequestTask(executionRequest));
                executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
                ExecutionRequestConfiguration.getInstance().update(executionRequest);
                log.debug(MessageFormat.format("executionrequestlistener=removing {0} from queue", executionRequest.getMetadataKey().toString()));
                executionRequestsQueue.remove();
            } catch (RejectedExecutionException e) {
                log.debug("executionrequestlistener=no workers available to execute execution requests");
                workersAvailable = false;
            }
        }
    }

    public void submit(ExecutionRequest executionRequest) {
        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
        ExecutionRequestConfiguration.getInstance().update(executionRequest);
        executionRequestsQueue.add(executionRequest);
    }

    private void pollNewRequests() {
        log.trace("executionrequestlistener=fetching new requests");
        List<ExecutionRequest> executionRequests = ExecutionRequestConfiguration.getInstance().getAllNew();
        log.trace(MessageFormat.format("executionrequestlistener=found {0} new execution requests", executionRequests.size()));
        for (ExecutionRequest executionRequest : executionRequests) {
            log.info(MessageFormat.format("executionrequestlistener=submitting request {0} for execution", executionRequest.getMetadataKey().getId()));
            executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
            ExecutionRequestConfiguration.getInstance().update(executionRequest);
            executionRequestsQueue.add(executionRequest);
        }
    }

    public void shutdown() throws InterruptedException {
        keepRunning = false;
        log.info("executionrequestlistener=shutting down execution request listener...");
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            log.info("executionrequestlistener=forcing execution request listener shutdown...");
            executor.shutdownNow();
        }
        ExecutionRequestMonitor.getInstance().shutdown();
        log.info("executionrequestlistener=execution request listener shutdown");
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
