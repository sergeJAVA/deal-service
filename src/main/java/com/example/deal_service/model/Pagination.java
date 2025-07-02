package com.example.deal_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Служебный класс для пагинации.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {

    /** Номер страницы (начиная с 0). */
    private int page;

    /** Количество элементов на странице. */
    private int size;

}
