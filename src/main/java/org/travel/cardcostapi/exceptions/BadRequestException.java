package org.travel.cardcostapi.exceptions;

/**
 * @author asafronov
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
