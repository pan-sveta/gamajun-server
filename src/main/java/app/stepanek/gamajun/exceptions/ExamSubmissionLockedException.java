package app.stepanek.gamajun.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ExamSubmissionLockedException extends RuntimeException{
    public ExamSubmissionLockedException(String message) {
        super(message);
    }
}
