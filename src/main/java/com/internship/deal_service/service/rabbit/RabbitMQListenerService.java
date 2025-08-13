package com.internship.deal_service.service.rabbit;

import com.internship.deal_service.event.Contractor;
import com.internship.deal_service.exception.DealContractorException;
import com.internship.deal_service.model.dto.ContractorRequestRabbit;
import com.internship.deal_service.repository.ContractorRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQListenerService {

    private final DealContractorRabbitService dealContractorRabbitService;
    private final ContractorRepository contractorRepository;

    @RabbitListener(queues = "deals_contractor_queue", containerFactory = "rabbitListenerContainerFactory")
    public void processContractorUpdate(Contractor update,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                        Channel channel) throws IOException {
        try {
            log.info("Contractor: {}", update);
            Optional<Contractor> savedContactor = contractorRepository.findById(update.getId());
            if (savedContactor.isEmpty()) {
                contractorRepository.save(update);
                dealContractorRabbitService.saveDealContractorWithUserId(toContractorRequest(update));
            } else {
                Contractor contractor = savedContactor.get();
                if (update.getModifyDate().isBefore(contractor.getModifyDate())) {
                    log.warn("Contractor entry is outdated and the entity will not be updated");
                    channel.basicAck(deliveryTag, false);
                    return;
                } else {
                    contractorRepository.save(update);
                    dealContractorRabbitService.saveDealContractorWithUserId(toContractorRequest(update));
                }
            }

            log.info("Contractor updated");
            channel.basicAck(deliveryTag, false);
        } catch (DealContractorException exception) {
            log.warn("DealContractorException: {}", exception.getMessage());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("An error occurred during processing Contractor. Exception message: {}", e.getMessage(), e);
            channel.basicReject(deliveryTag, false);
        }
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
