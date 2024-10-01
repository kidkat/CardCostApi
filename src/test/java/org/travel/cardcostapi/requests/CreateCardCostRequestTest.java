package org.travel.cardcostapi.requests;

import org.junit.jupiter.api.Test;
import org.travel.cardcostapi.exceptions.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

class CreateCardCostRequestTest {

    @Test
    void cardCostValidate1() {
        CreateCardCostRequest request = new CreateCardCostRequest();
        request.setCost(50);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            request.validate();
        });

        assertEquals("Country cannot be null or empty", exception.getMessage());
    }

    @Test
    void cardCostValidate2() {
        CreateCardCostRequest request = new CreateCardCostRequest();
        request.setCountry("GR");
        request.setCost(-50);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            request.validate();
        });

        assertEquals("Cost cannot be negative", exception.getMessage());
    }
}