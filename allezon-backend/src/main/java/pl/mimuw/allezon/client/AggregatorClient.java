package pl.mimuw.allezon.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Aggregate;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;

import java.util.List;

@FeignClient(name = "aggregator-client", url = "${app.client.aggregator.url}")
public interface AggregatorClient {

    @GetMapping(value = "/aggregates", produces = MediaType.APPLICATION_JSON_VALUE)
    AggregatesQueryResponse getAggregates(
            @RequestParam(Constants.TIME_RANGE_PARAM) String timeRangeStr,
            @RequestParam(Constants.ACTION_PARAM) Action action,
            @RequestParam(Constants.AGGREGATES_PARAM) List<Aggregate> aggregates,
            @RequestParam(value = Constants.ORIGIN_PARAM, required = false) String origin,
            @RequestParam(value = Constants.BRAND_ID_PARAM, required = false) String brandId,
            @RequestParam(value = Constants.CATEGORY_ID_PARAM, required = false) String categoryId);
}
