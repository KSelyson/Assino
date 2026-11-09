package com.assino.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tratamento centralizado de erros da API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErroResponse(int status, String mensagem, LocalDateTime timestamp, Map<String, String> campos) {
    }

    private ResponseEntity<ErroResponse> erro(HttpStatus status, String msg, Map<String, String> campos) {
        return ResponseEntity.status(status).body(new ErroResponse(status.value(), msg, LocalDateTime.now(), campos));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(ResourceNotFoundException e) {
        return erro(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErroResponse> requisicaoInvalida(BadRequestException e) {
        return erro(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tipoInvalido(MethodArgumentTypeMismatchException e) {
        return erro(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + e.getName() + "'", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> campos.put(f.getField(), f.getDefaultMessage()));
        return erro(HttpStatus.BAD_REQUEST, "Erro de validação", campos);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> generico(Exception e) {
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", null);
    }
}
