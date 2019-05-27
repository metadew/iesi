package io.metadew.iesi.metadata.definition.metadatarepository;

import io.metadew.iesi.metadata.definition.MetadataRepository;

public class ElasticsearchMetadataRepository extends MetadataRepository {

    private String url;

    // Constructors
    public ElasticsearchMetadataRepository() {
        super();
    }

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}