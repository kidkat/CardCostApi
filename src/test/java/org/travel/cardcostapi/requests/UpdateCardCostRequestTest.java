package org.travel.cardcostapi.requests;

import org.junit.jupiter.api.Test;
import org.travel.cardcostapi.exceptions.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCardCostRequestTest {

    @Test
    void updateCardCostValidate1() {
        UpdateCardCostRequest request = new UpdateCardCostRequest();
        request.setCost(50);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            request.validate();
        });

        assertEquals("Country cannot be null or empty", exception.getMessage());
    }

    @Test
    void updateCardCostValidate2() {
        UpdateCardCostRequest request = new UpdateCardCostRequest();
        request.setCountry("GR");
        request.setCost(-50);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            request.validate();
        });

        assertEquals("Cost cannot be negative", exception.getMessage());
    }
}