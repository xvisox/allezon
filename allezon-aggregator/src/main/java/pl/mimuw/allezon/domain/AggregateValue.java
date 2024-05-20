package pl.mimuw.allezon.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AggregateValue {
    private long count;
    private long priceSum;

    public void add(final long price) {
        count++;
        priceSum += price;
    }
}
