package io.metadew.iesi.script.execution.data_instruction.date;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DateTravelTest
{

	@Test
	public void generateOutputYear()
	{
		DateTravel dateTravel = new DateTravel();
		assertEquals("01052010", dateTravel.generateOutput("01052000, \"year\", 10"));
	}

	@Test
	public void generateOutputMonth()
	{
		DateTravel dateTravel = new DateTravel();
		assertEquals("01112000", dateTravel.generateOutput("01012000, \"month\", 10"));
	}

	@Test
	public void generateOutputMonthOverflow()
	{
		DateTravel dateTravel = new DateTravel();
		assertEquals("01012001", dateTravel.generateOutput("01012000, \"month\", 12"));
	}

	@Test
	public void generateOutputDay()
	{
		DateTravel dateTravel = new DateTravel();
		assertEquals("11052000", dateTravel.generateOutput("01052000, \"day\", 10"));
	}

	@Test
	public void generateOutputDayOverflow()
	{
		DateTravel dateTravel = new DateTravel();
		assertEquals("01062000", dateTravel.generateOutput("01052000, \"day\", 31"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputParseError()
	{
		DateTravel dateTravel = new DateTravel();
		assertEquals("01052000", dateTravel.generateOutput("xxyyzzzz, \"day\", 31"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputInputError()
	{
		DateTravel dateTravel = new DateTravel();
		assertEquals("01052000 11:11:11", dateTravel.generateOutput("test, test, test"));
	}
}
