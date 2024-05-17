package pl.mimuw.allezon.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AggregateController {

    /*
    @PostMapping("/aggregates")
    public ResponseEntity<AggregatesQueryResult> getAggregates(@RequestParam("time_range") String timeRangeStr,
                                                               @RequestParam("action") Action action,
                                                               @RequestParam("aggregates") List<Aggregate> aggregates,
                                                               @RequestParam(value = "origin", required = false) String origin,
                                                               @RequestParam(value = "brand_id", required = false) String brandId,
                                                               @RequestParam(value = "category_id", required = false) String categoryId,
                                                               @RequestBody(required = false) AggregatesQueryResult expectedResult) {

        return ResponseEntity.ok(expectedResult);
    }
    */
}
