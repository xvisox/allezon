package pl.mimuw.allezon.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mimuw.allezon.client.AggregatorClient;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Aggregate;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AggregateService {

    private final AggregatorClient aggregatorClient;

    public AggregatesQueryResponse getAggregates(final String timeRangeStr, final Action action,
                                                 final List<Aggregate> aggregates, final String origin,
                                                 final String brandId, final String categoryId) {
        return aggregatorClient.getAggregates(timeRangeStr, action, aggregates, origin, brandId, categoryId);
    }
}
