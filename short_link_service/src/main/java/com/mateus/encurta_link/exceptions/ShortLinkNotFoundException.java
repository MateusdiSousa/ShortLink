package com.mateus.encurta_link.exceptions;


public class ShortLinkNotFoundException extends RuntimeException {

    public ShortLinkNotFoundException() { 
        super("Link not found!.");
    }

    public ShortLinkNotFoundException(String message) { 
        super(message);
    }

}