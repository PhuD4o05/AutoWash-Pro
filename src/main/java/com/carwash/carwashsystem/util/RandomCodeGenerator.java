package com.carwash.carwashsystem.util;

import java.security.SecureRandom;
import java.util.UUID;

public class RandomCodeGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789";

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String generateAlphanumericCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String generateVoucherCode() {
        return "VCH" + generateAlphanumericCode(8);
    }
}