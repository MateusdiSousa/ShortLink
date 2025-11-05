package com.mateus.encurta_link.utils;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class RandomAlphanumeric {
    public static String GenerateString() {
        return new Random().ints(97, 122 + 1)
        .limit(10)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    }

}
