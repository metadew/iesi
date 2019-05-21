package io.metadew.iesi.framework.control;


public class ProcessIdentifierController {

	private Long processId;
	
	public ProcessIdentifierController() {
		this.setProcessId(-1L);
	}
	
	synchronized void getNextProcessId() {
	    this.processId++;
	}

	// Getters and Setters
	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	
//https://winterbe.com/posts/2015/04/30/java8-concurrency-tutorial-synchronized-locks-examples/

}