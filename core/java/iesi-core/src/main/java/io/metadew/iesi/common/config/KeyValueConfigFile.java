package io.metadew.iesi.common.config;

import io.metadew.iesi.framework.execution.FrameworkControl;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class KeyValueConfigFile extends ConfigFile {

    public KeyValueConfigFile(String fileName) {
        super();
        this.setFilePath(fileName);
        try {
            File file = new File(FilenameUtils.normalize(fileName));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                String innerpart = readLine.trim();
                int delim = innerpart.indexOf("=");
                if (!innerpart.startsWith("#") && !innerpart.equalsIgnoreCase("")) {
                    if (delim > 0) {
                        String key = innerpart.substring(0, delim);
                        String value = innerpart.substring(delim + 1);
                        this.setProperty(key, FrameworkControl.getInstance().resolveConfiguration(value));
                    } else {
                        throw new RuntimeException("Not a valid configuration file");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public KeyValueConfigFile(File file) {
        super();
        try {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
