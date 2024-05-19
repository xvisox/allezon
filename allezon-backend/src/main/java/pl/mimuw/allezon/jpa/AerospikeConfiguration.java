package pl.mimuw.allezon.jpa;

import com.aerospike.client.Host;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;

import java.util.List;

@Configuration
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

    private final AerospikeProperties aerospikeProperties;

    @Override
    protected List<Host> getHosts() {
        return aerospikeProperties.getHosts().stream()
                .map(host -> new Host(host, aerospikeProperties.getPort()))
                .toList();
    }

    @Override
    protected String nameSpace() {
        return aerospikeProperties.getNamespace();
    }
}
