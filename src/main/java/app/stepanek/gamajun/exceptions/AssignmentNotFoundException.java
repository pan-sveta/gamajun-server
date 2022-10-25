package app.stepanek.gamajun.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AssignmentNotFoundException extends RuntimeException{
    public AssignmentNotFoundException(String message) {
        super(message);
    }
}
