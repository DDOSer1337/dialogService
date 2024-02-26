package dialogService.services.interfaces;

import org.springframework.kafka.annotation.KafkaListener;

public interface KafkaDialogService {
        @KafkaListener(topics = "${application.kafka.topics.create-dialog}")
        void createDialog(String message);
        @KafkaListener(topics = "${application.kafka.topics.create-message}")
        void createMessage(String message);
}
