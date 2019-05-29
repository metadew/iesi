package io.metadew.iesi.server.execution.requestor;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.instance.FrameworkInstance;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class RequestorProcessor {

	private FrameworkInstance frameworkInstance;
	private FrameworkExecution frameworkExecution;
	public CachedRowSet crs;
	// fields
	public int processId;
	public String requestId;
	public String requestType;
	public String eng_cfg;
	public String contextName;
	public String scopeName;
	public int context_id;
	public int scope_id;

	public RequestorProcessor(FrameworkInstance frameworkInstance, int processId, String que_id) {
		this.setFrameworkInstance(frameworkInstance);
		// this.setFrameworkExecution(frameworkExecution);
		this.processId = processId;
		this.requestId = que_id;
		this.setProcessor();
		this.getFields();
	}

	public void setProcessor() {
		String QueryString = "update "
				+ this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel(
						"RequestExecutions")
				+ " set request_id = " + SQLTools.GetStringForSQL(this.requestId) + " where prc_id = "
				+ SQLTools.GetStringForSQL(this.processId);
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);

		QueryString = "update "
				+ this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
						.getTableNameByLabel("Requests")
				+ " set prc_id = " + SQLTools.GetStringForSQL(this.processId) + " where request_id = "
				+ SQLTools.GetStringForSQL(this.requestId);
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void clearProcessor() {
		String QueryString = "update "
				+ this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
						.getTableNameByLabel("RequestExecutions")
				+ " set request_id =  " + SQLTools.GetStringForSQL("-1") + " where prc_id = "
				+ SQLTools.GetStringForSQL(this.processId);
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void removeFromQueue() {
		String QueryString = "delete from " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests") + " where request_id = " + SQLTools.GetStringForSQL(this.requestId);
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void getFields() {
		String QueryString = "";
		CachedRowSet crs = null;
		QueryString = "select request_id, parent_request_id, request_typ_nm, request_tms, request_dsc, amount_nb, notif_email, scope_nm, context_nm, user_nm, user_password, prc_id from "
				+ this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel(
						"Requests")
				+ " where request_id = " + SQLTools.GetStringForSQL(this.requestId);
		crs = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString,
				"reader");

		try {
			while (crs.next()) {
				this.requestType = crs.getString("REQUEST_TYP_NM");
				this.contextName = crs.getString("CONTEXT_NM");
				this.scopeName = crs.getString("SCOPE_NM");
			}

			crs.close();
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
	}

	public void execute() {
		// Execution logic
		System.out.println("Execution logic here");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.removeFromQueue();
		this.clearProcessor();

	}

	// Getters and setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}
