package it.gulch.linuxday.android.exceptions;

/**
 * Created by paolo on 14/09/14.
 */
public class ImportException extends LDCAException
{
	public ImportException()
	{
	}

	public ImportException(String detailMessage)
	{
		super(detailMessage);
	}

	public ImportException(String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
	}

	public ImportException(Throwable throwable)
	{
		super(throwable);
	}
}
