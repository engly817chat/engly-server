package com.engly.engly_server.utils.passwordgenerateutil;

import com.engly.engly_server.exception.PasswordGeneratorException;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class PasswordGeneratorImpl implements PasswordGenerator {
    private final List<Character> pwd = new LinkedList<>();
    private final SecureRandom random = new SecureRandom();

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.<>?";
    private static final String ALL = UPPER + LOWER + DIGITS + SYMBOLS;

    public PasswordGeneratorImpl startGenerate(int length) {
        if (length < 8) throw new PasswordGeneratorException("Length must be at least 8");
        for (int i = pwd.size(); i < length; i++) pwd.add(randomChar(ALL));
        return this;
    }

    public PasswordGeneratorImpl addUpperLetters() {
        pwd.add(randomChar(UPPER));
        return this;
    }

    public PasswordGeneratorImpl addLowerLetters() {
        pwd.add(randomChar(LOWER));
        return this;
    }

    public PasswordGeneratorImpl addSymbols() {
        pwd.add(randomChar(SYMBOLS));
        return this;
    }

    public PasswordGeneratorImpl addDigits() {
        pwd.add(randomChar(DIGITS));
        return this;
    }

    public String generate() {
        Collections.shuffle(pwd, random);

        final var sb = new StringBuilder();
        pwd.forEach(sb::append);
        return sb.toString();
    }

    private char randomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }
}
