package io.metadew.iesi.script.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.KeyValueDataset;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.*;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.Iteration;
import io.metadew.iesi.metadata.definition.RuntimeVariable;
import io.metadew.iesi.metadata.definition.ScriptResultOutput;
import io.metadew.iesi.runtime.definition.LookupResult;
import io.metadew.iesi.script.configuration.IterationVariableConfiguration;
import io.metadew.iesi.script.configuration.RuntimeVariableConfiguration;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import io.metadew.iesi.script.execution.instruction.data.DataInstructionRepository;
import io.metadew.iesi.script.execution.instruction.lookup.LookupInstruction;
import io.metadew.iesi.script.execution.instruction.lookup.LookupInstructionRepository;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstruction;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstructionRepository;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstructionTools;
import io.metadew.iesi.script.operation.*;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
//import io.metadew.iesi.script.operation.StageOperation;

public class ExecutionRuntime {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    private RuntimeVariableConfiguration runtimeVariableConfiguration;
    private IterationVariableConfiguration iterationVariableConfiguration;

    private String runId;
    private String runCacheFolderName;

    private Level level = Level.TRACE;

    //private HashMap<String, StageOperation> stageOperationMap;
    private HashMap<String, RepositoryOperation> repositoryOperationMap;
    private HashMap<String, StageOperation> stageOperationMap;
    private HashMap<String, Dataset> datasetMap;
    private HashMap<String, IterationOperation> iterationOperationMap;

    private HashMap<String, ExecutionRuntimeExtension> executionRuntimeExtensionMap;

    private ImpersonationOperation impersonationOperation;

    private HashMap<String, DataInstruction> dataInstructions;
    private HashMap<String, VariableInstruction> variableInstructions;
    private HashMap<String, LookupInstruction> lookupInstructions;

    private final String INSTRUCTION_TYPE_KEY = "instructionType";

    private final String INSTRUCTION_KEYWORD_KEY = "instructionKeyword";

    private final String INSTRUCTION_ARGUMENTS_KEY = "instructionArguments";

    private final Pattern CONCEPT_LOOKUP_PATTERN = Pattern
            .compile("\\s*\\{\\{(?<" + INSTRUCTION_TYPE_KEY + ">[\\*=\\$!])(?<" + INSTRUCTION_KEYWORD_KEY + ">[\\w\\.]+)(?<" + INSTRUCTION_ARGUMENTS_KEY + ">\\(.*\\))?\\}\\}\\s*");

    public ExecutionRuntime() {

    }

    public ExecutionRuntime(FrameworkExecution frameworkExecution, ExecutionControl executionControl, String runId) {
        this.setExecutionControl(executionControl);
        this.init(frameworkExecution, runId);
    }

    public void init(FrameworkExecution frameworkExecution, String runId) {
        this.setFrameworkExecution(frameworkExecution);
        this.setRunId(runId);

        // Create cache folder
        this.setRunCacheFolderName(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
                .getFolderAbsolutePath("run.cache") + File.separator + this.getRunId());
        FolderTools.createFolder(this.getRunCacheFolderName());

        this.setRuntimeVariableConfiguration(
                new RuntimeVariableConfiguration(this.getFrameworkExecution(), this.getRunCacheFolderName()));
        this.setIterationVariableConfiguration(
                new IterationVariableConfiguration(this.getFrameworkExecution(), this.getRunCacheFolderName(), true));
        this.defineLoggingLevel();

        // Initialize maps
        this.setStageOperationMap(new HashMap<String, StageOperation>());
        this.setRepositoryOperationMap(new HashMap<String, RepositoryOperation>());
        this.setIterationOperationMap(new HashMap<String, IterationOperation>());
        this.setExecutionRuntimeExtensionMap(new HashMap<String, ExecutionRuntimeExtension>());

        // Initialize impersonations
        this.setImpersonationOperation(new ImpersonationOperation(this.getFrameworkExecution()));

        // Initialize extensions

        // Initialize data instructions
        dataInstructions = DataInstructionRepository.getRepository(new GenerationObjectExecution(this.getFrameworkExecution()));
        variableInstructions = VariableInstructionRepository.getRepository(this.getExecutionControl());
        lookupInstructions = LookupInstructionRepository.getRepository(getExecutionControl());
        datasetMap = new HashMap<>();
    }

    @SuppressWarnings("rawtypes")
	public void terminate() {
        // cleanup stage connections if needed
        ObjectMapper objectMapper = new ObjectMapper();
        Iterator iterator = this.getStageOperationMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            StageOperation stageOperation = objectMapper.convertValue(pair.getValue(), StageOperation.class);
            stageOperation.doCleanup();
            iterator.remove();
        }

        // remove cache folder
        //this.getFrameworkExecution().getFrameworkRuntime().terminate();
    }

    // Methods
    public void cleanRuntimeVariables() {
        this.getRuntimeVariableConfiguration().cleanRuntimeVariables(this.getRunId());
    }

    public void cleanRuntimeVariables(long processId) {
        this.getRuntimeVariableConfiguration().cleanRuntimeVariables(this.getRunId(), processId);
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

    // Set runtime variables
    public void setRuntimeVariable(Long processId, String name, String value) {
        this.getFrameworkExecution().getFrameworkLog().log("exec.runvar.set=" + name + ":" + value, this.getLevel());
        this.getRuntimeVariableConfiguration().setRuntimeVariable(this.getRunId(), processId, name, value);
    }

    public void setRuntimeVariable(ActionExecution actionExecution, String name, String value) {
        this.getFrameworkExecution().getFrameworkLog().log("exec.runvar.set=" + name + ":" + value, this.getLevel());
        this.getRuntimeVariableConfiguration().setRuntimeVariable(this.getRunId(), actionExecution.getProcessId(), name, value);
    }

    public void setRuntimeVariable(ScriptExecution scriptExecution, String name, String value) {
        this.getFrameworkExecution().getFrameworkLog().log("exec.runvar.set=" + name + ":" + value, this.getLevel());
        this.getRuntimeVariableConfiguration().setRuntimeVariable(this.getRunId(), scriptExecution.getProcessId(), name, value);
    }

    public RuntimeVariable getRuntimeVariable(String name) {
        return this.getRuntimeVariableConfiguration().getRuntimeVariable(this.getRunId(), name);
    }

    public String getRuntimeVariableValue(String name) {
        return this.getRuntimeVariableConfiguration().getRuntimeVariableValue(this.getRunId(), name);
    }

    // Iteration Variables
    public void setIterationVariables(String listName, ResultSet rs) {
        this.getIterationVariableConfiguration().setIterationList(this.getRunId(), listName, rs);
    }

    // Load lists
    public void loadParamList(ScriptExecution scriptExecution, String input) {
        String[] parts = input.split(",");
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            int delim = innerpart.indexOf("=");
            if (delim > 0) {
                String key = innerpart.substring(0, delim);
                String value = innerpart.substring(delim + 1);
                this.setRuntimeVariable(scriptExecution, key, value);
            } else {
                // Not a valid configuration
            }
        }
    }

    public void loadParamList(ActionExecution actionExecution, String input) {
        String[] parts = input.split(",");
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            int delim = innerpart.indexOf("=");
            if (delim > 0) {
                String key = innerpart.substring(0, delim);
                String value = innerpart.substring(delim + 1);
                this.setRuntimeVariable(actionExecution, key, value);
            } else {
                // Not a valid configuration
            }
        }
    }

    public void loadParamFiles(ScriptExecution scriptExecution, String files) {
        String[] parts = files.split(",");
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            this.loadParamFile(scriptExecution, innerpart);
        }
    }

    public void loadParamFile(ScriptExecution scriptExecution, String file) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = br.readLine()) != null) {
                String innerpart = line;
                int delim = innerpart.indexOf("=");
                if (delim > 0) {
                    String key = innerpart.substring(0, delim);
                    String value = innerpart.substring(delim + 1);
                    this.setRuntimeVariable(scriptExecution, key, value);
                } else {
                    // Not a valid configuration
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Resolution
    public String resolveVariables(String input) {
        // Prevent null issues during string operations
        if (input == null) {
            input = "";
        }
        String result = "";

        // First level: settings
        result = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(input);

        // Second level: runtime variables
        result = this.resolveRuntimeVariables(result);
        if (!input.equalsIgnoreCase(result))
            this.getFrameworkExecution().getFrameworkLog().log("exec.runvar.resolve=" + input + ":" + result,
                    Level.TRACE);

        return result;
    }

    public String resolveVariables(ActionExecution actionExecution, String input) {
        // Prevent null issues during string operations
        if (input == null) {
            input = "";
        }
        String result = "";

        // First level: settings
        result = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(input);

        // Second: Action attributes
        if (actionExecution != null) {
            result = this.resolveConfiguration(actionExecution, result);
        }

        // third level: runtime variables
        result = this.resolveRuntimeVariables(result);
        if (!input.equalsIgnoreCase(result))
            this.getFrameworkExecution().getFrameworkLog().log("exec.runvar.resolve=" + input + ":" + result,
                    Level.DEBUG);

        return result;
    }

    public String resolveVariables(String input, boolean dup) {
        // Prevent null issues during string operations
        if (input == null) {
            input = "";
        }
        String result = "";

        // First level: settings
        result = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(input);

        // third level: runtime variables
        result = this.resolveRuntimeVariables(result);
        if (!input.equalsIgnoreCase(result))
            this.getFrameworkExecution().getFrameworkLog().log("exec.runvar.resolve=" + input + ":" + result,
                    Level.TRACE);

        return result;
    }


    private String resolveRuntimeVariables(String input) {
        getFrameworkExecution().getFrameworkLog().log(
                MessageFormat.format("runvar.resolve=resolving {0} for runtime variables", input),
                Level.TRACE);
        int openPos;
        int closePos;
        String variable_char = "#";
        String midBit;
        String replaceValue;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char, openPos + 1);
            midBit = temp.substring(openPos + 1, closePos);

            // Replace
            replaceValue = this.getRuntimeVariableValue(midBit);
            if (replaceValue != null) {
                input = input.replaceAll(variable_char + midBit + variable_char, replaceValue);
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        getFrameworkExecution().getFrameworkLog().log(
                MessageFormat.format("runvar.resolve.result=resolved to {0}", input),
                Level.TRACE);
        return input;
    }


    public String resolveActionTypeVariables(String input,
                                             HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
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

    public String resolveMapVariables(String input, HashMap<String, String> variableMap) {
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
            replaceValue = variableMap.get(midBit);
            if (replaceValue != null) {
                input = input.replace(variable_char_open + midBit + variable_char_close, replaceValue);
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        return input;
    }

    public String resolveComponentTypeVariables(String input, List<ComponentAttribute> componentAttributeList,
                                                String environment) {
        HashMap<String, ComponentAttribute> componentAttributeMap = this
                .getComponentAttributeHashmap(componentAttributeList, environment);
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

    private HashMap<String, ComponentAttribute> getComponentAttributeHashmap(
            List<ComponentAttribute> componentAttributeList, String environment) {
        if (componentAttributeList == null) {
            return null;
        }

        HashMap<String, ComponentAttribute> componentAttributeMap = new HashMap<String, ComponentAttribute>();
        for (ComponentAttribute componentAttribute : componentAttributeList) {
            if (componentAttribute.getEnvironment().trim().equalsIgnoreCase(environment)) {
                componentAttributeMap.put(componentAttribute.getName(), componentAttribute);
            }
        }
        return componentAttributeMap;
    }

    public String resolveConfiguration(ActionExecution actionExecution, String input) {
        getFrameworkExecution().getFrameworkLog().log(
                MessageFormat.format("configuration.resolve=resolving {0} for configurations", input),
                Level.TRACE);

        int openPos;
        int closePos;
        String variable_char = "#";
        String midBit;
        String replaceValue = null;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char, openPos + 1);
            midBit = temp.substring(openPos + 1, closePos);

            // Try to find a configuration value
            // If none is found, null is set by default
            try {
                replaceValue = actionExecution.getComponentAttributeOperation().getProperty(midBit);
            } catch (Exception e) {
                replaceValue = null;
            }

            // Replacing the value if found
            if (replaceValue != null) {
                input = input.replaceAll(variable_char + midBit + variable_char, replaceValue);
            }
            temp = temp.substring(closePos + 1, temp.length());

        }

        getFrameworkExecution().getFrameworkLog().log(
                MessageFormat.format("configuration.resolve.result=resolved to {0}", input),
                Level.TRACE);

        return input;
    }

    /*
     * New function or concept lookups
     * Work in progress
     * We will move only here when stable
     */
    public LookupResult resolveConceptLookup(ExecutionControl executionControl, String input, boolean dup) {
        getFrameworkExecution().getFrameworkLog().log(
                MessageFormat.format("concept.lookup.resolve=resolving {0} for concept lookup instructions", input),
                Level.TRACE);
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
                frameworkExecution.getFrameworkLog().log(
                        MessageFormat.format("concept.lookup.resolve.error=error during concept lookup resolvement of {0}. Concept lookup instruction not properly closed.", input),
                        Level.WARN);
                lookupResult.setValue(input);
                return lookupResult;
            }
            lookupConceptStopIndex = input.indexOf(lookupConceptStopKey, lookupConceptStartIndex);
            nextLookupConceptStartIndex = input.indexOf(lookupConceptStartKey, lookupConceptStartIndex + lookupConceptStartKey.length());
            while (nextLookupConceptStartIndex > 0 && nextLookupConceptStartIndex < lookupConceptStopIndex) {
                lookupConceptStopIndex = input.indexOf(lookupConceptStopKey, lookupConceptStopIndex + lookupConceptStopKey.length());
                if (lookupConceptStopIndex < 0) {
                    frameworkExecution.getFrameworkLog().log(
                            MessageFormat.format("concept.lookup.resolve.error=error during concept lookup resolvement of {0}. Concept lookup instruction not properly closed.", input),
                            Level.WARN);
                    lookupResult.setValue(input);
                    return lookupResult;
                }
                nextLookupConceptStartIndex = input.indexOf(lookupConceptStartKey, nextLookupConceptStartIndex + lookupConceptStartKey.length());
            }
            String resolvement = executeConceptLookup(executionControl, input.substring(lookupConceptStartIndex, lookupConceptStopIndex + lookupConceptStopKey.length())).getValue();
            input = input.substring(0, lookupConceptStartIndex) +
                    resolvement +
                    input.substring(lookupConceptStopIndex + lookupConceptStopKey.length());
        }
        getFrameworkExecution().getFrameworkLog().log(
                MessageFormat.format("concept.lookup.resolve.result={0}:{1}", lookupResult.getInputValue(), input),
                Level.DEBUG);
        lookupResult.setValue(input);
        return lookupResult;
    }

    public LookupResult executeConceptLookup(ExecutionControl executionControl, String input) {
        getFrameworkExecution().getFrameworkLog().log(
                MessageFormat.format("concept.lookup.resolve.instruction=resolving instruction {0}", input),
                Level.TRACE);
        LookupResult lookupResult = new LookupResult();
        String resolvedInput = input;
        Matcher ConceptLookupMatcher = CONCEPT_LOOKUP_PATTERN.matcher(resolvedInput);

        if (!ConceptLookupMatcher.find()) {
            lookupResult.setValue(resolvedInput);
            getFrameworkExecution().getFrameworkLog().log(
                    MessageFormat.format("concept.lookup.resolve.instruction.error=no concept instruction found for {0}", input),
                    Level.TRACE);
            return lookupResult;
        } else {
            String instructionArgumentsString = ConceptLookupMatcher.group(INSTRUCTION_ARGUMENTS_KEY);
            String instructionType = ConceptLookupMatcher.group(INSTRUCTION_TYPE_KEY);
            String instructionKeyword = ConceptLookupMatcher.group(INSTRUCTION_KEYWORD_KEY).toLowerCase();

            getFrameworkExecution().getFrameworkLog().log(
                    MessageFormat.format("concept.lookup.resolve.instruction=executing instruction of type {0} with keyword {1} and unresolved parameters {2}", instructionType, instructionKeyword, instructionArgumentsString),
                    Level.DEBUG);
            List<String> instructionArguments = splitInstructionArguments(instructionArgumentsString);
            String instructionArgumentsResolved = instructionArguments.stream()
                    .map(instructionArgument -> resolveConceptLookup(executionControl, instructionArgument, true).getValue())
                    .collect(Collectors.joining(", "));
            getFrameworkExecution().getFrameworkLog().log(
                    MessageFormat.format("concept.lookup.resolve.instruction.parameters=resolved instructions parameters to {0}", instructionArgumentsString),
                    Level.DEBUG);

            switch (instructionType) {
                case "=":
                    resolvedInput = this.generateLookupInstruction(executionControl, instructionKeyword, instructionArgumentsResolved);
                    break;
                case "$":
                    resolvedInput = this.getVariableInstruction(executionControl, VariableInstructionTools.getSynonymKey(instructionKeyword), instructionArgumentsResolved);
                    break;
                case "*":
                    resolvedInput = this.generateDataInstruction(executionControl, instructionKeyword, instructionArgumentsResolved);
                    break;
                case "!":
                    if (instructionArgumentsResolved.startsWith("\""))
                        instructionArgumentsResolved = instructionArgumentsString.substring(1);
                    if (instructionArgumentsResolved.endsWith("\""))
                        instructionArgumentsResolved = instructionArgumentsString.substring(0, instructionArgumentsResolved.length() - 1);
                    resolvedInput = instructionArgumentsResolved;
                    break;
                default:

                    getFrameworkExecution().getFrameworkLog().log(
                            MessageFormat.format("concept.lookup.resolve.instruction.notfound=no instruction type found for {0}", instructionType),
                            Level.WARN);
                    // TODO: not correct
                    resolvedInput = "{{" + instructionType + instructionKeyword + "(" + instructionArgumentsResolved + ")}}";
            }
            getFrameworkExecution().getFrameworkLog().log(
                    MessageFormat.format("concept.lookup.resolve.instruction.result=resolved {0} to {1}", input, resolvedInput),
                    Level.TRACE);

            lookupResult.setInputValue(input);
            lookupResult.setType(instructionType);
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
            instructionArgumentsString = instructionArgumentsString.substring(1, instructionArgumentsString.length()-1);
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

    private String generateLookupInstruction(ExecutionControl executionControl, String context, String input) {
        LookupInstruction lookupInstruction = lookupInstructions.get(context);
        if (lookupInstruction == null) {
            throw new IllegalArgumentException(MessageFormat.format("No lookup instruction named {0} found.", context));
        } else {
            return lookupInstruction.generateOutput(input);
        }
    }

    // Get cross concept lookup
    public LookupResult resolveConceptLookup(ExecutionControl executionControl, String input) {
        LookupResult lookupResult = new LookupResult();
        int openPos;
        int closePos;
        String variable_char = "{{";
        String variable_char_close = "}}";
        String midBit;
        String replaceValue;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            List<String> items = new ArrayList<>();
            String tempInstructions = temp;
            while (tempInstructions.indexOf(variable_char) > 0 || tempInstructions.startsWith(variable_char)) {
                openPos = tempInstructions.indexOf(variable_char);
                closePos = tempInstructions.indexOf(variable_char_close);
                midBit = tempInstructions.substring(openPos + 2, closePos).trim();
                items.add(midBit);
                tempInstructions = midBit;
            }

            // get last value
            String instruction = items.get(items.size() - 1);

            // check split different types
            String instructionType = instruction.substring(0, 1).toLowerCase();
            String instructionOutput = instruction;

            // Lookup
            if (instructionType.equalsIgnoreCase("=")) {
                int lookupOpenPos = instruction.indexOf("(");
                int lookupClosePos = instruction.indexOf(")", lookupOpenPos + 1);
                String lookupContext = instruction.substring(1, lookupOpenPos).trim().toLowerCase();
                String lookupScope = instruction.substring(lookupOpenPos + 1, lookupClosePos).trim();
                if (lookupContext.equalsIgnoreCase("connection") || lookupContext.equalsIgnoreCase("conn")) {
                    instructionOutput = this.lookupConnectionInstruction(executionControl, lookupScope);
                } else if (lookupContext.equalsIgnoreCase("environment") || lookupContext.equalsIgnoreCase("env")) {
                    instructionOutput = this.lookupEnvironmentInstruction(executionControl, lookupScope);
                } else if (lookupContext.equalsIgnoreCase("dataset") || lookupContext.equalsIgnoreCase("ds")) {
                    instructionOutput = this.lookupDatasetInstruction(executionControl, lookupScope);
                } else if (lookupContext.equalsIgnoreCase("file") || lookupContext.equalsIgnoreCase("f")) {
                    instructionOutput = this.lookupFileInstruction(executionControl, lookupScope);
                } else if (lookupContext.equalsIgnoreCase("coalesce") || lookupContext.equalsIgnoreCase("ifnull") || lookupContext.equalsIgnoreCase("nvl")) {
                    instructionOutput = this.lookupCoalesceResult(executionControl, lookupScope);
                } else if (lookupContext.equalsIgnoreCase("script.output") || lookupContext.equalsIgnoreCase("s.out")) {
                    instructionOutput = this.lookupScriptResultInstruction(executionControl, lookupScope);
                }
                // Variable lookup
            } else if (instructionType.equalsIgnoreCase("$")) {
                String lookupContext = VariableInstructionTools.getSynonymKey(instruction.substring(1).trim().toLowerCase());
                instructionOutput = this.getVariableInstruction(executionControl, lookupContext, "");
                // Generate data
            } else if (instructionType.equalsIgnoreCase("*")) {
                int lookupOpenPos = instruction.indexOf("(");
                int lookupClosePos = instruction.indexOf(")", lookupOpenPos + 1);
                String lookupContext = instruction.substring(1, lookupOpenPos).trim().toLowerCase();
                String lookupScope = instruction.substring(lookupOpenPos + 1, lookupClosePos).trim();
                instructionOutput = this.generateDataInstruction(executionControl, lookupContext, lookupScope);
                // run scripts
            } else if (instructionType.equalsIgnoreCase("!")) {
                int lookupOpenPos = instruction.indexOf("(");
                int lookupClosePos = instruction.lastIndexOf(")");
                String lookupContext = instruction.substring(1, lookupOpenPos).trim().toLowerCase();
                String lookupScope = instruction.substring(lookupOpenPos + 1, lookupClosePos).trim();
                lookupResult.setContext(lookupContext);
                if (lookupScope.startsWith("\"")) lookupScope = lookupScope.substring(1);
                if (lookupScope.endsWith("\"")) lookupScope = lookupScope.substring(0, lookupScope.length() - 1);
                instructionOutput = lookupScope;
                // Verify for javascript / js and jexl / jxl
            }
            replaceValue = instructionOutput;
            // this.decrypt(variable_char + midBit + variable_char_close);
            if (replaceValue != null) {
                input = input.replace(variable_char + instruction + variable_char_close, replaceValue);
            }
            temp = input;
        }

        lookupResult.setValue(input);
        return lookupResult;

    }

    private String lookupConnectionInstruction(ExecutionControl executionControl, String input) {
        String output = input;

        // Parse input
        String[] parts = input.split(",");
        String connectionName = parts[0].trim();
        String connectionParameterName = parts[1].trim();

        ConnectionParameterConfiguration connectionParameterConfiguration = new ConnectionParameterConfiguration(
                this.getFrameworkExecution().getFrameworkInstance());
        Optional<String> connectionParameterValue = connectionParameterConfiguration.getConnectionParameterValue(connectionName,
                executionControl.getEnvName(), connectionParameterName);

        if (connectionParameterValue.isPresent()) {
            output = connectionParameterValue.get();
        }
        return output;
    }

    private String lookupEnvironmentInstruction(ExecutionControl executionControl, String input) {
        // Parse input
        String[] parts = input.split(",");
        String environmentName = parts[0].trim();
        String environmentParameterName = parts[1].trim();

        EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration(
                this.getFrameworkExecution().getFrameworkInstance());

        return environmentParameterConfiguration.getEnvironmentParameterValue(environmentName, environmentParameterName)
                .orElse(input);
    }


    private String lookupScriptResultInstruction(ExecutionControl executionControl, String input) {
        ScriptResultOutputConfiguration scriptResultOutputConfiguration = new ScriptResultOutputConfiguration(
                this.getFrameworkExecution().getFrameworkInstance());
        // TODO only for root scripts - extend to others
        return scriptResultOutputConfiguration.getScriptOutput(executionControl.getRunId(), 0, input)
                .map(ScriptResultOutput::getValue)
                .orElse(input);
    }

    @SuppressWarnings("unused")
	private String lookupDatasetInstruction(ExecutionControl executionControl, String input) {
        String output = input;

        // Parse input
        String[] parts = input.split(",");
        String datasetReferenceName = parts[0].trim();
        String datasetItem = parts[1].trim();


        Optional<Dataset> dataset = getDataset(datasetReferenceName);
        String dataItem = dataset
                .map(dataset1 -> dataset1.getDataItem(datasetItem)
                        .map(DataType::toString)
                        .orElse(input))
                .orElse(input);
//        DatasetOperation datasetOperation = executionControl.getExecutionRuntime().getDatasetOperation(datasetReferenceName);
//
//        if (!datasetItem.equalsIgnoreCase("")) {
//            Optional<String> dataitem = datasetOperation.getDataItem(datasetItem);
//            output = dataitem.orElse(input);
//        }

        return dataItem;
    }

    private String lookupFileInstruction(ExecutionControl executionControl, String input) {
        String output = "";
        File file = new File(input);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                output += this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(readLine);
                output += "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //TODO harmonize for first line input
        //String output = input.trim();
        //output = SQLTools.getFirstSQLStmt(input);
        return output;
    }

    private String lookupCoalesceResult(ExecutionControl executionControl, String input) {
        String output = "";
        String[] parts = input.split(",");
        for (int i = 0; i < parts.length; i++) {
            String temp = parts[i].trim();
            if (!temp.isEmpty()) {
                output = parts[i];
                break;
            }
        }
        return output;
    }

    private String getVariableInstruction(ExecutionControl executionControl, String context, String input) {
        VariableInstruction variableInstruction = this.getVariableInstructions().get(context);
        if (variableInstruction == null) {
            throw new IllegalArgumentException(MessageFormat.format("No variable instruction named {0} found.", context));
        } else {
            return variableInstruction.generateOutput();
        }
    }

    private String generateDataInstruction(ExecutionControl executionControl, String context, String input) {
        DataInstruction dataInstruction = dataInstructions.get(context);
        if (dataInstruction == null) {
            throw new IllegalArgumentException(MessageFormat.format("No data instruction named {0} found.", context));
        } else {
            return dataInstruction.generateOutput(input);
        }
    }

    // Conversion
    public InputStream convertToInputStream(File file) {
        String output = "";
        try {
            @SuppressWarnings("resource")
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                output += this.resolveVariables(readLine);
                output += "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("The system cannot find the path specified", e);
        }
        return new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
    }

    // Define logging level
    private void defineLoggingLevel() {
        if (this.getFrameworkExecution().getFrameworkControl()
                .getProperty(this.getFrameworkExecution().getFrameworkConfiguration().getSettingConfiguration()
                        .getSettingPath("commandline.display.runtime.variable").get())
                .equals("Y")) {
            this.setLevel(Level.INFO);
        } else {
            this.setLevel(Level.TRACE);
        }
    }

    // Stage Management
    public void setStage(String stageName, boolean stageCleanup) {
        StageOperation stageOperation = new StageOperation(this.getFrameworkExecution(), stageName, stageCleanup);
        this.getStageOperationMap().put(stageName, stageOperation);
    }

    public void setStageOperation(String stageName, StageOperation stageOperation) {
        this.getStageOperationMap().put(stageName, stageOperation);
    }

    public StageOperation getStageOperation(String stageName) {
        return this.getStageOperationMap().get(stageName);
    }

    // Repository Management
    public void setRepository(ExecutionControl executionControl, String repositoryReferenceName, String repositoryName, String repositoryInstanceName, String repositoryInstanceLabels) {
        RepositoryOperation repositoryOperation = new RepositoryOperation(this.getFrameworkExecution(), executionControl, repositoryName,
                repositoryInstanceName, repositoryInstanceLabels);
        this.getRepositoryOperationMap().put(repositoryReferenceName, repositoryOperation);
    }

    public void setKeyValueDataset(String referenceName, String datasetName, List<String> datasetLabels) throws IOException, SQLException {
        datasetMap.put(referenceName,
                new KeyValueDataset(datasetName, datasetLabels, frameworkExecution.getFrameworkConfiguration().getFolderConfiguration(),
                        this));
    }



    public Optional<Dataset> getDataset(String referenceName) {
        return Optional.ofNullable(datasetMap.get(referenceName));
    }

    // Iteration Management
    public void setIteration(Iteration iteration) {
        IterationOperation iterationOperation = new IterationOperation(iteration);
        this.getIterationOperationMap().put(iteration.getName(), iterationOperation);
    }

    public void setIterationOperation(IterationOperation iterationOperation) {
        this.getIterationOperationMap().put(iterationOperation.getIteration().getName(), iterationOperation);
    }

    public IterationOperation getIterationOperation(String iterationName) {
        return this.getIterationOperationMap().get(iterationName);
    }

    // Execution Runtime Extension Management
    public void setExecutionRuntimeExtension(ExecutionRuntimeExtension executionRuntimeExtension) {
        this.getExecutionRuntimeExtensionMap().put(executionRuntimeExtension.getExecutionRuntimeExtensionName(),
                executionRuntimeExtension);
    }

    public ExecutionRuntimeExtension getExecutionRuntimeExtension(String executionRuntimeExtensionName) {
        return this.getExecutionRuntimeExtensionMap().get(executionRuntimeExtensionName);
    }

    public boolean executionRuntimeExtensionExists(String executionRuntimeExtensionName) {
        ExecutionRuntimeExtension executionRuntimeExtension = this.getExecutionRuntimeExtensionMap()
                .get(executionRuntimeExtensionName);
        if (executionRuntimeExtension != null) {
            // Value exists
            return true;
        } else {
            // Check if only the key exists
            if (this.getExecutionRuntimeExtensionMap().containsKey(executionRuntimeExtensionName)) {
                // Only key exists with null value
                return true;
            } else {
                // No key and no value exist
                return false;
            }
        }
    }

    // Impersonations
    public void setImpersonationName(String impersonationName) {
        this.getImpersonationOperation().setImpersonation(impersonationName);
    }

    public void setImpersonationCustom(String impersonationCustom) {
        this.getImpersonationOperation().setImpersonationCustom(impersonationCustom);
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public RuntimeVariableConfiguration getRuntimeVariableConfiguration() {
        return runtimeVariableConfiguration;
    }

    public void setRuntimeVariableConfiguration(RuntimeVariableConfiguration runtimeVariableConfiguration) {
        this.runtimeVariableConfiguration = runtimeVariableConfiguration;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public HashMap<String, StageOperation> getStageOperationMap() {
        return stageOperationMap;
    }

    public void setStageOperationMap(HashMap<String, StageOperation> stageOperationMap) {
        this.stageOperationMap = stageOperationMap;
    }

    public ImpersonationOperation getImpersonationOperation() {
        return impersonationOperation;
    }

    public void setImpersonationOperation(ImpersonationOperation impersonationOperation) {
        this.impersonationOperation = impersonationOperation;
    }

    public HashMap<String, ExecutionRuntimeExtension> getExecutionRuntimeExtensionMap() {
        return executionRuntimeExtensionMap;
    }

    public void setExecutionRuntimeExtensionMap(
            HashMap<String, ExecutionRuntimeExtension> executionRuntimeExtensionMap) {
        this.executionRuntimeExtensionMap = executionRuntimeExtensionMap;
    }

    public String getRunCacheFolderName() {
        return runCacheFolderName;
    }

    public void setRunCacheFolderName(String runCacheFolderName) {
        this.runCacheFolderName = runCacheFolderName;
    }

    public HashMap<String, IterationOperation> getIterationOperationMap() {
        return iterationOperationMap;
    }

    public void setIterationOperationMap(HashMap<String, IterationOperation> iterationOperationMap) {
        this.iterationOperationMap = iterationOperationMap;
    }

    public IterationVariableConfiguration getIterationVariableConfiguration() {
        return iterationVariableConfiguration;
    }

    public void setIterationVariableConfiguration(IterationVariableConfiguration iterationVariableConfiguration) {
        this.iterationVariableConfiguration = iterationVariableConfiguration;
    }

    public HashMap<String, RepositoryOperation> getRepositoryOperationMap() {
        return repositoryOperationMap;
    }

    public void setRepositoryOperationMap(HashMap<String, RepositoryOperation> repositoryOperationMap) {
        this.repositoryOperationMap = repositoryOperationMap;
    }

    public HashMap<String, VariableInstruction> getVariableInstructions() {
        return variableInstructions;
    }

    public void setVariableInstructions(HashMap<String, VariableInstruction> variableInstructions) {
        this.variableInstructions = variableInstructions;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

}
