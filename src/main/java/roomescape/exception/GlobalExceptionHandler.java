package roomescape.exception;

import java.util.Optional;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.dto.ErrorResponse;

import java.time.DateTimeException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        ErrorResponse data = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exception) {
        ErrorResponse data = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        if (exception.getRootCause() instanceof DateTimeException) {
            return handleDateTimeParseException();
        }

        ErrorResponse data = new ErrorResponse(HttpStatus.BAD_REQUEST, "요청에 잘못된 형식의 값이 있습니다.");
        return ResponseEntity.badRequest().body(data);
    }

    private ResponseEntity<ErrorResponse> handleDateTimeParseException() {
        ErrorResponse data = new ErrorResponse(HttpStatus.BAD_REQUEST, "잘못된 형식의 날짜 혹은 시간입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @ExceptionHandler(NotEnoughPermissionException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughPermissionException(NotEnoughPermissionException exception) {
        ErrorResponse data = new ErrorResponse(HttpStatus.FORBIDDEN, exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(data);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        String message = Optional.ofNullable(exception.getBindingResult().getFieldError())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("요청 형식이 잘못되었습니다.");
        ErrorResponse data = new ErrorResponse(HttpStatus.BAD_REQUEST, message);
        return ResponseEntity.badRequest().body(data);
    }

}
