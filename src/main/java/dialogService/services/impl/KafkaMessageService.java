package dialogService.services.impl;

import dialogService.exeptionHandler.exceptions.AppKafkaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<SendResult<String, Object>> produce(String topic, Object object){
        var completableFuture =  kafkaTemplate.send(topic,object);
        return completableFuture.whenComplete(((sendResult, throwable) -> {
            if (throwable != null) {
                log.error("Error sent to Kafka -> {}, {}", object, throwable.getMessage());
                try {
                    throw new AppKafkaException("Error Sent to Kafka");
                } catch (AppKafkaException e) {
                    throw new RuntimeException(e);
                }
            } else {
                log.info("Sent to Kafka success topic: {} -> {}", topic,  object);
            }
        }));
    }

}
