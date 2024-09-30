package org.travel.cardcostapi.requests;

import org.junit.jupiter.api.Test;
import org.travel.cardcostapi.exceptions.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCardCostRequestTest {

    @Test
    void paymentCardCostValidate1() {
        PaymentCardCostRequest request = new PaymentCardCostRequest();

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            request.validate();
        });

        assertEquals("CardNumber cannot be null or empty", exception.getMessage());
    }

    @Test
    void paymentCardCostValidate2() {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("1234567");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            request.validate();
        });

        assertEquals("CardNumber must be greater than 8 and less than 19 digits", exception.getMessage());

        //test2
        request.setCardNumber("12345678901234567890");

        BadRequestException exception2 = assertThrows(BadRequestException.class, () -> {
            request.validate();
        });

        assertEquals("CardNumber must be greater than 8 and less than 19 digits", exception2.getMessage());

    }

}