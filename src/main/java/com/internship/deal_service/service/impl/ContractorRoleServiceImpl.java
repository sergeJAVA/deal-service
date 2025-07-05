package com.internship.deal_service.service.impl;

import com.internship.deal_service.exception.ContractorRoleException;
import com.internship.deal_service.model.ContractorRole;
import com.internship.deal_service.model.dto.ContractorRoleRequest;
import com.internship.deal_service.model.ContractorToRole;
import com.internship.deal_service.model.ContractorToRoleId;
import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.dto.ContractorRoleDto;
import com.internship.deal_service.model.mapper.ContractorRoleMapper;
import com.internship.deal_service.repository.ContractorRoleRepository;
import com.internship.deal_service.repository.ContractorToRoleRepository;
import com.internship.deal_service.repository.DealContractorRepository;
import com.internship.deal_service.service.ContractorRoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Реализация сервиса {@link ContractorRoleService} для управления ролями контрагентов.
 * <p>
 * Предоставляет бизнес-логику для добавления и удаления (логического) ролей
 * у контрагентов в рамках сделки.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ContractorRoleServiceImpl implements ContractorRoleService {

    private final DealContractorRepository dealContractorRepository;
    private final ContractorRoleRepository contractorRoleRepository;
    private final ContractorToRoleRepository contractorToRoleRepository;

    @Override
    @Transactional
    public ContractorRoleDto addRoleToContractor(ContractorRoleRequest request) {
        DealContractor dealContractor = dealContractorRepository.findByIdAndIsActiveTrue(request.getDealContractorId())
                .orElseThrow(() -> new ContractorRoleException("DealContractor c id <<" + request.getDealContractorId() + ">> не найден или неактивен."));

        ContractorRole contractorRole = contractorRoleRepository.findByIdAndIsActiveTrue(request.getRoleId())
                .orElseThrow(() -> new ContractorRoleException("ContractorRole с id <<" + request.getRoleId() + ">> не найден или неактивен."));

        Optional<ContractorToRole> existingLink = contractorToRoleRepository.findByContractorIdAndRoleId(
                dealContractor.getId(), contractorRole.getId());

        ContractorToRole link;
        if (existingLink.isPresent()) {
            link = existingLink.get();
            // Если связь уже существует и активна, ничего не делаем, просто возвращаем DTO
            if (link.getIsActive()) {
                return ContractorRoleMapper.toDto(link.getRole());
            } else {
                // Если связь существует, но неактивна, активируем её
                link.setIsActive(true);
            }
        } else {
            // Создаем новую связь
            ContractorToRoleId contractorToRoleId = new ContractorToRoleId(dealContractor.getId(), contractorRole.getId());
            link = new ContractorToRole();
            link.setContractor(dealContractor);
            link.setRole(contractorRole);
            link.setIsActive(true);
            link.setId(contractorToRoleId);
        }
        contractorToRoleRepository.save(link);
        return ContractorRoleMapper.toDto(link.getRole());
    }

    @Override
    @Transactional
    public void deleteRoleFromContractor(ContractorRoleRequest request) {
        DealContractor dealContractor = dealContractorRepository.findByIdAndIsActiveTrue(request.getDealContractorId())
                .orElseThrow(() -> new ContractorRoleException("DealContractor c id <<" + request.getDealContractorId() + ">> не найден или неактивен."));

        ContractorRole contractorRole = contractorRoleRepository.findByIdAndIsActiveTrue(request.getRoleId())
                .orElseThrow(() -> new ContractorRoleException("ContractorRole с id <<" + request.getRoleId() + ">> не найдена ли неактивна."));

        ContractorToRole link = contractorToRoleRepository.findByContractorIdAndRoleId(dealContractor.getId(), contractorRole.getId())
                .orElseThrow(() -> new ContractorRoleException("Связь ContractorRole между контрагентом <<" + request.getDealContractorId() + ">>" +
                        " и ролью " + request.getRoleId() + " не найдена."));

        if (link.getIsActive()) {
            link.setIsActive(false);
            contractorToRoleRepository.save(link);
        }
    }

}
