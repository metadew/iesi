package io.metadew.iesi.script.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.script.configuration.RuntimeActionCacheConfiguration;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.RouteOperation;

import java.io.File;
import java.util.*;

public class ActionRuntime {

    private FrameworkExecution frameworkExecution;
    private RuntimeActionCacheConfiguration runtimeActionCacheConfiguration;
    private String runCacheFolderName;
    private String runId;
    private Long processId;
    private List<RouteOperation> routeOperations;

    public ActionRuntime(FrameworkExecution frameworkExecution, String runId, long processId) {
        this.setFrameworkExecution(frameworkExecution);
        this.setRunId(runId);
        this.setProcessId(processId);
    }

    // Methods
    public void initActionCache(String actionName, String runCacheFolderName) {
        this.setRunCacheFolderName(runCacheFolderName + File.separator + this.getProcessId());
        FolderTools.createFolder(this.getRunCacheFolderName());
        this.setRuntimeActionCacheConfiguration(
                new RuntimeActionCacheConfiguration(this.getFrameworkExecution(), this.getRunCacheFolderName()));
    }

    @SuppressWarnings("rawtypes")
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

            this.getRuntimeActionCacheConfiguration().setRuntimeCache(this.getRunId(), "param",
                    actionParameterOperation.getName(), actionParameterOperation.getValue().toString());
            iterator.remove(); // avoids a ConcurrentModificationException
        }

    }

    @SuppressWarnings("rawtypes")
    public void setRuntimeParameters(String type, HashMap<String, String> variableMap) {
        Iterator iterator = variableMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();

            this.getRuntimeActionCacheConfiguration().setRuntimeCache(this.getRunId(), type, pair.getKey().toString(),
                    pair.getValue().toString());
            iterator.remove(); // avoids a ConcurrentModificationException
        }

    }

    public void setRuntimeParameter(String type, String name, String value) {
        this.getRuntimeActionCacheConfiguration().setRuntimeCache(this.getRunId(), type, name, value);
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
            replaceValue = this.getRuntimeActionCacheConfiguration().getRuntimeCacheValue(this.getRunId(), cacheType,
                    cacheName);

            // this.decrypt(variable_char + midBit + variable_char_close);
            if (replaceValue != null) {
                input = input.replace(variable_char + cacheItemOutput + variable_char_close, replaceValue);
            }
            temp = input;
        }

        return input;
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public String getRunCacheFolderName() {
        return runCacheFolderName;
    }

    public void setRunCacheFolderName(String runCacheFolderName) {
        this.runCacheFolderName = runCacheFolderName;
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

    public void setRuntimeActionCacheConfiguration(RuntimeActionCacheConfiguration runtimeActionCacheConfiguration) {
        this.runtimeActionCacheConfiguration = runtimeActionCacheConfiguration;
    }

    public List<RouteOperation> getRouteOperations() {
        return routeOperations;
    }

    public void setRouteOperations(List<RouteOperation> routeOperations) {
        this.routeOperations = routeOperations;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }
}