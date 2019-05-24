package io.metadew.iesi.launch.demo;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ParallelExecutionLauncher extends Thread {
    private Thread t;
    private String threadName;
    private String command;
    private String type = "windows";

    ParallelExecutionLauncher(String name, String command) {
        threadName = name;
        System.out.println("Creating " + threadName);
        this.setCommand(command);
    }

    public void run() {
        System.out.println("Running " + threadName);
        try {
/*         for(int i = 4; i > 0; i--) {
            System.out.println("Thread: " + threadName + ", " + i);
            // Let the thread sleep for a while.
            Thread.sleep(1000);
         }*/
            this.executeLocalCommand("", this.getCommand());
        } catch (Exception e) {
            System.out.println("Thread " + threadName + " interrupted.");
        }
        System.out.println("Thread " + threadName + " exiting.");
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    @SuppressWarnings("unused")
    public void executeLocalCommand(String shellPath, String shellCommand) {
        String executionShellPath = "";
        String executionShellCommand = "";

        executionShellPath = shellPath.trim();
        if (this.getType().equalsIgnoreCase("windows")) {
            // For Windows Commands, "cmd /c" needs to be put in front of the
            // command
            if (executionShellPath.equalsIgnoreCase("")) {
                executionShellCommand = "cmd /c " + "\"" + shellCommand + "\"";
            } else {
                executionShellCommand = "cmd /c " + "\"cd " + executionShellPath + " & " + shellCommand + "\"";
            }
        } else {
            if (executionShellPath.equalsIgnoreCase("")) {
                executionShellCommand = shellCommand;
            } else {
                executionShellCommand = "cd " + executionShellPath + " && " + shellCommand;
            }
        }

        //
        int rc;
        String systemOutput = "";
        String errorOutput = "";
        try {
            final Process p = Runtime.getRuntime().exec(executionShellCommand);

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            String lines = "";

            try {
                while ((line = input.readLine()) != null) {
                    if (!lines.equalsIgnoreCase(""))
                        lines = lines + "\n";
                    lines = lines + line;
                    System.out.println(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            rc = p.waitFor();
            systemOutput = lines;
            errorOutput = IOUtils.toString(p.getErrorStream());

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}