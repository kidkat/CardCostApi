package org.travel.cardcostapi.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void getMaskedCardNumber1() {
        String cardNumber = "12345678";
        String testResult = "1234**78";

        String result = Utils.getMaskedCardNumber(cardNumber);
        assertEquals(testResult, result);
    }

    @Test
    void getMaskedCardNumber2() {
        String cardNumber = "1234567890123456789";
        String testResult = "1234*************89";

        String result = Utils.getMaskedCardNumber(cardNumber);
        assertEquals(testResult, result);
    }
}