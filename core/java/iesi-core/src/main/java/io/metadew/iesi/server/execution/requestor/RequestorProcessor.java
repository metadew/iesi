package io.metadew.iesi.server.execution.requestor;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.request.RequestConfiguration;
import io.metadew.iesi.metadata.definition.Request;

import javax.sql.rowset.CachedRowSet;
import java.util.Optional;

public class RequestorProcessor {

	public CachedRowSet crs;
	// fields
	public int executionId;
	public String requestId;
	public String requestType;
	public String eng_cfg;
	public String contextName;
	public String scopeName;
	public int context_id;
	public int scope_id;
	public Optional<Request> request;

	public RequestorProcessor(int executionId, String requestId) {
		this.executionId = executionId;
		this.requestId = requestId;
		this.setProcessor();
		this.getFields();
	}

	public void setProcessor() {
		String QueryString = "update "
				+ FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel(
						"RequestExecutions")
				+ " set request_id = " + SQLTools.GetStringForSQL(this.requestId) + " where exe_id = "
				+ SQLTools.GetStringForSQL(this.executionId);
		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);

		QueryString = "update "
				+ FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
						.getTableNameByLabel("Requests")
				+ " set exe_id = " + SQLTools.GetStringForSQL(this.executionId) + " where request_id = "
				+ SQLTools.GetStringForSQL(this.requestId);
		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void clearProcessor() {
		String QueryString = "update "
				+ FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
						.getTableNameByLabel("RequestExecutions")
				+ " set request_id =  " + SQLTools.GetStringForSQL("-1") + " where exe_id = "
				+ SQLTools.GetStringForSQL(this.executionId);
		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void removeFromQueue() {
		String QueryString = "delete from " + FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests") + " where request_id = " + SQLTools.GetStringForSQL(this.requestId);
		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void getFields() {
		RequestConfiguration requestConfiguration = new RequestConfiguration();
		this.setRequest(requestConfiguration.getRequest(this.requestId));
	}

	public void execute() {
		// Execution logic
		// TODO
		// Executor.getInstance().execute(this.getRequest().get());
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.removeFromQueue();
		this.clearProcessor();

	}

	public void setRequest(Optional<Request> request) {
		this.request = request;
	}

	public Optional<Request> getRequest() {
		return request;
	}

}
