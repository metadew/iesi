package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.server.rest.dataset.Filter;

public class ScriptFilter extends Filter {

    public ScriptFilter(ScriptFilterOption scriptFilterOption, String value, boolean exactMatch) {
        super(scriptFilterOption, value, exactMatch);
    }

}
