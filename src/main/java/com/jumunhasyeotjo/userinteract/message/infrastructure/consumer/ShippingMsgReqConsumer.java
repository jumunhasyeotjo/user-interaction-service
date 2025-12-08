package com.jumunhasyeotjo.userinteract.message.infrastructure.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumunhasyeotjo.userinteract.message.application.MessageService;
import com.jumunhasyeotjo.userinteract.message.application.command.ShippingMessageCreateCommand;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.ShippingMessageCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingMsgReqConsumer {
    private final MessageService messageService;

    @KafkaListener(
        topics = "shipping-message",
        groupId = "message",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(
        ConsumerRecord<String, Object> record
    ) {
        String type = new String(record.headers().lastHeader("type").value());
        Object value = record.value();
        log.info("Received ShippingMessageCreateCommand: {}", value);

        switch (type) {
            case "SHIPPING_ETA_MESSAGE" -> shippingEtaMessage(value);
            default -> log.error("Unrecognized Type: {}", type);
        }
    }

    private void shippingEtaMessage(Object value) {
        try {
            ShippingMessageCreateDto dto = (ShippingMessageCreateDto) value;
            ShippingMessageCreateCommand command = dto.toCommand();
            messageService.createShippingMessage(command);
        } catch (Exception e) {
            log.error("Failed to consume shipping message. payload={}", value, e);
        }
    }
}
