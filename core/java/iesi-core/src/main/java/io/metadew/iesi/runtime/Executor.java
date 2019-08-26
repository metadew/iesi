package io.metadew.iesi.runtime;

import io.metadew.iesi.metadata.configuration.request.RequestResultConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Executor {

    private static Executor INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private RequestResultConfiguration requestResultConfiguration;

    private Executor() {
    }

    public synchronized static Executor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Executor();
        }
        return INSTANCE;
    }

    public void init() {
        this.requestResultConfiguration = new RequestResultConfiguration();
    }

    public synchronized void execute(NonAuthenticatedExecutionRequest request) {

    }

    public synchronized void execute(AuthenticatedExecutionRequest request) {
//        try {

//            RequestResult requestResult = new RequestResult(new RequestResultKey(request.getId()), "-1",
//                    request.getType(), FrameworkRuntime.getInstance().getFrameworkRunId(), request.getName(), request.getScope(), request.getContext(), request.getSpace(), request.getUser(),
//                    RequestStatus.RUNNING.value(), LocalDateTime.parse(request.getTimestamp()), LocalDateTime.now(), null);
//            requestResultConfiguration.insert(requestResult);
//
//            if (request.getType() != null) {
//                switch (request.getType()) {
//                    case "script":
//                        ScriptLaunchOperation.execute(request);
//                        break;
//                    default:
//                        throw new RuntimeException("Request type is not supported");
//                }
//            } else {
//                throw new RuntimeException("Empty request submitted for execution");
//            }
//
//            requestResult.setStatus(RequestStatus.SUCCESS.value());
//            requestResult.setEndTimestamp(LocalDateTime.now());
//            requestResultConfiguration.update(requestResult);
//        } catch (Exception e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//
//            LOGGER.warn("exception=" + e.getMessage());
//            LOGGER.warn("stacktrace=" + stackTrace.toString());
//        }
    }

}
