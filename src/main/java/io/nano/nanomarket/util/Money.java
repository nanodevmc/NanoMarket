// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.util.Money
package io.nano.nanomarket.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Money {

    // ---- поля ----
  private static final Pattern NUMBER;
  private static final BigDecimal LIMIT;

    static {
        NUMBER = Pattern.compile("^\\d+(?:[.,]\\d+)?[kmb]?$");
        LIMIT = new BigDecimal("1000000000000");
    }

  private Money() { // было: <init>
        super();
    }

  public static OptionalLong parse(String input) {
        BigDecimal temp1;
        OptionalLong temp3;
        if (input == null) {
            return OptionalLong.empty();
        }
        String string = input.trim().toLowerCase(Locale.ROOT);
        BigDecimal multiplier;
        if (NUMBER.matcher(string).matches()) {
            multiplier = BigDecimal.ONE;
            char last = string.charAt(string.length() - 1);
            if (last == 'k') {
                switch (last) {
                    case 107:
                        temp1 = BigDecimal.valueOf(1000L);
                        break;
                    case 109:
                        temp1 = BigDecimal.valueOf(1000000L);
                        break;
                    default:
                        temp1 = BigDecimal.valueOf(1000000000L);
                }
                multiplier = temp1;
                string = string.substring(0, string.length() - 1);
            } else if (last == 'm') {
                switch (last) {
                    case 107:
                        temp1 = BigDecimal.valueOf(1000L);
                        break;
                    case 109:
                        temp1 = BigDecimal.valueOf(1000000L);
                        break;
                    default:
                        temp1 = BigDecimal.valueOf(1000000000L);
                }
                multiplier = temp1;
                string = string.substring(0, string.length() - 1);
            } else if (last == 'b') {
                switch (last) {
                    case 107:
                        temp1 = BigDecimal.valueOf(1000L);
                        break;
                    case 109:
                        temp1 = BigDecimal.valueOf(1000000L);
                        break;
                    default:
                        temp1 = BigDecimal.valueOf(1000000000L);
                }
                multiplier = temp1;
                string = string.substring(0, string.length() - 1);
            }
        } else {
            return OptionalLong.empty();
        }
        try {
            BigDecimal value = new BigDecimal(string.replace(',', '.')).multiply(multiplier);
            if (value.compareTo(LIMIT) <= 0) {
                temp3 = OptionalLong.of(value.setScale(2, RoundingMode.UNNECESSARY).movePointRight(2).longValueExact());
            } else {
                return OptionalLong.empty();
            }
        } catch (ArithmeticException value) {
            return OptionalLong.empty();
        }
        return temp3;
    }

  public static long fromDouble(double amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValue();
    }

  public static double toDouble(long cents) {
        return BigDecimal.valueOf(cents, 2).doubleValue();
    }

  public static String format(long cents) {
        boolean negative = cents < 0L;
        long abs = Math.abs(cents);
        long whole = abs / 100L;
        long frac = abs % 100L;
        String digits = Long.toString(whole);
        StringBuilder grouped = new StringBuilder();
        for (out = 0; out < digits.length(); ++out) {
            if (out > 0 && !((digits.length() - out) % 3)) {
                grouped.append(' ');
            }
            grouped.append(digits.charAt(out));
        }
        StringBuilder out = new StringBuilder();
        if (negative != 0) {
            out.append('-');
        }
        out.append(grouped);
        if (frac != 0L) {
            out.append('.');
            if (frac < 10L) {
                out.append('0');
            }
            out.append(frac);
        }
        return out.toString();
    }

  public static String format(long cents, String currency) {
        return format(cents) + " " + currency;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
