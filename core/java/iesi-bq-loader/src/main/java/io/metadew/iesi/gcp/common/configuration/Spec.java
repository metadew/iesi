package io.metadew.iesi.gcp.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.gcp.common.tools.ConfigurationTools;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Spec {
    private static final String iesiKeyword = "iesi";
    private static final String gcpKeyword = "gcp";
    private static final String specKeyword = "spec";
    private static Spec INSTANCE;
    private HashMap<String, Object> specs;

    public synchronized static Spec getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Spec();
        }
        return INSTANCE;
    }

    private Spec() {
        specs = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void readSpec(Path path) {
        Yaml yaml = new Yaml();
        File file = new File(path.toString());
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // load the scepcs
        Map<String, Object> yamlProperties = yaml.load(inputStream);
        if (yamlProperties.containsKey(iesiKeyword)) {
            Map<String, Object> iesiProperties = (Map<String, Object>) yamlProperties.get(iesiKeyword);
            if (iesiProperties.containsKey(gcpKeyword)) {
                Map<String, Object> specProperties = (Map<String, Object>) iesiProperties.get(gcpKeyword);
                if (specProperties.containsKey(specKeyword)) {
                    ConfigurationTools.update(specs, (Map<String, Object>) specProperties.get(specKeyword), iesiKeyword +"." +gcpKeyword +"." +specKeyword);
                }
            }
        }

        System.out.println(specs.toString());
    }

    public String toString() {
        String output = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            output = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(specs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }
}
