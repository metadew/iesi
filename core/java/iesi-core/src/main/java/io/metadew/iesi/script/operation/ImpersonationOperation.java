package io.metadew.iesi.script.operation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Operation to manage impersonations of connection during script execution.
 *
 * @author peter.billen
 */
public class ImpersonationOperation {

    private HashMap<String, String> impersonationMap;

    // Constructors
    public ImpersonationOperation() {
        impersonationMap = new HashMap<String, String>();
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