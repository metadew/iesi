package io.metadew.iesi.script.execution.data_instruction.belgium;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BelgiumNationalRegisterNumberTest
{

	@Test
	public void generateOutputPost2000Male()
	{
		BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
		String belgiumNationalRegisterNumberOutput = belgiumNationalRegisterNumber.generateOutput("01051994, 1");
		assertEquals(11, belgiumNationalRegisterNumberOutput.length());
		assertEquals("940501", belgiumNationalRegisterNumberOutput.subSequence(0, 6));
		assertEquals(1, Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(6, 9).toString()) % 2);
		assertEquals(97 - Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(9, 11).toString()),
					Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(0, 9).toString()) % 97);
	}

	@Test
	public void generateOutputPost2000Female()
	{
		BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
		String belgiumNationalRegisterNumberOutput = belgiumNationalRegisterNumber.generateOutput("01051994, 2");
		assertEquals(11, belgiumNationalRegisterNumberOutput.length());
		assertEquals("940501", belgiumNationalRegisterNumberOutput.subSequence(0, 6));
		assertEquals(0, Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(6, 9).toString()) % 2);
		assertEquals(97 - Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(9, 11).toString()),
					Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(0, 9).toString()) % 97);
	}

	@Test
	public void generateOutputPre2000Male()
	{
		BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
		String belgiumNationalRegisterNumberOutput = belgiumNationalRegisterNumber.generateOutput("01052000, 1");
		assertEquals(11, belgiumNationalRegisterNumberOutput.length());
		assertEquals("000501", belgiumNationalRegisterNumberOutput.subSequence(0, 6));
		assertEquals(1, Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(6, 9).toString()) % 2);
		assertEquals(Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(9, 11).toString()),
					97 - (2 * (int)Math.pow(10, 9) + Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(0, 9).toString()))
								% 97);
	}

	@Test
	public void generateOutputPre2000Female()
	{
		BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
		String belgiumNationalRegisterNumberOutput = belgiumNationalRegisterNumber.generateOutput("01052000, 2");
		assertEquals(11, belgiumNationalRegisterNumberOutput.length());
		assertEquals("000501", belgiumNationalRegisterNumberOutput.subSequence(0, 6));
		assertEquals(0, Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(6, 9).toString()) % 2);
		assertEquals(Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(9, 11).toString()),
					97 - (2 * (int)Math.pow(10, 9) + Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(0, 9).toString()))
								% 97);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputInputErrorSex()
	{
		BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
		assertEquals("", belgiumNationalRegisterNumber.generateOutput("test, a"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputInputErrorUnknownSex()
	{
		BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
		assertEquals("", belgiumNationalRegisterNumber.generateOutput("01052000, 3"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateOutputInputErrorDateformat()
	{
		BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
		assertEquals("", belgiumNationalRegisterNumber.generateOutput("301052000, 3"));
	}
}
