package com.internship.deal_service.repository;

import com.internship.deal_service.model.rabbit.InboxMessage;
import com.internship.deal_service.model.rabbit.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InboxMessageRepository extends JpaRepository<InboxMessage, UUID> {

    List<InboxMessage> findFirstByStatusOrderByReceivedAtAsc(MessageStatus status);

}
