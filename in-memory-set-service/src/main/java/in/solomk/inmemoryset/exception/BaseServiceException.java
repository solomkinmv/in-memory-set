package in.solomk.inmemoryset.exception;

public abstract class BaseServiceException extends RuntimeException {

    public BaseServiceException(String messageTemplate, Object... args) {
        super(messageTemplate.formatted(args));
    }

    public BaseServiceException(Throwable cause, String messageTemplate, Object... args) {
        super(messageTemplate.formatted(args), cause);
    }
}
