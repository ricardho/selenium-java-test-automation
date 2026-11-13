package com.automation.support.evidence;

import java.util.Arrays;
import java.util.Locale;

/** How much evidence is collected for each test. Configured with {@code evidence.mode}. */
public enum EvidenceMode {

    /** Screenshot, URL and page source only when a test fails. Fastest; default for local runs. */
    FAILURE,

    /** Also a final screenshot of every test, passed or failed. */
    ALWAYS,

    /** Also a screenshot after every step. Used for audit and UAT sign-off. */
    STEPS;

    public boolean capturesPassedTests() {
        return this != FAILURE;
    }

    public static EvidenceMode from(String value) {
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported evidence.mode '%s'. Use one of %s".formatted(value, Arrays.toString(values())), e);
        }
    }
}
