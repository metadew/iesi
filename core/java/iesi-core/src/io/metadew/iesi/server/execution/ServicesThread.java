package io.metadew.iesi.server.execution;

import io.metadew.iesi.server.execution.configuration.ExecutionServerServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServicesThread extends Thread {
    private Socket socket = null;
    private Services services = null;
    private ArrayList<String> srvList = null;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ServicesThread(Socket socket, Services srv) {
        super("WorkshopThread");
        this.socket = socket;
        this.services = srv;
        srvList = new ArrayList();
        srvList.add(ExecutionServerServices.REQUESTOR.value());
    }

    public void run() {

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            String inputLine, outputLine;
            @SuppressWarnings("rawtypes")
            ArrayList outputMessage = new ArrayList();
            outputLine = "CON";
            out.println(outputLine);

            int i = 0;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase("ACK")) {
                    i++;
                    outputLine = (String) outputMessage.get(i);
                    if (outputLine.equalsIgnoreCase("EOM")) {
                        out.println(outputLine);
                        outputMessage.clear();
                        i = 0;
                    } else {
                        out.println(outputLine);
                    }
                } else {
                    outputMessage = this.executeCommand(inputLine);
                    outputLine = (String) outputMessage.get(i);
                    out.println(outputLine);
                }

                if (inputLine.equalsIgnoreCase("exit"))
                    break;
            }
            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ArrayList executeCommand(String command) {
        String delims = "[ ]+";
        String[] tokens = command.split(delims);
        ArrayList out = new ArrayList();

        if (tokens[0].equalsIgnoreCase("help")) {
            //help
            out.add("");
            out.add("List of available commands");
            out.add("--------------------------");
            out.add("services");
            out.add("status");
            out.add("start");
            out.add("stop");
            out.add("exit");
            out.add("");
            out.add("EOM");
        } else if (tokens[0].equalsIgnoreCase("services")) {
            //Services
            out.add("");
            out.add("List of available services");
            out.add("--------------------------");
            for (String srvName : this.srvList) {
                out.add(srvName);
            }
            out.add("");
            out.add("EOM");
        } else if (tokens[0].equalsIgnoreCase("status")) {
            //Status
            if (tokens.length < 2) {
                out.add("Please enter the appropriate arguments for the command");
            } else {
                if (this.inList(this.srvList, tokens[1]) == true) {
                    out.add("SERVICE " + tokens[1] + ": " + services.status(tokens[1]));
                } else {
                    out.add("'" + tokens[1] + "' is not a recognized service");
                }
            }
            out.add("");
            out.add("EOM");
        } else if (tokens[0].equalsIgnoreCase("start")) {
            //start
            if (tokens.length < 2) {
                out.add("Please enter the appropriate arguments for the command");
            } else {
                if (this.inList(this.srvList, tokens[1]) == true) {
                    out.add("SERVICE " + tokens[1] + ": " + services.start(tokens[1]));
                } else {
                    out.add("'" + tokens[1] + "' is not a recognized service");
                }
            }
            out.add("");
            out.add("EOM");
        } else if (tokens[0].equalsIgnoreCase("stop")) {
            //Stop
            if (tokens.length < 2) {
                out.add("Please enter the appropriate arguments for the command");
            } else {
                if (this.inList(this.srvList, tokens[1]) == true) {
                    out.add("SERVICE " + tokens[1] + ": " + services.stop(tokens[1]));
                } else {
                    out.add("'" + tokens[1] + "' is not a recognized service");
                }
            }
            out.add("");
            out.add("EOM");
        } else {
            //ELSE
            if (command.isEmpty()) {
                out.add("EOM");
            } else {
                out.add("'" + command + "' is not a recognized command");
                out.add("EOM");
            }
        }

        return out;
    }

    private boolean inList(ArrayList<String> list, String checkItem) {
        boolean tempResult = false;

        for (String curVal : list) {
            if (curVal.equalsIgnoreCase(checkItem)) {
                tempResult = true;
            }
        }

        return tempResult;
    }

}