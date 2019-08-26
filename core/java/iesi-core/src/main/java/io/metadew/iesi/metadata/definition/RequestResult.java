package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.metadata.definition.key.RequestResultKey;

import java.time.LocalDateTime;

public class RequestResult extends Metadata<RequestResultKey> {

	private RequestResultKey requestResultKey;

	private String parentRequestId;
	private String runId;
	private String origin;
	private String name;
	private String scope;
	private String context;
	private String space;
	private String user;
	private String status;
	private LocalDateTime requestTimestamp;
	private LocalDateTime startTimestamp;
	private LocalDateTime endTimestamp;

	public RequestResult(RequestResultKey requestResultKey, String parentRequestId, String origin, String runId,
			String name, String scope, String context, String space, String user, String status, LocalDateTime requestTimestamp, LocalDateTime startTimestamp, LocalDateTime stopTimestamp) {
		super(requestResultKey);
		this.setRequestResultKey(requestResultKey);
		this.setParentRequestId(parentRequestId);
		this.setRunId(runId);
		this.setOrigin(origin);
		this.setName(name);
		this.setScope(scope);
		this.setContext(context);
		this.setSpace(space);
		this.setUser(user);
		this.setStatus(status);
		this.setRequestTimestamp(requestTimestamp);
		this.setStartTimestamp(startTimestamp);
		this.setEndTimestamp(stopTimestamp);
	}
	
	public RequestResultKey getRequestResultKey() {
		return requestResultKey;
	}

	public void setRequestResultKey(RequestResultKey requestResultKey) {
		this.requestResultKey = requestResultKey;
	}

	public String getParentRequestId() {
		return parentRequestId;
	}

	public void setParentRequestId(String parentRequestId) {
		this.parentRequestId = parentRequestId;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getRequestTimestamp() {
		return requestTimestamp;
	}

	public void setRequestTimestamp(LocalDateTime requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	public LocalDateTime getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(LocalDateTime startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public LocalDateTime getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(LocalDateTime endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
