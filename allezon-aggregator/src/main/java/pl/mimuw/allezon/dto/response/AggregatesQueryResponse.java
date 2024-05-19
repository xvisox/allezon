package pl.mimuw.allezon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Schema(description = "Response containing aggregate query results")
@Value
@Jacksonized
@Builder
public class AggregatesQueryResponse {

    @Schema(description = "List of column names")
    List<String> columns;

    @Schema(description = "List of rows, where each row is a list of string values")
    List<List<String>> rows;
}