package io.metadew.iesi.server.execution;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
    public static void main(String[] args) throws IOException {
		Option oHelp = new Option("help", "print this message");
		Option oIni = new Option("ini", true, "define the initialization file");

		// create Options object
		Options options = new Options();
		// add options
		options.addOption(oHelp);
		options.addOption(oIni);
		
		// create the parser
		CommandLineParser parser = new DefaultParser();
		String initializationFile = "";
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("[command]", options);
				System.exit(0);
			}
			
			// Define the initialization file
			if (line.hasOption("ini")) {
				initializationFile = line.getOptionValue("ini");
			}
			System.out.println("Option -ini (ini) value = " + initializationFile);
    	
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
    	
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
            services = new Services(initializationFile);
            System.out.println("Services started");
        } catch (Exception e) {
            System.err.println("Unable to start services");
            e.printStackTrace();
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