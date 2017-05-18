package org.camokatuk.madcomposer.engine;

public class MadEngineFatalException extends RuntimeException
{
	public MadEngineFatalException(String message)
	{
		super(message);
	}

	public MadEngineFatalException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MadEngineFatalException(Throwable cause)
	{
		super(cause);
	}
}
