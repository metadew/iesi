package io.metadew.iesi.server.execution.requestor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.framework.instance.FrameworkInstance;

public class RequestorService {

	private FrameworkInstance frameworkInstance;
	public CachedRowSet crs;

	// fields
	public int request_id;

	public RequestorService(FrameworkInstance frameworkInstance) {
		this.setFrameworkInstance(frameworkInstance);
		this.clearProcessors();
		this.createProcessors();
	}

	// Initialize
	public void clearProcessors() {
		String QueryString = "delete from " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("RequestExecutions");
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void createProcessors() {
		String QueryString = "insert into " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("RequestExecutions")
				+ " (prc_id,request_id) values (1,-1)";
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public int getAvailableProcessor() {
		int prc_id = -1;

		String QueryString = "select min(prc_id) prc_id from "
				+ this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("RequestExecutions") + " where request_id = -1";
		this.crs = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString, "reader");

		try {
			while (crs.next()) {
				prc_id = crs.getInt("PRC_ID");
			}

			crs.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return prc_id;
	}

	public void getNextQueID() {
		String QueryString = "";
		CachedRowSet crs = null;
		QueryString = "select min(request_id) REQUEST_ID from "
				+ this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests") + " where prc_id = -1";
		crs = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString, "reader");
		try {
			while (crs.next()) {
				this.request_id = crs.getInt("REQUEST_ID");
			}

			crs.close();
		} catch (SQLException e) {
		StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
	}

	public void execute() {
		// Move to the runnable

		// Get next que_id
		this.getNextQueID();

		// check for processor
		int i = 1;
		int avaiable_prc_id = -1;
		boolean prcFound = false;
		while (i == 1 && prcFound == false) {
			try {
		Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			avaiable_prc_id = this.getAvailableProcessor();
		if (avaiable_prc_id > 0)
				prcFound = true;
		}

		// set processor
		RequestorProcessor prc = new RequestorProcessor(this.getFrameworkInstance(), avaiable_prc_id, this.request_id);
		prc.execute();

	}

	public boolean execListen() {
		int request_nb = -1;

		String QueryString = "select count(request_id) 'REQUEST_NB' from " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests");
		this.crs = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString,"reader");
		System.out.println(QueryString);
		try {
			while (crs.next()) {
				request_nb = crs.getInt("REQUEST_NB");
			}
			crs.close();
		} catch (Exception e) {
		StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		if (request_nb > 0) {
			return true;
	} else {
			return false;
		}
	}

	// Getters and setters
	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}


}
