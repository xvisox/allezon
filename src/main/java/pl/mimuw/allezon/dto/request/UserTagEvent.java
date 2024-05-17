package pl.mimuw.allezon.dto.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import pl.mimuw.allezon.domain.UserTag;

@Value
@Jacksonized
@Builder
@EqualsAndHashCode(callSuper = true)
public class UserTagEvent extends UserTag {
    String cookie;
}
