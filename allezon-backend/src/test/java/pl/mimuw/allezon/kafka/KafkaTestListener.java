package pl.mimuw.allezon.kafka;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.UserTagMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class KafkaTestListener {

    private final BlockingQueue<UserTagMessage> userTagMessages = new LinkedBlockingQueue<>();

    @SneakyThrows
    @KafkaListener(topics = Constants.USER_TAG_TOPIC, groupId = Constants.DEFAULT_GROUP_ID)
    private void listen(final UserTagMessage userTagEvent) {
        log.info("Received user tag event: {}", userTagEvent);
        userTagMessages.put(userTagEvent);
    }

    @SneakyThrows
    public UserTagMessage pollUserTagMessage() {
        return userTagMessages.poll(5, TimeUnit.SECONDS);
    }
}
