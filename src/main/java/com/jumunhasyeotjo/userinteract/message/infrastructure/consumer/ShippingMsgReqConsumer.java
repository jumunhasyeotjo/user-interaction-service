package com.jumunhasyeotjo.userinteract.message.infrastructure.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumunhasyeotjo.userinteract.message.application.MessageService;
import com.jumunhasyeotjo.userinteract.message.application.command.ShippingMessageCreateCommand;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.ShippingMessageCreateDto;
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
        String type = new String(record.headers().lastHeader("type").value());
        String payload = record.value();
        log.info("Received message: type={}, payload={}", type, payload);

        switch (type) {
            case "SHIPPING_ETA_MESSAGE" -> handleShippingEtaMessage(payload);
            // 다른 타입 메시지는 case 추가
            default -> log.error("Unrecognized Type: {}", type);
        }
    }

    private void handleShippingEtaMessage(String payload) {
        try {
            // JSON 문자열을 DTO로 변환
            ShippingMessageCreateDto dto = objectMapper.readValue(payload, ShippingMessageCreateDto.class);
            ShippingMessageCreateCommand command = dto.toCommand();
            messageService.createShippingMessage(command);
        } catch (Exception e) {
            log.error("Failed to consume shipping message. payload={}", payload, e);
        }
    }
}
