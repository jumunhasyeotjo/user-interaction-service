package com.jumunhasyeotjo.userinteract.message.application;

import com.jumunhasyeotjo.userinteract.message.application.command.CreateShippingDelayedMessageCommand;
import com.jumunhasyeotjo.userinteract.message.application.command.CreateShippingEtaMessageCommand;
import com.jumunhasyeotjo.userinteract.message.application.result.MessageResult;
import com.jumunhasyeotjo.userinteract.message.application.service.SlackClient;
import com.jumunhasyeotjo.userinteract.message.application.service.UserClient;
import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.repository.MessageRepository;
import com.jumunhasyeotjo.userinteract.message.domain.vo.Content;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.CompanyManagerDto;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.HubManagerDto;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    private final ApplicationEventPublisher publisher;
    private final MessageRepository messageRepository;
    private final UserClient userClient;
    private final SlackClient slackClient;

    public void createShippingDelayedMessage(CreateShippingDelayedMessageCommand command) {
        UUID companyId = command.companyId();
        Content content = Content.of(command.message());

        List<CompanyManagerDto> companyManagers = userClient.getCompanyManagers(companyId);

        List<Message> messages = new ArrayList<>();

        for (CompanyManagerDto companyManager : companyManagers) {
            UserId userId = UserId.of(companyManager.userId());
            String slackId = companyManager.slackId();

            messages.add(Message.create(userId, content));
            slackClient.sendMessage(List.of(slackId), content.getContent());
        }

        saveMessages(messages);
    }

    public void createShippingMessage(CreateShippingEtaMessageCommand command) {
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

        System.out.println("허브 매니저 수 : " + hubManagers.size());

        List<Message> messages = new ArrayList<>();
        for (HubManagerDto hubManager : hubManagers) {
            UserId userId = UserId.of(hubManager.userId());
            String slackId = hubManager.slackId();

            messages.add(Message.create(userId, content));
            log.info(slackId);
            slackClient.sendMessage(List.of(slackId), content.getContent());
        }

        saveMessages(messages);
    }

    @Transactional
    public void saveMessages(List<Message> messages) {
        messageRepository.saveAll(messages);
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
