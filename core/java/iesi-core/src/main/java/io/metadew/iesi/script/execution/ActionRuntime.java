package io.metadew.iesi.script.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.script.configuration.RuntimeActionCacheConfiguration;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.RouteOperation;

import java.io.File;
import java.util.*;

public class ActionRuntime {

    private RuntimeActionCacheConfiguration runtimeActionCacheConfiguration;
    private String runCacheFolderName;
    private String runId;
    private Long processId;
    private List<RouteOperation> routeOperations;

    public ActionRuntime(String runId, long processId) {
        this.setRunId(runId);
        this.setProcessId(processId);
    }

    // Methods
    public void initActionCache(String runCacheFolderName) {
        this.runCacheFolderName = runCacheFolderName + File.separator + processId;
        // FolderTools.createFolder(this.runCacheFolderName);
        this.runtimeActionCacheConfiguration = new RuntimeActionCacheConfiguration(this.runCacheFolderName);
    }

    public void setRuntimeParameters(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        Iterator iterator = actionParameterOperationMap.entrySet().iterator();
        ObjectMapper objectMapper = new ObjectMapper();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            ActionParameterOperation actionParameterOperation = objectMapper.convertValue(pair.getValue(),
                    ActionParameterOperation.class);

            // Handle null values when parameter has not been set
            if (actionParameterOperation == null || actionParameterOperation.getValue() == null)
                continue;

            this.getRuntimeActionCacheConfiguration().setRuntimeCache(this.getRunId(), this.processId,  "param",
                    actionParameterOperation.getName(), actionParameterOperation.getValue().toString());
            iterator.remove(); // avoids a ConcurrentModificationException
        }

    }

    public void setRuntimeParameters(String type, HashMap<String, String> variableMap) {
        Iterator iterator = variableMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();

            this.getRuntimeActionCacheConfiguration().setRuntimeCache(this.getRunId(), this.processId, type, pair.getKey().toString(),
                    pair.getValue().toString());
            iterator.remove(); // avoids a ConcurrentModificationException
        }

    }

    public void setRuntimeParameter(String type, String name, String value) {
        this.getRuntimeActionCacheConfiguration().setRuntimeCache(this.getRunId(), this.processId, type, name, value);
    }

    // Parameter resolution
    public String resolveRuntimeVariables(String input) {
        int openPos;
        int closePos;
        String variable_char = "[#";
        String variable_char_close = "#]";
        String midBit;
        String replaceValue = null;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            List<String> items = new ArrayList<>();
            String tempCacheItems = temp;
            while (tempCacheItems.indexOf(variable_char) > 0 || tempCacheItems.startsWith(variable_char)) {
                openPos = tempCacheItems.indexOf(variable_char);
                closePos = tempCacheItems.indexOf(variable_char_close);
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

            // this.decrypt(variable_char + midBit + variable_char_close);
            if (replaceValue != null) {
                input = input.replace(variable_char + cacheItemOutput + variable_char_close, replaceValue);
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

    public List<RouteOperation> getRouteOperations() {
        return routeOperations;
    }

    public void setRouteOperations(List<RouteOperation> routeOperations) {
        this.routeOperations = routeOperations;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }
}