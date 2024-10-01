package org.travel.cardcostapi.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCardCostResponse {
    @JsonProperty("country")
    private String country;
    @JsonProperty("cost")
    private double cost;
}
