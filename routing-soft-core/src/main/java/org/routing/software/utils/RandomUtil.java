package org.routing.software.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A utility class for producing random numbers
 */
public class RandomUtil {

    public static Integer produceRandomNumber(int startIndex, int endIndex) {
        return ThreadLocalRandom.current().nextInt(startIndex, endIndex);
        //or simpler return ThreadLocalRandom.current().nextInt(startIndex, endIndex);
    }
}
