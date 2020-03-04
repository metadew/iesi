package io.metadew.iesi.common.config;

import io.metadew.iesi.framework.execution.FrameworkControl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class KeyValueConfigFile extends ConfigFile {

    public KeyValueConfigFile(Path fileName) {
        super();
        this.setFilePath(fileName.toString());
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName.toFile()));
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
            bufferedReader.close();
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
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
