package io.metadew.iesi.server.rest.script;

import lombok.Data;

@Data
public class ScriptFilter {

    private final ScriptFilterOption scriptFilterOption;
    private final String value;
    private final boolean exactMatch;

}
