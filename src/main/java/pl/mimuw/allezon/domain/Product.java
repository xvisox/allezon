package pl.mimuw.allezon.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class Product {
    @JsonProperty("product_id")
    int productId;
    @JsonProperty("brand_id")
    String brandId;
    @JsonProperty("category_id")
    String categoryId;
    @JsonProperty("price")
    int price;
}
