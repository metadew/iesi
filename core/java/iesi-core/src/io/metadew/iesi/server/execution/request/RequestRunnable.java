package io.metadew.iesi.server.execution.request;

public class RequestRunnable implements Runnable {
    @Override
    public void run() {

    }
//
//	private FrameworkExecution frameworkExecution;
//
//	public RequestRunnable(FrameworkExecution frameworkExecution) {
//		this.setFrameworkExecution(frameworkExecution);
//	}
//
//	// @Override
//	public void run() {
//
//		int interval = Integer.parseInt("1000");
//
//		RequestService requestServer = new RequestService(this.getFrameworkExecution());
//
//		int i = 1;
//		while (i == 1) {
//			try {
//				Thread.sleep(interval);
//			} catch (InterruptedException ex) {
//				Thread.currentThread().interrupt();
//			}
//
//			if (requestServer.execListen()) {
//				requestServer.execute();
//			}
//
//		}
//
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