package io.metadew.iesi.connection;

import io.metadew.iesi.metadata.definition.script.result.ScriptResult;

public interface IPublisherHandler {

    void publish(Publisher publisher, ScriptResult scriptResult) throws Exception;

    void shutdown(Publisher publisher);

}
