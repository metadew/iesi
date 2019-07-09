package io.metadew.iesi.util.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.metadata.definition.DataObject;

public class ConvertConfigFiles {

	public static void main(String[] args) {

		final String fileFilter = ".+\\.json";
		final File[] files = FolderTools.getFilesInFolder("C:/Data/metadew/iesi/core/metadata/conf/UserType",
				"regex", fileFilter);

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