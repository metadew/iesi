package io.metadew.iesi.common.config;

import io.metadew.iesi.framework.execution.FrameworkControl;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class KeyValueConfigFile extends ConfigFile {

    @SuppressWarnings("resource")
    public KeyValueConfigFile(FrameworkControl frameworkControl, String fileName) {
        super();
        this.setFilePath(fileName);
        try {
            File file = new File(FilenameUtils.normalize(fileName));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                String innerpart = readLine.trim();
                int delim = innerpart.indexOf("=");
                if (!innerpart.startsWith("#") && !innerpart.equalsIgnoreCase("")) {
                    if (delim > 0) {
                        String key = innerpart.substring(0, delim);
                        String value = innerpart.substring(delim + 1);
                        this.setProperty(key, frameworkControl.resolveConfiguration(value));
                    } else {
                        throw new RuntimeException("Not a valid configuration file");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
    public KeyValueConfigFile(String fileName) {
        super();
        try {
            File file = new File(FilenameUtils.normalize(fileName));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                String innerpart = readLine.trim();
                int delim = innerpart.indexOf("=");
                if (!innerpart.startsWith("#") && !innerpart.equalsIgnoreCase("")) {
                    if (delim > 0) {
                        String key = innerpart.substring(0, delim);
                        String value = innerpart.substring(delim + 1);
                        this.setProperty(key, value);
                    } else {
                        throw new RuntimeException("Not a valid configuration file");
                    }
                }
            }
        } catch (

                Exception e) {
            e.printStackTrace();
        }
    }
}
