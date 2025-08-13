package com.internship.deal_service.service.rabbit;

import com.internship.deal_service.event.Contractor;
import com.internship.deal_service.exception.DealContractorException;
import com.internship.deal_service.model.dto.ContractorRequestRabbit;
import com.internship.deal_service.model.dto.DealContractorDto;
import com.internship.deal_service.repository.ContractorRepository;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitMQListenerServiceTest {

    @Mock
    private DealContractorRabbitService dealContractorRabbitService;

    @Mock
    private ContractorRepository contractorRepository;

    @Mock
    private Channel channel;

    @InjectMocks
    private RabbitMQListenerService rabbitMQListenerService;

    private Contractor testContractor;

    private Contractor existingContractor;

    @BeforeEach
    void setUp() {
        testContractor = new Contractor(
                "testContr",
                "testParent",
                "test",
                "testFullName",
                "111-222-333",
                "ogrnTest",
                "RUS",
                11,
                22,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "system",
                "system",
                true
        );

        existingContractor = new Contractor(
                "testContr",
                "testParent",
                "test",
                "testFullName",
                "111-222-333",
                "ogrnTest",
                "RUS",
                11,
                22,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                "system",
                "system",
                true
        );
    }

    @Test
    @DisplayName("Успешно сохранится Contractor и обновится DealContractor")
    void processContractorUpdate_Success() throws IOException {
        when(contractorRepository.findById(any(String.class))).thenReturn(Optional.empty());
        when(dealContractorRabbitService.saveDealContractorWithUserId(any(ContractorRequestRabbit.class)))
                .thenReturn(any(DealContractorDto.class));
        rabbitMQListenerService.processContractorUpdate(testContractor, 1L, channel);

        verify(contractorRepository, times(1)).save(testContractor);
        verify(dealContractorRabbitService, times(1)).saveDealContractorWithUserId(any(ContractorRequestRabbit.class));
        verify(channel, times(1)).basicAck(1L, false);
        verify(channel, times(0)).basicReject(1L, false);
    }

    @Test
    @DisplayName("Contractor и DealContractor не изменятся, т.к. изменения неактуальные")
    void processContractorUpdate_OutdatedContractor() throws IOException {
        when(contractorRepository.findById(any(String.class))).thenReturn(Optional.of(existingContractor));

        rabbitMQListenerService.processContractorUpdate(testContractor, 1L, channel);

        verify(contractorRepository, times(0)).save(any(Contractor.class));
        verify(dealContractorRabbitService, times(0)).saveDealContractorWithUserId(any(ContractorRequestRabbit.class));
        verify(channel, times(1)).basicAck(1L, false);
        verify(channel, times(0)).basicReject(1L, false);
    }

    @Test
    @DisplayName("Вылетел Exception во время обработки")
    void processContractorUpdate_ExceptionDuringProcessing() throws IOException {
        when(contractorRepository.findById(any(String.class))).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Какая-то ошибка в БД")).when(contractorRepository).save(any(Contractor.class));

        rabbitMQListenerService.processContractorUpdate(testContractor, 1L, channel);

        verify(channel, times(1)).basicReject(1L, false);
        verify(contractorRepository, times(1)).save(testContractor);
    }

    @Test
    @DisplayName("Вылетел DealContractorException во время обработки")
    void processContractorUpdate_DealContractorException() throws IOException {
        when(contractorRepository.findById(any(String.class))).thenReturn(Optional.empty());
        doThrow(new DealContractorException("DealContractor'а не существует")).when(dealContractorRabbitService)
                .saveDealContractorWithUserId(any(ContractorRequestRabbit.class));

        rabbitMQListenerService.processContractorUpdate(testContractor, 1L, channel);

        verify(contractorRepository, times(1)).save(testContractor);
        verify(channel, times(1)).basicAck(1L, false);
        verify(channel, times(0)).basicReject(1L, false);
    }

}