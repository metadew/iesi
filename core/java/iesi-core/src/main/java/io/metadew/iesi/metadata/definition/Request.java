package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.util.List;

public class Request {

	private String id;
	private String type;
	private String timestamp;
	private String name;
	private String description;
	private int amount;
	private String email;
	private String scope;
	private String context;
	private String space;
	private String user;
	private String password;
	private List<RequestParameter> parameters;

	// Constructors
	public Request() {
	}

	public Request(String id, String type, String timestamp, String name, String description, int amount, String email,
			String scope, String context, String space, String user, String password, List<RequestParameter> parameters) {
		this.id = id;
		this.type = type;
		this.timestamp = timestamp;
		this.name = name;
		this.description = description;
		this.setAmount(amount);
		this.email = email;
		this.scope = scope;
		this.context = context;
		this.space = space;
		this.user = user;
		this.password = password;
		this.setParameters(parameters);
	}

	public Request(String type, String timestamp, String name, String description, int amount, String email,
			String scope, String context, String space, String user, String password, List<RequestParameter> parameters) {
		this.id = IdentifierTools.getRequestIdentifier();
		this.type = type;
		this.timestamp = timestamp;
		this.name = name;
		this.description = description;
		this.setAmount(amount);
		this.email = email;
		this.scope = scope;
		this.context = context;
		this.space = space;
		this.user = user;
		this.password = password;
		this.setParameters(parameters);
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		if (id == null)
			this.id = IdentifierTools.getScriptIdentifier(this.getName());
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<RequestParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<RequestParameter> parameters) {
		this.parameters = parameters;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}