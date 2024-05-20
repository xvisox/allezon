package pl.mimuw.allezon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Aggregate;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;
import pl.mimuw.allezon.service.AggregateService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AggregateController {

    private final AggregateService aggregateService;

    @Operation(summary = "Get aggregated user tags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.HTTP_OK, description = "Aggregated user tags returned",
                    content = @Content(schema = @Schema(implementation = AggregatesQueryResponse.class))),
            @ApiResponse(responseCode = Constants.HTTP_INTERNAL_SERVER_ERROR, description = "Internal server error",
                    content = @Content())
    })
    @GetMapping(value = "/aggregates", produces = MediaType.APPLICATION_JSON_VALUE)
    public AggregatesQueryResponse getAggregates(
            @RequestParam(Constants.TIME_RANGE_PARAM) String timeRangeStr,
            @RequestParam(Constants.ACTION_PARAM) Action action,
            @RequestParam(Constants.AGGREGATES_PARAM) List<Aggregate> aggregates,
            @RequestParam(value = Constants.ORIGIN_PARAM, required = false) String origin,
            @RequestParam(value = Constants.BRAND_ID_PARAM, required = false) String brandId,
            @RequestParam(value = Constants.CATEGORY_ID_PARAM, required = false) String categoryId
    ) {
        origin = Objects.equals(origin, "") ? null : origin;
        brandId = Objects.equals(brandId, "") ? null : brandId;
        categoryId = Objects.equals(categoryId, "") ? null : categoryId;
        return aggregateService.getAggregates(timeRangeStr, action, aggregates, origin, brandId, categoryId);
    }
}
