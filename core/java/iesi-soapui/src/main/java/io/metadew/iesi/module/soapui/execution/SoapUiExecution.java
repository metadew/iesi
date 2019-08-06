package io.metadew.iesi.module.soapui.execution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.StandaloneSoapUICore;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestSuite;

public class SoapUiExecution {

	private String project;
	private String testSuite;
	private String testCase;
	private String output;

	public SoapUiExecution(String project, String testSuite, String testCase, String output) {
		this.setProject(project);
		this.setTestSuite(testSuite);
		this.setTestCase(testCase);
		this.setOutput(output);
	}

	public void prepare() {

	}

	public void execute() {
		try {
			String suiteName = "";
			String reportStr = "";

			// variables for getting duration
			long startTime = 0;
			long duration = 0;

			List<TestSuite> suiteList = new ArrayList<TestSuite>();
			List<TestCase> caseList = new ArrayList<TestCase>();

			SoapUI.setSoapUICore(new StandaloneSoapUICore(true));

			// specified soapUI project
			WsdlProject wsdlProject = new WsdlProject(this.getProject());

			// Define test suite scope
			if (this.getTestSuite().isEmpty()) {
				suiteList = wsdlProject.getTestSuiteList();
			} else {
				suiteList.add(wsdlProject.getTestSuiteByName(this.getTestSuite()));
			}

			// TODO add support for selecting test cases

			// Loop test suites
			for (int i = 0; i < suiteList.size(); i++) {

				// get name of the "i" element in the list of a test suites
				suiteName = suiteList.get(i).getName();
				reportStr = reportStr + "\nTest Suite: " + suiteName;

				// get a list of all test cases on the "i"-test suite
				caseList = suiteList.get(i).getTestCaseList();

				for (int k = 0; k < caseList.size(); k++) {

					startTime = System.currentTimeMillis();

					// run "k"-test case in the "i"-test suite
					TestCaseRunner testCaseRunner = wsdlProject.getTestSuiteByName(suiteName)
							.getTestCaseByName(caseList.get(k).getName()).run(new PropertiesMap(), false);

					duration = System.currentTimeMillis() - startTime;

					reportStr = reportStr + "\n\tTestCase: " + caseList.get(k).getName() + "\tStatus: "
							+ testCaseRunner.getStatus() + "\tReason: " + testCaseRunner.getReason() + "\tDuration: "
							+ testCaseRunner.getTimeTaken() + "\tCalculated Duration: "
									+ duration;;

					int l = 1;
					for (TestStepResult tsr : testCaseRunner.getResults()) {
						String request = ((MessageExchange) tsr).getRequestContent();
						this.writeToFile("request." + k + "." + l + ".out", request);
						String response = ((MessageExchange) tsr).getResponseContent();
						this.writeToFile("response." + k + "." + l + ".out", response);
						l++;
					}
				}

			}

			// string of the results
			//System.out.println(reportStr);
			this.writeToFile("project.out", reportStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeToFile(String fileName, String content) {
		try (FileWriter fileWriter = new FileWriter(this.getOutput() + File.separator + fileName);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

			bufferedWriter.write(content);

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	}

	// Getters and setters
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTestSuite() {
		return testSuite;
	}

	public void setTestSuite(String testSuite) {
		this.testSuite = testSuite;
	}

	public String getTestCase() {
		return testCase;
	}

	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}