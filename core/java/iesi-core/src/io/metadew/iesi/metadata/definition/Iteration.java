package io.metadew.iesi.metadata.definition;

public class Iteration {
	
	private String name;
	private String type;
	private String list;
	private String values;
	private String from;
	private String to;
	private String step;
	private String condition;
	private String interrupt;
	
	//Constructors
	public Iteration() {
		
	}

	//Getters and Setters
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getInterrupt() {
		return interrupt;
	}

	public void setInterrupt(String interrupt) {
		this.interrupt = interrupt;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}