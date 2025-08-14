package com.internship.deal_service.service.impl;

import com.internship.deal_service.exception.DealException;
import com.internship.deal_service.model.*;
import com.internship.deal_service.model.Currency;
import com.internship.deal_service.model.dto.*;
import com.internship.deal_service.model.mapper.DealMapper;
import com.internship.deal_service.model.mapper.DealSumMapper;
import com.internship.deal_service.repository.*;
import com.internship.deal_service.service.file.DealXlsxGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {

    @Mock
    private DealRepository dealRepository;
    @Mock
    private DealTypeRepository dealTypeRepository;
    @Mock
    private DealStatusRepository dealStatusRepository;
    @Mock
    private DealSumRepository dealSumRepository;
    @Mock
    private CurrencyRepository currencyRepository;

    private DealXlsxGenerator dealXlsxGenerator;

    @InjectMocks
    private DealServiceImpl dealService;

    private UUID testDealId;
    private String testDealTypeId;
    private String testCurrencyId;
    private Deal testDeal;
    private DealType testDealType;
    private DealStatus testDealStatusDraft;
    private Currency testCurrency;
    private DealDto testDealDto;
    private DealRequest testDealRequest;

    @BeforeEach
    void setUp() {
        testDealId = UUID.randomUUID();
        testDealTypeId = "CREDIT";
        testCurrencyId = "USD";

        testDealType = new DealType();
        testDealType.setId(testDealTypeId);
        testDealType.setName("Type");
        testDealType.setIsActive(true);

        testDealStatusDraft = new DealStatus();
        testDealStatusDraft.setId("DRAFT");
        testDealStatusDraft.setName("Черновик");
        testDealStatusDraft.setIsActive(true);

        testCurrency = new Currency();
        testCurrency.setId(testCurrencyId);
        testCurrency.setName("USD");
        testCurrency.setIsActive(true);

        testDeal = new Deal();
        testDeal.setId(testDealId);
        testDeal.setDescription("Test Deal Description");
        testDeal.setIsActive(true);
        testDeal.setType(testDealType);
        testDeal.setStatus(testDealStatusDraft);
        testDeal.setCreateDate(LocalDateTime.now().minusDays(1));
        testDeal.setDealSums(new HashSet<>());

        testDealDto = new DealDto();
        testDealDto.setId(testDealId);
        testDealDto.setDescription("Test Deal Description");

        DealTypeDto testDealTypeDto = new DealTypeDto();
        testDealTypeDto.setId(testDealTypeId);

        testDealRequest = new DealRequest();
        testDealRequest.setAgreementNumber("123");
        testDealRequest.setDescription("New Deal Description");
        testDealRequest.setType(testDealTypeDto);
        testDealRequest.setAgreementDate(LocalDate.now());
        testDealRequest.setAgreementStartDt(LocalDateTime.now().plusDays(1));
        testDealRequest.setAvailabilityDate(LocalDate.now().plusDays(7));
        testDealRequest.setCloseDt(LocalDateTime.now().plusMonths(6));
    }

    @Test
    void getDealById_ExistingDeal_ReturnsDealDto() {
        when(dealRepository.findByIdAndIsActiveTrue(testDealId)).thenReturn(Optional.of(testDeal));

        try (MockedStatic<DealMapper> mockedMapper = Mockito.mockStatic(DealMapper.class)) {
            mockedMapper.when(() -> DealMapper.mapToDto(testDeal)).thenReturn(testDealDto);

            DealDto result = dealService.getDealById(testDealId);

            assertNotNull(result);
            assertEquals(testDealDto.getId(), result.getId());
            assertEquals(testDealDto.getDescription(), result.getDescription());
            verify(dealRepository, times(1)).findByIdAndIsActiveTrue(testDealId);
            mockedMapper.verify(() -> DealMapper.mapToDto(testDeal), times(1));
        }
    }

    @Test
    void getDealById_NonExistingDeal_ThrowsEntityNotFoundException() {
        when(dealRepository.findByIdAndIsActiveTrue(testDealId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            dealService.getDealById(testDealId);
        });

        assertEquals("Deal с id " + testDealId + " не найдена или неактивна", exception.getMessage());
        verify(dealRepository, times(1)).findByIdAndIsActiveTrue(testDealId);
    }

    @Test
    void saveDeal_NewDeal_SavesAndReturnsDto() {
        when(dealTypeRepository.findByIdAndIsActiveTrue(testDealType.getId())).thenReturn(Optional.of(testDealType));
        when(dealStatusRepository.findByIdAndIsActiveTrue("DRAFT")).thenReturn(Optional.of(testDealStatusDraft));

        when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> {
            Deal deal = invocation.getArgument(0);
            deal.setId(UUID.randomUUID());
            return deal;
        });

        try (MockedStatic<DealMapper> mockedDealMapper = Mockito.mockStatic(DealMapper.class)) {
            mockedDealMapper.when(() -> DealMapper.dealRequestToEntity(any(DealRequest.class)))
                    .thenReturn(testDeal);

            mockedDealMapper.when(() -> DealMapper.mapToDto(any(Deal.class)))
                    .thenReturn(testDealDto);

            DealDto result = dealService.saveDeal(testDealRequest);

            verify(dealTypeRepository, times(1)).findByIdAndIsActiveTrue(testDealType.getId());
            verify(dealStatusRepository, times(1)).findByIdAndIsActiveTrue("DRAFT");
            verify(dealRepository, times(1)).save(any(Deal.class));
            verify(dealSumRepository, never()).save(any(DealSum.class));

            mockedDealMapper.verify(() -> DealMapper.dealRequestToEntity(testDealRequest), times(1));
            mockedDealMapper.verify(() -> DealMapper.mapToDto(any(Deal.class)), times(1));

            assertNotNull(result);
            assertEquals(testDealDto.getId(), result.getId());
            assertEquals(testDealDto.getDescription(), result.getDescription());
        }
    }

    @Test
    void saveDeal_NewDealWithSums_SavesAndReturnsDtoWithMainSum() {
        DealSumRequest mainSumRequest = DealSumRequest.builder()
                .currency(testCurrencyId)
                .value(BigDecimal.valueOf(100.0))
                .isMain(true)
                .build();
        DealSumRequest secondarySumRequest = DealSumRequest.builder()
                .currency(testCurrencyId)
                .value(BigDecimal.valueOf(50.0))
                .isMain(false)
                .build();
        testDealRequest.setSum(Arrays.asList(mainSumRequest, secondarySumRequest));

        when(dealTypeRepository.findByIdAndIsActiveTrue(testDealType.getId())).thenReturn(Optional.of(testDealType));
        when(dealStatusRepository.findByIdAndIsActiveTrue("DRAFT")).thenReturn(Optional.of(testDealStatusDraft));
        when(currencyRepository.findByIdAndIsActiveTrue(testCurrencyId)).thenReturn(Optional.of(testCurrency));

        when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> {
            Deal deal = invocation.getArgument(0);
            deal.setId(UUID.randomUUID());
            return deal;
        });

        when(dealSumRepository.save(any(DealSum.class))).thenAnswer(invocation -> {
            DealSum sum = invocation.getArgument(0);
            sum.setId(1L);
            return sum;
        });

        try (MockedStatic<DealMapper> mockedDealMapper = Mockito.mockStatic(DealMapper.class);
             MockedStatic<DealSumMapper> mockedDealSumMapper = Mockito.mockStatic(DealSumMapper.class)) {

            mockedDealMapper.when(() -> DealMapper.dealRequestToEntity(any(DealRequest.class))).thenReturn(testDeal);
            mockedDealMapper.when(() -> DealMapper.mapToDto(any(Deal.class))).thenReturn(testDealDto);

            DealSumDto mainSumDto = new DealSumDto();
            mainSumDto.setCurrency(testCurrencyId);
            mainSumDto.setValue(BigDecimal.valueOf(100.0));
            mockedDealSumMapper.when(() -> DealSumMapper.toDto(any(DealSum.class))).thenReturn(mainSumDto);


            DealDto result = dealService.saveDeal(testDealRequest);

            verify(dealRepository, times(1)).save(any(Deal.class));
            verify(dealSumRepository, times(2)).save(any(DealSum.class));
            verify(currencyRepository, times(2)).findByIdAndIsActiveTrue(testCurrencyId);

            assertNotNull(result.getSum());
        }
    }


    @Test
    void saveDeal_NewDeal_DealTypeNotFound_ThrowsEntityNotFoundException() {
        when(dealTypeRepository.findByIdAndIsActiveTrue(testDealType.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            dealService.saveDeal(testDealRequest);
        });

        assertEquals("DealType с id " + testDealType.getId() + " не был найден или неактивен.", exception.getMessage());
        verify(dealStatusRepository, never()).findByIdAndIsActiveTrue(any());
        verify(dealRepository, never()).save(any(Deal.class));
    }

    @Test
    void saveDeal_NewDeal_DealStatusDraftNotFound_ThrowsEntityNotFoundException() {
        when(dealTypeRepository.findByIdAndIsActiveTrue(testDealType.getId())).thenReturn(Optional.of(testDealType));
        when(dealStatusRepository.findByIdAndIsActiveTrue("DRAFT")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            dealService.saveDeal(testDealRequest);
        });

        assertEquals("DealStatus \"DRAFT\" не был найден или неактивен.", exception.getMessage());
        verify(dealRepository, never()).save(any(Deal.class));
    }

    @Test
    void saveDeal_NewDealWithSums_CurrencyNotFound_ThrowsEntityNotFoundException() {
        DealSumRequest sumRequest = DealSumRequest.builder()
                .currency("NON_EXISTENT")
                .value(BigDecimal.valueOf(100.0))
                .isMain(true)
                .build();
        testDealRequest.setSum(Collections.singletonList(sumRequest));

        when(dealTypeRepository.findByIdAndIsActiveTrue(testDealType.getId())).thenReturn(Optional.of(testDealType));
        when(dealStatusRepository.findByIdAndIsActiveTrue("DRAFT")).thenReturn(Optional.of(testDealStatusDraft));
        when(currencyRepository.findByIdAndIsActiveTrue("NON_EXISTENT")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            dealService.saveDeal(testDealRequest);
        });

        assertEquals("Currency с id NON_EXISTENT не найдена или неактивна.", exception.getMessage());
        verify(dealSumRepository, never()).save(any(DealSum.class));
    }

    @Test
    void saveDeal_UpdateExistingDeal_UpdatesFieldsAndReturnsDto() {
        testDealRequest.setId(testDealId);

        when(dealStatusRepository.findByIdAndIsActiveTrue("DRAFT")).thenReturn(Optional.of(testDealStatusDraft));
        when(dealRepository.findByIdAndIsActiveTrue(testDealId)).thenReturn(Optional.of(testDeal));
        when(dealTypeRepository.findByIdAndIsActiveTrue(testDealType.getId())).thenReturn(Optional.of(testDealType));
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        try (MockedStatic<DealMapper> mockedDealMapper = Mockito.mockStatic(DealMapper.class)) {
            mockedDealMapper.when(() -> DealMapper.mapToDto(any(Deal.class))).thenReturn(testDealDto);

            DealDto result = dealService.saveDeal(testDealRequest);

            assertEquals(testDealRequest.getDescription(), testDeal.getDescription());
            assertEquals(testDealRequest.getAgreementNumber(), testDeal.getAgreementNumber());
            assertEquals(testDealType, testDeal.getType());
            assertNotNull(testDeal.getModifyDate());

            verify(dealRepository, times(1)).findByIdAndIsActiveTrue(testDealId);
            verify(dealTypeRepository, times(1)).findByIdAndIsActiveTrue(testDealType.getId());
            verify(dealStatusRepository, times(1)).findByIdAndIsActiveTrue("DRAFT");
            verify(dealRepository, times(1)).save(testDeal);

            mockedDealMapper.verify(() -> DealMapper.mapToDto(testDeal), times(1));
            assertNotNull(result);
        }
    }

    @Test
    void saveDeal_UpdateExistingDeal_WithSums_ClearsAndAddsNewSums() {
        testDealRequest.setId(testDealId);
        DealSum oldSum = DealSum.builder()
                .id(1L)
                .deal(testDeal)
                .sum(BigDecimal.valueOf(500.0))
                .currency(testCurrency)
                .isMain(true)
                .build();
        testDeal.getDealSums().add(oldSum);

        DealSumRequest newMainSumRequest = DealSumRequest.builder()
                .currency(testCurrencyId)
                .value(BigDecimal.valueOf(200.0))
                .isMain(true)
                .build();
        DealSumRequest newSecondarySumRequest = DealSumRequest.builder()
                .currency(testCurrencyId)
                .value(BigDecimal.valueOf(100.0))
                .isMain(false)
                .build();
        testDealRequest.setSum(Arrays.asList(newMainSumRequest, newSecondarySumRequest));

        when(dealStatusRepository.findByIdAndIsActiveTrue("DRAFT")).thenReturn(Optional.of(testDealStatusDraft));
        when(dealRepository.findByIdAndIsActiveTrue(testDealId)).thenReturn(Optional.of(testDeal));
        when(dealTypeRepository.findByIdAndIsActiveTrue(testDealType.getId())).thenReturn(Optional.of(testDealType));
        when(currencyRepository.findByIdAndIsActiveTrue(testCurrencyId)).thenReturn(Optional.of(testCurrency));
        when(dealSumRepository.save(any(DealSum.class))).thenAnswer(invocation -> {
            DealSum sum = invocation.getArgument(0);
            sum.setId(1L);
            return sum;
        });

        try (MockedStatic<DealMapper> mockedDealMapper = Mockito.mockStatic(DealMapper.class)) {
            mockedDealMapper.when(() -> DealMapper.mapToDto(any(Deal.class))).thenReturn(testDealDto);

            dealService.saveDeal(testDealRequest);
            verify(dealSumRepository, times(2)).save(any(DealSum.class));
            verify(dealRepository, times(1)).findByIdAndIsActiveTrue(testDealId);
            verify(dealTypeRepository, times(1)).findByIdAndIsActiveTrue(testDealType.getId());
            verify(dealStatusRepository, times(1)).findByIdAndIsActiveTrue("DRAFT");

            mockedDealMapper.verify(() -> DealMapper.mapToDto(testDeal), times(1));
        }
    }


    @Test
    void saveDealWithUserId_NewDeal_SavesWithUserIdAndReturnsDto() {
        String userId = "newCreator";
        testDealRequest.setId(null);

        when(dealTypeRepository.findByIdAndIsActiveTrue(testDealType.getId())).thenReturn(Optional.of(testDealType));
        when(dealStatusRepository.findByIdAndIsActiveTrue("DRAFT")).thenReturn(Optional.of(testDealStatusDraft));

        when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> {
            Deal deal = invocation.getArgument(0);
            deal.setId(UUID.randomUUID());
            return deal;
        });

        try (MockedStatic<DealMapper> mockedDealMapper = Mockito.mockStatic(DealMapper.class)) {
            mockedDealMapper.when(() -> DealMapper.dealRequestToEntity(testDealRequest))
                    .thenReturn(testDeal);

            mockedDealMapper.when(() -> DealMapper.mapToDto(any(Deal.class)))
                    .thenReturn(testDealDto);

            dealService.saveDealWithUserId(testDealRequest, userId);

            verify(dealRepository, times(1)).save(argThat(deal ->
                    deal.getCreateUserId().equals(userId) && deal.getStatus().equals(testDealStatusDraft)
            ));
        }
    }

    @Test
    void changeDealStatus_SuccessfulChange() {
        UUID newStatusId = UUID.randomUUID();
        DealStatus newStatus = new DealStatus();
        newStatus.setId(newStatusId.toString());
        newStatus.setName("NEW_STATUS");
        newStatus.setIsActive(true);

        DealStatusUpdateRequest request = new DealStatusUpdateRequest();
        request.setNewStatusId(newStatusId.toString());

        when(dealRepository.findByIdAndIsActiveTrue(testDealId)).thenReturn(Optional.of(testDeal));
        when(dealStatusRepository.findByIdAndIsActiveTrue(newStatusId.toString())).thenReturn(Optional.of(newStatus));
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        try (MockedStatic<DealMapper> mockedDealMapper = Mockito.mockStatic(DealMapper.class)) {
            mockedDealMapper.when(() -> DealMapper.mapToDto(any(Deal.class))).thenReturn(testDealDto);

            DealDto result = dealService.changeDealStatus(testDealId, request);

            assertEquals(newStatus, testDeal.getStatus());
            assertNotNull(testDeal.getModifyDate());
            verify(dealRepository, times(1)).save(testDeal);
            mockedDealMapper.verify(() -> DealMapper.mapToDto(testDeal), times(1));
            assertNotNull(result);
        }
    }

    @Test
    void changeDealStatus_ToClosedStatus_SetsCloseDt() {
        String closedStatusId = "CLOSED";
        DealStatus closedStatus = new DealStatus();
        closedStatus.setId(closedStatusId);
        closedStatus.setName("CLOSED");
        closedStatus.setIsActive(true);

        DealStatusUpdateRequest request = new DealStatusUpdateRequest();
        request.setNewStatusId(closedStatusId);

        when(dealRepository.findByIdAndIsActiveTrue(testDealId)).thenReturn(Optional.of(testDeal));
        when(dealStatusRepository.findByIdAndIsActiveTrue(closedStatusId)).thenReturn(Optional.of(closedStatus));
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        try (MockedStatic<DealMapper> mockedDealMapper = Mockito.mockStatic(DealMapper.class)) {
            mockedDealMapper.when(() -> DealMapper.mapToDto(any(Deal.class))).thenReturn(testDealDto);

            dealService.changeDealStatus(testDealId, request);

            assertEquals(closedStatus, testDeal.getStatus());
            assertNotNull(testDeal.getCloseDt());
            verify(dealRepository, times(1)).save(testDeal);
        }
    }

    @Test
    void changeDealStatus_DealNotFound_ThrowsDealException() {
        String newStatusId = "SOME_STATUS";
        DealStatusUpdateRequest request = new DealStatusUpdateRequest();
        request.setNewStatusId(newStatusId);

        when(dealRepository.findByIdAndIsActiveTrue(testDealId)).thenReturn(Optional.empty());

        DealException exception = assertThrows(DealException.class, () -> {
            dealService.changeDealStatus(testDealId, request);
        });

        assertEquals("Deal с id <<" + testDealId + ">> не найдена или неактивна.", exception.getMessage());
        verify(dealStatusRepository, never()).findByIdAndIsActiveTrue(any());
        verify(dealRepository, never()).save(any(Deal.class));
    }

    @Test
    void changeDealStatus_NewStatusNotFound_ThrowsDealException() {
        String nonExistentStatusId = "NON_EXISTENT";
        DealStatusUpdateRequest request = new DealStatusUpdateRequest();
        request.setNewStatusId(nonExistentStatusId);

        when(dealRepository.findByIdAndIsActiveTrue(testDealId)).thenReturn(Optional.of(testDeal));
        when(dealStatusRepository.findByIdAndIsActiveTrue(nonExistentStatusId)).thenReturn(Optional.empty());

        DealException exception = assertThrows(DealException.class, () -> {
            dealService.changeDealStatus(testDealId, request);
        });

        assertEquals("Новый DealStatus с id <<" + nonExistentStatusId + ">> не найден или неактивен.", exception.getMessage());
        verify(dealRepository, never()).save(any(Deal.class));
    }

    @Test
    void searchDeals_ReturnsPageOfDealDtos() {
        DealSearchRequest searchRequest = new DealSearchRequest();
        Pagination pagination = new Pagination(0, 10);
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());

        List<Deal> deals = Collections.singletonList(testDeal);
        Page<Deal> dealPage = new PageImpl<>(deals, pageable, 1);

        when(dealRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dealPage);

        try (MockedStatic<DealMapper> mockedDealMapper = Mockito.mockStatic(DealMapper.class)) {
            mockedDealMapper.when(() -> DealMapper.mapToDto(testDeal)).thenReturn(testDealDto);

            Page<DealDto> resultPage = dealService.searchDeals(searchRequest, pagination);

            assertNotNull(resultPage);
            assertEquals(1, resultPage.getTotalElements());
            assertEquals(testDealDto.getId(), resultPage.getContent().get(0).getId());

            verify(dealRepository, times(1)).findAll(any(Specification.class), eq(pageable));
            mockedDealMapper.verify(() -> DealMapper.mapToDto(testDeal), times(1));
        }
    }

    @Test
    void exportDealsToExcel_ReturnsByteArray() {
        DealSearchRequest searchRequest = new DealSearchRequest();
        Pagination pagination = new Pagination(0, 10);
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), org.springframework.data.domain.Sort.unsorted());

        List<Deal> deals = Collections.singletonList(testDeal);
        Page<Deal> dealPage = new PageImpl<>(deals, pageable, 1);
        byte[] expectedBytes = "excel_data".getBytes();

        when(dealRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dealPage);

        try (MockedStatic<DealXlsxGenerator> mockedGenerator = Mockito.mockStatic(DealXlsxGenerator.class)) {
            mockedGenerator.when(() -> DealXlsxGenerator.createAndFillDealXlsxTable(deals))
                    .thenReturn(expectedBytes);

            byte[] resultBytes = dealService.exportDealsToExcel(searchRequest, pagination);

            assertNotNull(resultBytes);
            assertArrayEquals(expectedBytes, resultBytes);

            verify(dealRepository, times(1)).findAll(any(Specification.class), eq(pageable));
            mockedGenerator.verify(() -> DealXlsxGenerator.createAndFillDealXlsxTable(deals), times(1));
        }
    }

}