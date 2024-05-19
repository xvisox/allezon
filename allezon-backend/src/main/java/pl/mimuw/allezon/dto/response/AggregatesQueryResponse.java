package pl.mimuw.allezon.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Jacksonized
@Builder
public class AggregatesQueryResponse {
    List<String> columns;
    List<List<String>> rows;
}