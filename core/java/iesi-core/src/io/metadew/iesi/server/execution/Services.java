package io.metadew.iesi.server.execution;

import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Context;

public class Services {
	public Runnable requestServerRunnable;
	public Thread requestServerThread;
	public Runnable schedulerServerRunnable;
	public Thread schedulerServerThread;
	private FrameworkExecution frameworkExecution;

	public Services() {
		// Create the framework instance
		FrameworkInstance frameworkInstance = new FrameworkInstance();
		
		// Create the framework execution
		Context context = new Context();
		context.setName("server");
		context.setScope("");
		this.setFrameworkExecution(new FrameworkExecution(frameworkInstance, new FrameworkExecutionContext(context), null));
		
		//requestServerRunnable = new RequestRunnable(this.getFrameworkExecution());
		requestServerThread = new Thread(requestServerRunnable);
		requestServerThread.setName("RequestServer");
		// schedulerServerRunnable = new SchedulerServerRunnable();
		// schedulerServerThread = new Thread(schedulerServerRunnable);
	}

	public void startAll() {
		if (requestServerThread.isAlive() == false) {
			this.startRequestServer();
		}
	}

	@SuppressWarnings("deprecation")
	public void stopAll() {
		if (requestServerThread.isAlive() == true) {
			requestServerThread.stop();
		}
	}

	public String status(String serviceName) {
		boolean temp_status = false;
		boolean temp_service = false;

		if (serviceName.equalsIgnoreCase("REQUESTSERVER")) {
			temp_status = this.statusRequestServer();
			temp_service = true;
		} else if (serviceName.equalsIgnoreCase("SCHEDULERSERVER")) {
			temp_status = this.statusSchedulerServer();
			temp_service = true;
		} else {
			// ELSE
			temp_status = false;
		}

		if (temp_service == false) {
			return "Invalid Service";
		} else {
			if (temp_status == true) {
				return "Active";
			} else {
				return "Inactive";
			}
		}

	}

	public String start(String serviceName) {
		boolean temp_service = false;

		if (serviceName.equalsIgnoreCase("REQUESTSERVER")) {
			this.startRequestServer();
			temp_service = true;
		} else if (serviceName.equalsIgnoreCase("SCHEDULERSERVER")) {
			this.startSchedulerServer();
			temp_service = true;
		} else {
			// ELSE
		}

		if (temp_service == false) {
			return "Invalid Service";
		} else {
			return this.status(serviceName);
		}
	}

	public String stop(String serviceName) {
		boolean temp_service = false;

		if (serviceName.equalsIgnoreCase("REQUESTSERVER")) {
			this.stopRequestServer();
			temp_service = true;
		} else if (serviceName.equalsIgnoreCase("SCHEDULERSERVER")) {
			this.stopSchedulerServer();
			temp_service = true;
		} else {
			// ELSE
		}

		if (temp_service == false) {
			return "Invalid Service";
		} else {
			return this.status(serviceName);
		}
	}

	/*
	 * -------------------------------------------------------------------------
	 * ---------------------------- Request Server
	 * -------------------------------------------------------------------------
	 * ----------------------------
	 */
	public boolean startRequestServer() {
		boolean temp_result = false;
		if (requestServerThread.isAlive() == false) {
			requestServerThread.start();
			temp_result = true;
		}
		return temp_result;
	}

	@SuppressWarnings("deprecation")
	public boolean stopRequestServer() {
		boolean temp_result = false;
		if (requestServerThread.isAlive() == true) {
			requestServerThread.stop();
			requestServerThread = new Thread(requestServerRunnable);
			temp_result = true;
		}
		return temp_result;
	}

	public boolean statusRequestServer() {
		return requestServerThread.isAlive();
	}

	/*
	 * -------------------------------------------------------------------------
	 * ---------------------------- Scheduler Server
	 * -------------------------------------------------------------------------
	 * ----------------------------
	 */
	public boolean startSchedulerServer() {
		boolean temp_result = false;
		if (schedulerServerThread.isAlive() == false) {
			schedulerServerThread.start();
			temp_result = true;
		}
		return temp_result;
	}

	@SuppressWarnings("deprecation")
	public boolean stopSchedulerServer() {
		boolean temp_result = false;
		if (schedulerServerThread.isAlive() == true) {
			schedulerServerThread.stop();
			schedulerServerThread = new Thread(schedulerServerThread);
			temp_result = true;
		}
		return temp_result;
	}

	public boolean statusSchedulerServer() {
		return schedulerServerThread.isAlive();
	}

	// Getters and setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}