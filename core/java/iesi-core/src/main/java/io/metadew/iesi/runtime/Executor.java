package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.definition.FrameworkRunIdentifier;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.launch.operation.ScriptLaunchOperation;
import io.metadew.iesi.metadata.configuration.RequestResultConfiguration;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestResult;
import io.metadew.iesi.metadata.definition.key.RequestResultKey;
import io.metadew.iesi.runtime.configuration.RequestStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class Executor {

	private static Executor INSTANCE;
	private FrameworkInstance frameworkInstance;

	private static final Logger LOGGER = LogManager.getLogger();
	private RequestResultConfiguration requestResultConfiguration;

	private Executor() {}

	public synchronized static Executor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Executor();
		}
		return INSTANCE;
	}

	public void init(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
		this.requestResultConfiguration = new RequestResultConfiguration();
	}

	public synchronized void execute(Request request) {
		try {
			FrameworkRunIdentifier frameworkRunIdentifier = new FrameworkRunIdentifier();

			RequestResult requestResult = new RequestResult(new RequestResultKey(request.getId()), "-1", frameworkRunIdentifier.getId(),
					request.getType(), request.getName(), request.getScope(), request.getContext(), request.getSpace(), request.getUser(),
					RequestStatus.RUNNING.value(), LocalDateTime.parse(request.getTimestamp()), LocalDateTime.now(), null);
			requestResultConfiguration.insert(requestResult);

			if (request.getType() != null) {
				switch (request.getType()) {
				case "script":
					ScriptLaunchOperation.execute(request, frameworkRunIdentifier);
					break;
				default:
					throw new RuntimeException("Request type is not supported");
				}
			} else {
				throw new RuntimeException("Empty request submitted for execution");
			}

			requestResult.setStatus(RequestStatus.SUCCESS.value());
			requestResult.setEndTimestamp(LocalDateTime.now());
			requestResultConfiguration.update(requestResult);
		} catch (Exception e) {
			StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));

			LOGGER.warn("exception=" + e.getMessage());
			LOGGER.warn("stacktrace=" + stackTrace.toString());
		}
	}

	// Getters and setters
	public synchronized FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public synchronized void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}
