package io.metadew.iesi.script.execution.data_instruction.time;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TimeTravelTest
{

	@Test
	public void generateOutputHour()
	{
		TimeTravel TimeTravel = new TimeTravel();
		assertEquals("2000-05-02 22:12:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"hour\", 10"));
	}

	@Test
	public void generateOutputHourOverflow()
	{
		TimeTravel TimeTravel = new TimeTravel();
		assertEquals("2000-05-03 12:12:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"hour\", 24"));
	}

	@Test
	public void generateOutputMinute()
	{
		TimeTravel TimeTravel = new TimeTravel();
		assertEquals("2000-05-02 12:22:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"minute\", 10"));
	}

	@Test
	public void generateOutputMinuteOverflow()
	{
		TimeTravel TimeTravel = new TimeTravel();
		assertEquals("2000-05-02 13:12:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"minute\", 60"));
	}

	@Test
	public void generateOutputSecond()
	{
		TimeTravel TimeTravel = new TimeTravel();
		assertEquals("2000-05-02 12:12:22.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"second\", 10"));
	}

	@Test
	public void generateOutputSecondOverflow()
	{
		TimeTravel TimeTravel = new TimeTravel();
		assertEquals("2000-05-02 12:13:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"second\", 60"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputInputErrorTimeUnit()
	{
		TimeTravel TimeTravel = new TimeTravel();
		assertEquals("2000-05-02 22:12:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"test\", 10"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputInputErrorStartTime()
	{
		TimeTravel TimeTravel = new TimeTravel();
		assertEquals("2000-05-02 22:12:12.121", TimeTravel.generateOutput("05-02-2000 12:12:12.121, \"hour\", 10"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputInputErrorTimeQuantity()
	{
		TimeTravel TimeTravel = new TimeTravel();
		assertEquals("2000-05-02 22:12:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"hour\", a"));
	}
}
