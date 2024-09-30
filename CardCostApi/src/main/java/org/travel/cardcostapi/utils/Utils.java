package org.travel.cardcostapi.utils;

/**
 * @author asafronov
 */
public class Utils {
    public static long getStartTime() {
        return System.currentTimeMillis();
    }

    public static long getExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    public static String getMaskedCardNumber(String cardNumber){
        String firstSixDigits = cardNumber.substring(0, 4);
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 2);
        String maskedPart = "*".repeat(cardNumber.length() - 6);
        return firstSixDigits + maskedPart + lastFourDigits;
    }
}
