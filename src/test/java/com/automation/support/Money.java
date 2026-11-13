package com.automation.support;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses monetary values such as "$29.99" or "Item total: $29.99". */
public final class Money {

    private static final Pattern AMOUNT = Pattern.compile("\\d+(?:\\.\\d{2})?");

    private Money() {
    }

    public static BigDecimal parse(String text) {
        Matcher matcher = AMOUNT.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No monetary value found in: " + text);
        }
        return new BigDecimal(matcher.group());
    }
}
