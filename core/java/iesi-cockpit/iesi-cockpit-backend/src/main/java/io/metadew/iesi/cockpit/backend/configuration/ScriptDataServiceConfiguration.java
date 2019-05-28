package io.metadew.iesi.cockpit.backend.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.metadew.iesi.cockpit.backend.ScriptDataService;
import io.metadew.iesi.metadata.definition.Script;

public class ScriptDataServiceConfiguration extends ScriptDataService {

	private static final long serialVersionUID = 1L;

	private static ScriptDataServiceConfiguration INSTANCE;

    private List<Script> scripts;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private ScriptDataServiceConfiguration() {
    	Script e = new Script();
    	scripts = new ArrayList();
    	e.setName("test");
    	e.setDescription("ok");
    	scripts.add(e);
    	
    	
    }

    public synchronized static ScriptDataService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptDataServiceConfiguration();
        }
        return INSTANCE;
    }

    @Override
    public synchronized List<Script> getAllScripts() {
        return Collections.unmodifiableList(scripts);
    }

    @Override
    public synchronized void updateScript(Script scriptName) {
    	// add logic
    }

    @Override
    public synchronized Script getScriptByName(String scriptName) {
    	// add logic
    	return null;
    }

    @Override
    public synchronized void deleteScript(String scriptName) {
    	// add logic
    }
}
