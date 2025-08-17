package com.internship.deal_service.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.deal_service.event.Contractor;
import com.internship.deal_service.model.dto.ContractorRequestRabbit;
import com.internship.deal_service.model.rabbit.InboxMessage;
import com.internship.deal_service.model.rabbit.MessageStatus;
import com.internship.deal_service.repository.ContractorRepository;
import com.internship.deal_service.repository.InboxMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractorProcessorTest {

    @Mock
    private InboxMessageRepository inboxMessageRepository;

    @Mock
    private DealContractorRabbitService dealContractorRabbitService;

    @Mock
    private ContractorRepository contractorRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ContractorProcessor contractorProcessor;

    private InboxMessage testMessage;
    private Contractor contractor;

    @BeforeEach
    void setUp() throws Exception {
        contractor = new Contractor(
                "testId",
                "parentId",
                "Name",
                "fullName",
                "111-222-333",
                "ogrnTest",
                "RUS",
                11,
                22,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                "system",
                "system",
                true
        );

        testMessage = InboxMessage.builder()
                .messageId(UUID.randomUUID())
                .payload("{\"id\":\"testId\"}")
                .status(MessageStatus.RECEIVED)
                .attempts(0)
                .receivedAt(LocalDateTime.now())
                .build();
    }


    @Test
    @DisplayName("Новый Contractor сохраняется и отправляется в dealService")
    void processSingleMessage_NewContractor() throws JsonProcessingException {
        when(objectMapper.readValue(anyString(), eq(Contractor.class)))
                .thenReturn(contractor);
        when(contractorRepository.findById(contractor.getId()))
                .thenReturn(Optional.empty());

        contractorProcessor.processSingleMessage(testMessage);

        verify(contractorRepository).save(contractor);
        verify(dealContractorRabbitService).saveDealContractorWithUserId(any(ContractorRequestRabbit.class));
        verify(inboxMessageRepository).save(argThat(msg ->
                msg.getStatus() == MessageStatus.PROCESSED
                        && msg.getAttempts() == 1
        ));
    }

    @Test
    @DisplayName("Contractor обновляется если modifyDate новее")
    void processSingleMessage_UpdateContractor() throws JsonProcessingException {
        when(objectMapper.readValue(anyString(), eq(Contractor.class)))
                .thenReturn(contractor);
        Contractor oldContractor = new Contractor(
                contractor.getId(),
                "parentId",
                "test",
                "test full",
                "12312345",
                "ogrn",
                "RUS",
                11,
                22,
                contractor.getCreateDate(),
                contractor.getModifyDate().minusDays(5),
                "system",
                "system",
                true
        );

        when(contractorRepository.findById(contractor.getId()))
                .thenReturn(Optional.of(oldContractor));

        contractorProcessor.processSingleMessage(testMessage);

        verify(contractorRepository).save(contractor);
        verify(dealContractorRabbitService).saveDealContractorWithUserId(any(ContractorRequestRabbit.class));
        verify(inboxMessageRepository).save(argThat(msg -> msg.getStatus() == MessageStatus.PROCESSED));
    }

    @Test
    @DisplayName("Не обновляется, если сообщение устаревшее")
    void processSingleMessage_OutdatedContractor() throws JsonProcessingException {
        when(objectMapper.readValue(anyString(), eq(Contractor.class)))
                .thenReturn(contractor);
        Contractor newerSaved = new Contractor(
                contractor.getId(),
                "parentId",
                "test",
                "test full",
                "123456721",
                "ogrn",
                "RUS",
                11,
                22,
                contractor.getCreateDate(),
                contractor.getModifyDate().plusDays(2),
                "system",
                "system",
                true
        );

        when(contractorRepository.findById(contractor.getId()))
                .thenReturn(Optional.of(newerSaved));

        contractorProcessor.processSingleMessage(testMessage);

        verify(contractorRepository, never()).save(contractor);
        verify(dealContractorRabbitService, never()).saveDealContractorWithUserId(any());
        verify(inboxMessageRepository).save(argThat(msg -> msg.getStatus() == MessageStatus.PROCESSED));
    }

    @Test
    @DisplayName("Ошибка при обработке помечает сообщение как FAILED")
    void processSingleMessage_Exception() throws Exception {
        when(objectMapper.readValue(anyString(), eq(Contractor.class)))
                .thenThrow(new RuntimeException("wrong payload"));

        contractorProcessor.processSingleMessage(testMessage);

        assertEquals(MessageStatus.FAILED, testMessage.getStatus());
    }

    @Test
    @DisplayName("Обрабатывает первое сообщение из репозитория")
    void processInboxMessage_Messages() throws JsonProcessingException {
        when(objectMapper.readValue(anyString(), eq(Contractor.class)))
                .thenReturn(contractor);
        when(inboxMessageRepository.findFirstByStatusOrderByReceivedAtAsc(MessageStatus.RECEIVED))
                .thenReturn(List.of(testMessage));
        when(contractorRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        contractorProcessor.processInboxMessage();

        verify(contractorRepository).save(any(Contractor.class));
        verify(dealContractorRabbitService).saveDealContractorWithUserId(any());
    }

}