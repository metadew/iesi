package io.metadew.iesi.connection.elasticsearch;

public abstract class ElasticSearchConnection {

    public abstract void ingest(ElasticSearchDocument o);
    public abstract void ingest(String o);

}
