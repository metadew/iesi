package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.connection.Connection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TransformResult {
    private List<Connection> connections;
    private List<Component> components;
}
