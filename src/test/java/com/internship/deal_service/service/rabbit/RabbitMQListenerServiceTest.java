package com.internship.deal_service.service.rabbit;

import com.internship.deal_service.model.rabbit.InboxMessage;
import com.internship.deal_service.repository.InboxMessageRepository;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitMQListenerServiceTest {

    @Mock
    private InboxMessageRepository inboxMessageRepository;

    @Mock
    private Channel channel;

    @InjectMocks
    private RabbitMQListenerService rabbitMQListenerService;

    private Message message;
    private UUID messageId;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        MessageProperties props = new MessageProperties();
        props.setMessageId(messageId.toString());
        props.setDeliveryTag(1L);
        message = new Message("test-payload".getBytes(StandardCharsets.UTF_8), props);
    }

    @Test
    @DisplayName("Сообщение успешно сохраняется в Inbox")
    void receiveContractorUpdate_Success() throws Exception {
        when(inboxMessageRepository.existsById(messageId)).thenReturn(false);

        rabbitMQListenerService.receiveContractorUpdate(message, channel);

        verify(inboxMessageRepository, times(1)).save(any(InboxMessage.class));
        verify(channel, times(1)).basicAck(1L, false);
        verify(channel, never()).basicReject(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("Сообщение без messageId")
    void receiveContractorUpdate_NoMessageId() throws Exception {
        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(1L);
        Message noIdMessage = new Message("test".getBytes(StandardCharsets.UTF_8), props);

        rabbitMQListenerService.receiveContractorUpdate(noIdMessage, channel);

        verify(channel, times(1)).basicReject(1L, false);
        verify(inboxMessageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Повторное сообщение")
    void receiveContractorUpdate_Duplicate() throws Exception {
        when(inboxMessageRepository.existsById(messageId)).thenReturn(true);

        rabbitMQListenerService.receiveContractorUpdate(message, channel);

        verify(channel, times(1)).basicAck(1L, false);
        verify(inboxMessageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Неожиданное исключение при сохранении")
    void receiveContractorUpdate_Exception() throws Exception {
        when(inboxMessageRepository.existsById(messageId)).thenReturn(false);
        doThrow(new RuntimeException("DB error"))
                .when(inboxMessageRepository).save(any());

        rabbitMQListenerService.receiveContractorUpdate(message, channel);

        verify(channel, times(1)).basicReject(1L, false);
    }

}