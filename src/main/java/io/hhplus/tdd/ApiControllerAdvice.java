package io.hhplus.tdd;

import io.hhplus.tdd.point.service.exception.InsufficientPointException;
import io.hhplus.tdd.point.service.exception.MaxBalanceExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = MaxBalanceExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxBalanceExceededException(Exception e) {
        return ResponseEntity.status(400).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(value = InsufficientPointException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPointException(Exception e) {
        return ResponseEntity.status(400).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }
}
