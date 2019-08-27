package io.metadew.iesi.test.launch;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilderException;
import org.apache.commons.cli.ParseException;

import java.sql.SQLException;
import java.util.List;

public final class Launcher {

	
	public static void execute(String launcher, List<LaunchArgument> inputArgs) throws ExecutionRequestBuilderException, ScriptExecutionRequestBuilderException, MetadataAlreadyExistsException, SQLException, MetadataDoesNotExistException, ParseException {
		
		int inputArgsArraySize = 0;
		for (LaunchArgument launchArgument : inputArgs) {
			if (launchArgument.isKeyvalue()) {
				inputArgsArraySize = inputArgsArraySize + 2;
			} else {
				inputArgsArraySize++;
			}
		}

		String[] inputArgsArray = new String[inputArgsArraySize];
		int k = 0;
		int i = 0;
		while (i < inputArgsArraySize) {
			LaunchArgument launchArgument = inputArgs.get(k);
			if (launchArgument.isKeyvalue()) {
				inputArgsArray[i] = launchArgument.getKey();
				inputArgsArray[i+1] = launchArgument.getValue();
				i = i + 2;
			} else {
				inputArgsArray[i] = launchArgument.getKey();
				i++;
			}
			k++;
		}

		switch (launcher) {
		case "metadata":
			io.metadew.iesi.launch.MetadataLauncher.main(inputArgsArray);
			break;
		case "script":
			io.metadew.iesi.launch.ScriptLauncher.main(inputArgsArray);
			break;
		default:
			break;
		}

	}
	
}