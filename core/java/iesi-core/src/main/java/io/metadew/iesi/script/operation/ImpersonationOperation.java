package io.metadew.iesi.script.operation;

import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

/**
 * Operation to manage impersonations of connection during script execution.
 *
 * @author peter.billen
 */
public class ImpersonationOperation {

    private HashMap<String, String> impersonationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public ImpersonationOperation() {
        impersonationMap = new HashMap<String, String>();
    }

    // Methods
    public void setImpersonation(String impersonationName) {
        if (!impersonationName.trim().equalsIgnoreCase("")) {
            ImpersonationConfiguration impersonationConfiguration = ImpersonationConfiguration.getInstance();
            Optional<Impersonation> impersonation = impersonationConfiguration.getImpersonation(impersonationName);


            if (!impersonation.isPresent()) {
                LOGGER.info("impersonation.notfound=" + impersonationName);
            } else {
                if (impersonation.get().getParameters() == null) {
                    LOGGER.info("impersonation.conn.notfound=" + impersonationName);
                } else {
                    for (ImpersonationParameter impersonationParameter : impersonation.get().getParameters()) {
                        this.setImpersonation(impersonationParameter.getConnection(), impersonationParameter.getImpersonatedConnection());
                    }
                }
            }
        }
    }

    public void setImpersonationCustom(String impersonationCustom) {
        this.loadCustomInput(impersonationCustom);
    }

    public void loadCustomInput(String input) {
        String[] parts = input.split(",");
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            int delim = innerpart.indexOf("=");
            if (delim > 0) {
                String key = innerpart.substring(0, delim);
                String value = innerpart.substring(delim + 1);

                if (key.equalsIgnoreCase("list")) {
                    this.loadImpersonationList(value);
                }

                if (key.equalsIgnoreCase("file")) {
                    this.loadImpersonationFiles(value);
                }

            } else {
                // Not a valid configuration
            }
        }

    }

    public void loadImpersonationList(String input) {
        String[] parts = input.split(";");
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            int delim = innerpart.indexOf(":");
            if (delim > 0) {
                String key = innerpart.substring(0, delim);
                String value = innerpart.substring(delim + 1);
                this.setImpersonation(key, value);
            } else {
                // Not a valid configuration
            }
        }
    }

    public void loadImpersonationFiles(String files) {
        String[] parts = files.split(",");
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            this.loadImpersonationFile(innerpart);
        }
    }

    public void loadImpersonationFile(String file) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null) {
                String innerpart = line;
                int delim = innerpart.indexOf(":");
                if (delim > 0) {
                    String key = innerpart.substring(0, delim);
                    String value = innerpart.substring(delim + 1);
                    this.setImpersonation(key, value);
                } else {
                    // Not a valid configuration
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImpersonation(String connectionName, String impersonatedConnectionName) {
        impersonationMap.put(connectionName, impersonatedConnectionName);
    }

    public String getImpersonatedConnection(String connectionName) {
        String result = impersonationMap.get(connectionName);
        if (result == null) result = "";
        return result;
    }

}