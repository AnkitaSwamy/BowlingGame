package bowling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPlayException extends RuntimeException {

	public InvalidPlayException(String message) {
		super(message);
	}
}
