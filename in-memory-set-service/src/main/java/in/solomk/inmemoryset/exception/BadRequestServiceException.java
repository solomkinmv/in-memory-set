package in.solomk.inmemoryset.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestServiceException extends BaseServiceException {
    public BadRequestServiceException(Throwable cause, String messageTemplate, Object... args) {
        super(cause, messageTemplate, args);
    }
}
