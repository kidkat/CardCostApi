package org.travel.cardcostapi.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.travel.cardcostapi.exceptions.BadRequestException;

/**
 * @author asafronov
 */
@Data
public class PaymentCardCostRequest {
    @JsonProperty("card_number")
    private String cardNumber;

    public void validate(){
        if (cardNumber == null || cardNumber.trim().isEmpty())
            throw new BadRequestException("CardNumber cannot be null or empty");

        if(cardNumber.length() < 8 || cardNumber.length() > 19)
            throw new BadRequestException("CardNumber must be greater than 8 and less than 19 digits");
    }
}
