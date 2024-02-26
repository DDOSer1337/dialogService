package dialogService.exeptionHandler.exceptions;

public class AppKafkaException extends Exception {
    public AppKafkaException(String errorSentToKafka) {
        super(errorSentToKafka);
    }
}
