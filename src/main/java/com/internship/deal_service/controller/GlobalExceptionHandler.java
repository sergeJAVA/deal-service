package com.internship.deal_service.controller;

import com.internship.deal_service.exception.ContractorRoleException;
import com.internship.deal_service.exception.DealContractorException;
import com.internship.deal_service.exception.DealException;
import com.internship.deal_service.model.dto.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Обработчик кастомных исключений
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContractorRoleException.class)
    public ResponseEntity<ResponseException> handleContractorRoleException(ContractorRoleException ex) {
        log.info(ex.getMessage());
        ResponseException response = new ResponseException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DealContractorException.class)
    public ResponseEntity<ResponseException> handleDealContractorException(DealContractorException ex) {
        log.info(ex.getMessage());
        ResponseException response = new ResponseException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DealException.class)
    public ResponseEntity<ResponseException> handleDealException(DealException ex) {
        log.info(ex.getMessage());
        ResponseException response = new ResponseException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
