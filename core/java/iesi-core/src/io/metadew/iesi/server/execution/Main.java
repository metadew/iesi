package io.metadew.iesi.server.execution;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) throws IOException {
        //Get Server configuration
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

        Services services = null;

        //Start Workshop Server
        try {
            services = new Services();
            System.out.println("Services started");

        } catch (Exception e) {
            System.err.println("Unable to start services");
            System.exit(1);
        }

        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening to port " + port);
        } catch (IOException e) {
            System.err.println("Unable to listen to port " + port);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unable to listen to port " + port);
            System.exit(1);
        }

        while (listening)
            new ServicesThread(serverSocket.accept(), services).start();

        serverSocket.close();
    }
}