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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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


public class FwkExecuteScript extends ActionTypeExecution {

    private final Pattern keyValuePattern = Pattern.compile("\\s*(?<parameter>.+)\\s*=\\s*(?<value>.+)\\s*");

    private ActionParameterOperation scriptName;
    private ActionParameterOperation scriptVersion;
    private ActionParameterOperation environmentName;
    private ActionParameterOperation paramList;
    private ActionParameterOperation paramFile;
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkExecuteScript(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setScriptName(new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "script"));
        this.setScriptVersion(new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "version"));
        this.setEnvironmentName(new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "environment"));
        this.setParamList(new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "paramList"));
        this.setParamFile(new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "paramFile"));

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("script")) {
                this.getScriptName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("version")) {
                this.getScriptVersion().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("environment")) {
                this.getEnvironmentName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("paramlist")) {
                this.getParamList().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("paramfile")) {
                this.getParamFile().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("script", this.getScriptName());
        this.getActionParameterOperationMap().put("version", this.getScriptVersion());
        this.getActionParameterOperationMap().put("environment", this.getEnvironmentName());
        this.getActionParameterOperationMap().put("paramList", this.getParamList());
        this.getActionParameterOperationMap().put("paramFile", this.getParamFile());
    }

    private Optional<String> convertParameterList2(DataType parameterList) {
        if (parameterList == null) {
            return Optional.empty();
        }
        if (parameterList instanceof Text) {
            return Optional.of(parameterList.toString());
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for parameterList",
                    parameterList.getClass()));
            return Optional.empty();
        }
    }

    protected boolean executeAction() throws ScriptExecutionBuildException, InterruptedException {
        // Check on Running a script in a loop
        String scriptName = convertScriptName(getScriptName().getValue());
        Optional<Long> scriptVersion = convertScriptVersion(getScriptVersion().getValue());
        Optional<String> environmentName = convertEnvironmentName(getEnvironmentName().getValue());
        // TODO: see setParameterList for nicer version
        Optional<String> parameterList = convertParameterList2(getParamList().getValue());
        Optional<String> parameterFileName = convertParameterFileName(getParamFile().getValue());
        if (getScriptExecution().getScript().getName().equals(scriptName)) {
            throw new RuntimeException(MessageFormat.format("Not allowed to run the script recursively. Attempting to run {0} in {1}", scriptName, getScriptExecution().getScript().getName()));
        }

        // Script script = ScriptConfiguration.getInstance().get(this.getScriptName().getValue());
        Script script = scriptVersion
                .map(version -> ScriptConfiguration.getInstance()
                        .get(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptName), version))
                        .orElseThrow(() -> new RuntimeException(MessageFormat.format("No implementation for script {0}-{1} found", scriptName, version))))
                .orElse(ScriptConfiguration.getInstance().getLatestVersion(scriptName)
                        .orElseThrow(() -> new RuntimeException(MessageFormat.format("No implementation for script {0} found", scriptName))));

        Map<String, String> parameters = new HashMap<>();
        parameterFileName.ifPresent(parameterfilename -> parameters.putAll(parseParameterFiles(parameterfilename)));
        parameterList.ifPresent(parameterlist -> parameters.putAll(parseParameterRepresentation(parameterlist)));

        // TODO: impersonations?
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
        } else if (subScriptScriptExecution.getResult()
                .equalsIgnoreCase(ScriptRunStatus.ERROR.value())) {
            getActionExecution().getActionControl().logOutput("action.error", "script " + script.getName() + "-" + script.getVersion().getNumber() + " ended in " + subScriptScriptExecution.getResult());
            getActionExecution().getActionControl().increaseErrorCount();
        } else {
            getActionExecution().getActionControl().logOutput("action.error", "script " + script.getName() + "-" + script.getVersion().getNumber() + " ended in " + subScriptScriptExecution.getResult());
            getActionExecution().getActionControl().increaseErrorCount();
        }

        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.executeScript";
    }

    private Map<String, String> convertParameterEntry(DataType parameterEntry) {
        Map<String, String> parameterMap = new HashMap<>();
        if (parameterEntry instanceof Text) {
            Matcher matcher = keyValuePattern.matcher(parameterEntry.toString());
            if (matcher.find()) {
                parameterMap.put(matcher.group("parameter"), matcher.group("value"));
            } else {
                LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " parameter entry ''{0}'' does not follow correct syntax",
                        parameterEntry));
            }
            return parameterMap;
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for parameter entry",
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
            LOGGER.warn(MessageFormat.format("fwk.setParameterList does not accept {0} as type for list",
                    list.getClass()));
            return Optional.empty();
        }
    }

    private Optional<Long> convertScriptVersion(DataType scriptVersion) {
        if (scriptVersion == null) {
            return Optional.empty();
        }
        if (scriptVersion instanceof Text) {
            return Optional.of(Long.parseLong(scriptVersion.toString()));
        } else {
            LOGGER.warn(MessageFormat.format("fwk.executeScript does not accept {0} as type for script name",
                    scriptVersion.getClass()));
            return Optional.empty();
        }
    }

    private Optional<String> convertParameterFileName(DataType parameterFileName) {
        if (parameterFileName == null) {
            return Optional.empty();
        }
        if (parameterFileName instanceof Text) {
            return Optional.of(parameterFileName.toString());
        } else {
            LOGGER.warn(MessageFormat.format("fwk.executeScript does not accept {0} as type for parameter file name",
                    parameterFileName.getClass()));
            return Optional.empty();
        }
    }

    private Optional<String> convertEnvironmentName(DataType environmentName) {
        if (environmentName == null) {
            return Optional.empty();
        }
        // TODO: if null get current Environment, here or in execute(...)
        if (environmentName instanceof Text) {
            return Optional.of(environmentName.toString());
        } else {
            LOGGER.warn(MessageFormat.format("fwk.executeScript does not accept {0} as type for environment name",
                    environmentName.getClass()));
            return Optional.of(environmentName.toString());
        }
    }

    private String convertScriptName(DataType scriptName) {
        if (scriptName instanceof Text) {
            return scriptName.toString();
        } else {
            LOGGER.warn(MessageFormat.format("fwk.executeScript does not accept {0} as type for script name",
                    scriptName.getClass()));
            return scriptName.toString();
        }
    }

    public Map<String, String> parseParameterFiles(String files) {
        Map<String, String> parameters = new HashMap<>();
        String[] parts = files.split(",");
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            parameters.putAll(parseParameterFile(innerpart));
        }
        return parameters;
    }

    public Map<String, String> parseParameterFile(String file) {
        Map<String, String> parameters = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null) {
                int delim = line.indexOf("=");
                if (delim > 0) {
                    parameters.put(line.substring(0, delim), line.substring(delim + 1));
                }
            }
            br.close();
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

    // Getters and Setters
    public ActionParameterOperation getScriptName() {
        return scriptName;
    }

    public void setScriptName(ActionParameterOperation scriptName) {
        this.scriptName = scriptName;
    }

    public ActionParameterOperation getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(ActionParameterOperation environmentName) {
        this.environmentName = environmentName;
    }

    public ActionParameterOperation getParamList() {
        return paramList;
    }

    public void setParamList(ActionParameterOperation paramList) {
        this.paramList = paramList;
    }

    public ActionParameterOperation getParamFile() {
        return paramFile;
    }

    public void setParamFile(ActionParameterOperation paramFile) {
        this.paramFile = paramFile;
    }

    public ActionParameterOperation getScriptVersion() {
        return scriptVersion;
    }

    public void setScriptVersion(ActionParameterOperation scriptVersion) {
        this.scriptVersion = scriptVersion;
    }
}