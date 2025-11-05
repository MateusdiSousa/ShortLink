package com.mateus.encurta_link.exceptions;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(){
        super("User not found ot not exist!");
    }
    
}
