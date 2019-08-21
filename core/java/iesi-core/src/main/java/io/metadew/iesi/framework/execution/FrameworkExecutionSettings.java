package io.metadew.iesi.framework.execution;

public class FrameworkExecutionSettings {

    private String settingsList = "";

    public FrameworkExecutionSettings() {
        this("");
    }

    public FrameworkExecutionSettings(String settingsList) {
        this.settingsList = settingsList;
    }

    // Getters and setters
    public String getSettingsList() {
        return settingsList;
    }

    public void setSettingsList(String settingsList) {
        this.settingsList = settingsList;
    }

}