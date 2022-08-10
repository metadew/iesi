package io.metadew.iesi.common.configuration.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Getter
public class PluginConfiguration {

    private static PluginConfiguration INSTANCE;
    private static final String pluginsKey = "plugins";

    private Map<String, FrameworkPlugin> frameworkPluginMap;
    Configuration configuration = SpringContext.getBean(Configuration.class);

    public synchronized static PluginConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PluginConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private PluginConfiguration() {
        frameworkPluginMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkPluginsConfigurations = (Map<String, Object>) configuration.getProperties()
                    .get(pluginsKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkPluginsConfigurations.entrySet()) {
                frameworkPluginMap.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), FrameworkPlugin.class));
            }
        } else {
            log.warn("no metadata configuration found on system variable, classpath or filesystem");
        }
    }


    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(PluginConfiguration.pluginsKey) &&
                (configuration.getProperties().get(PluginConfiguration.pluginsKey) instanceof Map);
    }

}
