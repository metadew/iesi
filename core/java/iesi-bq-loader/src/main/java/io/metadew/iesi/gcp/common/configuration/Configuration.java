package io.metadew.iesi.gcp.common.configuration;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.*;

@Log4j2
@Getter
public class Configuration {

    private static final String iesiKeyword = "iesi";
    private static final String configurationKeyword = "gcp";
    private static Configuration INSTANCE;
    private HashMap<String, Object> properties;

    public synchronized static Configuration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Configuration();
        }
        return INSTANCE;
    }

    private Configuration() {
        properties = new HashMap<>();
        loadClasspathFiles();
        log.debug("configuration after classpath loading: " + properties);
        loadFilesystemFiles();
        log.debug("configuration after configuration file loading: " + properties);
        loadSystemVariables();
        log.debug("configuration after system variable loading: " + properties);
    }

    public String resolve(String input) {
        int openPos;
        int closePos;
        String variable_char = "#";
        String midBit;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char, openPos + 1);
            midBit = temp.substring(openPos + 1, closePos);

            // Replacing the value if found
            if (getProperty(midBit).isPresent()) {
                input = input.replaceAll(variable_char + midBit + variable_char, getProperty(midBit)
                        .map(o -> (String) o)
                        .get());
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        return input;
    }

    public Optional<Object> getProperty(String key) {
        if (key.startsWith(iesiKeyword + "." + configurationKeyword + ".")) {
            return getProperty(key.substring(iesiKeyword.length() + 1 + configurationKeyword.length() + 1), properties);
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private Optional<Object> getProperty(String key, Map<String, Object> properties) {
        log.trace("looking for " + key + " in " + properties);
        if (properties == null) {
            return Optional.empty();
        } else if (key.contains(".")) {
            String[] splittedKey = key.split("\\.", 2);
            return getProperty(splittedKey[1], (Map<String, Object>) properties.get(splittedKey[0]));
        } else {
            return Optional.ofNullable(properties.get(key));
        }
    }

    private void loadSystemVariables() {
        Properties systemProperties = System.getProperties();
        systemProperties.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(iesiKeyword + "." + configurationKeyword))
                .forEach(entry -> {
                            log.debug("property " + entry.getKey() + " set via System variable");

                            //Get lowest level property and value
                            HashMap<String, Object> filteredSystemProperties = new HashMap<>();
                            String[] splittedKey = entry.getKey().toString().split("\\.");
                            HashMap<String, Object> currentHashmap = filteredSystemProperties;
                            for (int i = 0; i < splittedKey.length - 1; i++) {
                                HashMap<String, Object> newHashMap = new HashMap<>();
                                currentHashmap.put(splittedKey[i], newHashMap);
                                currentHashmap = newHashMap;
                            }
                            currentHashmap.put(splittedKey[splittedKey.length - 1], entry.getValue());

                            //Rebuild hashmap levels
                            for (int i = splittedKey.length - 2; i > 1; i--) {
                                HashMap<String, Object> newHashMap = new HashMap<>();
                                newHashMap.put(splittedKey[i], currentHashmap);
                                currentHashmap = newHashMap;
                            }

                            //Update properties
                            update(properties, currentHashmap, "");
                        }

                );
    }

    private void loadFilesystemFiles() {
        try {
            Files.walkFileTree(Paths.get("..", "conf"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @SuppressWarnings("unchecked")
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    if (isApplicationConfigurationFile(file.getFileName().toString())) {
                        Yaml yaml = new Yaml();
                        Map<String, Object> yamlProperties = yaml.load(Files.newBufferedReader(file));
                        if (yamlProperties.containsKey(iesiKeyword)) {
                            Map<String, Object> iesiProperties = (Map<String, Object>) yamlProperties.get(iesiKeyword);
                            if (iesiProperties.containsKey(configurationKeyword)) {
                                log.debug("loading configurations from " + file.getFileName());
                                update(properties, (Map<String, Object>) iesiProperties.get(configurationKeyword), iesiKeyword + "." + configurationKeyword);
                            }
                        } else {
                            log.warn("configuration " + file.toString() + " does not contain any iesi properties");
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.SKIP_SUBTREE;
                }


            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadClasspathFiles() {
        Yaml yaml = new Yaml();
        for (String resourceName : getApplicationResourceFiles()) {
            Map<String, Object> yamlProperties = yaml.load(getClass().getClassLoader().getResourceAsStream(resourceName));
            if (yamlProperties.containsKey(iesiKeyword)) {
                Map<String, Object> iesiProperties = (Map<String, Object>) yamlProperties.get(iesiKeyword);
                if (iesiProperties.containsKey(configurationKeyword)) {
                    update(properties, (Map<String, Object>) iesiProperties.get(configurationKeyword), iesiKeyword +"." +configurationKeyword);
                }
            } else {
                log.warn("configuration " + resourceName + " on classpath does not contain any iesi properties");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void update(Map<String, Object> original, Map<String, Object> update, String initialKey) {
        log.trace("updating " + original + " with " + update + " with initial key " + initialKey);
        for (Map.Entry<String, Object> entry : update.entrySet()) {
            if (original.containsKey(entry.getKey()) && original.get(entry.getKey()) == null) {
                original.put(entry.getKey(), entry.getValue());
            } else if (original.containsKey(entry.getKey())) {
                if (original.get(entry.getKey()).getClass().equals(entry.getValue().getClass())) {
                    if (entry.getValue() instanceof Map) {
                        update((Map<String, Object>) original.get(entry.getKey()), (Map<String, Object>) entry.getValue(), initialKey + "." + entry.getKey());
                    } else {
                        original.put(entry.getKey(), entry.getValue());
                    }
                } else {
                    //Different structures are allowed. This makes it possible to overwrite values
                    original.putAll(update);
                }
            } else {
                original.putAll(update);
            }
        }
    }

    private List<String> getApplicationResourceFiles() {
        List<String> filenames = new ArrayList<>();
        try {
            InputStream in = getResourceAsStream("");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String resource;

            while ((resource = br.readLine()) != null) {
                if (isApplicationConfigurationFile(resource)) {
                    filenames.add(resource);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filenames;
    }

    private boolean isApplicationConfigurationFile(String filename) {
        return filename.startsWith("application-gcp") && filename.endsWith(".yml");
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public Object getMandatoryProperty(String code) {
        return getProperty(code)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("No value found for property ''{0}''", code)));
    }

}