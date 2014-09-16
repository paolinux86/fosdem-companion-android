package it.gulch.linuxday.android.exceptions;

/**
 * Created by paolo on 14/09/14.
 */
public class LDCAException extends Exception
{
	public LDCAException()
	{
	}

	public LDCAException(String detailMessage)
	{
		super(detailMessage);
	}

	public LDCAException(String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
	}

	public LDCAException(Throwable throwable)
	{
		super(throwable);
	}
}
