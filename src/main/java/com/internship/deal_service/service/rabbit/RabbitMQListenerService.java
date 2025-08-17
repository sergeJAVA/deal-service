package com.internship.deal_service.service.rabbit;

import com.internship.deal_service.model.rabbit.InboxMessage;
import com.internship.deal_service.model.rabbit.MessageStatus;
import com.internship.deal_service.repository.InboxMessageRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQListenerService {

    private final InboxMessageRepository inboxMessageRepository;

    @RabbitListener(queues = "deals_contractor_queue", containerFactory = "rabbitListenerContainerFactory")
    public void receiveContractorUpdate(Message message,
                                        Channel channel) throws IOException {
        UUID messageId = null;
        long deliveryTag = 0;
        try {
            deliveryTag = message.getMessageProperties().getDeliveryTag();
            String messageIdStr = message.getMessageProperties().getMessageId();

            if (messageIdStr == null) {
                log.error("Message is missing a messageId. Rejected.");
                channel.basicReject(deliveryTag, false);
                return;
            }

            messageId = UUID.fromString(messageIdStr);

            if (inboxMessageRepository.existsById(messageId)) {
                log.warn("Duplicate message received with id: {}. Acknowledging and ignoring.", messageId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            String payload = new String(message.getBody(), StandardCharsets.UTF_8);

            InboxMessage inboxMessage = InboxMessage.builder()
                    .messageId(messageId)
                    .payload(payload)
                    .status(MessageStatus.RECEIVED)
                    .build();

            inboxMessageRepository.save(inboxMessage);
            log.info("Message with id: {} saved to inbox.", messageId);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Failed to save message to inbox.", e);
            channel.basicReject(deliveryTag, false);
        }
    }

}
