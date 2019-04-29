package io.metadew.iesi.server.execution.request;

public class RequestService {
//
//	private FrameworkExecution frameworkExecution;
//
//	public CachedRowSet crs;
//	// fields
//	public int request_id;
//
//	public RequestService(FrameworkExecution frameworkExecution) {
//		this.setFrameworkExecution(frameworkExecution);
//		this.clearProcessors();
//		this.createProcessors();
//	}
//
//	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	// Initialize
//	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
//	public void clearProcessors() {
//		String QueryString = "delete from " + this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_CTL();
//		this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
//	}
//
//	public void createProcessors() {
//		String QueryString = "insert into " + this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_CTL()
//				+ " (prc_id,request_id) values (1,-1)";
//		this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
//	}
//
//	public int getAvailableProcessor() {
//		int prc_id = -1;
//
//		String QueryString = "select min(prc_id) prc_id from "
//				+ this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_CTL() + " where request_id = -1";
//		this.crs = this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeQuery(QueryString);
//
//		try {
//			while (crs.next()) {
//				prc_id = crs.getInt("PRC_ID");
//			}
//
//			crs.close();
//		} catch (Exception e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//
//		return prc_id;
//	}
//
//	public void getNextQueID() {
//		String QueryString = "";
//		CachedRowSet crs = null;
//		QueryString = "select min(request_id) REQUEST_ID from "
//				+ this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_REQ() + " where prc_id = -1";
//		crs = this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeQuery(QueryString);
//		try {
//			while (crs.next()) {
//				this.request_id = crs.getInt("REQUEST_ID");
//			}
//
//			crs.close();
//		} catch (SQLException e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//	}
//
//	public void execute() {
//		// Move to the runnable
//
//		// Get next que_id
//		this.getNextQueID();
//
//		// check for processor
//		int i = 1;
//		int avaiable_prc_id = -1;
//		boolean prcFound = false;
//		while (i == 1 && prcFound == false) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException ex) {
//				Thread.currentThread().interrupt();
//			}
//
//			avaiable_prc_id = this.getAvailableProcessor();
//			if (avaiable_prc_id > 0)
//				prcFound = true;
//		}
//
//		// set processor
//		RequestProcessor prc = new RequestProcessor(this.getFrameworkExecution(), avaiable_prc_id, this.request_id);
//		prc.execute();
//
//	}
//
//	public boolean execListen() {
//		int request_nb = -1;
//
//		String QueryString = "select count(request_id) 'REQUEST_NB' from " + this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_REQ();
//		this.crs = this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeQuery(QueryString);
//
//		try {
//			while (crs.next()) {
//				request_nb = crs.getInt("REQUEST_NB");
//			}
//			crs.close();
//		} catch (Exception e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//
//		if (request_nb > 0) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	// Getters and setters
//	public FrameworkExecution getFrameworkExecution() {
//		return frameworkExecution;
//	}
//
//	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
//		this.frameworkExecution = frameworkExecution;
//	}

}
