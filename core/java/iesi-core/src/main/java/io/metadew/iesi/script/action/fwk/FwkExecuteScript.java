package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class FwkExecuteScript extends ActionTypeExecution {

    private final Pattern keyValuePattern = Pattern.compile("\\s*(?<parameter>.+)\\s*=\\s*(?<value>.+)\\s*");

    private static final String SCRIPT_NAME_KEY = "script";
    private static final String SCRIPT_VERSION_KEY = "version";
    private static final String ENVIRONMENT_KEY = "environment";
    private static final String PARAMETER_LIST_KEY = "paramList";
    private static final String PARAMETER_FILE_KEY = "paramFile";

    private String scriptName;
    private Long scriptVersion;
    private String environmentName;
    private String parameterList;
    private String parameterFileName;

    public FwkExecuteScript(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        ActionParameterOperation scriptNameActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), SCRIPT_NAME_KEY);
        ActionParameterOperation scriptVersionActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), SCRIPT_VERSION_KEY);
        ActionParameterOperation environmentNameActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), ENVIRONMENT_KEY);
        ActionParameterOperation paramListActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), PARAMETER_LIST_KEY);
        ActionParameterOperation paramFileActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), PARAMETER_FILE_KEY);

        // Get Parameters
        // TODO: equals with case
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(SCRIPT_NAME_KEY)) {
                scriptNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(SCRIPT_VERSION_KEY)) {
                scriptVersionActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(ENVIRONMENT_KEY)) {
                environmentNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(PARAMETER_LIST_KEY)) {
                paramListActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(PARAMETER_FILE_KEY)) {
                paramFileActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put(SCRIPT_NAME_KEY, scriptNameActionParameterOperation);
        this.getActionParameterOperationMap().put(SCRIPT_VERSION_KEY, scriptVersionActionParameterOperation);
        this.getActionParameterOperationMap().put(ENVIRONMENT_KEY, environmentNameActionParameterOperation);
        this.getActionParameterOperationMap().put(PARAMETER_LIST_KEY, paramListActionParameterOperation);
        this.getActionParameterOperationMap().put(PARAMETER_FILE_KEY, paramFileActionParameterOperation);


        scriptName = convertScriptName(scriptNameActionParameterOperation.getValue());
        scriptVersion = convertScriptVersion(scriptVersionActionParameterOperation.getValue());
        environmentName = convertEnvironmentName(environmentNameActionParameterOperation.getValue());
        parameterList = convertParameterList2(paramListActionParameterOperation.getValue());
        parameterFileName = convertParameterFileName(paramListActionParameterOperation.getValue());

    }

    private String convertParameterList2(DataType parameterList) {
        if (parameterList == null) {
            return null;
        } else if (parameterList instanceof Text) {
            return ((Text) parameterList).getString();
        } else {
            throw new RuntimeException(String.format("%s does not accept %s as type for parameterList",
                    getActionExecution().getAction().getType(),
                    parameterList.getClass()));
        }
    }

    protected boolean executeAction() throws ScriptExecutionBuildException, InterruptedException {
        // Check on Running a script in a loop
        if (getScriptExecution().getScript().getName().equals(scriptName)) {
            throw new RuntimeException(String.format("Not allowed to run the script recursively. Attempting to run %s in %s", scriptName, getScriptExecution().getScript().getName()));
        }
        Script script;
        if (scriptVersion == null) {
            script = ScriptConfiguration.getInstance()
                    .get(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptName), scriptVersion))
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("No implementation for script {0}-{1} found", scriptName, scriptVersion)));
        } else {
            script = ScriptConfiguration.getInstance().getLatestVersion(scriptName)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("No implementation for script {0} found", scriptName)));
        }

        Map<String, String> parameters = new HashMap<>();
        if (parameterFileName != null) {
            parameters.putAll(parseParameterFiles(parameterFileName));
        }
        if (parameterList != null) {
            parameters.putAll(parseParameterRepresentation(parameterList));
        }

        ScriptExecution subScriptScriptExecution = new ScriptExecutionBuilder(false, false)
                .script(script)
                .executionControl(getExecutionControl())
                .processId(getExecutionControl().getLastProcessId())
                .parentScriptExecution(getScriptExecution())
                .exitOnCompletion(false)
                .parameters(parameters)
                .environment(getExecutionControl().getEnvName())
                .build();

        subScriptScriptExecution.execute();

        if (subScriptScriptExecution.getResult().equalsIgnoreCase(ScriptRunStatus.SUCCESS.value())) {
            getActionExecution().getActionControl().increaseSuccessCount();
        } else if (subScriptScriptExecution.getResult().equalsIgnoreCase(ScriptRunStatus.WARNING.value())) {
            getActionExecution().getActionControl().increaseWarningCount();
        } else if (subScriptScriptExecution.getResult().equalsIgnoreCase(ScriptRunStatus.ERROR.value())) {
            getActionExecution().getActionControl().logOutput("action.error", "script " + script.getName() + "-" + script.getVersion().getNumber() + " ended in " + subScriptScriptExecution.getResult());
            getActionExecution().getActionControl().increaseErrorCount();
        } else {
            getActionExecution().getActionControl().logOutput("action.error", "script " + script.getName() + "-" + script.getVersion().getNumber() + " ended in " + subScriptScriptExecution.getResult());
            getActionExecution().getActionControl().increaseErrorCount();
        }

        return true;
    }

    private Map<String, String> convertParameterEntry(DataType parameterEntry) {
        Map<String, String> parameterMap = new HashMap<>();
        if (parameterEntry instanceof Text) {
            Matcher matcher = keyValuePattern.matcher(parameterEntry.toString());
            if (matcher.find()) {
                parameterMap.put(matcher.group("parameter"), matcher.group("value"));
            } else {
                log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " parameter entry ''{0}'' does not follow correct syntax",
                        parameterEntry));
            }
            return parameterMap;
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for parameter entry",
                    parameterEntry.getClass()));
            return parameterMap;
        }
    }

    @SuppressWarnings("unused")
    private Optional<Map<String, String>> convertParameterList(DataType list) {
        if (list == null) {
            return Optional.empty();
        }
        Map<String, String> parameterMap = new HashMap<>();
        if (list instanceof Text) {
            Arrays.stream(list.toString().split(","))
                    .forEach(parameterEntry -> parameterMap.putAll(convertParameterEntry(DataTypeHandler.getInstance().resolve(parameterEntry, getExecutionControl().getExecutionRuntime()))));
            return Optional.of(parameterMap);
        } else if (list instanceof Array) {
            for (DataType parameterEntry : ((Array) list).getList()) {
                parameterMap.putAll(convertParameterEntry(parameterEntry));
            }
            return Optional.of(parameterMap);
        } else {
            log.warn(MessageFormat.format("fwk.setParameterList does not accept {0} as type for list",
                    list.getClass()));
            return Optional.empty();
        }
    }

    private Long convertScriptVersion(DataType scriptVersion) {
        if (scriptVersion == null) {
            return null;
        } else if (scriptVersion instanceof Text) {
            return Long.parseLong(((Text) scriptVersion).getString());
        } else {
            throw new RuntimeException(String.format("fwk.executeScript does not accept %s as type for script name",
                    scriptVersion.getClass()));
        }
    }

    private String convertParameterFileName(DataType parameterFileName) {
        if (parameterFileName == null) {
            return null;
        } else if (parameterFileName instanceof Text) {
            return ((Text) parameterFileName).getString();
        } else {
            throw new RuntimeException(String.format("fwk.executeScript does not accept %s as type for parameter file name",
                    parameterFileName.getClass()));
        }
    }

    private String convertEnvironmentName(DataType environmentName) {
        if (environmentName == null) {
            return null;
        } else if (environmentName instanceof Text) {
            return ((Text) environmentName).getString();
        } else {
            throw new RuntimeException(String.format("fwk.executeScript does not accept %s as type for environment name",
                    environmentName.getClass()));
        }
    }

    private String convertScriptName(DataType scriptName) {
        if (scriptName instanceof Text) {
            return ((Text) scriptName).getString();
        } else {
            log.warn(MessageFormat.format("fwk.executeScript does not accept {0} as type for script name",
                    scriptName.getClass()));
            return scriptName.toString();
        }
    }

    public Map<String, String> parseParameterFiles(String files) {
        Map<String, String> parameters = new HashMap<>();
        String[] parts = files.split(",");
        for (String part : parts) {
            parameters.putAll(parseParameterFile(part));
        }
        return parameters;
    }

    public Map<String, String> parseParameterFile(String file) {
        Map<String, String> parameters = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                int delim = line.indexOf("=");
                if (delim > 0) {
                    parameters.put(line.substring(0, delim), line.substring(delim + 1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parameters;
    }

    public Map<String, String> parseParameterRepresentation(String parametersRepresentation) {
        Map<String, String> parameters = new HashMap<>();
        for (String parameterCombination : parametersRepresentation.split(",")) {
            String[] parameter = parameterCombination.split("=");
            if (parameter.length == 2) {
                parameters.put(parameter[0], parameter[1]);
            }
        }
        return parameters;
    }

}