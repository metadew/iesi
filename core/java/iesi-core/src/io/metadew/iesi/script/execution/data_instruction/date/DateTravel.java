package io.metadew.iesi.script.execution.data_instruction.date;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.metadew.iesi.script.execution.data_instruction.DataInstruction;

/**
 * @author robbe.berrevoets
 */
public class DateTravel implements DataInstruction
{
	private final String ORIGINAL_DATE_KEY = "OriginalDate";

	private final String DATE_TRAVEL_UNIT_KEY = "DateTravelUnit";

	private final String DATE_TRAVEL_QUANTITY_KEY = "DateTravelQuantity";

	private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile("\\s*\"?(?<" + ORIGINAL_DATE_KEY + ">\\d{8})\"?\\s*,\\s*\"(?<"
				+ DATE_TRAVEL_UNIT_KEY + ">\\w*)\"\\s*,\\s*(?<" + DATE_TRAVEL_QUANTITY_KEY + ">\\d+)");

	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@Override
	public String generateOutput(String parameters)
	{
		Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
		if (!inputParameterMatcher.find())
		{
			throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to date format: {0}", parameters));
		}
		else
		{
			try
			{
				Calendar originalCalendar = Calendar.getInstance();
				originalCalendar.setTime(DATE_FORMAT.parse(inputParameterMatcher.group(ORIGINAL_DATE_KEY)));
				int travelUnit = resolveTravelUnit(inputParameterMatcher.group(DATE_TRAVEL_UNIT_KEY));
				int travelQuantity = Integer.parseInt(inputParameterMatcher.group(DATE_TRAVEL_QUANTITY_KEY));
				originalCalendar.add(travelUnit, travelQuantity);
				return DATE_FORMAT.format(originalCalendar.getTime());
			}
			catch (ParseException e)
			{
				throw new IllegalArgumentException(
							MessageFormat.format("Cannot generate Date from {0}", inputParameterMatcher.group(ORIGINAL_DATE_KEY)));
			}

		}
	}

	private int resolveTravelUnit(String representation)
	{
		switch (representation.toLowerCase())
		{
			case "year" :
				return Calendar.YEAR;
			case "month" :
				return Calendar.MONTH;
			case "day" :
				return Calendar.DATE;
			default :
				throw new IllegalArgumentException(MessageFormat.format("Date travel does not work with unit {0}", representation));
		}
	}

	@Override
	public String getKeyword()
	{
		return "date.travel";
	}

}
