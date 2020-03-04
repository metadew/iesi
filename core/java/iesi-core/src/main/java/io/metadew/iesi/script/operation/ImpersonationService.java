package io.metadew.iesi.script.operation;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImpersonationService {

    private final ImpersonationConfiguration impersonationConfiguration;

    public ImpersonationService() {
        this.impersonationConfiguration = ImpersonationConfiguration.getInstance();
    }

    public Map<String, String> getImpersontations(String impersonationName) {
        Map<String, String> impersonations = new HashMap<>();
        Impersonation impersonation = impersonationConfiguration.getImpersonation(impersonationName)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ImpersonationKey(impersonationName)));
        impersonation.getParameters().forEach(impersonationParameter -> impersonations.put(impersonationParameter.getConnection(), impersonationParameter.getImpersonatedConnection()));
        return impersonations;
    }

    public Map<String, String> getImpersontationsFromCommandline(String impersonationCmdRepresentation) {
        Map<String, String> impersonations = new HashMap<>();
        String[] impersonationRepresentations = impersonationCmdRepresentation.split(",");
        for (String impersonationRepresentation : impersonationRepresentations) {
            impersonations.putAll(getImpersontationsFromCommandlineSingle(impersonationRepresentation));
        }
        return impersonations;
    }

    private Map<String, String> getImpersontationsFromCommandlineSingle(String impersonationRepresentation) {
        int delim = impersonationRepresentation.indexOf("=");
        if (delim > 0) {
            String key = impersonationRepresentation.substring(0, delim);
            String value = impersonationRepresentation.substring(delim + 1);

            if (key.equalsIgnoreCase("list")) {
                return getImpersontationsFromList(value);
            } else if (key.equalsIgnoreCase("file")){
                return getImpersontationsFromFiles(value);
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    private Map<String, String> getImpersontationsFromFiles(String impersonationFileRepresentations) {
        Map<String, String> impersonations = new HashMap<>();
        String[] impersonationFiles = impersonationFileRepresentations.split(",");
        for (String impersonationFile : impersonationFiles) {
            impersonations.putAll(getImpersontationsFromFile(impersonationFile));
        }
        return impersonations;
    }

    private Map<String, String> getImpersontationsFromFile(String fileName) {
        Map<String, String> impersonations = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int delim = line.indexOf(":");
                if (delim > 0) {
                    String key = line.substring(0, delim);
                    String value = line.substring(delim + 1);
                    impersonations.put(key, value);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return impersonations;
    }


    private Map<String, String> getImpersontationsFromList(String impersonationListRepresentation) {
        Map<String, String> impersonations = new HashMap<>();
        String[] impersonationRepresentations = impersonationListRepresentation.split(";");
        for (String impersonationRepresentation : impersonationRepresentations) {
            int delim = impersonationRepresentation.indexOf(":");
            if (delim > 0) {
                String key = impersonationRepresentation.substring(0, delim);
                String value = impersonationRepresentation.substring(delim + 1);
                impersonations.put(key, value);
            } else {
                throw new RuntimeException();
            }
        }
        return impersonations;
    }






}
