package pl.mimuw.allezon.jpa.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.xerial.snappy.Snappy;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.UserTag;

import java.io.IOException;
import java.util.List;

@Setter
@NoArgsConstructor
@Document(collection = Constants.PROFILES_COLLECTION)
public class ProfileEntity {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    private String cookie;

    @Getter
    @Version
    private int generation;

    private byte[] compressedViews;

    private byte[] compressedBuys;

    public ProfileEntity(final String cookie, final int generation,
                         final List<UserTag> views, final List<UserTag> buys) {
        this.cookie = cookie;
        this.generation = generation;
        this.compressedViews = compress(views);
        this.compressedBuys = compress(buys);
    }

    public List<UserTag> getViews() {
        return decompress(compressedViews);
    }

    public List<UserTag> getBuys() {
        return decompress(compressedBuys);
    }

    private byte[] compress(final List<UserTag> userTags) {
        try {
            String json = objectMapper.writeValueAsString(userTags);
            return Snappy.compress(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress data", e);
        }
    }

    private List<UserTag> decompress(final byte[] compressedData) {
        try {
            if (compressedData == null) {
                return null;
            }
            final String json = Snappy.uncompressString(compressedData);
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, UserTag.class));
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress data", e);
        }
    }
}
