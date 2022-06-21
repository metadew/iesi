{% include navigation.html %}
# Configure your script - Using Expected Error

The script design allows you to also indicate whether we expect a failing action and thus expect our script to result in success/failure. Enabling the expected error checkbox per action allows us to define behavior of the framework for negative testing.

-	Positive tests verify if the system is working as expected
-	Negative tests verify what happens if the system or data is not as expected
-	Negative tests are tests that are a success if they fail and are captured correctly

If the checkbox is enabled, then an error in the execution is considered as a successful execution. The result interpretation of the action is reversed. Below are some examples where the expected error flag has been set to Y - checkbox is selected

