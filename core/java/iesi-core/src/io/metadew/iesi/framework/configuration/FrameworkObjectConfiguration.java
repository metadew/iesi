package io.metadew.iesi.framework.configuration;

public class FrameworkObjectConfiguration {

    public static String getFrameworkObjectType(Object object) {
        String type = "";

        if (object.getClass().getSimpleName().equalsIgnoreCase("Script")) type = "script";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ScriptParameter")) type = "scriptParameter";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ScriptType")) type = "scriptType";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ScriptTypeParameter"))
            type = "scriptTypeParameter";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("Connection")) type = "connection";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ConnectionParameter"))
            type = "connectionParameter";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ConnectionType")) type = "connectionType";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ConnectionTypeParameter"))
            type = "connectionTypeParameter";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("DataObject")) type = "data";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("Environment")) type = "environment";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("EnvironmentParameter"))
            type = "environmentParameter";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ErrorObject")) type = "error";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ListObject")) type = "list";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("MetadataField")) type = "metadataField";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("MetadataTable")) type = "metadataTable";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ResultObject")) type = "result";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("RuntimeVariable")) type = "runtimeVariable";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("Action")) type = "action";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ActionParameter")) type = "actionParameter";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ActionType")) type = "actionType";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("ActionTypeParameter"))
            type = "actionTypeParameter";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("Subroutine")) type = "subroutine";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("SubroutineParameter"))
            type = "subroutineParameter";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("SubroutineType")) type = "subroutineType";
        else if (object.getClass().getSimpleName().equalsIgnoreCase("SubroutineTypeParameter"))
            type = "subroutineTypeParameter";
        else type = "unknown";
        return type;
    }

}