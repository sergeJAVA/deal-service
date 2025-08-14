package com.internship.deal_service.service.impl;

import com.internship.deal_service.exception.DealContractorException;
import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.dto.DealContractorDto;
import com.internship.deal_service.model.dto.DealContractorRequest;
import com.internship.deal_service.model.mapper.DealContractorMapper;
import com.internship.deal_service.repository.DealContractorRepository;
import com.internship.deal_service.repository.DealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealContractorServiceImplTest {

    @Mock
    private DealContractorRepository dealContractorRepository;
    @Mock
    private DealRepository dealRepository;

    @InjectMocks
    private DealContractorServiceImpl dealContractorService;

    private Deal testDeal;
    private DealContractorRequest createRequest;
    private DealContractorRequest updateRequest;
    private DealContractor existingDealContractor;
    private DealContractorDto expectedDto;

    @BeforeEach
    void setUp() {
        UUID dealId = UUID.randomUUID();
        UUID contractorId = UUID.randomUUID();
        UUID existingDealContractorId = UUID.randomUUID();

        testDeal = new Deal();
        testDeal.setId(dealId);
        testDeal.setDescription("Test Deal");
        testDeal.setIsActive(true);

        createRequest = new DealContractorRequest();
        createRequest.setDealId(dealId);
        createRequest.setContractorId(contractorId.toString());
        createRequest.setName("New Contractor");
        createRequest.setInn("111222333");
        createRequest.setMain(false);

        existingDealContractor = new DealContractor();
        existingDealContractor.setId(existingDealContractorId);
        existingDealContractor.setDeal(testDeal);
        existingDealContractor.setContractorId(contractorId.toString());
        existingDealContractor.setName("Existing Contractor");
        existingDealContractor.setInn("444555666");
        existingDealContractor.setMain(false);
        existingDealContractor.setIsActive(true);
        existingDealContractor.setCreateDate(LocalDateTime.now().minusDays(5));

        updateRequest = new DealContractorRequest();
        updateRequest.setId(existingDealContractorId);
        updateRequest.setDealId(dealId);
        updateRequest.setContractorId(UUID.randomUUID().toString());
        updateRequest.setName("Updated Contractor Name");
        updateRequest.setInn("777888999");
        updateRequest.setMain(true);

        expectedDto = new DealContractorDto();
        expectedDto.setId(existingDealContractorId);
        expectedDto.setContractorId(updateRequest.getContractorId());
        expectedDto.setName(updateRequest.getName());
        expectedDto.setMain(updateRequest.getMain());
    }


    @Test
    void saveDealContractor_DealNotFound_ThrowsException() {
        when(dealRepository.findByIdAndIsActiveTrue(any(UUID.class))).thenReturn(Optional.empty());

        DealContractorException exception = assertThrows(DealContractorException.class, () -> {
            dealContractorService.saveDealContractor(createRequest);
        });

        assertEquals("Deal с id <<" + createRequest.getDealId() + ">> не найдена или неактивна.", exception.getMessage());
        verify(dealContractorRepository, never()).save(any(DealContractor.class));
    }

    @Test
    void saveDealContractor_NewContractor_SavesAndReturnsDto() {
        when(dealRepository.findByIdAndIsActiveTrue(createRequest.getDealId())).thenReturn(Optional.of(testDeal));
        when(dealContractorRepository.save(any(DealContractor.class))).thenAnswer(invocation -> {
            DealContractor dc = invocation.getArgument(0);
            dc.setId(UUID.randomUUID());
            return dc;
        });

        try (MockedStatic<DealContractorMapper> mockedMapper = Mockito.mockStatic(DealContractorMapper.class)) {
            mockedMapper.when(() -> DealContractorMapper.toDto(any(DealContractor.class)))
                    .thenReturn(new DealContractorDto());

            DealContractorDto result = dealContractorService.saveDealContractor(createRequest);

            assertNotNull(result);
            verify(dealContractorRepository, times(1)).save(any(DealContractor.class));
            verify(dealContractorRepository, never()).updateAllOthersMainToFalseForDeal(any(), any());
            mockedMapper.verify(() -> DealContractorMapper.toDto(any(DealContractor.class)), times(1));
        }
    }

    @Test
    void saveDealContractor_UpdateExistingContractor_UpdatesAndReturnsDto() {
        when(dealRepository.findByIdAndIsActiveTrue(updateRequest.getDealId())).thenReturn(Optional.of(testDeal));
        when(dealContractorRepository.findAllByDealIdAndIsActiveTrue(updateRequest.getDealId()))
                .thenReturn(Arrays.asList(existingDealContractor));

        try (MockedStatic<DealContractorMapper> mockedMapper = Mockito.mockStatic(DealContractorMapper.class)) {
            mockedMapper.when(() -> DealContractorMapper.toDto(any(DealContractor.class)))
                    .thenReturn(expectedDto);

            DealContractorDto result = dealContractorService.saveDealContractor(updateRequest);

            assertNotNull(result);
            assertEquals(updateRequest.getContractorId(), existingDealContractor.getContractorId());
            assertEquals(updateRequest.getName(), existingDealContractor.getName());
            assertEquals(updateRequest.getInn(), existingDealContractor.getInn());
            assertEquals(updateRequest.getMain(), existingDealContractor.getMain());

            verify(dealContractorRepository, times(1))
                    .updateAllOthersMainToFalseForDeal(updateRequest.getDealId(), updateRequest.getId());
            verify(dealContractorRepository, never()).save(any(DealContractor.class));
            mockedMapper.verify(() -> DealContractorMapper.toDto(existingDealContractor), times(1));
        }
    }

    @Test
    void saveDealContractor_UpdateExistingContractor_NoInnChange() {
        updateRequest.setInn(null);
        String originalInn = existingDealContractor.getInn();

        when(dealRepository.findByIdAndIsActiveTrue(updateRequest.getDealId())).thenReturn(Optional.of(testDeal));
        when(dealContractorRepository.findAllByDealIdAndIsActiveTrue(updateRequest.getDealId()))
                .thenReturn(Arrays.asList(existingDealContractor));

        try (MockedStatic<DealContractorMapper> mockedMapper = Mockito.mockStatic(DealContractorMapper.class)) {
            mockedMapper.when(() -> DealContractorMapper.toDto(any(DealContractor.class)))
                    .thenReturn(expectedDto);

            dealContractorService.saveDealContractor(updateRequest);

            assertEquals(originalInn, existingDealContractor.getInn());
        }
    }

    @Test
    void saveDealContractor_UpdateExistingContractor_MainRemainsFalse() {
        updateRequest.setMain(false);
        existingDealContractor.setMain(false);

        when(dealRepository.findByIdAndIsActiveTrue(updateRequest.getDealId())).thenReturn(Optional.of(testDeal));
        when(dealContractorRepository.findAllByDealIdAndIsActiveTrue(updateRequest.getDealId()))
                .thenReturn(Arrays.asList(existingDealContractor));

        try (MockedStatic<DealContractorMapper> mockedMapper = Mockito.mockStatic(DealContractorMapper.class)) {
            mockedMapper.when(() -> DealContractorMapper.toDto(any(DealContractor.class)))
                    .thenReturn(expectedDto);

            dealContractorService.saveDealContractor(updateRequest);

            assertFalse(existingDealContractor.getMain());
            verify(dealContractorRepository, never()).updateAllOthersMainToFalseForDeal(any(), any());
        }
    }

    @Test
    void saveDealContractor_UpdateExistingContractor_MainChangesTrueToFalse() {
        updateRequest.setMain(false);
        existingDealContractor.setMain(true);

        when(dealRepository.findByIdAndIsActiveTrue(updateRequest.getDealId())).thenReturn(Optional.of(testDeal));
        when(dealContractorRepository.findAllByDealIdAndIsActiveTrue(updateRequest.getDealId()))
                .thenReturn(Arrays.asList(existingDealContractor));

        try (MockedStatic<DealContractorMapper> mockedMapper = Mockito.mockStatic(DealContractorMapper.class)) {
            mockedMapper.when(() -> DealContractorMapper.toDto(any(DealContractor.class)))
                    .thenReturn(expectedDto);

            dealContractorService.saveDealContractor(updateRequest);

            assertFalse(existingDealContractor.getMain());
            verify(dealContractorRepository, never()).updateAllOthersMainToFalseForDeal(any(), any());
        }
    }

    @Test
    void saveDealContractor_UpdateExistingContractor_NotFoundByIdInList_ThrowsException() {
        updateRequest.setId(UUID.randomUUID());

        when(dealRepository.findByIdAndIsActiveTrue(updateRequest.getDealId())).thenReturn(Optional.of(testDeal));
        when(dealContractorRepository.findAllByDealIdAndIsActiveTrue(updateRequest.getDealId()))
                .thenReturn(Arrays.asList(existingDealContractor));

        DealContractorException exception = assertThrows(DealContractorException.class, () -> {
            dealContractorService.saveDealContractor(updateRequest);
        });

        assertEquals("DealContractor с id <<" + updateRequest.getId() + ">> не найден или неактивен.", exception.getMessage());
        verify(dealContractorRepository, never()).save(any(DealContractor.class));
    }

    @Test
    void saveDealContractorWithUserId_NewContractor_SavesWithUserIdAndReturnsDto() {
        String userId = "testUserId";
        when(dealRepository.findByIdAndIsActiveTrue(createRequest.getDealId())).thenReturn(Optional.of(testDeal));
        when(dealContractorRepository.save(any(DealContractor.class))).thenAnswer(invocation -> {
            DealContractor dc = invocation.getArgument(0);
            dc.setId(UUID.randomUUID());
            return dc;
        });

        try (MockedStatic<DealContractorMapper> mockedMapper = Mockito.mockStatic(DealContractorMapper.class)) {
            mockedMapper.when(() -> DealContractorMapper.toDto(any(DealContractor.class)))
                    .thenReturn(new DealContractorDto());

            dealContractorService.saveDealContractorWithUserId(createRequest, userId);

            verify(dealContractorRepository, times(1)).save(argThat(dc ->
                    dc.getCreateUserId().equals(userId) && dc.getDeal().equals(testDeal)
            ));
        }
    }

    @Test
    void saveDealContractorWithUserId_UpdateExistingContractor_UpdatesWithUserIdAndReturnsDto() {
        String userId = "testModifyUserId";
        when(dealRepository.findByIdAndIsActiveTrue(updateRequest.getDealId())).thenReturn(Optional.of(testDeal));
        when(dealContractorRepository.findAllByDealIdAndIsActiveTrue(updateRequest.getDealId()))
                .thenReturn(Arrays.asList(existingDealContractor));

        try (MockedStatic<DealContractorMapper> mockedMapper = Mockito.mockStatic(DealContractorMapper.class)) {
            mockedMapper.when(() -> DealContractorMapper.toDto(any(DealContractor.class)))
                    .thenReturn(expectedDto);

            dealContractorService.saveDealContractorWithUserId(updateRequest, userId);

            assertEquals(userId, existingDealContractor.getModifyUserId());
            verify(dealContractorRepository, times(1))
                    .updateAllOthersMainToFalseForDeal(any(), any());
            mockedMapper.verify(() -> DealContractorMapper.toDto(existingDealContractor), times(1));
        }
    }

    @Test
    void deleteDealContractor_ContractorNotFound_ThrowsException() {
        UUID nonExistentId = UUID.randomUUID();
        when(dealContractorRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        DealContractorException exception = assertThrows(DealContractorException.class, () -> {
            dealContractorService.deleteDealContractor(nonExistentId);
        });

        assertEquals("DealContractor с id <<" + nonExistentId + ">> не найден.", exception.getMessage());
        verify(dealContractorRepository, never()).save(any(DealContractor.class));
    }

    @Test
    void deleteDealContractor_ContractorFoundAndActive_DeactivatesAndSaves() {
        UUID idToDelete = existingDealContractor.getId();
        existingDealContractor.setIsActive(true);
        when(dealContractorRepository.findById(idToDelete)).thenReturn(Optional.of(existingDealContractor));

        dealContractorService.deleteDealContractor(idToDelete);

        assertFalse(existingDealContractor.getIsActive());
        assertNotNull(existingDealContractor.getModifyDate());
        verify(dealContractorRepository, times(1)).save(existingDealContractor);
    }

    @Test
    void deleteDealContractor_ContractorFoundAndInactive_DoesNothing() {
        UUID idToDelete = existingDealContractor.getId();
        existingDealContractor.setIsActive(false);
        when(dealContractorRepository.findById(idToDelete)).thenReturn(Optional.of(existingDealContractor));

        dealContractorService.deleteDealContractor(idToDelete);

        assertFalse(existingDealContractor.getIsActive());
        verify(dealContractorRepository, never()).save(any(DealContractor.class));
    }

}