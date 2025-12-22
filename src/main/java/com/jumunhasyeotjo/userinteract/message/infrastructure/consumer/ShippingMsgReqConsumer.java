package com.jumunhasyeotjo.userinteract.message.infrastructure.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumunhasyeotjo.userinteract.message.application.MessageService;
import com.jumunhasyeotjo.userinteract.message.application.command.CreateShippingDelayedMessageCommand;
import com.jumunhasyeotjo.userinteract.message.application.command.CreateShippingEtaMessageCommand;
import com.jumunhasyeotjo.userinteract.message.infrastructure.event.ShippingDelayedMessageEvent;
import com.jumunhasyeotjo.userinteract.message.infrastructure.event.ShippingEtaMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingMsgReqConsumer {

    private final MessageService messageService;
    private final ObjectMapper objectMapper; // JSON -> DTO 변환용

    @KafkaListener(
        topics = "shipping-message",
        groupId = "message",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record) {
        String type = new String(record.headers().lastHeader("eventType").value());
        String payload = record.value();
        log.info("Received message: type={}, payload={}", type, payload);

        switch (type) {
            case "SHIPPING_ETA_MESSAGE" -> handleShippingEtaMessage(payload);
            case "SHIPPING_DELAYED" -> handleShippingDelayedMessage(payload);
            default -> log.error("Unrecognized Type: {}", type);
        }
    }

    // 배송 생성
    private void handleShippingEtaMessage(String payload) {
        try {
            log.info("Send Slack ShippingEtaMessage");
//            ShippingEtaMessageEvent event = objectMapper.readValue(payload, ShippingEtaMessageEvent.class);
//            CreateShippingEtaMessageCommand command = event.toCommand();
//            messageService.createShippingMessage(command);
        } catch (Exception e) {
            log.error("Failed to consume shipping message. payload={}", payload, e);
        }
    }

    // 배송 지연
    private void handleShippingDelayedMessage(String payload) {
        try {
            log.info("Send Slack ShippingDelayedMessage");
//            ShippingDelayedMessageEvent event = objectMapper.readValue(payload, ShippingDelayedMessageEvent.class);
//            CreateShippingDelayedMessageCommand command = event.toCommand();
//            messageService.createShippingDelayedMessage(command);
        } catch (Exception e) {
            log.error("Failed to consume shipping message. payload={}", payload, e);
        }
    }
}
