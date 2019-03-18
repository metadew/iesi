package io.metadew.iesi.assembly.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import io.metadew.iesi.connection.tools.FolderTools;

public class FileSystemOperation {

	public FileSystemOperation() {

	}

	@SuppressWarnings("resource")
	public void createSolutionStructure(String configurationFilePath, String targetFolderPath) {
		try {
			File file = new File(configurationFilePath);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String readLine = "";			
			while ((readLine = bufferedReader.readLine()) != null) {
				String innerpart = readLine.trim();
				String[] parts = innerpart.split(";");
				FolderTools.createFolder(targetFolderPath + parts[0]);
			}
		} catch (IOException e) {
			throw new RuntimeException("Issue creating solution structure",e);
		}
	}

}