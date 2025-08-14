package com.internship.deal_service.service.impl;

import com.internship.deal_service.exception.ContractorRoleException;
import com.internship.deal_service.model.ContractorRole;
import com.internship.deal_service.model.ContractorToRole;
import com.internship.deal_service.model.ContractorToRoleId;
import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.dto.ContractorRoleDto;
import com.internship.deal_service.model.dto.ContractorRoleRequest;
import com.internship.deal_service.model.mapper.ContractorRoleMapper;
import com.internship.deal_service.repository.ContractorRoleRepository;
import com.internship.deal_service.repository.ContractorToRoleRepository;
import com.internship.deal_service.repository.DealContractorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractorRoleServiceImplTest {

    @Mock
    private DealContractorRepository dealContractorRepository;
    @Mock
    private ContractorRoleRepository contractorRoleRepository;
    @Mock
    private ContractorToRoleRepository contractorToRoleRepository;

    @InjectMocks
    private ContractorRoleServiceImpl contractorRoleService;

    private UUID dealContractorId;
    private UUID roleId;
    private ContractorRoleRequest testRequest;
    private DealContractor testDealContractor;
    private ContractorRole testContractorRole;
    private ContractorRoleDto expectedDto;

    @BeforeEach
    void setUp() {
        dealContractorId = UUID.randomUUID();
        roleId = UUID.randomUUID();

        testRequest = new ContractorRoleRequest();
        testRequest.setDealContractorId(dealContractorId);
        testRequest.setRoleId(roleId.toString());

        testDealContractor = new DealContractor();
        testDealContractor.setId(dealContractorId);
        testDealContractor.setName("Test Deal Contractor");
        testDealContractor.setIsActive(true);

        testContractorRole = new ContractorRole();
        testContractorRole.setId(roleId.toString());
        testContractorRole.setName("Test Role");
        testContractorRole.setIsActive(true);

        expectedDto = new ContractorRoleDto();
        expectedDto.setId(roleId.toString());
        expectedDto.setName("Test Role");
    }

    @Test
    void addRoleToContractor_DealContractorNotFound_ThrowsException() {
        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.empty());

        ContractorRoleException exception = assertThrows(ContractorRoleException.class, () -> {
            contractorRoleService.addRoleToContractor(testRequest);
        });

        assertEquals("DealContractor c id <<" + dealContractorId + ">> не найден или неактивен.", exception.getMessage());
        verify(contractorRoleRepository, never()).findByIdAndIsActiveTrue(any());
        verify(contractorToRoleRepository, never()).findByContractorIdAndRoleId(any(), any());
        verify(contractorToRoleRepository, never()).save(any());
    }

    @Test
    void addRoleToContractor_ContractorRoleNotFound_ThrowsException() {
        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.of(testDealContractor));
        when(contractorRoleRepository.findByIdAndIsActiveTrue(roleId.toString())).thenReturn(Optional.empty());

        ContractorRoleException exception = assertThrows(ContractorRoleException.class, () -> {
            contractorRoleService.addRoleToContractor(testRequest);
        });

        assertEquals("ContractorRole с id <<" + roleId + ">> не найден или неактивен.", exception.getMessage());
        verify(contractorToRoleRepository, never()).findByContractorIdAndRoleId(any(), any());
        verify(contractorToRoleRepository, never()).save(any());
    }

    @Test
    void addRoleToContractor_ExistingActiveLink_ReturnsDtoWithoutSaving() {
        ContractorToRole existingLink = new ContractorToRole();
        existingLink.setContractor(testDealContractor);
        existingLink.setRole(testContractorRole);
        existingLink.setIsActive(true);
        existingLink.setId(new ContractorToRoleId(dealContractorId, roleId.toString()));

        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.of(testDealContractor));
        when(contractorRoleRepository.findByIdAndIsActiveTrue(roleId.toString())).thenReturn(Optional.of(testContractorRole));
        when(contractorToRoleRepository.findByContractorIdAndRoleId(dealContractorId, roleId.toString())).thenReturn(Optional.of(existingLink));

        try (MockedStatic<ContractorRoleMapper> mockedMapper = Mockito.mockStatic(ContractorRoleMapper.class)) {
            mockedMapper.when(() -> ContractorRoleMapper.toDto(any(ContractorRole.class)))
                    .thenReturn(expectedDto);

            ContractorRoleDto result = contractorRoleService.addRoleToContractor(testRequest);

            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(contractorToRoleRepository, never()).save(any(ContractorToRole.class));
            mockedMapper.verify(() -> ContractorRoleMapper.toDto(existingLink.getRole()), times(1));
        }
    }

    @Test
    void addRoleToContractor_ExistingInactiveLink_ActivatesAndSaves() {
        ContractorToRole existingLink = new ContractorToRole();
        existingLink.setContractor(testDealContractor);
        existingLink.setRole(testContractorRole);
        existingLink.setIsActive(false);
        existingLink.setId(new ContractorToRoleId(dealContractorId, roleId.toString()));

        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.of(testDealContractor));
        when(contractorRoleRepository.findByIdAndIsActiveTrue(roleId.toString())).thenReturn(Optional.of(testContractorRole));
        when(contractorToRoleRepository.findByContractorIdAndRoleId(dealContractorId, roleId.toString())).thenReturn(Optional.of(existingLink));
        when(contractorToRoleRepository.save(any(ContractorToRole.class))).thenReturn(existingLink);

        try (MockedStatic<ContractorRoleMapper> mockedMapper = Mockito.mockStatic(ContractorRoleMapper.class)) {
            mockedMapper.when(() -> ContractorRoleMapper.toDto(any(ContractorRole.class)))
                    .thenReturn(expectedDto);

            ContractorRoleDto result = contractorRoleService.addRoleToContractor(testRequest);

            assertNotNull(result);
            assertTrue(existingLink.getIsActive());
            assertEquals(expectedDto, result);
            verify(contractorToRoleRepository, times(1)).save(existingLink);
            mockedMapper.verify(() -> ContractorRoleMapper.toDto(existingLink.getRole()), times(1));
        }
    }

    @Test
    void addRoleToContractor_NewLink_CreatesAndSaves() {
        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.of(testDealContractor));
        when(contractorRoleRepository.findByIdAndIsActiveTrue(roleId.toString())).thenReturn(Optional.of(testContractorRole));
        when(contractorToRoleRepository.findByContractorIdAndRoleId(dealContractorId, roleId.toString())).thenReturn(Optional.empty());
        when(contractorToRoleRepository.save(any(ContractorToRole.class))).thenAnswer(invocation -> {
            ContractorToRole savedLink = invocation.getArgument(0);
            savedLink.setId(new ContractorToRoleId(dealContractorId, roleId.toString()));
            return savedLink;
        });

        try (MockedStatic<ContractorRoleMapper> mockedMapper = Mockito.mockStatic(ContractorRoleMapper.class)) {
            mockedMapper.when(() -> ContractorRoleMapper.toDto(any(ContractorRole.class)))
                    .thenReturn(expectedDto);

            ContractorRoleDto result = contractorRoleService.addRoleToContractor(testRequest);

            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(contractorToRoleRepository, times(1)).save(any(ContractorToRole.class));
            mockedMapper.verify(() -> ContractorRoleMapper.toDto(testContractorRole), times(1));
        }
    }

    @Test
    void deleteRoleFromContractor_DealContractorNotFound_ThrowsException() {
        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.empty());

        ContractorRoleException exception = assertThrows(ContractorRoleException.class, () -> {
            contractorRoleService.deleteRoleFromContractor(testRequest);
        });

        assertEquals("DealContractor c id <<" + dealContractorId + ">> не найден или неактивен.", exception.getMessage());
        verify(contractorRoleRepository, never()).findByIdAndIsActiveTrue(any());
        verify(contractorToRoleRepository, never()).findByContractorIdAndRoleId(any(), any());
        verify(contractorToRoleRepository, never()).save(any());
    }

    @Test
    void deleteRoleFromContractor_ContractorRoleNotFound_ThrowsException() {
        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.of(testDealContractor));
        when(contractorRoleRepository.findByIdAndIsActiveTrue(roleId.toString())).thenReturn(Optional.empty());

        ContractorRoleException exception = assertThrows(ContractorRoleException.class, () -> {
            contractorRoleService.deleteRoleFromContractor(testRequest);
        });

        assertEquals("ContractorRole с id <<" + roleId + ">> не найдена ли неактивна.", exception.getMessage());
        verify(contractorToRoleRepository, never()).findByContractorIdAndRoleId(any(), any());
        verify(contractorToRoleRepository, never()).save(any());
    }

    @Test
    void deleteRoleFromContractor_LinkNotFound_ThrowsException() {
        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.of(testDealContractor));
        when(contractorRoleRepository.findByIdAndIsActiveTrue(roleId.toString())).thenReturn(Optional.of(testContractorRole));
        when(contractorToRoleRepository.findByContractorIdAndRoleId(dealContractorId, roleId.toString())).thenReturn(Optional.empty());

        ContractorRoleException exception = assertThrows(ContractorRoleException.class, () -> {
            contractorRoleService.deleteRoleFromContractor(testRequest);
        });

        assertEquals("Связь ContractorRole между контрагентом <<" + dealContractorId + ">> и ролью " + roleId + " не найдена.", exception.getMessage());
        verify(contractorToRoleRepository, never()).save(any());
    }

    @Test
    void deleteRoleFromContractor_LinkFoundAndActive_DeactivatesAndSaves() {
        ContractorToRole existingLink = new ContractorToRole();
        existingLink.setContractor(testDealContractor);
        existingLink.setRole(testContractorRole);
        existingLink.setIsActive(true);
        existingLink.setId(new ContractorToRoleId(dealContractorId, roleId.toString()));

        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.of(testDealContractor));
        when(contractorRoleRepository.findByIdAndIsActiveTrue(roleId.toString())).thenReturn(Optional.of(testContractorRole));
        when(contractorToRoleRepository.findByContractorIdAndRoleId(dealContractorId, roleId.toString())).thenReturn(Optional.of(existingLink));

        contractorRoleService.deleteRoleFromContractor(testRequest);

        assertFalse(existingLink.getIsActive());
        verify(contractorToRoleRepository, times(1)).save(existingLink);
    }

    @Test
    void deleteRoleFromContractor_LinkFoundAndInactive_DoesNothing() {
        ContractorToRole existingLink = new ContractorToRole();
        existingLink.setContractor(testDealContractor);
        existingLink.setRole(testContractorRole);
        existingLink.setIsActive(false);
        existingLink.setId(new ContractorToRoleId(dealContractorId, roleId.toString()));

        when(dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)).thenReturn(Optional.of(testDealContractor));
        when(contractorRoleRepository.findByIdAndIsActiveTrue(roleId.toString())).thenReturn(Optional.of(testContractorRole));
        when(contractorToRoleRepository.findByContractorIdAndRoleId(dealContractorId, roleId.toString())).thenReturn(Optional.of(existingLink));

        contractorRoleService.deleteRoleFromContractor(testRequest);

        assertFalse(existingLink.getIsActive());
        verify(contractorToRoleRepository, never()).save(any(ContractorToRole.class));
    }

}