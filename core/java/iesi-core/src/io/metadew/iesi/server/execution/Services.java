package io.metadew.iesi.server.execution;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.server.execution.configuration.ExecutionServerServices;
import io.metadew.iesi.server.execution.requestor.RequestorRunnable;

public class Services {
    public Runnable requestorRunnable;
    public Thread requestorThread;
    public Runnable schedulerRunnable;
    public Thread schedulerThread;
    private FrameworkInstance frameworkInstance;

    public Services() {
        // Create the framework instance
        FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
        frameworkInitializationFile.setName("iesi-test.ini");
        this.setFrameworkInstance(new FrameworkInstance(frameworkInitializationFile));

        // Create the framework settings
        //FrameworkExecutionSettings frameworkExecutionSettings = new FrameworkExecutionSettings("");

        // Create the framework execution
        Context context = new Context();
        context.setName("server");
        context.setScope("");

        requestorRunnable = new RequestorRunnable(this.getFrameworkInstance());
        requestorThread = new Thread(requestorRunnable);
        requestorThread.setName(ExecutionServerServices.REQUESTOR.value());
        // schedulerRunnable = new SchedulerRunnable();
        // schedulerThread = new Thread(schedulerRunnable);
        this.startRequestor();
    }

    public void startAll() {
        if (requestorThread.isAlive() == false) {
            this.startRequestor();
        }
    }

    @SuppressWarnings("deprecation")
    public void stopAll() {
        if (requestorThread.isAlive() == true) {
            requestorThread.stop();
        }
    }

    public String status(String serviceName) {
        boolean temp_status = false;
        boolean temp_service = false;

        if (serviceName.equalsIgnoreCase(ExecutionServerServices.REQUESTOR.value())) {
            temp_status = this.statusRequestor();
            temp_service = true;
        } else if (serviceName.equalsIgnoreCase(ExecutionServerServices.SCHEDULER.value())) {
            temp_status = this.statusScheduler();
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

        if (serviceName.equalsIgnoreCase(ExecutionServerServices.REQUESTOR.value())) {
            this.startRequestor();
            temp_service = true;
        } else if (serviceName.equalsIgnoreCase(ExecutionServerServices.SCHEDULER.value())) {
            this.startScheduler();
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

        if (serviceName.equalsIgnoreCase(ExecutionServerServices.REQUESTOR.value())) {
            this.stopRequestor();
            temp_service = true;
        } else if (serviceName.equalsIgnoreCase(ExecutionServerServices.SCHEDULER.value())) {
            this.stopScheduler();
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
    public boolean startRequestor() {
        boolean temp_result = false;
        if (requestorThread.isAlive() == false) {
            requestorThread.start();
            temp_result = true;
        }
        return temp_result;
    }

    @SuppressWarnings("deprecation")
    public boolean stopRequestor() {
        boolean temp_result = false;
        if (requestorThread.isAlive() == true) {
            requestorThread.stop();
            requestorThread = new Thread(requestorRunnable);
            temp_result = true;
        }
        return temp_result;
    }

    public boolean statusRequestor() {
        return requestorThread.isAlive();
    }

    /*
     * -------------------------------------------------------------------------
     * ---------------------------- Scheduler Server
     * -------------------------------------------------------------------------
     * ----------------------------
     */
    public boolean startScheduler() {
        boolean temp_result = false;
        if (schedulerThread.isAlive() == false) {
            schedulerThread.start();
            temp_result = true;
        }
        return temp_result;
    }

    @SuppressWarnings("deprecation")
    public boolean stopScheduler() {
        boolean temp_result = false;
        if (schedulerThread.isAlive() == true) {
            schedulerThread.stop();
            schedulerThread = new Thread(schedulerThread);
            temp_result = true;
        }
        return temp_result;
    }

    public boolean statusScheduler() {
        return schedulerThread.isAlive();
    }

    // Getters and setters
    public FrameworkInstance getFrameworkInstance() {
        return frameworkInstance;
    }

    public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
        this.frameworkInstance = frameworkInstance;
    }


}