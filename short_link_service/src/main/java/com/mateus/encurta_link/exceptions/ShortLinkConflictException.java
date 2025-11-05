package com.mateus.encurta_link.exceptions;

public class ShortLinkConflictException extends RuntimeException {

    public ShortLinkConflictException(String message) {
        super(message);
    }

    public ShortLinkConflictException() {
        super("encurted link already exist!.");
    }
}
