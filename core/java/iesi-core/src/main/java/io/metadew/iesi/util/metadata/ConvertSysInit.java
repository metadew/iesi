package io.metadew.iesi.util.metadata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConvertSysInit {

	public static void main(String[] args) {

		final String fileFilter = ".+\\.json";
		final File[] files = FolderTools.getFilesInFolder("C:/Data/metadew/iesi/core/sys/init", "regex",
				fileFilter);

		for (final File file : files) {
			if (file.isDirectory()) {
				// Ignore
			} else {
				System.out.println(file.getAbsolutePath());
				System.out.println(file.getName());
				System.out.println(FileTools.getFileName(file, false));
				System.out.println(FileTools.getFolderPath(file));

				// Define input file
				BufferedReader bufferedReader = null;
				try {
					bufferedReader = new BufferedReader(new FileReader(file));
					String readLine = "";
					boolean jsonArrray = true;

					while ((readLine = bufferedReader.readLine()) != null) {
						if (readLine.trim().toLowerCase().startsWith("[") && (!readLine.trim().equalsIgnoreCase(""))) {
							jsonArrray = true;
							break;
						} else if (!readLine.trim().equalsIgnoreCase("")) {
							jsonArrray = false;
							break;
						}
					}

					ObjectMapper objectMapper = new ObjectMapper();
					ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

					if (jsonArrray) {
						DataObjectOperation dataObjectOperation = new DataObjectOperation();
						dataObjectOperation.setInputFile(file.getAbsolutePath());
						dataObjectOperation.parseFile();
						System.out.println(FileTools.getFolderPath(file) + File.separator
								+ FileTools.getFileName(file, false) + ".yml");
						yamlMapper.writeValue(new File(FileTools.getFolderPath(file) + File.separator
								+ FileTools.getFileName(file, false) + ".yml"), dataObjectOperation.getDataObjects());
						
					} else {
						DataObject dataObject = objectMapper.readValue(file, new TypeReference<DataObject>() {
						});
						System.out.println(FileTools.getFolderPath(file) + File.separator
								+ FileTools.getFileName(file, false) + ".yml");
						yamlMapper.writeValue(new File(FileTools.getFolderPath(file) + File.separator
								+ FileTools.getFileName(file, false) + ".yml"), dataObject);

					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		System.out.println("ok");
	}

}