package pl.mimuw.allezon;

import com.aerospike.client.policy.CommitLevel;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.WritePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@DirtiesContext
@AutoConfigureObservability
@ActiveProfiles("it")
@SpringBootTest(classes = {AllezonApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIT {

    @Autowired
    protected AerospikeTemplate aerospikeTemplate;

    @LocalServerPort
    protected Integer port;

    protected static WritePolicy createWritePolicy(final int generation) {
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.socketTimeout = 15000;
        writePolicy.totalTimeout = 35000;
        writePolicy.commitLevel = CommitLevel.COMMIT_MASTER;
        writePolicy.generation = generation;
        writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
        return writePolicy;
    }
}
