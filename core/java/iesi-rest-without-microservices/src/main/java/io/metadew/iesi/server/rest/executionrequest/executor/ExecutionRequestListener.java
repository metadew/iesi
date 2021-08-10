//package io.metadew.iesi.server.rest.executionrequest.executor;
//
//import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
//import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
//import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
//import io.metadew.iesi.runtime.ExecutionRequestMonitor;
//import io.metadew.iesi.runtime.ExecutionRequestTask;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.text.MessageFormat;
//import java.util.Queue;
//import java.util.concurrent.*;
//
//@Service
//@Log4j2
//public class ExecutionRequestListener {
//
//    private final Queue<ExecutionRequest> executionRequestsQueue;
//    private final ExecutorService executor;
//    private final ExecutionRequestConfiguration executionRequestConfiguration;
//    private final ExecutionRequestExecutorService executionRequestExecutorService;
//
//    @Autowired
//    public ExecutionRequestListener(ExecutorService executor, ExecutionRequestConfiguration executionRequestConfiguration, ExecutionRequestExecutorService executionRequestExecutorService) {
//        this.executor = executor;
//        this.executionRequestConfiguration = executionRequestConfiguration;
//        this.executionRequestExecutorService = executionRequestExecutorService;
//        // TODO: follow up on how many processes have started, queue or just counter?
//        executionRequestsQueue = new LinkedBlockingQueue<>();
//    }
//
//    public void submit(ExecutionRequest executionRequest) {
//        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
//        ExecutionRequestConfiguration.getInstance().update(executionRequest);
//        executionRequestsQueue.add(executionRequest);
//        // the submitted request will be fetched by the scheduled task
//    }
//
//    public void shutdown() throws InterruptedException {
//        log.info("executionrequestlistener=shutting down execution request listener...");
//        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
//            log.info("executionrequestlistener=forcing execution request listener shutdown...");
//            executor.shutdownNow();
//        }
//        ExecutionRequestMonitor.getInstance().shutdown();
//        log.info("executionrequestlistener=execution request listener shutdown");
//        Thread mainThread = Thread.currentThread();
//        mainThread.join(2000);
//    }
//
////    @Scheduled(fixedRate = 2000)
////    public void scheduleRequests() {
////        log.debug(MessageFormat.format("executionrequestlistener={0} execution requests in queue", executionRequestsQueue.size()));
////        boolean workersAvailable = true;
////        while (workersAvailable && executionRequestsQueue.peek() != null) {
////            ExecutionRequest executionRequest = executionRequestsQueue.peek();
////            try {
////                log.debug(String.format("executionrequestlistener=trying to assign %s to execute execution requests", executionRequest.getMetadataKey().toString()));
////
////                executionRequestExecutorService.execute(executionRequest);
////
////                executor.submit(new ExecutionRequestTask(executionRequest));
////                executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
////                executionRequestConfiguration.update(executionRequest);
////                log.debug(String.format("executionrequestlistener=removing %s from queue", executionRequest.getMetadataKey().toString()));
////                executionRequestsQueue.remove();
////            } catch (RejectedExecutionException e) {
////                log.debug("executionrequestlistener=no workers available to execute execution requests");
////                workersAvailable = false;
////            }
////        }
////    }
//
//}
