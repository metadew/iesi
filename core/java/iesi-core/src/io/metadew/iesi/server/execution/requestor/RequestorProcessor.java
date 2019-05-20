package io.metadew.iesi.server.execution.requestor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.instance.FrameworkInstance;

public class RequestorProcessor {

	private FrameworkInstance frameworkInstance;
	private FrameworkExecution frameworkExecution;
	public CachedRowSet crs;
	// fields
	public int prc_id;
	public int que_id;
	public String request_type;
	public String eng_cfg;
	public String env_nm;
	public String script_nm;
	public int context_id;
	public int scope_id;

	public RequestorProcessor(FrameworkInstance frameworkInstance, int prc_id, int que_id) {
		this.setFrameworkInstance(frameworkInstance);
		//this.setFrameworkExecution(frameworkExecution);
		this.prc_id = prc_id;
		this.que_id = que_id;
		this.setProcessor();
		this.getFields();
	}

	public void setProcessor() {
		String QueryString = "update " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("RequestExecutions")
				+ " set request_id = " + this.que_id + " where prc_id = " + this.prc_id;
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);

		QueryString = "update " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests")
				+ " set prc_id = " + this.prc_id + " where request_id = " + this.que_id;
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void clearProcessor() {
		String QueryString = "update " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("RequestExecutions")
				+ " set request_id = -1 where prc_id = " + this.prc_id;
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void removeFromQueue() {
		String QueryString = "delete from " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests")
				+ " where request_id = " + this.que_id;
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void getFields() {
		String QueryString = "";
		CachedRowSet crs = null;
		QueryString = "select request_id, request_type, script_nm, env_nm, prc_id from "
				+ this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests") + " where request_id = "
				+ this.que_id;
		crs = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString, "reader");

		try {
			while (crs.next()) {
				this.request_type = crs.getString("REQUEST_TYPE");
				this.env_nm = crs.getString("ENV_NM");
				this.script_nm = crs.getString("SCRIPT_NM");
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
