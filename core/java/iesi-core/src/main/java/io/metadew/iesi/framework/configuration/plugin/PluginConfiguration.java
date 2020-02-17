package io.metadew.iesi.framework.configuration.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.configuration.Configuration;
import io.metadew.iesi.framework.definition.FrameworkPlugin;
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
            Map<String, Object> frameworkPluginsConfigurations = (Map<String, Object>) Configuration.getInstance().getProperties()
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
        return Configuration.getInstance().getProperties().containsKey(PluginConfiguration.pluginsKey) ||
                (Configuration.getInstance().getProperties().get(PluginConfiguration.pluginsKey) instanceof Map);
    }

}
