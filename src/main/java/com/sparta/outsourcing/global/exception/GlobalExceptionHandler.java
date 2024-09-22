package com.sparta.outsourcing.global.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 예외 발생 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());

        // 클라이언트에게는 예외 메시지를 그대로 전달
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // EntityNotFoundException 처리
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 리소스를 찾을 수 없음 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // IllegalStateException 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 상태 오류 발생 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // AccessDeniedException 처리
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 권한 없음 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}
