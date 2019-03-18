package io.metadew.iesi.framework.configuration;

public class FrameworkObjectConfiguration {

	public static String getFrameworkObjectType(Object object) {
		String type = "";
		
		if (object.getClass().getSimpleName().equals("Script")) type = "script";
		else if (object.getClass().getSimpleName().equals("ScriptParameter")) type = "scriptParameter";
		else if (object.getClass().getSimpleName().equals("ScriptType")) type = "scriptType";
		else if (object.getClass().getSimpleName().equals("ScriptTypeParameter")) type = "scriptTypeParameter";
		else if (object.getClass().getSimpleName().equals("Connection")) type = "connection";
		else if (object.getClass().getSimpleName().equals("ConnectionParameter")) type = "connectionParameter";
		else if (object.getClass().getSimpleName().equals("ConnectionType")) type = "connectionType";
		else if (object.getClass().getSimpleName().equals("ConnectionTypeParameter")) type = "connectionTypeParameter";
		else if (object.getClass().getSimpleName().equals("DataObject")) type = "data";
		else if (object.getClass().getSimpleName().equals("Environment")) type = "environment";
		else if (object.getClass().getSimpleName().equals("EnvironmentParameter")) type = "environmentParameter";
		else if (object.getClass().getSimpleName().equals("ErrorObject")) type = "error";
		else if (object.getClass().getSimpleName().equals("ListObject")) type = "list";
		else if (object.getClass().getSimpleName().equals("MetadataField")) type = "metadataField";
		else if (object.getClass().getSimpleName().equals("MetadataTable")) type = "metadataTable";
		else if (object.getClass().getSimpleName().equals("ResultObject")) type = "result";
		else if (object.getClass().getSimpleName().equals("RuntimeVariable")) type = "runtimeVariable";
		else if (object.getClass().getSimpleName().equals("Action")) type = "action";
		else if (object.getClass().getSimpleName().equals("ActionParameter")) type = "actionParameter";
		else if (object.getClass().getSimpleName().equals("ActionType")) type = "actionType";
		else if (object.getClass().getSimpleName().equals("ActionTypeParameter")) type = "actionTypeParameter";
		else if (object.getClass().getSimpleName().equals("Subroutine")) type = "subroutine";
		else if (object.getClass().getSimpleName().equals("SubroutineParameter")) type = "subroutineParameter";
		else if (object.getClass().getSimpleName().equals("SubroutineType")) type = "subroutineType";
		else if (object.getClass().getSimpleName().equals("SubroutineTypeParameter")) type = "subroutineTypeParameter";
		else type = "unknown";
		return type;
	}

}