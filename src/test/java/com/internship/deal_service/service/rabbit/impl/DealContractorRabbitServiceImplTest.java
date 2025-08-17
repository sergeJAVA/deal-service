package com.internship.deal_service.service.rabbit.impl;

import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.dto.ContractorRequestRabbit;
import com.internship.deal_service.model.dto.DealContractorDto;
import com.internship.deal_service.model.mapper.DealContractorMapper;
import com.internship.deal_service.repository.DealContractorRepository;
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
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealContractorRabbitServiceImplTest {

    @Mock
    private DealContractorRepository dealContractorRepository;

    @InjectMocks
    private DealContractorRabbitServiceImpl dealContractorRabbitService;

    private ContractorRequestRabbit testRequest;
    private DealContractor existingDealContractor;
    private DealContractorDto expectedDto;

    @BeforeEach
    void setUp() {
        UUID contractorId = UUID.randomUUID();
        UUID dealContractorId = UUID.randomUUID();

        testRequest = ContractorRequestRabbit.builder()
                .contractorId(contractorId.toString())
                .name("UpdateTest")
                .inn("9876543210")
                .createUserId("testUserCreate")
                .modifyUserId("testUserModify")
                .createDate(LocalDateTime.now().minusDays(1))
                .modifyDate(LocalDateTime.now())
                .build();

        existingDealContractor = new DealContractor();
        existingDealContractor.setId(dealContractorId);
        existingDealContractor.setContractorId(contractorId.toString());
        existingDealContractor.setName("TestName");
        existingDealContractor.setInn("1234567890");
        existingDealContractor.setIsActive(true);
        existingDealContractor.setCreateDate(LocalDateTime.now().minusDays(2));
        existingDealContractor.setModifyDate(LocalDateTime.now().minusDays(1));
        existingDealContractor.setCreateUserId("originalUser");

        expectedDto = new DealContractorDto();
        expectedDto.setId(dealContractorId);
        expectedDto.setContractorId(testRequest.getContractorId());
        expectedDto.setName(testRequest.getName());
    }

    @Test
    void saveDealContractorWithUserId_ExistingActiveContractor_UpdatesAndReturnsDto() {
        when(dealContractorRepository.findByContractorIdAndIsActiveTrue(testRequest.getContractorId()))
                .thenReturn(Arrays.asList(existingDealContractor));

        try (MockedStatic<DealContractorMapper> mockedMapper = Mockito.mockStatic(DealContractorMapper.class)) {
            mockedMapper.when(() -> DealContractorMapper.toDto(any(DealContractor.class)))
                    .thenReturn(expectedDto);

            DealContractorDto result = dealContractorRabbitService.saveDealContractorWithUserId(testRequest);

            verify(dealContractorRepository, times(1)).findByContractorIdAndIsActiveTrue(testRequest.getContractorId());

            assertEquals(testRequest.getContractorId(), existingDealContractor.getContractorId());
            assertEquals(testRequest.getName(), existingDealContractor.getName());
            assertEquals(testRequest.getInn(), existingDealContractor.getInn());
            assertEquals(testRequest.getModifyUserId(), existingDealContractor.getModifyUserId());

            mockedMapper.verify(() -> DealContractorMapper.toDto(existingDealContractor), times(1));

            assertNotNull(result);
            assertEquals(expectedDto.getId(), result.getId());
            assertEquals(expectedDto.getName(), result.getName());
            assertEquals(expectedDto.getContractorId(), result.getContractorId());
        }
    }

    @Test
    void saveDealContractorWithUserId_ExistingActiveContractor_NoInnUpdate() {
        testRequest.setInn(null);

        when(dealContractorRepository.findByContractorIdAndIsActiveTrue(testRequest.getContractorId()))
                .thenReturn(Arrays.asList(existingDealContractor));

        try (MockedStatic<DealContractorMapper> mockedMapper = Mockito.mockStatic(DealContractorMapper.class)) {
            mockedMapper.when(() -> DealContractorMapper.toDto(any(DealContractor.class)))
                    .thenReturn(expectedDto);

            String originalInn = existingDealContractor.getInn();

            DealContractorDto result = dealContractorRabbitService.saveDealContractorWithUserId(testRequest);

            assertEquals(originalInn, existingDealContractor.getInn());
            assertEquals(testRequest.getContractorId(), existingDealContractor.getContractorId());
            assertEquals(testRequest.getName(), existingDealContractor.getName());

            mockedMapper.verify(() -> DealContractorMapper.toDto(existingDealContractor), times(1));
            assertNotNull(result);
        }
    }

    @Test
    void saveDealContractorWithUserId_NoExistingContractor() {
        when(dealContractorRepository.findByContractorIdAndIsActiveTrue(testRequest.getContractorId()))
                .thenReturn(Collections.emptyList());

        DealContractorDto dto = dealContractorRabbitService.saveDealContractorWithUserId(testRequest);

        verify(dealContractorRepository, times(1)).findByContractorIdAndIsActiveTrue(testRequest.getContractorId());
        verify(dealContractorRepository, never()).save(any(DealContractor.class));
        assertNull(dto);
    }

}