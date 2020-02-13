package io.metadew.iesi.script.action.ddl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.tools.DatabaseTools;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;


public class DdlGenerateFromFile {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation inputPath;
    private ActionParameterOperation inputFile;
    private ActionParameterOperation outputType;
    private ActionParameterOperation outputPath;
    private ActionParameterOperation outputFile;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public DdlGenerateFromFile() {

    }

    public DdlGenerateFromFile(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Set Parameters
        this.setInputPath(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "inputPath"));
        this.setInputFile(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "inputFile"));
        this.setOutputType(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "type"));
        this.setOutputPath(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "outputPath"));
        this.setOutputFile(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "outputFile"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("inputPath")) {
                this.getInputPath().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("inputFile")) {
                this.getInputFile().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("type")) {
                this.getOutputType().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("outputPath")) {
                this.getOutputPath().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("outputFile")) {
                this.getOutputFile().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("inputPath", this.getInputPath());
        this.getActionParameterOperationMap().put("inputFile", this.getInputFile());
        this.getActionParameterOperationMap().put("type", this.getOutputType());
        this.getActionParameterOperationMap().put("outputPath", this.getOutputPath());
        this.getActionParameterOperationMap().put("outputFile", this.getOutputFile());
    }


    public boolean execute() throws InterruptedException {
        try {
            String inputPath = convertInputPath(getInputPath().getValue());
            String inputFile = convertInputFile(getInputFile().getValue());
            String outputType = convertOutputType(getOutputType().getValue());
            String outputPath = convertOutputPath(getOutputPath().getValue());
            String outputFile = convertOutputFile(getOutputFile().getValue());
            return executeQuery(inputPath, inputFile, outputType, outputPath, outputFile);
        } catch (InterruptedException e) {
            throw(e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private String convertInputFile(DataType inputFile) {
        if (inputFile instanceof Text) {
            return inputFile.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
            		inputFile.getClass()));
            return inputFile.toString();
        }
    }

    private String convertInputPath(DataType inputPath) {
        if (inputPath instanceof Text) {
            return inputPath.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for query",
            		inputPath.getClass()));
            return inputPath.toString();
        }
    }


    private String convertOutputType(DataType outputType) {
        if (outputType instanceof Text) {
            return outputType.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for dataset reference name",
            		outputType.getClass()));
            return outputType.toString();
        }
    }
    
    private String convertOutputPath(DataType outputPath) {
        if (outputPath instanceof Text) {
            return outputPath.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for dataset reference name",
            		outputPath.getClass()));
            return outputPath.toString();
        }
    }
    
    private String convertOutputFile(DataType outputFile) {
        if (outputFile instanceof Text) {
            return outputFile.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for dataset reference name",
            		outputFile.getClass()));
            return outputFile.toString();
        }
    }

    private boolean executeQuery(String inputPath, String inputFile, String outputType, String outputPath, String outputFile) throws InterruptedException{

    	// TODO - fix for schema databases (dummy database connection)
    	Database database = DatabaseTools.getDatabase("io.metadew.iesi.connection.database.SqliteDatabase");
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	DataObjectOperation dataObjectOperation = new DataObjectOperation(inputFile);
    	
    	for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
    		MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);
    		FileTools.appendToFile(outputFile, "", database.getCreateStatement(metadataTable, "IESI_"));
    	}
    	
    	this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getActionParameterOperation(String key) {
        return this.getActionParameterOperationMap().get(key);
    }

	public ActionParameterOperation getInputPath() {
		return inputPath;
	}

	public void setInputPath(ActionParameterOperation inputPath) {
		this.inputPath = inputPath;
	}

	public ActionParameterOperation getInputFile() {
		return inputFile;
	}

	public void setInputFile(ActionParameterOperation inputFile) {
		this.inputFile = inputFile;
	}

	public ActionParameterOperation getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(ActionParameterOperation outputPath) {
		this.outputPath = outputPath;
	}

	public ActionParameterOperation getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(ActionParameterOperation outputFile) {
		this.outputFile = outputFile;
	}

	public ActionParameterOperation getOutputType() {
		return outputType;
	}

	public void setOutputType(ActionParameterOperation outputType) {
		this.outputType = outputType;
	}

}
