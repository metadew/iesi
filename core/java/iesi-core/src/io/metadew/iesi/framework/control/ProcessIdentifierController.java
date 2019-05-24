package io.metadew.iesi.framework.control;

import java.util.Properties;
import java.util.concurrent.Semaphore;

import io.metadew.iesi.common.properties.PropertiesTools;
import io.metadew.iesi.connection.tools.FileTools;

public final class ProcessIdentifierController {
	
	synchronized Long getNextProcessId(Long processId) {
	    return processId++;
	}

	public static void getNextProcessId(String fileName, String spoolName) {
		NextProcessIdThread nextProcessIdThread = new NextProcessIdThread(fileName, spoolName);
		try {
			nextProcessIdThread.start();
			nextProcessIdThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException("processId.next.error");
		}
	}

	// Mutex implementation
	static Semaphore semaphore = new Semaphore(1);

	static class NextProcessIdThread extends Thread {

		String spoolName = "";
		String fileName = "";

		NextProcessIdThread(String fileName, String spoolName) {
			this.fileName = fileName;
			this.spoolName = spoolName;
			
		}

		public void run() {

			try {
				semaphore.acquire();
				try {

					Properties processIdProperties = PropertiesTools.getProperties(fileName);
					Long processId = Long.parseLong(processIdProperties.getProperty("processId"));
					processId++;
					processIdProperties.put("processId", Long.toString(processId));
					PropertiesTools.setProperties(fileName, processIdProperties);
					FileTools.copyFromFileToFile(fileName, spoolName);
				} finally {
					semaphore.release();
				}

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

		}
	}


}