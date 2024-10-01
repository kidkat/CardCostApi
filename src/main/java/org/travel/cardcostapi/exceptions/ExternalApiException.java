package org.travel.cardcostapi.exceptions;

/**
 * @author asafronov
 */
public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message);
    }
}
