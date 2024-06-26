package pl.mimuw.allezon.kafka;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import pl.mimuw.allezon.domain.UserTagMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, UserTagMessage> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps,
                new StringDeserializer(),
                new JsonDeserializer<>(UserTagMessage.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserTagMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserTagMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(kafkaProperties.getConcurrency());
        return factory;
    }
}
