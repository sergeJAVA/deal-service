package com.internship.deal_service.model.dto;

import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Объект для стандартизированного ответа об ошибке в API.
 * <p>
 * Используется в теле {@link ResponseEntity} при возникновении исключений
 * для предоставления клиенту консистентной информации об ошибке.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseException {

    /**
     * Сообщение об ошибке, описывающее, что произошло.
     */
    private String message;

    /**
     * HTTP-статус код, соответствующий ошибке.
     */
    private int code;

}
