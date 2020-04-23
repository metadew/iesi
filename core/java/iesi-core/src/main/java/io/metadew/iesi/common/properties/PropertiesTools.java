package io.metadew.iesi.common.properties;

import java.io.*;
import java.util.Properties;

public final class PropertiesTools {

	public static void setProperties(String filePath, Properties properties) {
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null);
        } catch (IOException e) {
            throw new RuntimeException("properties.store.error");
        }
	}
	
	public static Properties getProperties(String filePath) {
        Properties properties = new Properties();
		try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("properties.load.error");
        }
		return properties;
	}

	public static String getProperty(String filePath, String key) {
        Properties properties = new Properties();
		try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("properties.load.error");
        }
		return (String) properties.get(key);
	}
	
}