package io.metadew.iesi.metadata.definition.connection;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionTypeParameter {

    private String description;
    private String type;
    private boolean mandatory = false;
    private boolean encrypted = false;


}