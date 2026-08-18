package com.tplite.banking.accountservice.util;

import java.util.Random;

public class AccountNumberGenerator {
    
    private static final String TPBANK_BIN = "970423";
    private static final String DEFAULT_SEQUENCE = "01"; // 01 for primary payment account
    
    public static String generate() {
        // CIF is an 8-digit number
        String cif = generateRandomDigits(8);
        return TPBANK_BIN + cif + DEFAULT_SEQUENCE;
    }
    
    private static String generateRandomDigits(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
