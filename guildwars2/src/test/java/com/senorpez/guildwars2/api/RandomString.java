package com.senorpez.guildwars2.api;

import java.util.Random;

public class RandomString {
    public static final String generateString = new Random().ints(97, 123)
            .limit(64)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
}
