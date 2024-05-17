package pl.mimuw.allezon.jpa.entity;

import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.UserTag;

import java.util.List;

@Document(collection = Constants.PROFILES_COLLECTION)
public record ProfileEntity(
        @Id String cookie,
        @Version int generation,
        List<UserTag> views,
        List<UserTag> buys
) {

}
