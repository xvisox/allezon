package pl.mimuw.allezon.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AggregateValue {
    private long count;
    private long priceSum;

    public void add(final int price) {
        count++;
        priceSum += price;
    }
}
