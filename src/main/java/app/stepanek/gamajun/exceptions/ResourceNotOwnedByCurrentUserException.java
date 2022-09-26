package app.stepanek.gamajun.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class ResourceNotOwnedByCurrentUserException extends RuntimeException{
    public ResourceNotOwnedByCurrentUserException(String message) {
        super(message);
    }
}