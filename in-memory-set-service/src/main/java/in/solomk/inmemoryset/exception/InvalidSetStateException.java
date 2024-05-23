package in.solomk.inmemoryset.exception;

public class InvalidSetStateException extends BaseServiceException {
    public InvalidSetStateException(String messageTemplate, Object... args) {
        super(messageTemplate, args);
    }
}
