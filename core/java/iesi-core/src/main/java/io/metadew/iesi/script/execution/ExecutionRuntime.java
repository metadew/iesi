package io.metadew.iesi.script.execution;

import io.metadew.iesi.common.FrameworkControl;
import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.common.configuration.framework.FrameworkFolder;
import io.metadew.iesi.connection.r.RWorkspace;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
import io.metadew.iesi.metadata.definition.Iteration;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.script.configuration.IterationVariableConfiguration;
import io.metadew.iesi.script.configuration.RuntimeVariableConfiguration;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import io.metadew.iesi.script.execution.instruction.data.DataInstructionRepository;
import io.metadew.iesi.script.execution.instruction.lookup.LookupInstruction;
import io.metadew.iesi.script.execution.instruction.lookup.LookupInstructionRepository;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstruction;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstructionRepository;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstructionTools;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.ImpersonationOperation;
import io.metadew.iesi.script.operation.IterationOperation;
import io.metadew.iesi.script.operation.StageOperation;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExecutionRuntime {

    private RuntimeVariableConfiguration runtimeVariableConfiguration;
    private IterationVariableConfiguration iterationVariableConfiguration;
    @Getter
    private String runId;
    private String runCacheFolderName;

    //private HashMap<String, StageOperation> stageOperationMap;
    private HashMap<String, StageOperation> stageOperationMap;
    private HashMap<String, Dataset> datasetMap;
    private HashMap<String, RWorkspace> RWorkspaceMap;
    private HashMap<String, IterationOperation> iterationOperationMap;
    private ImpersonationOperation impersonationOperation;
    private HashMap<String, DataInstruction> dataInstructions;
    private HashMap<String, VariableInstruction> variableInstructions;
    private HashMap<String, LookupInstruction> lookupInstructions;

    private final String INSTRUCTION_TYPE_KEY = "instructionType";
    private final String INSTRUCTION_KEYWORD_KEY = "instructionKeyword";
    private final String INSTRUCTION_ARGUMENTS_KEY = "instructionArguments";
    private final Pattern CONCEPT_LOOKUP_PATTERN = Pattern
            .compile("\\s*\\{\\{(?<" + INSTRUCTION_TYPE_KEY + ">[\\^\\*=\\$!])(?<" + INSTRUCTION_KEYWORD_KEY + ">[\\w\\.]+)(?<" + INSTRUCTION_ARGUMENTS_KEY + ">\\(.*\\))?\\}\\}\\s*");

    private static final Logger LOGGER = LogManager.getLogger();

    public ExecutionRuntime(ExecutionControl executionControl, String runId) {
        this.runId = runId;

        // Create cache folder
        this.runCacheFolderName = FrameworkConfiguration.getInstance().getFrameworkFolder("run.cache")
                .map(FrameworkFolder::getAbsolutePath)
                .orElseThrow(() -> new RuntimeException("no definition found for run.cache"))+ File.separator + runId;
        // FolderTools.createFolder(runCacheFolderName);
        this.runtimeVariableConfiguration = new RuntimeVariableConfiguration(this.runCacheFolderName);
        this.iterationVariableConfiguration = new IterationVariableConfiguration(this.runCacheFolderName, true);

        // Initialize maps
        stageOperationMap = new HashMap<>();
        iterationOperationMap = new HashMap<>();
        // Initialize impersonations
        impersonationOperation = new ImpersonationOperation();

        // Initialize extensions

        // Initialize data instructions
        dataInstructions = DataInstructionRepository.getRepository(new GenerationObjectExecution());
        variableInstructions = VariableInstructionRepository.getRepository(executionControl);
        lookupInstructions = LookupInstructionRepository.getRepository(executionControl, this);
        datasetMap = new HashMap<>();
        RWorkspaceMap = new HashMap<>();
    }

    public void terminate() {
        for (StageOperation stageOperation : stageOperationMap.values()) {
            stageOperation.doCleanup();
        }
        stageOperationMap = new HashMap<>();
    }

    public void setRuntimeVariables(ActionExecution actionExecution, ResultSet rs) {
        if (SQLTools.getRowCount(rs) == 1) {
            try {
                ResultSetMetaData rsmd = rs.getMetaData();
                int numberOfColums = rsmd.getColumnCount();
                rs.beforeFirst();
                while (rs.next()) {
                    for (int i = 1; i < numberOfColums + 1; i++) {
                        this.setRuntimeVariable(actionExecution, rsmd.getColumnName(i), rs.getString(i));
                    }
                }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error getting sql result " + e, e);
            }
        } else {
            throw new RuntimeException("Only 1 line of data expected");
        }
    }

    public void setRuntimeVariables(ActionExecution actionExecution, String input) {
        String[] lines = input.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int delim = line.indexOf("=");
            if (delim > 0) {
                String key = line.substring(0, delim);
                String value = line.substring(delim + 1);
                this.setRuntimeVariable(actionExecution, key, value);
            } else {
                // Not a valid configuration
            }
        }
    }

    public void setRuntimeVariablesFromList(ActionExecution actionExecution, ResultSet rs) {
        try {
            rs.beforeFirst();
            while (rs.next()) {
                this.setRuntimeVariable(actionExecution, rs.getString(1), rs.getString(2));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting sql result " + e, e);
        }
    }

    public void setRuntimeVariablesFromList(ScriptExecution scriptExecution, ResultSet rs) {
        try {
            rs.beforeFirst();
            while (rs.next()) {
                this.setRuntimeVariable(scriptExecution, rs.getString(1), rs.getString(2));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting sql result " + e, e);
        }
    }

    public void setRuntimeVariable(ActionExecution actionExecution, String name, String value) {
        LOGGER.debug("exec.runvar.set=" + name + ":" + value);
        runtimeVariableConfiguration.setRuntimeVariable(runId, actionExecution.getProcessId(), name, value);
    }

    public void setRuntimeVariable(ScriptExecution scriptExecution, String name, String value) {
        LOGGER.debug("exec.runvar.set=" + name + ":" + value);
        runtimeVariableConfiguration.setRuntimeVariable(runId, scriptExecution.getProcessId(), name, value);
    }

    public Optional<String> getRuntimeVariableValue(String name) {
        return runtimeVariableConfiguration.getRuntimeVariableValue(runId, name);
    }

    // Iteration Variables
    public void setIterationVariables(String listName, ResultSet rs) {
        this.getIterationVariableConfiguration().setIterationList(runId, listName, rs);
    }

    public String resolveVariables(String input) {
        // Prevent null issues during string operations
        if (input == null) {
            input = "";
        }
        String result = "";

        // First level: settings
        result = FrameworkControl.getInstance().resolveConfiguration(input);

        // Second level: runtime variables
        result = this.resolveRuntimeVariables(result);
        if (!input.equalsIgnoreCase(result))
            LOGGER.trace("exec.runvar.resolve=" + input + ":" + result);
        return result;
    }

    public String resolveVariables(ActionExecution actionExecution, String input) {
        // Prevent null issues during string operations
        if (input == null) {
            input = "";
        }
        String result;

        // First level: settings
        result = FrameworkControl.getInstance().resolveConfiguration(input);

        // Second: Action attributes
        result = this.resolveConfiguration(actionExecution, result);

        // third level: runtime variables
        result = this.resolveRuntimeVariables(result);
        if (!input.equalsIgnoreCase(result))
            LOGGER.debug("exec.runvar.resolve=" + input + ":" + result);
        return result;
    }


    private String resolveRuntimeVariables(String input) {
        LOGGER.trace(MessageFormat.format("runvar.resolve=resolving {0} for runtime variables", input));
        int openPos;
        int closePos;
        String variable_char = "#";
        String midBit;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char, openPos + 1);
            midBit = temp.substring(openPos + 1, closePos);

            // Replace
            String replaceValue = this.getRuntimeVariableValue(midBit).orElse("");
            input = input.replaceAll(variable_char + midBit + variable_char, replaceValue);
            temp = temp.substring(closePos + 1, temp.length());

        }
        LOGGER.trace(MessageFormat.format("runvar.resolve.result=resolved to {0}", input));
        return input;
    }


    public String resolveActionTypeVariables(String input, HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        int openPos;
        int closePos;
        String variable_char_open = "[";
        String variable_char_close = "]";
        String midBit;
        String replaceValue;
        String temp = input;
        while (temp.indexOf(variable_char_open) > 0 || temp.startsWith(variable_char_open)) {
            openPos = temp.indexOf(variable_char_open);
            closePos = temp.indexOf(variable_char_close, openPos + 1);
            midBit = temp.substring(openPos + 1, closePos);

            // Replace
            replaceValue = actionParameterOperationMap.get(midBit).getValue().toString();
            if (replaceValue != null) {
                input = input.replace(variable_char_open + midBit + variable_char_close, replaceValue);
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        return input;
    }

    public String resolveComponentTypeVariables(String input, List<ComponentAttribute> componentAttributeList,
                                                String environment) {

        Map<String, ComponentAttribute> componentAttributeMap = this.getComponentAttributeHashmap(componentAttributeList, environment);
        int openPos;
        int closePos;
        String variable_char_open = "[";
        String variable_char_close = "]";
        String midBit;
        String replaceValue;
        String temp = input;
        while (temp.indexOf(variable_char_open) > 0 || temp.startsWith(variable_char_open)) {
            openPos = temp.indexOf(variable_char_open);
            closePos = temp.indexOf(variable_char_close, openPos + 1);
            midBit = temp.substring(openPos + 1, closePos);

            // Replace
            replaceValue = componentAttributeMap.get(midBit).getValue();
            if (replaceValue != null) {
                input = input.replace(variable_char_open + midBit + variable_char_close, replaceValue);
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        return input;
    }

    private Map<String, ComponentAttribute> getComponentAttributeHashmap(List<ComponentAttribute> componentAttributeList, String environment) {
        return componentAttributeList.stream()
                .filter(componentAttribute -> componentAttribute.getMetadataKey().getEnvironmentKey().getName().equalsIgnoreCase(environment))
                .collect(Collectors.toMap(componentAttribute -> componentAttribute.getMetadataKey().getComponentAttributeName(), Function.identity()));
    }

    public String resolveConfiguration(ActionExecution actionExecution, String input) {
        LOGGER.trace(MessageFormat.format("configuration.resolve=resolving {0} for configurations", input));
        int openPos;
        int closePos;
        String variable_char = "#";
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char, openPos + 1);
            final String midBit = temp.substring(openPos + 1, closePos);

            // Try to find a configuration value
            // If none is found, null is set by default
            Optional<String> replaceValue = actionExecution.getComponentAttributeOperation()
                    .flatMap(componentAttributeOperation -> componentAttributeOperation.getProperty(midBit));

            // Replacing the value if found
            if (replaceValue.isPresent()) {
                input = input.replaceAll(variable_char + midBit + variable_char, replaceValue.get());
            }
            temp = temp.substring(closePos + 1, temp.length());

        }

        LOGGER.trace(MessageFormat.format("configuration.resolve.result=resolved to {0}", input));

        return input;
    }

    /*
     * New function or concept lookups
     * Work in progress
     * We will move only here when stable
     */
    public LookupResult resolveConceptLookup(String input) {
        LOGGER.trace(MessageFormat.format("concept.lookup.resolve=resolving {0} for concept lookup instructions", input));
        LookupResult lookupResult = new LookupResult();
        lookupResult.setInputValue(input);
        // TODO: move to Antler
        String lookupConceptStartKey = "{{";
        String lookupConceptStopKey = "}}";

        int lookupConceptStartIndex;
        int lookupConceptStopIndex = 0;
        int nextLookupConceptStartIndex;
        while (input.indexOf(lookupConceptStartKey, lookupConceptStopIndex) != -1) {
            lookupConceptStartIndex = input.indexOf(lookupConceptStartKey, lookupConceptStopIndex);
            if (input.indexOf(lookupConceptStopKey, lookupConceptStartIndex) == -1) {
                LOGGER.warn(MessageFormat.format("concept.lookup.resolve.error=error during concept lookup resolvement of {0}. Concept lookup instruction not properly closed.", input));
                lookupResult.setValue(input);
                return lookupResult;
            }
            lookupConceptStopIndex = input.indexOf(lookupConceptStopKey, lookupConceptStartIndex);
            nextLookupConceptStartIndex = input.indexOf(lookupConceptStartKey, lookupConceptStartIndex + lookupConceptStartKey.length());
            while (nextLookupConceptStartIndex > 0 && nextLookupConceptStartIndex < lookupConceptStopIndex) {
                lookupConceptStopIndex = input.indexOf(lookupConceptStopKey, lookupConceptStopIndex + lookupConceptStopKey.length());
                if (lookupConceptStopIndex < 0) {
                    LOGGER.warn(MessageFormat.format("concept.lookup.resolve.error=error during concept lookup resolvement of {0}. Concept lookup instruction not properly closed.", input));
                    lookupResult.setValue(input);
                    return lookupResult;
                }
                nextLookupConceptStartIndex = input.indexOf(lookupConceptStartKey, nextLookupConceptStartIndex + lookupConceptStartKey.length());
            }

            String resolvement = executeConceptLookup(input.substring(lookupConceptStartIndex, lookupConceptStopIndex + lookupConceptStopKey.length())).getValue();
            input = input.substring(0, lookupConceptStartIndex) +
                    resolvement +
                    input.substring(lookupConceptStopIndex + lookupConceptStopKey.length());
        }
        LOGGER.debug(MessageFormat.format("concept.lookup.resolve.result={0}:{1}", lookupResult.getInputValue(), input));

        lookupResult.setValue(input);
        return lookupResult;
    }

    public LookupResult executeConceptLookup(String input) {
        LOGGER.trace(MessageFormat.format("concept.lookup.resolve.instruction=resolving instruction {0}", input));
        LookupResult lookupResult = new LookupResult();
        String resolvedInput = input;
        Matcher ConceptLookupMatcher = CONCEPT_LOOKUP_PATTERN.matcher(resolvedInput);

        if (!ConceptLookupMatcher.find()) {
            lookupResult.setValue(resolvedInput);
            LOGGER.warn(MessageFormat.format("concept.lookup.resolve.instruction.error=no concept instruction found for {0}", input));
            return lookupResult;
        } else {
            String instructionArgumentsString = ConceptLookupMatcher.group(INSTRUCTION_ARGUMENTS_KEY);
            String instructionType = ConceptLookupMatcher.group(INSTRUCTION_TYPE_KEY);
            String instructionKeyword = ConceptLookupMatcher.group(INSTRUCTION_KEYWORD_KEY).toLowerCase();

            LOGGER.debug(MessageFormat.format("concept.lookup.resolve.instruction=executing instruction of type {0} with keyword {1} and unresolved parameters {2}", instructionType, instructionKeyword, instructionArgumentsString));

            List<String> instructionArguments = splitInstructionArguments(instructionArgumentsString);
            String instructionArgumentsResolved = instructionArguments.stream()
                    .map(instructionArgument -> resolveConceptLookup(instructionArgument).getValue())
                    .collect(Collectors.joining(", "));

            LOGGER.debug(MessageFormat.format("concept.lookup.resolve.instruction.parameters=resolved instructions parameters to {0}", instructionArgumentsResolved));

            switch (instructionType) {
                case "=":
                    resolvedInput = this.generateLookupInstruction(instructionKeyword, instructionArgumentsResolved);
                    break;
                case "$":
                    resolvedInput = this.getVariableInstruction(VariableInstructionTools.getSynonymKey(instructionKeyword));
                    break;
                case "*":
                    resolvedInput = this.generateDataInstruction(instructionKeyword, instructionArgumentsResolved);
                    break;
                case "!":
                    if (instructionArgumentsResolved.startsWith("\""))
                        instructionArgumentsResolved = instructionArgumentsString.substring(1);
                    if (instructionArgumentsResolved.endsWith("\""))
                        instructionArgumentsResolved = instructionArgumentsString.substring(0, instructionArgumentsResolved.length() - 1);
                    resolvedInput = instructionArgumentsResolved;
                    break;
                case "^":
                    break;
                default:
                    LOGGER.warn(MessageFormat.format("concept.lookup.resolve.instruction.notfound=no instruction type found for {0}", instructionType));
                    resolvedInput = "{{" + instructionType + instructionKeyword + "(" + instructionArgumentsResolved + ")}}";
            }
            LOGGER.trace(MessageFormat.format("concept.lookup.resolve.instruction.result=resolved {0} to {1}", input, resolvedInput));

            lookupResult.setInputValue(input);
            lookupResult.setContext(instructionKeyword);
            lookupResult.setValue(resolvedInput);
            return lookupResult;
        }
    }

    private List<String> splitInstructionArguments(String instructionArgumentsString) {
        // TODO: move to Antler
        List<String> instructionArguments = new ArrayList<>();
        String instructionStart = "(";
        String instructionStop = ")";
        String argumentSeparator = ",";
        if (instructionArgumentsString == null) {
            return instructionArguments;
        } else if (instructionArgumentsString.startsWith("(") && instructionArgumentsString.endsWith(")")) {
            instructionArgumentsString = instructionArgumentsString.substring(1, instructionArgumentsString.length() - 1);
        }

        while (!instructionArgumentsString.isEmpty()) {
            int instructionStartIndex = instructionArgumentsString.indexOf(instructionStart);
            int argumentSeparatorIndex = instructionArgumentsString.indexOf(argumentSeparator);
            // only or last argument
            if (argumentSeparatorIndex == -1) {
                instructionArguments.add(instructionArgumentsString.trim());
                break;
            }
            // only simple arguments left or a simple argument before a function argument
            else if (instructionStartIndex == -1 || instructionStartIndex > argumentSeparatorIndex) {
                String[] splittedInstructionArguments = instructionArgumentsString.split(argumentSeparator, 2);
                instructionArguments.add(splittedInstructionArguments[0].trim());
                instructionArgumentsString = splittedInstructionArguments[1].trim();
            }
            // function argument before one or more other arguments
            else {
                int nextInstructionStartIndex = instructionArgumentsString.indexOf(instructionStart, instructionStartIndex + 1);
                int instructionStopIndex = instructionArgumentsString.indexOf(instructionStop);
                while (nextInstructionStartIndex != -1 && nextInstructionStartIndex < instructionStopIndex) {
                    instructionStopIndex = instructionArgumentsString.indexOf(instructionStop, instructionStopIndex + 1);
                    nextInstructionStartIndex = instructionArgumentsString.indexOf(instructionStart, nextInstructionStartIndex + 1);
                }
                argumentSeparatorIndex = instructionArgumentsString.indexOf(argumentSeparator, instructionStopIndex + 1);
                if (argumentSeparatorIndex == -1) {
                    instructionArguments.add(instructionArgumentsString.trim());
                    break;
                } else {
                    instructionArguments.add(instructionArgumentsString.substring(0, argumentSeparatorIndex));
                    instructionArgumentsString = instructionArgumentsString.substring(argumentSeparatorIndex + 1).trim();
                }
            }
        }
        return instructionArguments;
    }

    private String generateLookupInstruction(String context, String input) {
        LookupInstruction lookupInstruction = lookupInstructions.get(context);
        if (lookupInstruction == null) {
            throw new IllegalArgumentException(MessageFormat.format("No lookup instruction named {0} found.", context));
        } else {
            return lookupInstruction.generateOutput(input);
        }
    }

    private String getVariableInstruction(String context) {
        VariableInstruction variableInstruction = this.getVariableInstructions().get(context);
        if (variableInstruction == null) {
            throw new IllegalArgumentException(MessageFormat.format("No variable instruction named {0} found.", context));
        } else {
            return variableInstruction.generateOutput();
        }
    }

    private String generateDataInstruction(String context, String input) {
        DataInstruction dataInstruction = dataInstructions.get(context);
        if (dataInstruction == null) {
            throw new IllegalArgumentException(MessageFormat.format("No data instruction named {0} found.", context));
        } else {
            return dataInstruction.generateOutput(input);
        }
    }

    // Define logging level
//    private void defineLoggingLevel() {
//        if (FrameworkControl.getInstance()
//                .getProperty(FrameworkSettingConfiguration.getInstance().getSettingPath("commandline.display.runtime.variable").get())
//                .equals("Y")) {
//            this.setLevel(Level.INFO);
//        } else {
//            this.setLevel(Level.TRACE);
//        }
//    }

    // Stage Management
    public void setStage(String stageName, boolean stageCleanup) {
        StageOperation stageOperation = new StageOperation(stageName, stageCleanup);
        this.getStageOperationMap().put(stageName, stageOperation);
    }

    public void setKeyValueDataset(String referenceName, String datasetName, List<String> datasetLabels) throws IOException {
        datasetMap.put(referenceName, DatasetHandler.getInstance().getByNameAndLabels(datasetName, datasetLabels, this));
    }

    public Optional<Dataset> getDataset(String referenceName) {
        return Optional.ofNullable(datasetMap.get(referenceName));
    }

    public void setRWorkspace(String referenceName, RWorkspace rWorkspace) {
        RWorkspaceMap.put(referenceName, rWorkspace);
    }

    public Optional<RWorkspace> getRWorkspace(String referenceName) {
        return Optional.ofNullable(RWorkspaceMap.get(referenceName));
    }

    // Iteration Management
    public void setIteration(Iteration iteration) {
        IterationOperation iterationOperation = new IterationOperation(iteration);
        this.getIterationOperationMap().put(iteration.getName(), iterationOperation);
    }

    public IterationOperation getIterationOperation(String iterationName) {
        return this.getIterationOperationMap().get(iterationName);
    }

    // Execution Runtime Extension Management

    public HashMap<String, StageOperation> getStageOperationMap() {
        return stageOperationMap;
    }

    public ImpersonationOperation getImpersonationOperation() {
        return impersonationOperation;
    }

    public String getRunCacheFolderName() {
        return runCacheFolderName;
    }


    public HashMap<String, IterationOperation> getIterationOperationMap() {
        return iterationOperationMap;
    }


    public IterationVariableConfiguration getIterationVariableConfiguration() {
        return iterationVariableConfiguration;
    }

    public HashMap<String, VariableInstruction> getVariableInstructions() {
        return variableInstructions;
    }

    public RuntimeVariableConfiguration getRuntimeVariableConfiguration() {
        return runtimeVariableConfiguration;
    }


}
