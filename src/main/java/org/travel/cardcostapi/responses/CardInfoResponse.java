package org.travel.cardcostapi.responses;

import lombok.Data;

/**
 * @author asafronov
 * {
 *   "number": {
 *     "length": 16,
 *     "luhn": true
 *   },
 *   "scheme": "visa",
 *   "type": "debit",
 *   "brand": "Visa/Dankort",
 *   "prepaid": false,
 *   "country": {
 *     "numeric": "208",
 *     "alpha2": "DK",
 *     "name": "Denmark",
 *     "emoji": "🇩🇰",
 *     "currency": "DKK",
 *     "latitude": 56,
 *     "longitude": 10
 *   },
 *   "bank": {
 *     "name": "Jyske Bank",
 *     "url": "www.jyskebank.dk",
 *     "phone": "+4589893300",
 *     "city": "Hjørring"
 *   }
 * }
 */
@Data
public class CardInfoResponse {
    private Number number;
    private String scheme;
    private String type;
    private String brand;
    private boolean prepaid;
    private Country country;
    private Bank bank;

    @Data
    public static class Number {
        private int length;
        private boolean luhn;
    }

    @Data
    public static class Country {
        private String numeric;
        private String alpha2;
        private String name;
        private String emoji;
        private String currency;
        private double latitude;
        private double longitude;
    }

    @Data
    public static class Bank {
        private String name;
        private String url;
        private String phone;
        private String city;
    }
}
