package com.internship.deal_service.service.rabbit;

import com.internship.deal_service.event.Contractor;
import com.internship.deal_service.model.dto.ContractorRequestRabbit;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RabbitMQListenerService {

    private final DealContractorRabbitService dealContractorRabbitService;

    @RabbitListener(queues = "deals_contractor_queue", containerFactory = "rabbitListenerContainerFactory")
    public void processContractorUpdate(Contractor update,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                        Channel channel) throws IOException {
        try {

            dealContractorRabbitService.saveDealContractorWithUserId(toContractorRequest(update));
            // Надо сделать сохранение inbox для Contractor

            System.out.println("Обновлено: " + update);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            System.out.println("Ошибка обработки: " + e.getMessage() + ". Отправка в DLX.");
            channel.basicReject(deliveryTag, false);
        }
    }

    private ContractorRequestRabbit toContractorRequest(Contractor contractor) {
        return ContractorRequestRabbit.builder()
                .contractorId(contractor.getId())
                .name(contractor.getName())
                .main(true)
                .createUserId(contractor.getCreateUserId())
                .modifyUserId(contractor.getModifyUserId())
                .inn(contractor.getInn())
                .createDate(contractor.getCreateDate())
                .modifyDate(contractor.getModifyDate())
                .build();
    }

}
