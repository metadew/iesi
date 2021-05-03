package io.metadew.iesi.script.execution;

import io.metadew.iesi.script.action.ActionParameterResolvement;
import io.metadew.iesi.script.configuration.RuntimeActionCacheConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionRuntime {

    private RuntimeActionCacheConfiguration runtimeActionCacheConfiguration;
    private String runCacheFolderName;
    private String runId;
    private Long processId;

    public ActionRuntime(String runId, long processId) {
        this.setRunId(runId);
        this.setProcessId(processId);
    }

    // Methods
    public void initActionCache(String runCacheFolderName) {
        this.runCacheFolderName = runCacheFolderName + File.separator + processId;
        this.runtimeActionCacheConfiguration = new RuntimeActionCacheConfiguration(this.runCacheFolderName);
    }

    public void setRuntimeParameters(String type, Map<String, String> variableMap) {
        variableMap
                .forEach((key, value) -> this.getRuntimeActionCacheConfiguration().setRuntimeCache(this.getRunId(), this.processId, type, key, value));

    }

    public void setRuntimeParameter(String type, String name, String value) {
        this.getRuntimeActionCacheConfiguration().setRuntimeCache(this.getRunId(), this.processId, type, name, value);
    }

    // Parameter resolution
    public String resolveRuntimeVariables(String input) {
        int openPos;
        int closePos;
        String variableChar = "[#";
        String variableCharClose = "#]";
        String midBit;
        String replaceValue = null;
        String temp = input;
        while (temp.indexOf(variableChar) > 0 || temp.startsWith(variableChar)) {
            List<String> items = new ArrayList<>();
            String tempCacheItems = temp;
            while (tempCacheItems.indexOf(variableChar) > 0 || tempCacheItems.startsWith(variableChar)) {
                openPos = tempCacheItems.indexOf(variableChar);
                closePos = tempCacheItems.indexOf(variableCharClose);
                midBit = tempCacheItems.substring(openPos + 2, closePos).trim();
                items.add(midBit);
                tempCacheItems = midBit;
            }

            // get last value
            String cacheItem = items.get(items.size() - 1);

            // check split different types
            String cacheItemOutput = cacheItem;

            // Lookup
            int cacheTypePos = cacheItem.indexOf(".");
            String cacheType = cacheItem.substring(0, cacheTypePos).trim().toLowerCase();
            String cacheName = cacheItem.substring(cacheTypePos + 1).trim();
            replaceValue = this.getRuntimeActionCacheConfiguration().getRuntimeCacheValue(this.getRunId(), this.processId, cacheType,
                    cacheName);

            // this.decrypt(variableChar + midBit + variableCharClose);
            if (replaceValue != null) {
                input = input.replace(variableChar + cacheItemOutput + variableCharClose, replaceValue);
            }
            temp = input;
        }

        return input;
    }

    public String getRunCacheFolderName() {
        return runCacheFolderName;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public RuntimeActionCacheConfiguration getRuntimeActionCacheConfiguration() {
        return runtimeActionCacheConfiguration;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public void setRuntimeParameters(List<ActionParameterResolvement> actionParameterResolvements) {
        actionParameterResolvements
                .forEach(actionParameterResolvement -> getRuntimeActionCacheConfiguration()
                        .setRuntimeCache(this.getRunId(),
                                this.processId,
                                "param",
                                actionParameterResolvement.getActionParameter().getMetadataKey().getParameterName(),
                                actionParameterResolvement.getResolvedValue().toString()));

    }
}