package io.metadew.iesi.metadata.service.user;

public enum IESIPrivilege {
    // Scripts
    SCRIPTS_MODIFY("SCRIPTS_WRITE"),
    SCRIPTS_READ("SCRIPTS_READ"),
    //SCRIPTS_DELETE("SCRIPTS_DELETE"),
    // Components
    COMPONENTS_MODIFY("COMPONENTS_WRITE"),
    COMPONENTS_READ("COMPONENTS_READ"),
    //COMPONENTS_DELETE("COMPONENTS_DELETE"),
    // Connections
    CONNECTIONS_MODIFY("CONNECTIONS_WRITE"),
    CONNECTIONS_READ("CONNECTIONS_READ"),
    //CONNECTIONS_DELETE("CONNECTIONS_DELETE"),
    // Environments
    ENVIRONMENTS_MODIFY("ENVIRONMENTS_WRITE"),
    ENVIRONMENTS_READ("ENVIRONMENTS_READ"),
    //ENVIRONMENTS_DELETE("ENVIRONMENTS_DELETE"),
    // Execution Requests
    EXECUTION_REQUESTS_MODIFY("EXECUTION_REQUESTS_WRITE"),
    EXECUTION_REQUESTS_READ("EXECUTION_REQUESTS_READ"),
    //EXECUTION_REQUESTS_DELETE("EXECUTION_REQUESTS_DELETE"),
    // Script Executions
    SCRIPT_EXECUTIONS_MODIFY("SCRIPT_EXECUTIONS_WRITE"),
    SCRIPT_EXECUTIONS_READ("SCRIPT_EXECUTIONS_READ"),
    //SCRIPT_EXECUTIONS_DELETE("SCRIPT_EXECUTIONS_DELETE"),
    // IMPERSONATIONS
    IMPERSONATIONS_READ("IMPERSONATIONS_READ"),
    IMPERSONATIONS_MODIFY("IMPERSONATIONS_WRITE"),
    //IMPERSONATIONS_DELETE("IMPERSONATIONS_DELETE"),
    // SCRIPT_RESULTS
    SCRIPT_RESULTS_READ("SCRIPT_RESULTS_READ"),
    // USERS
    USERS_WRITE("USERS_WRITE"),
    USERS_READ("USERS_READ"),
    USERS_DELETE("USERS_DELETE"),
    // TEAMS
    TEAMS_MODIFY("TEAMS_WRITE"),
    TEAMS_READ("TEAMS_READ"),
    //TEAMS_DELETE("TEAMS_DELETE"),
    // ROLES
    ROLES_MODIFY("ROLES_WRITE"),
    //ROLES_DELETE("ROLES_DELETE"),
    // SECURITY_GROUP
    SECURITY_GROUP_MODIFY("GROUPS_WRITE"),
    SECURITY_GROUP_READ("GROUPS_READ"),
    //SECURITY_GROUP_DELETE("GROUPS_DELETE"),
    // DATASETS
    DATASET_READ("DATASETS_READ"),
    DATASET_MODIFY("DATASETS_WRITE"),
    // TEMPLATES
    TEMPLATES_READ("TEMPLATES_READ"),
    TEMPLATES_MODIFY("TEMPLATES_WRITE");


    private final String privilege;

    IESIPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getPrivilege() {
        return privilege;
    }
}
