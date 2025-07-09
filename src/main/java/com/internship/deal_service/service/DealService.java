package com.internship.deal_service.service;

import com.internship.deal_service.exception.DealException;
import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealRequest;
import com.internship.deal_service.model.DealSum;
import com.internship.deal_service.model.dto.DealSearchRequest;
import com.internship.deal_service.model.dto.DealStatusUpdateRequest;
import com.internship.deal_service.model.dto.DealDto;
import com.internship.deal_service.model.Pagination;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface DealService {

    /**
     * {@inheritDoc}
     * Поиск сущности {@link Deal} в БД по передаваемому ID
     * @throws EntityNotFoundException если сделка с указанным ID не найдена или неактивна.
     */
    DealDto getDealById(UUID id);

    /**
     * {@inheritDoc}
     * <p>
     * Если в запросе {@code request} передан ID, метод обновляет существующую сделку.
     * В противном случае создается новая сделка со статусом "DRAFT".
     * Метод также обрабатывает связанные суммы ({@link DealSum}), гарантируя,
     * что только одна из них может быть основной ({@code isMain = true}).
     * </p>
     * @throws EntityNotFoundException если связанные сущности (тип, статус, валюта) не найдены,
     * или при попытке обновления не найдена сама сделка.
     */
    DealDto saveDeal(DealRequest request);

    /**
     * {@inheritDoc}
     * Метод для изменения статуса сделки
     * <p>
     * При изменении статуса на "CLOSED", метод также автоматически устанавливает
     * дату закрытия сделки ({@code closeDt}) на текущее время.
     * </p>
     * @throws DealException если сделка или новый статус не найдены или неактивны.
     */
    DealDto changeDealStatus(UUID dealId, DealStatusUpdateRequest request);

    /**
     * {@inheritDoc}
     * Поиск сделки по фильтрации.
     * <p>
     * Использует {@link Specification} для построения динамического запроса
     * на основе предоставленных фильтров.
     * </p>
     */
    Page<DealDto> searchDeals(DealSearchRequest request, Pagination pagination);

    /**
     * {@inheritDoc}
     * Поиск сделки по фильтрации для создания XLSX файла.
     */
    byte[] exportDealsToExcel(DealSearchRequest searchRequest, Pagination pagination);

}
