package org.turbofinn.util;

import java.util.Random;

public class TFUtils {
    public static String generateRestaurantAccountNo(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        String initial = "TT";

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return initial+sb.toString();
    }
}
