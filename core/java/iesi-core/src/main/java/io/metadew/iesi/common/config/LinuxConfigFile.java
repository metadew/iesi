package io.metadew.iesi.common.config;

import io.metadew.iesi.framework.execution.FrameworkControl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LinuxConfigFile extends ConfigFile {

    public LinuxConfigFile(Path fileName) {
        super();
        try {
            @SuppressWarnings("resource")
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName.toFile()));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                if (readLine.trim().toLowerCase().startsWith("export ")) {
                    String innerpart = readLine.trim().substring(7);
                    int delim = innerpart.indexOf("=");
                    if (delim > 0) {
                        String key = innerpart.substring(0, delim);
                        String value = innerpart.substring(delim + 1);
                        this.setProperty(key, FrameworkControl.getInstance().resolveConfiguration(value));
                    } else {
                        // Not a valid configuration
                    }

                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}