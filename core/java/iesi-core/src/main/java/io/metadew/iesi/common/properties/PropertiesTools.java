package io.metadew.iesi.common.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public final class PropertiesTools {

	public static void setProperties(String filePath, Properties properties) {
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null);
        } catch (Exception e) {
            throw new RuntimeException("properties.store.error");
        }
	}
	
	public static Properties getProperties(String filePath) {
        Properties properties = new Properties();
		try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("properties.load.error");
        }
		return properties;
	}

	public static void setProperty(String filePath, String key, String value) {
		Properties properties = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
        	properties.load(input);
            
        	Properties updatedProperties = new Properties();
        	properties.forEach((propertyKey, propertyValue) -> {
        		if (key.equalsIgnoreCase((String) propertyKey)) {
        			updatedProperties.put(key, value);
        		} else {
        			updatedProperties.put(propertyKey, propertyValue);
        		}
            });

        	PropertiesTools.setProperties(filePath, updatedProperties);
        } catch (Exception e) {
            throw new RuntimeException("properties.store.error");
        }
	}
	
	public static String getProperty(String filePath, String key) {
        Properties properties = new Properties();
		try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("properties.load.error");
        }
		return (String) properties.get(key);
	}
	
}