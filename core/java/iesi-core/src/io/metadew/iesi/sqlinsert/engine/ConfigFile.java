package io.metadew.iesi.sqlinsert.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * SQLInsert configuration file
 *
 * @author peter.billen
 */
public class ConfigFile {

    Properties materialSheet;

    public ConfigFile(String fileName) {
        materialSheet = new java.util.Properties();
        try {
            File configFileName = new File(fileName);
            InputStream is = new FileInputStream(configFileName.getAbsolutePath());
            materialSheet.load(is);
        } catch (Exception eta) {
            eta.printStackTrace();
        }
    }

    public String getProperty(String key) {
        String value = this.materialSheet.getProperty(key);
        return value;
    }
}
