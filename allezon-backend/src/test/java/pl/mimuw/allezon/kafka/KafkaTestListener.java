package pl.mimuw.allezon.kafka;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.dto.request.UserTagEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class KafkaTestListener {

    private final BlockingQueue<UserTagEvent> userTagEvents = new LinkedBlockingQueue<>();

    @SneakyThrows
    @KafkaListener(topics = Constants.USER_TAG_TOPIC, groupId = "allezon")
    private void listen(UserTagEvent userTagEvent) {
        log.info("Received user tag event: {}", userTagEvent);
        userTagEvents.put(userTagEvent);
    }

    @SneakyThrows
    public UserTagEvent pollUserTagEvent() {
        return userTagEvents.poll(5, TimeUnit.SECONDS);
    }
}
