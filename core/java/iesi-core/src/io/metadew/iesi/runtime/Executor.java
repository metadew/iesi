package io.metadew.iesi.runtime;

import java.time.LocalDateTime;

import io.metadew.iesi.framework.definition.FrameworkRunIdentifier;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.launch.operation.ScriptLaunchOperation;
import io.metadew.iesi.metadata.configuration.RequestResultConfiguration;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestResult;
import io.metadew.iesi.metadata.definition.key.RequestResultKey;
import io.metadew.iesi.runtime.configuration.RequestStatus;

public class Executor {

	private static Executor INSTANCE;
	private FrameworkInstance frameworkInstance;

	public Executor(FrameworkInstance frameworkInstance) {
		this.setFrameworkInstance(frameworkInstance);
	}

	public synchronized static Executor getInstance(FrameworkInstance frameworkInstance) {
		if (INSTANCE == null) {
			INSTANCE = new Executor(frameworkInstance);
		}
		return INSTANCE;
	}

	public synchronized String execute(Request request) {
		
		try {
			RequestResultConfiguration requestResultConfiguration = new RequestResultConfiguration(
					this.getFrameworkInstance().getMetadataControl());
			RequestResultKey requestResultKey = new RequestResultKey(request.getId());

			FrameworkRunIdentifier frameworkRunIdentifier = new FrameworkRunIdentifier();

			RequestResult requestResult = new RequestResult(requestResultKey, "", frameworkRunIdentifier.getId(),
					request.getType(), request.getName(), request.getScope(), request.getContext(), request.getSpace(), request.getUser(),
					RequestStatus.RUNNING.value(), LocalDateTime.parse(request.getTimestamp()), LocalDateTime.now(), null);
			requestResultConfiguration.insert(requestResult);

			if (request.getType() != null) {
				switch (request.getType()) {
				case "script":
					ScriptLaunchOperation.execute(this.getFrameworkInstance(), request, frameworkRunIdentifier);
					break;
				default:
					throw new RuntimeException("Request type is not supported");
				}
			} else {
				throw new RuntimeException("Empty request submitted for execution");
			}

			requestResult.setStatus(RequestStatus.SUCCESS.value());;
			requestResult.setEndTimestamp(LocalDateTime.now());
			requestResultConfiguration.update(requestResult);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	// Getters and setters
	public synchronized FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public synchronized void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}
