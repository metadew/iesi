package io.metadew.iesi.test.launch;

public class LaunchItem {
	
	private String script;
	private String description;
	private String parameterList;
	
	public LaunchItem() {
		
	}
	
	public LaunchItem(String script, String description, String parameterList) {
		this.setScript(script);
		this.setDescription(description);
		this.setParameterList(parameterList);
	}

	// Getters and setters
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParameterList() {
		return parameterList;
	}

	public void setParameterList(String parameterList) {
		this.parameterList = parameterList;
	}

	
}