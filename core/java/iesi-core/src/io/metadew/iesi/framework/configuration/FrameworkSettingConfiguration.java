package io.metadew.iesi.framework.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.definition.FrameworkSetting;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;

public class FrameworkSettingConfiguration {

    private String solutionHome;
    private HashMap<String, String> settingMap;

    public FrameworkSettingConfiguration(String solutionHome) {
        this.setSolutionHome(solutionHome);
        this.initalizeValues();
    }

    private void initalizeValues() {
        this.setSettingMap(new HashMap<String, String>());

        StringBuilder initFilePath = new StringBuilder();
        initFilePath.append(this.getSolutionHome());
        initFilePath.append(File.separator);
        initFilePath.append("sys");
        initFilePath.append(File.separator);
        initFilePath.append("init");
        initFilePath.append(File.separator);
        initFilePath.append("FrameworkSettings.json");

        DataObjectOperation dataObjectOperation = new DataObjectOperation();
        dataObjectOperation.setInputFile(initFilePath.toString());
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("frameworksetting")) {
                FrameworkSetting frameworkSetting = objectMapper.convertValue(dataObject.getData(), FrameworkSetting.class);
                this.getSettingMap().put(frameworkSetting.getName(), frameworkSetting.getPath());
            }
        }
    }

    // Create Getters and Setters

    /**
     * @param key: key to lookup in framework settings
     * @return: Optional of the value if key is present in the settings map. If no value or an empty value is present an Optional empty is returned
     */
    public Optional<String> getSettingPath(String key) {
        return Optional.ofNullable(this.getSettingMap().get(key)).filter(s -> !s.isEmpty());
    }

    public String getSolutionHome() {
        return solutionHome;
    }

    public void setSolutionHome(String solutionHome) {
        this.solutionHome = solutionHome;
    }

    public HashMap<String, String> getSettingMap() {
        return settingMap;
    }

    public void setSettingMap(HashMap<String, String> settingMap) {
        this.settingMap = settingMap;
    }


}