package ec.fin.be.sigered.exception;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 *
 * @author jorquera
 */
@Getter
@Setter
@SuppressWarnings("squid:S1068")
public class ApiEx {

    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;

    public ApiEx(String message,  HttpStatus httpStatus, ZonedDateTime timestamp) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }

}
