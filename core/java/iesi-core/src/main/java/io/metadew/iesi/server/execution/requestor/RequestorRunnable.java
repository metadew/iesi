package io.metadew.iesi.server.execution.requestor;

import io.metadew.iesi.framework.instance.FrameworkInstance;

public class RequestorRunnable implements Runnable {
    private FrameworkInstance frameworkInstance;

    public RequestorRunnable(FrameworkInstance frameworkInstance) {
        this.setFrameworkInstance(frameworkInstance);
    }

    // @Override
    public void run() {

        int interval = Integer.parseInt("1000");

        RequestorService requestServer = new RequestorService(this.getFrameworkInstance());

        int i = 1;
        while (i == 1) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            if (requestServer.execListen()) {
                requestServer.execute();
            }
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