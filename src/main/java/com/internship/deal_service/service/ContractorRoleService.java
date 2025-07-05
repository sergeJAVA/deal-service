package com.internship.deal_service.service;

import com.internship.deal_service.exception.ContractorRoleException;
import com.internship.deal_service.model.dto.ContractorRoleRequest;
import com.internship.deal_service.model.dto.ContractorRoleDto;

public interface ContractorRoleService {

    /**
     * {@inheritDoc}
     * <p>
     * Добавляет роль контрагенту. Если связь уже существует, но неактивна,
     * метод активирует её. Если связь уже существует и активна, никаких действий не производится.
     * В противном случае создается новая активная связь.
     * </p>
     * @throws ContractorRoleException если контрагент или роль не найдены или неактивны.
     */
    ContractorRoleDto addRoleToContractor(ContractorRoleRequest request);

    /**
     * {@inheritDoc}
     * <p>
     * Выполняет логическое удаление, деактивируя связь между контрагентом и ролью,
     * устанавливая флаг {@code isActive} в {@code false}. Физического удаления из БД не происходит.
     * </p>
     * @throws ContractorRoleException если контрагент, роль или их связь не найдены.
     */
    void deleteRoleFromContractor(ContractorRoleRequest request);

}
