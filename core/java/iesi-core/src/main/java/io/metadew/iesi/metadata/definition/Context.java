package io.metadew.iesi.metadata.definition;

/**
 * Context is a generic object is defined by a name and a scope.
 *
 * @author peter.billen
 */
public class Context {

    private String name;
    private String scope;

    // Constructors
    public Context() {
        this.name = "";
        this.scope = "";
    }

    public Context(String name, String scope) {
        this.name = name;
        this.scope = scope;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }


}