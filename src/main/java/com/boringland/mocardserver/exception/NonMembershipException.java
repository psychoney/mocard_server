package com.boringland.mocardserver.exception;

public class NonMembershipException extends RuntimeException{
    public NonMembershipException(String message) {
        super(message);
    }
}
