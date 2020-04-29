package io.metadew.iesi.common.configuration;

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
        if (!getProperty("iesi.home").isPresent()) {
            properties.put("home", Paths.get("..").toString());
        }
    }

    public Optional<Object> getProperty(String key) {
        if (key.startsWith(iesiKeyword + ".")) {
            return getProperty(key.substring(iesiKeyword.length() + 1), properties);
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
        if (systemProperties.containsKey(iesiKeyword)) {
            HashMap<String, Object> filteredSystemProperties = new HashMap<>();
            filteredSystemProperties.put(iesiKeyword, systemProperties.getProperty(iesiKeyword));
            update(properties, filteredSystemProperties, "");
        }
    }

    private void loadFilesystemFiles() {
        try {
            Files.walkFileTree(Paths.get("..","conf"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @SuppressWarnings("unchecked")
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    if (isIESIApplicationConfigurationFile(file.getFileName().toString())) {
                        Yaml yaml = new Yaml();
                        Map<String, Object> yamlProperties = yaml.load(Files.newBufferedReader(file));
                        if (yamlProperties.containsKey(iesiKeyword)) {
                            log.debug("loading configurations from " + file.getFileName());
                            update(properties, (Map<String, Object>) yamlProperties.get(iesiKeyword), iesiKeyword);
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
                update(properties, (Map<String, Object>) yamlProperties.get(iesiKeyword), iesiKeyword);
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
                    throw new RuntimeException("original value " + initialKey + original.get(entry.getKey()) + " (" + original.get(entry.getKey()).getClass().getSimpleName() + ")" +
                            " does not match update value " + initialKey + entry.getValue() + " (" + entry.getValue().getClass().getSimpleName() + ")");
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
                if (isIESIApplicationConfigurationFile(resource)) {
                    filenames.add(resource);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filenames;
    }

    private boolean isIESIApplicationConfigurationFile(String filename) {
        return filename.startsWith("application") && filename.endsWith(".yml");
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
