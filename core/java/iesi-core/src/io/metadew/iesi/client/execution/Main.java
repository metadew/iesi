package io.metadew.iesi.client.execution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Main {
    public static void main(String[] args) throws IOException {
        int port = -1;
        try {
            port = Integer.parseInt("2222");
        } catch (Exception e) {
            System.err.println("Unable to read port configuration");
            System.exit(1);
        }
        if (port == -1) {
            System.err.println("No port defined for Workshop Server");
            System.exit(1);
        }

        String host = "";
        try {
            host = "localhost";
        } catch (Exception e) {
            System.err.println("Unable to read host configuration");
            System.exit(1);
        }
        if (host.isEmpty()) {
            System.err.println("No host defined for Workshop Server");
            System.exit(1);
        }


        Socket WorkshopSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;


        try {
            WorkshopSocket = new Socket(host, port);
            out = new PrintWriter(WorkshopSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(WorkshopSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Unable to connect to unknown host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Unable to get I/O from host " + host + " on port " + port);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unable to connect to host " + host + " on port " + port);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        while ((fromServer = in.readLine()) != null) {
            if (fromServer.equalsIgnoreCase("EOM")) {
                System.out.print("> ");
                fromUser = stdIn.readLine();
            } else if (fromServer.equalsIgnoreCase("CON")) {
                System.out.println("Execution client connection successful");
                System.out.println("Host: " + host);
                System.out.println("Port: " + port);
                System.out.println();
                System.out.println("Type help for a list of commands. Type exit to quit the execution client.");
                System.out.println();
                System.out.print("> ");
                fromUser = stdIn.readLine();
            } else {
                System.out.println(fromServer);
                fromUser = "ACK";
            }

            if (fromUser != null) {
                out.println(fromUser);
                if (fromUser.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting execution client ... Goodbye!");
                    System.exit(0);
                }

            }
        }

        out.close();
        in.close();
        stdIn.close();
        WorkshopSocket.close();
    }
}