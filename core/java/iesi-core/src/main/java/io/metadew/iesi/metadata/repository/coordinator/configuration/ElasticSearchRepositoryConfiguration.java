//package io.metadew.iesi.metadata.repository.coordinator.configuration;
//
//import io.metadew.iesi.common.config.ConfigFile;
//import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
//
//public class ElasticSearchRepositoryConfiguration extends RepositoryConfiguration {
//
//    private String url;
//
//
//    public ElasticSearchRepositoryConfiguration(ConfigFile configFile) {
//        super(configFile);
//    }
//
//    @Override
//    void fromConfigFile(ConfigFile configFile) {
//    	url = getSettingValue(configFile, "metadata.repository.elasticsearch.url");
//    }
//
//    @Override
//    public RepositoryCoordinator toRepository() {
//        return null;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//}
