package com.example.deal_service.service;

import com.example.deal_service.model.ContractorRole;
import com.example.deal_service.model.ContractorRoleRequest;
import com.example.deal_service.model.ContractorToRole;
import com.example.deal_service.model.DealContractor;
import com.example.deal_service.model.dto.ContractorRoleDto;
import com.example.deal_service.model.mapper.ContractorRoleMapper;
import com.example.deal_service.repository.ContractorRoleRepository;
import com.example.deal_service.repository.ContractorToRoleRepository;
import com.example.deal_service.repository.DealContractorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
                .orElseThrow(() -> new EntityNotFoundException("DealContractor with id " + request.getDealContractorId() + " not found or is inactive."));

        ContractorRole contractorRole = contractorRoleRepository.findByIdAndIsActiveTrue(request.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("ContractorRole with id " + request.getRoleId() + " not found or is inactive."));

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
                link.setIsActive(true);;
            }
        } else {
            // Создаем новую связь
            link = new ContractorToRole();
            link.setContractor(dealContractor);
            link.setRole(contractorRole);
            link.setIsActive(true);

        }
        contractorToRoleRepository.save(link);
        return ContractorRoleMapper.toDto(link.getRole());
    }

    @Override
    @Transactional
    public void deleteRoleFromContractor(ContractorRoleRequest request) {
        DealContractor dealContractor = dealContractorRepository.findByIdAndIsActiveTrue(request.getDealContractorId())
                .orElseThrow(() -> new EntityNotFoundException("DealContractor c id " + request.getDealContractorId() + " не найден или неактивен."));

        ContractorRole contractorRole = contractorRoleRepository.findByIdAndIsActiveTrue(request.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("ContractorRole с id " + request.getRoleId() + " не найдена ли неактивна."));

        ContractorToRole link = contractorToRoleRepository.findByContractorIdAndRoleId(dealContractor.getId(), contractorRole.getId())
                .orElseThrow(() -> new EntityNotFoundException("Связь ContractorRole между контрагентом " + request.getDealContractorId() + " и ролью " + request.getRoleId() + " не найдена."));

        if (link.getIsActive()) {
            link.setIsActive(false);
            contractorToRoleRepository.save(link);
        }
    }
}
