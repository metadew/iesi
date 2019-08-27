package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionRequestListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ExecutorService executor;
    private final ExecutionRequestConfiguration executionRequestConfiguration;

    public ExecutionRequestListener() {
        executionRequestConfiguration = new ExecutionRequestConfiguration();
        int threadSize = FrameworkSettingConfiguration.getInstance().getSettingPath("server.threads")
                .map(settingPath -> Integer.parseInt(FrameworkControl.getInstance().getProperty(settingPath)))
                .orElse(4);
        LOGGER.info(MessageFormat.format("starting listener with thread pool size {0}", threadSize));
        executor = Executors.newFixedThreadPool(threadSize);
    }

    public void run() throws SQLException, InterruptedException, MetadataDoesNotExistException {
        while(true) {
            LOGGER.trace("executionrequestlistener=fetching new requests");
            List<ExecutionRequest> executionRequests = executionRequestConfiguration.getAllNew();
            LOGGER.trace(MessageFormat.format("fexecutionrequestlistener=found {0} Requests", executionRequests.size()));
            for (ExecutionRequest executionRequest : executionRequests) {
                LOGGER.info(MessageFormat.format("executionrequestlistener=submitting request {0} for execution", executionRequest.getMetadataKey().getId()));
                executionRequest.updateExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
                executionRequestConfiguration.update(executionRequest);

                executor.submit(new ExecutionRequestTask(executionRequest));
            }
            Thread.sleep(4000);
        }
    }

}
