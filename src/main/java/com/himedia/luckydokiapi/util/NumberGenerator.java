package com.himedia.luckydokiapi.util;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NumberGenerator {

    public static String generateRandomNumber(int length) {
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, length)
                .map(n -> random.nextInt(10))
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());
    }

}
