package io.metadew.iesi.test.unit;

public class UnitTest_1 {
	
	public static void main(String[] args) {
		String inputArgs[] = new String[6];
		inputArgs[0] = ("-script");
		inputArgs[1] = ("ut-1");
		inputArgs[2] = ("-env");
		inputArgs[3] = ("dev");
		inputArgs[4] = ("-impersonation");
		inputArgs[5] = ("ut-ds-1");
		io.metadew.iesi.launch.ScriptLauncher.main(inputArgs);
	}
}