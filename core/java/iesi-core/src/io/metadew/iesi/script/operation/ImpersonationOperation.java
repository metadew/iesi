package io.metadew.iesi.script.operation;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ImpersonationConfiguration;
import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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

    private FrameworkExecution frameworkExecution;
    private HashMap<String, String> impersonationMap;

    // Constructors
    public ImpersonationOperation(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
        // Initialize impersonation
        this.setImpersonationMap(new HashMap<String, String>());
    }

    // Methods
    public void setImpersonation(String impersonationName) {
        if (!impersonationName.trim().equalsIgnoreCase("")) {
            ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(this.getFrameworkExecution().getFrameworkInstance());
            Optional<Impersonation> impersonation = impersonationConfiguration.getImpersonation(impersonationName);


            if (!impersonation.isPresent()) {
                this.getFrameworkExecution().getFrameworkLog().log("impersonation.notfound=" + impersonationName, Level.INFO);
            } else {
                if (impersonation.get().getParameters() == null) {
                    this.getFrameworkExecution().getFrameworkLog().log("impersonation.conn.notfound=" + impersonationName, Level.INFO);
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

            String line = null;
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImpersonation(String connectionName, String impersonatedConnectionName) {
        this.getImpersonationMap().put(connectionName, impersonatedConnectionName);
    }

    public String getImpersonatedConnection(String connectionName) {
        String result = this.getImpersonationMap().get(connectionName);
        if (result == null) result = "";
        return result;
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public HashMap<String, String> getImpersonationMap() {
        return impersonationMap;
    }

    public void setImpersonationMap(HashMap<String, String> impersonationMap) {
        this.impersonationMap = impersonationMap;
    }

}