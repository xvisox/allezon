package pl.mimuw.allezon.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.UserTag;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor
@Document(collection = Constants.PROFILES_COLLECTION)
public class ProfileEntity {

    @Id
    private String cookie;

    @Version
    private int generation;

    private List<UserTag> views;

    private List<UserTag> buys;
}
