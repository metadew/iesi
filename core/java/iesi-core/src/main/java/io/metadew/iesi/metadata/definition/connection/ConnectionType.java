package io.metadew.iesi.metadata.definition.connection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionType {

    private String description;
    private Map<String, ConnectionTypeParameter> parameters;

}