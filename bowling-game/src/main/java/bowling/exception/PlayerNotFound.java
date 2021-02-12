package bowling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotFound extends RuntimeException {

	public PlayerNotFound(String message) {
		super(message);
	}

}
