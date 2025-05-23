package com.engly.engly_server.utils.passwordgenerateutil;

public sealed interface PasswordGenerator permits PasswordGeneratorImpl {
    static PasswordGenerator builder() {
        return new PasswordGeneratorImpl();
    }

    PasswordGenerator startGenerate(int length);
    PasswordGenerator addUpperLetters();
    PasswordGenerator addLowerLetters();
    PasswordGenerator addSymbols();
    PasswordGenerator addDigits();
    String generate();
}
