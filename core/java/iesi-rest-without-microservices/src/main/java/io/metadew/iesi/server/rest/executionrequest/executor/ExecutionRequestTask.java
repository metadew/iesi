//package io.metadew.iesi.server.rest.executionrequest.executor;
//
//import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
//import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
//import io.metadew.iesi.runtime.ExecutionRequestExecutorService;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.ThreadContext;
//
//public class ExecutionRequestTask implements Runnable {
//
//    private static final Logger LOGGER = LogManager.getLogger();
//    private ExecutionRequest executionRequest;
//
//    public ExecutionRequestTask(ExecutionRequest executionRequest) {
//        this.executionRequest = executionRequest;
//    }
//
//    @Override
//    public void run() {
//        ThreadContext.put("location", FrameworkConfiguration.getInstance().getMandatoryFrameworkFolder("logs").getAbsolutePath().toString());
//        LOGGER.info("running " + executionRequest.getMetadataKey().getId());
//        ExecutionRequestMonitor.getInstance().monitor(executionRequest.getMetadataKey(), Thread.currentThread());
//        ExecutionRequestExecutorService.getInstance().execute(executionRequest);
//        ExecutionRequestMonitor.getInstance().stopMonitoring(executionRequest.getMetadataKey());
//    }
//}
