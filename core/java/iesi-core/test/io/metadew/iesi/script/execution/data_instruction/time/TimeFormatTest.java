package io.metadew.iesi.script.execution.data_instruction.time;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TimeFormatTest
{
	// yyyy-MM-dd HH:mm:ss.SSS

	@Test
	public void generateOutputHHmmssSSS()
	{
		TimeFormat timeFormat = new TimeFormat();
		assertEquals("2000-05-02 12:12:12.121", timeFormat.generateOutput("2000-05-02 12:12:12.121, \"yyyy-MM-dd HH:mm:ss.SSS\""));
	}

	@Test
	public void generateOutputHHmmss()
	{
		TimeFormat timeFormat = new TimeFormat();
		assertEquals("05-02-2000 12:12:12", timeFormat.generateOutput("2000-05-02 12:12:12.121, \"MM-dd-yyyy HH:mm:ss\""));
	}

	@Test
	public void generateOutputSlash()
	{
		TimeFormat timeFormat = new TimeFormat();
		assertEquals("05/02/2000 12:12:12", timeFormat.generateOutput("2000-05-02 12:12:12.121, \"MM/dd/yyyy HH:mm:ss\""));
	}

	@Test
	public void generateOutputText()
	{
		TimeFormat timeFormat = new TimeFormat();
		assertEquals("05 of 02 of 2000 12:12:12",
					timeFormat.generateOutput("2000-05-02 12:12:12.121, \"MM 'of' dd 'of' yyyy HH:mm:ss\""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputParseError()
	{
		TimeFormat timeFormat = new TimeFormat();
		assertEquals("01052000", timeFormat.generateOutput("abc, \"MM-dd-yyyy HH:mm:ss\""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputInputError()
	{
		TimeFormat timeFormat = new TimeFormat();
		assertEquals("01052000", timeFormat.generateOutput("01052000, \"xxyyzzzz\""));
	}
}
