package io.metadew.iesi.script.execution.data_instruction.date;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DateFormatTest
{

	@Test
	public void generateOutputddMMyyyy()
	{
		DateFormat dateFormatddMMyyyy = new DateFormat();
		assertEquals("01052000", dateFormatddMMyyyy.generateOutput("01052000, \"ddMMyyyy\""));
	}

	@Test
	public void generateOutputHyphen()
	{
		DateFormat dateFormatddMMyyyy = new DateFormat();
		assertEquals("01-05-2000", dateFormatddMMyyyy.generateOutput("01052000, \"dd-MM-yyyy\""));
	}

	@Test
	public void generateOutputSlash()
	{
		DateFormat dateFormatddMMyyyy = new DateFormat();
		assertEquals("01/05/2000", dateFormatddMMyyyy.generateOutput("01052000, \"dd/MM/yyyy\""));
	}

	@Test
	public void generateOutputText()
	{
		DateFormat dateFormatddMMyyyy = new DateFormat();
		assertEquals("01 of 05 of 2000", dateFormatddMMyyyy.generateOutput("01052000, \"dd 'of' MM 'of' yyyy\""));
	}

	@Test
	public void generateOutputMMddyyyy()
	{
		DateFormat dateFormatddMMyyyy = new DateFormat();
		assertEquals("05012000", dateFormatddMMyyyy.generateOutput("01052000, \"MMddyyyy\""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputParseError()
	{
		DateFormat dateFormatddMMyyyy = new DateFormat();
		assertEquals("01052000", dateFormatddMMyyyy.generateOutput("01052000 11:11:11, \"xxyyzzzz\""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputInputError()
	{
		DateFormat dateFormatddMMyyyy = new DateFormat();
		assertEquals("01052000", dateFormatddMMyyyy.generateOutput("01052000, \"xxyyzzzz\""));
	}
}
