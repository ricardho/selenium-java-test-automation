package com.automation.driver;

import java.util.Arrays;
import java.util.Locale;

public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String value) {
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported browser '%s'. Use one of %s".formatted(value, Arrays.toString(values())), e);
        }
    }
}
