package io.metadew.iesi.server.execution.tools;

import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.execution.FrameworkControl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class ExecutionServerTools {
	public static String getServerMode() {
		String serverMode = "off";
		try {
			serverMode = FrameworkControl.getInstance().getProperty(FrameworkSettingConfiguration.getInstance().getSettingPath("server.mode").get()).toLowerCase();
		} catch (Exception e) {
			serverMode = "off";
		}
		return serverMode;
	}
	
	public static boolean isAlive() {
		int port = -1;
		try {
			port = Integer.parseInt("2222");
		} catch (Exception e) {
			System.err.println("Unable to read port configuration");
			System.exit(1);
		}
		if (port == -1) {
			System.err.println("No port defined for Execution Server");
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
		} catch (Exception e) {
			return false;
		}

		String fromUser;

		try {
			while ((in.readLine()) != null) {
				fromUser = "exit";
				out.println(fromUser);
				break;
			}

			out.close();
			in.close();
			WorkshopSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
}