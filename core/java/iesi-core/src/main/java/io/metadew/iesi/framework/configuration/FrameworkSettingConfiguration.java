package io.metadew.iesi.framework.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.definition.FrameworkSetting;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FrameworkSettingConfiguration {

    private Path solutionHome;
    private Map<String, String> settingMap;

    private static FrameworkSettingConfiguration INSTANCE;

    public synchronized static FrameworkSettingConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkSettingConfiguration();
        }
        return INSTANCE;
    }

    private FrameworkSettingConfiguration() {}

    public void init(String solutionHome, Map<String, String> settingMap) {
        this.solutionHome = Paths.get(solutionHome);
        this.settingMap = settingMap;
    }

    public void init(String solutionHome) {
        init(Paths.get(solutionHome));
    }

    public void init(Path solutionHome) {
        this.solutionHome = solutionHome;
        this.settingMap = new HashMap<>();
        Path initFilePath = solutionHome.resolve("sys").resolve("init").resolve("FrameworkSettings.json");
        DataObjectOperation dataObjectOperation = new DataObjectOperation(initFilePath.toString());
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("frameworksetting")) {
                FrameworkSetting frameworkSetting = objectMapper.convertValue(dataObject.getData(), FrameworkSetting.class);
                settingMap.put(frameworkSetting.getName(), frameworkSetting.getPath());
            }
        }
    }

    // Create Getters and Setters

    /**
     * @param key: key to lookup in framework settings
     * @return: Optional of the value if key is present in the settings map. If no value or an empty value is present an Optional empty is returned
     */
    public Optional<String> getSettingPath(String key) {
        return Optional.ofNullable(settingMap.get(key)).filter(s -> !s.isEmpty());
    }


}