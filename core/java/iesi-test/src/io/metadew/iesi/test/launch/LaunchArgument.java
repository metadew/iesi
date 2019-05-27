package io.metadew.iesi.test.launch;

public class LaunchArgument {
	
	private boolean keyvalue;
	private String key;
	private String value;
	
	public LaunchArgument() {
		
	}
	
	public LaunchArgument(boolean keyValue, String key, String value) {
		this.setKeyvalue(keyValue);
		this.setKey(key);
		this.setValue(value);
	}

	// Getters and setters
	public boolean isKeyvalue() {
		return keyvalue;
	}

	public void setKeyvalue(boolean keyvalue) {
		this.keyvalue = keyvalue;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}