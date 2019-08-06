package io.metadew.iesi.util.sql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.StandaloneSoapUICore;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestRunner.Status;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.tools.SoapUITestCaseRunner;

public class SOAPUIIntegration {

	public static void main(String[] args) {

		try {
			getTestSuite();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.exit(0);
		// https://www.soapui.org/test-automation/junit/junit-integration.html#_ga=2.157007227.1117033134.1563254348-690893157.1557407813
		// out-app-analytics-provider-5.5.0.jar causes issues in eclipse on the build
		// path

		SoapUITestCaseRunner runner = new SoapUITestCaseRunner();
		runner.setProjectFile("c:/Data/ApiCase-soapui-project.xml");
		try {
			runner.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WsdlProject project;
		try {
			project = new WsdlProject("c:/Data/ApiCase-soapui-project.xml");
			TestSuite testSuite = project.getTestSuiteByName("https://sandbox.api.belfius.be:8443 TestSuite");
			// TestCase testCase = testSuite.getTestCaseByName( "Test Conversions" );

			// create empty properties and run synchronously
			TestRunner runner2 = testSuite.run(new PropertiesMap(), false);
			System.out.println(runner2.getStatus());
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SoapUIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static void getTestSuite() throws Exception {

		String suiteName = "";
		String reportStr = "";

		// variables for getting duration
		long startTime = 0;
		long duration = 0;

		TestRunner runner = null;

		List<TestSuite> suiteList = new ArrayList<TestSuite>();
		List<TestCase> caseList = new ArrayList<TestCase>();

		SoapUI.setSoapUICore(new StandaloneSoapUICore(true));

		// specified soapUI project
		WsdlProject project = new WsdlProject("c:/Data/ApiCase-soapui-project.xml");

		// get a list of all test suites on the project
		suiteList = project.getTestSuiteList();

		// you can use for each loop
		for (int i = 0; i < suiteList.size(); i++) {

			// get name of the "i" element in the list of a test suites
			suiteName = suiteList.get(i).getName();
			reportStr = reportStr + "\nTest Suite: " + suiteName;

			// get a list of all test cases on the "i"-test suite
			caseList = suiteList.get(i).getTestCaseList();

			for (int k = 0; k < caseList.size(); k++) {

				startTime = System.currentTimeMillis();

				// run "k"-test case in the "i"-test suite
				TestCaseRunner tcr = null;
				// runner =
				// project.getTestSuiteByName(suiteName).getTestCaseByName(caseList.get(k).getName()).run(new
				// PropertiesMap(), false);
				tcr = project.getTestSuiteByName(suiteName).getTestCaseByName(caseList.get(k).getName())
						.run(new PropertiesMap(), false);

				duration = System.currentTimeMillis() - startTime;

				// reportStr = reportStr + "\n\tTestCase: " + caseList.get(k).getName() +
				// "\tStatus: " + runner.getStatus() + "\tReason: " + runner.getReason() +
				// "\tDuration: " + duration;
				reportStr = reportStr + "\n\tTestCase: " + caseList.get(k).getName() + "\tStatus: " + tcr.getStatus()
						+ "\tReason: " + tcr.getReason() + "\tDuration: " + tcr.getTimeTaken();

				for (TestStepResult tsr : tcr.getResults()) {
					String request = ((MessageExchange) tsr).getRequestContent();
					String response = ((MessageExchange) tsr).getResponseContent();
					System.out.println(response);
				}
			}

		}

		// string of the results
		System.out.println(reportStr);
	}
}