package org.travel.cardcostapi.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.travel.cardcostapi.exceptions.BadRequestException;

/**
 * @author asafronov
 */
@Data
public class UpdateCardCostRequest {
    @NotNull
    @NotEmpty
    @JsonProperty("country")
    private String country;
    @NotNull
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
