package pl.mimuw.allezon.jpa;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.mimuw.allezon.Constants;

import java.util.List;

@Slf4j
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = AerospikeProperties.PREFIX)
public class AerospikeProperties {
    public static final String PREFIX = Constants.APP_PROPS_PREFIX + ".aerospike";

    private List<String> hosts;
    private int port;
    private String namespace;

    @PostConstruct
    private void postConstruct() {
        log.info("Aerospike properties: hosts={}, port={}, namespace={}", hosts, port, namespace);
    }
}
