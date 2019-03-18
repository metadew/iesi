package io.metadew.iesi.script.execution.data_instruction.date;

import java.time.format.DateTimeFormatter;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.script.execution.data_instruction.DataInstruction;

/**
 * @author robbe.berrevoets
 */
public class DateToday implements DataInstruction
{
	private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

	private final GenerationObjectExecution generationObjectExecution;

	public DateToday(GenerationObjectExecution generationObjectExecution)
	{
		this.generationObjectExecution = generationObjectExecution;
	}

	/**
	 * @return the current date in format ddMMyyyy
	 */
	@Override
	public String generateOutput(String parameters)
	{
		return generationObjectExecution.getDate().today().format(DATE_FORMAT);
	}

	@Override
	public String getKeyword()
	{
		return "date.today";
	}
}
