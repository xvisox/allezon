package pl.mimuw.allezon.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Details about the product")
@Value
@Jacksonized
@Builder
public class Product {

    @Schema(description = "The unique ID of the product", example = "21")
    @JsonProperty("product_id")
    int productId;

    @Schema(description = "The brand ID of the product", example = "NIKE")
    @JsonProperty("brand_id")
    String brandId;

    @Schema(description = "The category ID of the product", example = "WOMAN_SHOES")
    @JsonProperty("category_id")
    String categoryId;

    @Schema(description = "The price of the product", example = "2115")
    @JsonProperty("price")
    int price;
}
