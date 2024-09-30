package org.travel.cardcostapi.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.travel.cardcostapi.exceptions.BadRequestException;

/**
 * @author asafronov
 */
@Data
public class UpdateCardCostRequest {
    @JsonProperty("country")
    private String country;
    @JsonProperty("cost")
    private double cost;

    public void validate(){
        if (country == null || country.trim().isEmpty()) {
            throw new BadRequestException("Country cannot be null or empty");
        }

        country = country.toUpperCase();

        if (cost < 0) {
            throw new BadRequestException("Cost cannot be negative");
        }
    }
}
