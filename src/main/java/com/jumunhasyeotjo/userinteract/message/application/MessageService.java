package com.jumunhasyeotjo.userinteract.message.application;

import com.jumunhasyeotjo.userinteract.message.application.command.MessageCreateCommand;
import com.jumunhasyeotjo.userinteract.message.application.command.ShippingMessageCreateCommand;
import com.jumunhasyeotjo.userinteract.message.application.result.MessageResult;
import com.jumunhasyeotjo.userinteract.message.application.service.UserClient;
import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.event.MessageCreatedEvent;
import com.jumunhasyeotjo.userinteract.message.domain.event.BulkMessagesCreatedEvent;
import com.jumunhasyeotjo.userinteract.message.domain.repository.MessageRepository;
import com.jumunhasyeotjo.userinteract.message.domain.vo.Content;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.CompanyManagerDto;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.HubManagerDto;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    private final ApplicationEventPublisher publisher;
    private final MessageRepository messageRepository;
    private final UserClient userClient;

    @Transactional
    public void createMessage(MessageCreateCommand command) {
        UserId userId = UserId.of(command.userId());
        Content content = Content.of(command.content());
        String slackId = userClient.getSlackId(userId.getUserId());

        Message message = Message.create(userId, content);
        messageRepository.save(message);

        publisher.publishEvent(new MessageCreatedEvent(message, slackId));
    }

    public void createShippingMessage(ShippingMessageCreateCommand command) {
        UUID hubId = command.originHubId();
        String orderIdMessage = command.orderIdMessage();
        UUID companyId = command.receiverCompanyId();
        String infoMessage = command.infoMessage();
        Long driverId = command.driverId();
        String etaMessage = command.etaMessage();

        Content content = buildContent(
            orderIdMessage,
            companyId,
            infoMessage,
            driverId,
            etaMessage
        );

        List<HubManagerDto> hubManagers = userClient.getHubManagers(hubId);

        List<Message> messages = new ArrayList<>();
        List<String> slackIds = new ArrayList<>();
        for (HubManagerDto hubManager : hubManagers) {
            UserId userId = UserId.of(hubManager.userId());
            String slackId = hubManager.slackId();

            messages.add(Message.create(userId, content));
            slackIds.add(slackId);
        }

        saveAllAndPublishEvent(messages, slackIds, content);
    }

    @Transactional
    public void saveAllAndPublishEvent(List<Message> messages, List<String> slackIds, Content content) {
        messageRepository.saveAll(messages);
        publisher.publishEvent(new BulkMessagesCreatedEvent(messages, slackIds, content));
    }

    private Content buildContent(
        String orderIdMessage,
        UUID companyId,
        String infoMessage,
        Long driverId,
        String etaMessage
    ) {
        List<CompanyManagerDto> companyManagers = userClient.getCompanyManagers(companyId);
        UserDto driver = userClient.getUser(driverId);

        StringBuilder sb = new StringBuilder();

        sb.append(orderIdMessage).append("\n");

        int companyManagerCount = 0;

        for (CompanyManagerDto companyManager : companyManagers) {
            companyManagerCount++;
            sb.append(String.format("주문자 정보(%d) : %s / %s\n",
                companyManagerCount,
                companyManager.name(),
                companyManager.slackId()
            ));
        }

        sb.append(infoMessage).append("\n");

        sb.append(String.format("배송 담당자 : %s / %s\n", driver.name(), driver.slackId()));

        sb.append(etaMessage);

        return Content.of(sb.toString());
    }

    public MessageResult getMessage(UUID messageId) {
        return MessageResult.from(messageRepository.findById(messageId));
    }

    public Page<MessageResult> getMessagesByUserId(Long userId, Pageable pageable) {
        return messageRepository.findAllByUserId(UserId.of(userId), pageable).map(MessageResult::from);
    }
}
