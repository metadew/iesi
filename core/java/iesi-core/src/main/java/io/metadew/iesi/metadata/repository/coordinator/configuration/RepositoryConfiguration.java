//package io.metadew.iesi.metadata.repository.coordinator.configuration;
//
//import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
//
//public abstract class RepositoryConfiguration {
//
//    RepositoryConfiguration(ConfigFile configFile) {
//    	fromConfigFile(configFile);
//    }
//
//    abstract void fromConfigFile(ConfigFile configFile);
//
//    public abstract RepositoryCoordinator toRepository();
//
//    public String getSettingValue(ConfigFile configFile, String settingPath) {
//        return FrameworkSettingConfiguration.getInstance().getSettingPath(settingPath)
//                .map(s -> configFile.getProperty(s)
//                        .orElse(null))
//                .orElse(null);
//    }
//
//}
