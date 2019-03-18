package io.metadew.iesi.script.execution.data_instruction;

public interface DataInstruction
{
	public abstract String getKeyword();

	public abstract String generateOutput(String parameters);
}
