//package io.metadew.iesi.framework.configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.metadew.iesi.framework.definition.FrameworkSetting;
//import io.metadew.iesi.metadata.definition.DataObject;
//import io.metadew.iesi.metadata.operation.DataObjectOperation;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//public class FrameworkSettingConfiguration {
//
//    private String solutionHome;
//    private Map<String, String> settingMap;
//
//    private static FrameworkSettingConfiguration INSTANCE;
//
//    public synchronized static FrameworkSettingConfiguration getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new FrameworkSettingConfiguration();
//        }
//        return INSTANCE;
//    }
//
//    private FrameworkSettingConfiguration() {}
//
//    public void init(String solutionHome, Map<String, String> settingMap) {
//        this.solutionHome = solutionHome;
//        this.settingMap = settingMap;
//    }
//
//    public void init(String solutionHome) {
//        this.solutionHome = solutionHome;
//        this.settingMap = new HashMap<>();
//        String initFilePath = this.solutionHome + File.separator + "sys" + File.separator + "init" + File.separator +
//                "FrameworkSettings.json";
//        DataObjectOperation dataObjectOperation = new DataObjectOperation(initFilePath);
//        dataObjectOperation.parseFile();
//        ObjectMapper objectMapper = new ObjectMapper();
////        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
////            if (dataObject.getType().equalsIgnoreCase("frameworksetting")) {
////                FrameworkSetting frameworkSetting = objectMapper.convertValue(dataObject.getData(), FrameworkSetting.class);
////                settingMap.put(frameworkSetting.getName(), frameworkSetting.getPath());
////            }
////        }
//    }
//
//    // Create Getters and Setters
//
//    /**
//     * @param key: key to lookup in framework settings
//     * @return: Optional of the value if key is present in the settings map. If no value or an empty value is present an Optional empty is returned
//     */
//    public Optional<String> getSettingPath(String key) {
//        return Optional.ofNullable(settingMap.get(key)).filter(s -> !s.isEmpty());
//    }
//
//
//}