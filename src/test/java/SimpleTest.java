import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class SimpleTest
{
	AppiumDriver<WebElement> appium;

	String TESTOBJECT_SERVER_US = "https://us1.appium.testobject.com/wd/hub";
	String TESTOBJECT_USERNAME = System.getenv("TESTOBJECT_USERNAME");
	String TESTOBJECT_API_KEY = System.getenv("TESTOBJECT_API_KEY");

	@BeforeMethod
	public void setup(Method method) throws MalformedURLException
	{
		URL url = new URL(TESTOBJECT_SERVER_US);

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("platformName", "iOS");
		capabilities.setCapability("browserName", "Safari");
		capabilities.setCapability("testobject_api_key", TESTOBJECT_API_KEY);
		capabilities.setCapability("testobject_test_name", method.getName());
		capabilities.setCapability("testobject_suite_name", this.getClass().getSimpleName());

		appium = new IOSDriver<WebElement>(url, capabilities);
	}

	@Test
	public void openPage()
	{
		appium.get("https://saucelabs.com");
		System.out.println(appium.getTitle());
	}

	@AfterMethod
	public void teardown(ITestResult result) throws InterruptedException
	{
		String sessionId = appium.getSessionId().toString();
		appium.quit();

		Thread.sleep(10000);

		TestObjectAPI api = new TestObjectAPI(TESTOBJECT_USERNAME, TESTOBJECT_API_KEY);
		api.updateTestStatus(sessionId, result.isSuccess());
	}
}
