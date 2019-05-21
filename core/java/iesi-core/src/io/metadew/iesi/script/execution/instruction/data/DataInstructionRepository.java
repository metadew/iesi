package io.metadew.iesi.script.execution.instruction.data;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.script.execution.instruction.data.belgium.BelgiumNationalRegisterNumber;
import io.metadew.iesi.script.execution.instruction.data.date.DateBetween;
import io.metadew.iesi.script.execution.instruction.data.date.DateFormat;
import io.metadew.iesi.script.execution.instruction.data.date.DateToday;
import io.metadew.iesi.script.execution.instruction.data.date.DateTravel;
import io.metadew.iesi.script.execution.instruction.data.number.NumberBetween;
import io.metadew.iesi.script.execution.instruction.data.person.PersonEmail;
import io.metadew.iesi.script.execution.instruction.data.person.PersonFirstName;
import io.metadew.iesi.script.execution.instruction.data.person.PersonLastName;
import io.metadew.iesi.script.execution.instruction.data.person.PersonPhoneNumber;
import io.metadew.iesi.script.execution.instruction.data.time.TimeFormat;
import io.metadew.iesi.script.execution.instruction.data.time.TimeNow;
import io.metadew.iesi.script.execution.instruction.data.time.TimeTravel;

import java.util.HashMap;

public class DataInstructionRepository {

    public static HashMap<String, DataInstruction> getRepository(GenerationObjectExecution generationObjectExecution) {
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

        NumberBetween numberBetween = new NumberBetween(generationObjectExecution);
        dataInstructions.put(numberBetween.getKeyword(), numberBetween);

        return dataInstructions;
    }

}
