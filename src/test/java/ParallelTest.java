import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class ParallelTest
{
	ThreadLocal<AppiumDriver<WebElement>> appium = new ThreadLocal<>();
	ThreadLocal<String> sessionId = new ThreadLocal<>();

	String TESTOBJECT_SERVER_US = "https://us1.appium.testobject.com/wd/hub";
	String TESTOBJECT_USERNAME = System.getenv("TESTOBJECT_USERNAME");
	String TESTOBJECT_API_KEY = System.getenv("TESTOBJECT_API_KEY");


	@DataProvider(name = "ios", parallel = true)
	public static Object[][] data1()
	{
		return new Object[][]{
				new Object[]{"iOS", "iPhone XR", "Safari"},
				new Object[]{"iOS", "iPad.*", "Safari"},
		};
	}

	@DataProvider(name = "android", parallel = true)
	public static Object[][] data2()
	{
		return new Object[][]{
				new Object[]{"Android", "Google Pixel 4", "Chrome"},
				new Object[]{"Android", "Samsung Galaxy S10", "Chrome"}
		};
	}

	@Test(dataProvider = "ios")
	public void openPage1(String platformName, String deviceName, String browserName, Method method) throws MalformedURLException
	{
		createThreadSafeAppiumInstance(platformName, deviceName, browserName, method.getName());

		safeAppium().get("https://saucelabs.com");
		System.out.println(safeAppium().getTitle());
	}


	@Test(dataProvider = "android")
	public void openPage2(String platformName, String deviceName, String browserName, Method method) throws MalformedURLException
	{
		createThreadSafeAppiumInstance(platformName, deviceName, browserName, method.getName());

		safeAppium().get("https://saucelabs.com");
		System.out.println(safeAppium().getTitle());
	}


	@AfterMethod
	public void tearDown(ITestResult result) throws InterruptedException
	{
		String sessionId = safeAppium().getSessionId().toString();
		safeAppium().quit();

		Thread.sleep(10000);

		TestObjectAPI api = new TestObjectAPI(TESTOBJECT_USERNAME, TESTOBJECT_API_KEY);
		api.updateTestStatus(sessionId, result.isSuccess());
	}

	public void createThreadSafeAppiumInstance(String platformName, String deviceName, String browserName, String testName) throws MalformedURLException
	{
		URL url = new URL(TESTOBJECT_SERVER_US);

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("platformName", platformName);
		capabilities.setCapability("deviceName", deviceName);
		capabilities.setCapability("browserName", browserName);

		capabilities.setCapability("testobject_api_key", TESTOBJECT_API_KEY);
		capabilities.setCapability("testobject_test_name", testName);
		capabilities.setCapability("testobject_suite_name", this.getClass().getSimpleName());

		appium.set(new AppiumDriver<>(url, capabilities));
	}

	public AppiumDriver<WebElement> safeAppium()
	{
		return appium.get();
	}
}