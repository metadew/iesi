package io.metadew.iesi.script.action.ddl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.text.MessageFormat;


public class DdlGenerateFromFile extends ActionTypeExecution {

    private static final Logger LOGGER = LogManager.getLogger();
        private static final String INPUT_PATH_KEY = "inputPath";
        private static final String INPUT_FILE_KEY = "inputFile";
        private static final String TYPE_KEY = "type";
        private static final String OUTPUT_PATH_KEY = "outputPath";
        private static final String OUTPUT_FILE_KEY = "outputFile";

    public DdlGenerateFromFile(ExecutionControl executionControl,
                               ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
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
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for query",
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

    protected boolean executeAction() throws InterruptedException {

        String inputPath = convertInputPath(getParameterResolvedValue(INPUT_PATH_KEY));
        String inputFile = convertInputFile(getParameterResolvedValue(INPUT_FILE_KEY));
        String outputType = convertOutputType(getParameterResolvedValue(TYPE_KEY));
        String outputPath = convertOutputPath(getParameterResolvedValue(OUTPUT_PATH_KEY));
        String outputFile = convertOutputFile(getParameterResolvedValue(OUTPUT_FILE_KEY));
        // TODO - fix for schema databases (dummy database connection)
        Database database = null;
        //DatabaseTools.getDatabase("io.metadew.iesi.connection.database.sqlite.SqliteDatabase");

        ObjectMapper objectMapper = new ObjectMapper();
        DataObjectOperation dataObjectOperation = new DataObjectOperation(Paths.get(inputFile));

        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);
            FileTools.appendToFile(outputFile, "", SpringContext.getBean(DatabaseHandler.class).getCreateStatement(database, metadataTable, "IESI_"));
        }

        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "ddl.generateFromFile";
    }

}
