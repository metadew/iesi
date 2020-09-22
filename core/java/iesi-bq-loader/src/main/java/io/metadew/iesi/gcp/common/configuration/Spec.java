package io.metadew.iesi.gcp.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.gcp.common.tools.ConfigurationTools;
import io.metadew.iesi.gcp.spec.GcpSpec;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Getter
public class Spec {
    private static final String iesiKeyword = "iesi";
    private static final String gcpKeyword = "gcp";
    private static final String specKeyword = "spec";
    private static Spec INSTANCE;
    private HashMap<String, Object> specs;
    private GcpSpec gcpSpec;

    public synchronized static Spec getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Spec();
        }
        return INSTANCE;
    }

    private Spec() {
        specs = new HashMap<>();
    }

    public Optional<Object> getSpec(String key) {
        return getSpec(key, specs);
    }

    @SuppressWarnings("unchecked")
    private Optional<Object> getSpec(String key, Map<String, Object> properties) {
        log.trace("looking for " + key + " in " + properties);
        if (properties == null) {
            return Optional.empty();
        } else if (key.contains(".")) {
            String[] splittedKey = key.split("\\.", 2);
            return getSpec(splittedKey[1], (Map<String, Object>) properties.get(splittedKey[0]));
        } else {
            return Optional.ofNullable(properties.get(key));
        }
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
                    ConfigurationTools.update(specs, (Map<String, Object>) specProperties.get(specKeyword));
                }
            }
        }

        //Create a spec document
        ObjectMapper objectMapper = new ObjectMapper();
        gcpSpec = objectMapper.convertValue(specs, GcpSpec.class);
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
