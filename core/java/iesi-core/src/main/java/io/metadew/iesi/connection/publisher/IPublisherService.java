package io.metadew.iesi.connection.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;

import java.util.concurrent.ExecutionException;

public interface IPublisherService<T extends Publisher> {

    void publish(T publisher, ScriptResult scriptResult) throws ExecutionException, InterruptedException, JsonProcessingException;

    void shutdown(T publisher);

    Class<T> appliesTo();


}
