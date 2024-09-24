package com.sparta.outsourcing.global.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    // IllegalStateException 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 상태 오류 발생 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // EntityNotFoundException 처리
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 리소스를 찾을 수 없음 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // SecurityException 처리
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 권한 없음 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 런타임 예외 발생 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    // 알 수 없는 예외에 대한 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 알 수 없는 오류 발생 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());
        String responseMessage = String.format("예기치 못한 오류가 발생했습니다. URI: %s, 메시지: %s", uri, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "잘못된 값이 입력되었습니다: " + ex.getName());
        errorResponse.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
