public class TestStatusException extends RuntimeException
{
	public TestStatusException(String message)
	{
		super(message);
	}

	public void printMessage()
	{
		System.err.println(getMessage());
	}
}

