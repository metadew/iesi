package io.metadew.iesi.common;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class FrameworkInstance {

    private static FrameworkInstance INSTANCE;

    public synchronized static FrameworkInstance getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkInstance();
        }
        return INSTANCE;
    }

    private FrameworkInstance() throws IOException {
        log.info("loading plugins");
        try {
            Files.walk(Paths.get((String) Configuration.getInstance().getMandatoryProperty("iesi.home"), "plugins"), Integer.MAX_VALUE)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(file -> {
                        try {
                            URL url = file.toURI().toURL();
                            log.info(String.format("adding %s", url));

                            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                            method.setAccessible(true);
                            method.invoke(classLoader, url);
                        } catch (Exception e) {
                            throw new RuntimeException("Unexpected exception", e);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        FrameworkRuntime.getInstance().init();
    }


    public void shutdown() {
        log.debug("closing framework instance");
        for (MetadataRepository metadataRepository : MetadataRepositoryConfiguration.getInstance().getMetadataRepositories()) {
            if (metadataRepository != null) {
                metadataRepository.shutdown();
            }
        }
    }


}