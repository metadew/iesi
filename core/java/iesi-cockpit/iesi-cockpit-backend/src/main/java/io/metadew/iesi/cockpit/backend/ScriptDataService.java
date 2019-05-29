package io.metadew.iesi.cockpit.backend;

import java.io.Serializable;
import java.util.Collection;

import io.metadew.iesi.cockpit.backend.configuration.ScriptDataServiceConfiguration;
import io.metadew.iesi.metadata.definition.Script;

public abstract class ScriptDataService implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract Collection<Script> getAllScripts();

    public abstract void updateScript(Script script);

    public abstract void deleteScript(String scriptName);

	public abstract Script getScriptByName(String scriptName);

    public static ScriptDataService get() {
        return ScriptDataServiceConfiguration.getInstance();
    }

}
