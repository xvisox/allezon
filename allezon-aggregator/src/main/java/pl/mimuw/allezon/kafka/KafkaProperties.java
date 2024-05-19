package pl.mimuw.allezon.kafka;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.mimuw.allezon.Constants;

@Slf4j
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = KafkaProperties.PREFIX)
public class KafkaProperties {
    public static final String PREFIX = Constants.APP_PROPS_PREFIX + ".kafka";

    private String bootstrapServers;
    private String groupId;
    private int concurrency;

    @PostConstruct
    private void postConstruct() {
        log.info("Kafka properties: bootstrapServers={}, groupId={}, concurrency={}", bootstrapServers, groupId, concurrency);
    }
}
