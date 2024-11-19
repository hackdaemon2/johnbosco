package com.interswittch.johnbosco.common.exception;

import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleEnumException(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        return switch (Objects.requireNonNull(requiredType).getSimpleName()) {
            case "SortOrder", "LoanStatus", "TransactionType" ->
                    ResponseEntity.badRequest().body(formulateErrorResponse("Invalid value: %s".formatted(ex.getValue())));
            case "LocalDateTime" -> {
                String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
                String message = "Invalid date-time format: %s. Please use 'yyyy-MM-ddTHH:mm:ss' format.".formatted(invalidValue);
                yield ResponseEntity.badRequest().body(formulateErrorResponse(message));
            }
            default -> ResponseEntity.badRequest().body(formulateErrorResponse("Invalid request parameter."));
        };
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleNoHandlerFoundException(NoHandlerFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(formulateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        List<ObjectError> objectErrors = result.getAllErrors();
        String errorMessage = objectErrors.get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(formulateErrorResponse(errorMessage));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception) {
        String supportedMethods = Arrays.toString(Objects.requireNonNull(exception.getSupportedHttpMethods(), "").toArray());
        String reason = String.format("Request method for this resource is not allowed (%s).", supportedMethods);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(formulateErrorResponse(reason));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(formulateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleCustomException(BusinessException exception) {
        return ResponseEntity.internalServerError().body(formulateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleBadRequestException(BadRequestException exception) {
        return ResponseEntity.badRequest().body(formulateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return new ResponseEntity<>(formulateErrorResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleResourceConflictException(ResourceConflictException exception) {
        return new ResponseEntity<>(formulateErrorResponse(exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleDateTimeParseException(DateTimeParseException exception) {
        return ResponseEntity.internalServerError().body(formulateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleNullPointerException(NullPointerException exception) {
        return ResponseEntity.internalServerError().body(formulateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleIllegalStateException(IllegalStateException exception) {
        return ResponseEntity.internalServerError().body(formulateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.internalServerError().body(formulateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleConstraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.internalServerError().body(formulateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handleValidationException(ValidationException exception) {
        return ResponseEntity.internalServerError().body(formulateErrorResponse(exception.getMessage()));
    }

    private static CustomApiResponse<ErrorResponseDto> formulateErrorResponse(String message) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(message);
        return new CustomApiResponse<>(errorResponseDto, true);
    }

}
