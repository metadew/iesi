package io.metadew.iesi.sqlinsert.engine;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;

import java.io.File;
import java.io.IOException;

/**
 * SQLInsert connection object.
 *
 * @author peter.billen
 */
public class Engine {

    // host connection details
    public String name;
    public String dbdriver;
    public String connectionURL;
    public String user;
    public String password = null;
    public String schema;
    public String file;

    // Configuration
    private FrameworkConfiguration frameworkConfiguration;

    // return object variables
    public String getName() {
        return this.name;
    }

    public String getDBDriver() {
        return this.dbdriver;
    }

    public String getConnectionURL() {
        return this.connectionURL;
    }

    public String getUser() {
        return this.user;
    }

    public String getHostPassword() {
        return this.password;
    }

    public String getSchema() {
        return this.schema;
    }

    public String getFile() {
        return this.file;
    }

    // constructor method
    public Engine(String RDBMS) {
        this.setFrameworkConfiguration(new FrameworkConfiguration());
        ConfigFile cfg = new ConfigFile(this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run") + File.separator + "engine.config");
        this.name = cfg.getProperty("name");
        this.dbdriver = cfg.getProperty("dbdriver");
        this.connectionURL = cfg.getProperty("connectionURL");
        this.user = cfg.getProperty("user");
        String temp = cfg.getProperty("password");
        if (temp.substring(0, 4).equalsIgnoreCase("ENC(")) {
            if (!temp.substring(temp.length() - 1).equalsIgnoreCase(")")) {
                try {
                    throw new IOException("Encrypted password not set correctly");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FrameworkCrypto cryptoOperation = new FrameworkCrypto();
            this.password = cryptoOperation.decrypt(temp.substring(4, temp.length() - 1));
        } else {
            this.password = temp;
        }
        this.schema = cfg.getProperty("schema");
        this.file = cfg.getProperty("file");
    }

    // Getters and Setters
    public FrameworkConfiguration getFrameworkConfiguration() {
        return frameworkConfiguration;
    }

    public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
        this.frameworkConfiguration = frameworkConfiguration;
    }
}


