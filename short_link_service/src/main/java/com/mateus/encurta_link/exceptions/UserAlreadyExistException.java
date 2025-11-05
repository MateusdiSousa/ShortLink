package com.mateus.encurta_link.exceptions;

public class UserAlreadyExistException extends Exception{
    public UserAlreadyExistException() {
        super("Email already used!");
    }
}
