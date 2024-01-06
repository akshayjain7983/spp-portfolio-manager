package spp.portfolio.model.exception;

public class SppException extends RuntimeException
{
    private static final long serialVersionUID = -3491637258442268775L;

    public SppException()
    {
        super();
    }

    public SppException(String message)
    {
        super(message);
    }

    public SppException(Throwable cause)
    {
        super(cause);
    }

    public SppException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SppException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
