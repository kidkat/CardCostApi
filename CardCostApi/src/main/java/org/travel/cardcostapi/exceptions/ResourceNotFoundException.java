package org.travel.cardcostapi.exceptions;

/**
 * @author asafronov
 */
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
