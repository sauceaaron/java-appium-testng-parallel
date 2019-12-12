import kong.unirest.HttpResponse;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class TestObjectAPI
{
	public String username;
	public String apiKey;

	public TestObjectAPI(String username, String apiKey)
	{
		this.username = username;
		this.apiKey = apiKey;
	}

	public void updateTestStatus(String sessionId, Boolean passed)
	{
		try
		{
			URL url = Endpoints.UpdateTestStatusURL(sessionId);
			JSONObject body = new JSONObject().put("passed", passed);

			RequestBodyEntity updateStatusRequest = Unirest.put(url.toString())
					.body(body.toString())
					.header("Content-type", "application/json")
					.basicAuth(username, apiKey);

			HttpResponse<String> response = updateStatusRequest.asString();

			if (response.getStatus() != 204)
			{
				throw new TestStatusException("Failed to update test status.");
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

	public static class Endpoints
	{
		public static final String URL_BASE = "https://app.testobject.com/api/rest";
		public static final String UPDATE_TEST_STATUS = "/v2/appium/session/{sessionId}/test";

		public static URL UpdateTestStatusURL(String sessionId) throws MalformedURLException
		{
			return new URL(URL_BASE + UPDATE_TEST_STATUS.replace("{sessionId}", sessionId));
		}
	}
}