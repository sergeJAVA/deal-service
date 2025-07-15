package com.internship.deal_service.service;

import com.internship.deal_service.exception.DealContractorException;
import com.internship.deal_service.model.dto.DealContractorRequest;
import com.internship.deal_service.model.dto.DealContractorDto;

import java.util.UUID;

public interface DealContractorService {

    /**
     * {@inheritDoc}
     * <p>
     * Метод создает нового контрагента или обновляет существующего, если в запросе
     * передан {@code id}. При установке флага {@code main = true}, метод обеспечивает,
     * что только один контрагент в сделке будет главным.
     * </p>
     * @throws DealContractorException если сделка не найдена, или при попытке обновления
     * не найден контрагент с указанным ID.
     */
    DealContractorDto saveDealContractor(DealContractorRequest request);

    DealContractorDto saveDealContractorWithUserId(DealContractorRequest request, String userId);

    /**
     * {@inheritDoc}
     * <p>
     * Выполняет логическое удаление контрагента из сделки, устанавливая его флаг
     * {@code isActive} в {@code false}.
     * </p>
     * @throws DealContractorException если контрагент с указанным ID не найден.
     */
    void deleteDealContractor(UUID id);

}
