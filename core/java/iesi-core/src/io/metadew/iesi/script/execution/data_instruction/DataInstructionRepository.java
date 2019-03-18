package io.metadew.iesi.script.execution.data_instruction;

import java.util.HashMap;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.script.execution.data_instruction.belgium.BelgiumNationalRegisterNumber;
import io.metadew.iesi.script.execution.data_instruction.date.DateBetween;
import io.metadew.iesi.script.execution.data_instruction.date.DateFormat;
import io.metadew.iesi.script.execution.data_instruction.date.DateToday;
import io.metadew.iesi.script.execution.data_instruction.date.DateTravel;
import io.metadew.iesi.script.execution.data_instruction.person.PersonEmail;
import io.metadew.iesi.script.execution.data_instruction.person.PersonFirstName;
import io.metadew.iesi.script.execution.data_instruction.person.PersonLastName;
import io.metadew.iesi.script.execution.data_instruction.person.PersonPhoneNumber;
import io.metadew.iesi.script.execution.data_instruction.time.TimeFormat;
import io.metadew.iesi.script.execution.data_instruction.time.TimeNow;
import io.metadew.iesi.script.execution.data_instruction.time.TimeTravel;

public class DataInstructionRepository
{

	public static HashMap<String, DataInstruction> getReposistory(GenerationObjectExecution generationObjectExecution)
	{
		HashMap<String, DataInstruction> dataInstructions = new HashMap<>();

		DateBetween dateBetween = new DateBetween(generationObjectExecution);
		dataInstructions.put(dateBetween.getKeyword(), dateBetween);
		DateFormat dateFormat = new DateFormat();
		dataInstructions.put(dateFormat.getKeyword(), dateFormat);
		DateToday dateToday = new DateToday(generationObjectExecution);
		dataInstructions.put(dateToday.getKeyword(), dateToday);
		DateTravel dateTravel = new DateTravel();
		dataInstructions.put(dateTravel.getKeyword(), dateTravel);

		TimeFormat timeFormat = new TimeFormat();
		dataInstructions.put(timeFormat.getKeyword(), timeFormat);
		TimeNow timeNow = new TimeNow(generationObjectExecution);
		dataInstructions.put(timeNow.getKeyword(), timeNow);
		TimeTravel timeTravel = new TimeTravel();
		dataInstructions.put(timeTravel.getKeyword(), timeTravel);

		PersonEmail personEmail = new PersonEmail(generationObjectExecution);
		dataInstructions.put(personEmail.getKeyword(), personEmail);
		PersonFirstName personFirstName = new PersonFirstName(generationObjectExecution);
		dataInstructions.put(personFirstName.getKeyword(), personFirstName);
		PersonLastName personLastName = new PersonLastName(generationObjectExecution);
		dataInstructions.put(personLastName.getKeyword(), personLastName);
		PersonPhoneNumber personPhoneNumber = new PersonPhoneNumber(generationObjectExecution);
		dataInstructions.put(personPhoneNumber.getKeyword(), personPhoneNumber);

		BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
		dataInstructions.put(belgiumNationalRegisterNumber.getKeyword(), belgiumNationalRegisterNumber);

		return dataInstructions;
	}

}
