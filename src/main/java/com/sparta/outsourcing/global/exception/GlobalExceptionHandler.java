package com.sparta.outsourcing.global.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // MethodArgumentNotValidException 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 유효성 검증 오류 발생 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());

        // 첫 번째 발생한 필드 오류만 반환
        FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        String errorMessage = fieldError.getField() + ": " + fieldError.getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("[{}] 예외 발생 - URI: {}, 메시지: {}", LocalDateTime.now(), uri, ex.getMessage());

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

    // MethodArgumentTypeMismatchException 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "잘못된 값이 입력되었습니다: " + ex.getName());
        errorResponse.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
