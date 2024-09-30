package org.travel.cardcostapi.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.travel.cardcostapi.exceptions.BadRequestException;

/**
 * @author asafronov
 */
@Data
public class PaymentCardCostRequest {
    @JsonProperty("cardNumber")
    private String cardNumber;

    public void validate(){
        if (cardNumber == null || cardNumber.trim().isEmpty())
            throw new BadRequestException("CardNumber cannot be null or empty");

        if(cardNumber.length() != 16)
            throw new BadRequestException("CardNumber must be 16 digits");
    }
}
