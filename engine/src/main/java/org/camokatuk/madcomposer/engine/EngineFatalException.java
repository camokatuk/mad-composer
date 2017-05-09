package org.camokatuk.madcomposer.engine;

public class EngineFatalException extends RuntimeException
{
    public EngineFatalException(String message)
    {
        super(message);
    }

    public EngineFatalException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EngineFatalException(Throwable cause)
    {
        super(cause);
    }
}
