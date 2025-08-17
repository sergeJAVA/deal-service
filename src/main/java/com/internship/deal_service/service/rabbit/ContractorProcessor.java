package com.internship.deal_service.service.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.deal_service.event.Contractor;
import com.internship.deal_service.model.dto.ContractorRequestRabbit;
import com.internship.deal_service.model.rabbit.InboxMessage;
import com.internship.deal_service.model.rabbit.MessageStatus;
import com.internship.deal_service.repository.ContractorRepository;
import com.internship.deal_service.repository.InboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractorProcessor {

    private final InboxMessageRepository inboxMessageRepository;
    private final DealContractorRabbitService dealContractorRabbitService;
    private final ContractorRepository contractorRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${schedule.fixed-delay}")
    public void processInboxMessage() {
        List<InboxMessage> messages = inboxMessageRepository
                .findFirstByStatusOrderByReceivedAtAsc(MessageStatus.RECEIVED);

        if (messages.isEmpty()) {
            return;
        }

        InboxMessage message = messages.getFirst();
        log.info("Processing message from inbox with id: {}", message.getMessageId());

        try {
            processSingleMessage(message);
        } catch (Exception e) {
            log.error("Unexpected error while processing message {}: {}",
                    message.getMessageId(), e.getMessage(), e);
            markFailed(message);
        }
    }

    @Transactional
    protected void processSingleMessage(InboxMessage message) {
        message.setAttempts(message.getAttempts() + 1);

        try {
            Contractor update = objectMapper.readValue(message.getPayload(), Contractor.class);
            log.info("Contractor: {}", update);

            Optional<Contractor> savedContractor = contractorRepository.findById(update.getId());
            if (savedContractor.isEmpty()) {
                contractorRepository.save(update);
                callDealService(update);
            } else {
                Contractor contractor = savedContractor.get();
                if (update.getModifyDate().isAfter(contractor.getModifyDate())) {
                    contractorRepository.save(update);
                    callDealService(update);
                } else {
                    log.warn("Contractor entry is outdated and the entity will not be updated");
                }
            }

            markProcessed(message);
            log.info("Successfully processed message with id: {}", message.getMessageId());
        } catch (Exception ex) {
            log.error("Failed to process message {}. Error: {}", message.getMessageId(), ex.getMessage(), ex);
            markFailed(message);
        }
    }

    private void markFailed(InboxMessage message) {
        try {
            message.setStatus(MessageStatus.FAILED);
            inboxMessageRepository.save(message);
        } catch (Exception e) {
            log.error("Failed to mark message {} as FAILED", message.getMessageId(), e);
        }
    }

    private void markProcessed(InboxMessage message) {
        try {
            message.setStatus(MessageStatus.PROCESSED);
            inboxMessageRepository.save(message);
        } catch (Exception e) {
            log.error("Failed to mark message {} as PROCESSED", message.getMessageId(), e);
        }
    }

    private void callDealService(Contractor contractor) {
        dealContractorRabbitService.saveDealContractorWithUserId(toContractorRequest(contractor));
    }

    private ContractorRequestRabbit toContractorRequest(Contractor contractor) {
        return ContractorRequestRabbit.builder()
                .contractorId(contractor.getId())
                .name(contractor.getName())
                .createUserId(contractor.getCreateUserId())
                .modifyUserId(contractor.getModifyUserId())
                .inn(contractor.getInn())
                .createDate(contractor.getCreateDate())
                .modifyDate(contractor.getModifyDate())
                .build();
    }

}
