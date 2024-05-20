package pl.mimuw.allezon.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.aerospike.annotation.Expiration;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;
import pl.mimuw.allezon.Constants;

@ToString
@Getter
@AllArgsConstructor
@Document(collection = Constants.AGGREGATES_COLLECTION)
public class AggregateEntity {

    @Id
    private String hashKey;

    @Expiration
    private long expirationSeconds;

    private long count;

    private long sumPrice;
}
